package com.ambrosia.profile_service.kafka;

import java.util.HashMap;
import java.util.UUID;

import org.ambrosia.notification_service.kafka_events.ActivityEvent;
import org.ambrosia.notification_service.kafka_events.ActivityEventType;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.Stores;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties.AckMode;

import com.ambrosia.content_service.kafka_events.PostEvent;
import com.ambrosia.content_service.kafka_events.UserFollowEvent;
import com.ambrosia.library_core.dto.Topics;
import com.ambrosia.comment_service.kafka_events.CommentEvent;
import com.ambrosia.profile_service.kafka.serde.ActivityEventSerde;
import com.ambrosia.profile_service.kafka.serde.CommentEventSerde;
import com.ambrosia.profile_service.kafka.serde.PostEventSerde;
import com.ambrosia.profile_service.kafka.serde.PresenseStateSerde;
import com.ambrosia.profile_service.kafka.serde.CompactUuidSerde;
import com.ambrosia.profile_service.kafka.serde.UserAggregationSerde;
import com.ambrosia.profile_service.kafka.serde.UserFollowAggregationSerde;
import com.ambrosia.profile_service.kafka.serde.UserFollowEventSerde;
import com.ambrosia.profile_service.kafka_events.UserAggregation;
import com.ambrosia.profile_service.kafka_events.UserFollowAggregation;

import lombok.extern.slf4j.Slf4j;

/**
 * Configuration for kafka stream
 */
@Slf4j
@Configuration
@EnableKafka
@EnableKafkaStreams
public class KafkaStream{
    private final CompactUuidSerde uuidSerde = new CompactUuidSerde();

    /**
     * Merges partial data streams and publishes it to a local topic
     * for enrichment with local user projections
     */
    @Bean
    KStream<String, UserAggregation> kStreamUser(StreamsBuilder streamsBuilder){
        var commentMessageSerde = new CommentEventSerde();
        var postMessageSerde = new PostEventSerde();
        var userAggregationSerde = new UserAggregationSerde();
        
        var postStream = streamsBuilder.stream(Topics.POST_EVENT, Consumed.with(Serdes.String(), postMessageSerde))
            .filter((key, value) -> value.getEventCase() == PostEvent.EventCase.CREATED)
            .mapValues(postMessage -> UserAggregation.newBuilder()
                    .setPost(postMessage.getCreated())
                    .build()
            );

        var commStream = streamsBuilder.stream(Topics.COMMENT_EVENT, Consumed.with(Serdes.String(), commentMessageSerde))
            .filter((key, value) -> value.getEventCase() == CommentEvent.EventCase.CREATED)
            .mapValues(commentMessage -> UserAggregation.newBuilder()
                    .setComment(commentMessage.getCreated())
                    .build()
            )
            .merge(postStream);
        commStream.to(Topics.USER_AGGREGATION,
            Produced.with(Serdes.String(), userAggregationSerde)
        );
        return commStream;
    }

    @Bean
    KStream<String, UserFollowEvent> userFollowStream(StreamsBuilder builder){
        var userFollowSerde = new UserFollowEventSerde();
        builder.addStateStore(
            Stores.keyValueStoreBuilder(
                Stores.inMemoryKeyValueStore("user-follow-aggregation-store"),
                uuidSerde,
                Serdes.Integer()
            )
        );
        return builder.stream(Topics.USER_FOLLOW_EVENT, Consumed.with(Serdes.String(), userFollowSerde));
    }

    @Bean
    KStream<String, UserFollowAggregation> kStreamUserFollow(KStream<String, UserFollowEvent> userFollowStream){
        var userFollowAggregationSerde = new UserFollowAggregationSerde();
        var userStream = userFollowStream
            .map((key, value) -> KeyValue.pair(
                UUID.fromString(key),
                switch(value.getEventCase()){
                    case FOLLOWED -> 1;
                    case UNFOLLOWED -> -1;
                    default -> 0;
                }
            ))
            .process(
                () -> new AggregationBatchProcessor<UUID>("user-follow-aggregation-store"),
                Named.as("user-follow-aggregation-processor"),
                "user-follow-aggregation-store"
            )
            .map((key, value) -> KeyValue.pair(
                key.toString(),
                UserFollowAggregation.newBuilder()
                    .setUserId(key.toString())
                    .setDelta(value)
                    .build()
            ));
        userStream.to(
            Topics.USER_FOLLOW_AGGREGATION,
            Produced.with(
                Serdes.String(),
                userFollowAggregationSerde
            )
        );
        return userStream;
    }

    // агрегация состояния присутствия пользователя с дедупликацией множества подключений
    // при connect -> +1 к внутреннему счетчику соединений агрегата
    // при disconnect -> -1 к внутреннему счетчику соединений агрегата
    // processor проверяет в state store по timestamp данные, вычисляя дельту времени, в случае большой дельты
    // событие disconnect downstream + удаление из kv-store
    // События (connect, disconnect) считаются во временные окна (по одному ключу) + reduce для последнего события (через suppress),
    // тем самым отображая количество подключенных пользователей
    // Для дедупликации событий (при нескольких подключений через sse), используем connection_delta - счетчик, отображающий
    // состояние подключений пользователя через notification-service
    // состояние connect/disconnect меняется при изменении счетчика до 0, с 0, либо при срабатывании 
    // выброса через рассчет дельты времени
    @Bean 
    KStream<UUID, ActivityEvent> kStreamActivityEvent(StreamsBuilder builder){
        var activityEventSerde = new ActivityEventSerde();
        var presenseStateSerde = new PresenseStateSerde();

        // храним состояния в оперативной памяти
        var activityStore = Stores.keyValueStoreBuilder(
            Stores.inMemoryKeyValueStore("user-activity-store"),
            uuidSerde,
            presenseStateSerde
        );

        var dedupStore = Stores.keyValueStoreBuilder(
            Stores.inMemoryKeyValueStore("user-activity-deduplication-store"),
            uuidSerde,
            activityEventSerde
        );

        builder.addStateStore(activityStore);
        builder.addStateStore(dedupStore);

        // дедупликация соединений, считаем только последние события по ключу пользователя
        // во временном окне
        var stream = builder.stream(Topics.USER_ACTIVITY, Consumed.with(Serdes.String(), activityEventSerde))
            .selectKey((key, value) -> UUID.fromString(key))
            .process(ActivityIdleProcessor::new, "user-activity-store")
            .process(
                () -> new ReduceBatchProcessor<UUID, ActivityEvent>("user-activity-deduplication-store"), 
                Named.as("user-activity-deduplication-process"),
                "user-activity-deduplication-store"
            )
            .filter((key, value) -> value.getType() != ActivityEventType.LEASE_REFRESH);

        stream
            .selectKey((key, value) -> key.toString())
            .to(
                Topics.USER_STATUS, 
                Produced.with(
                    Serdes.String(),
                    activityEventSerde
                )
            );
        
        // агрегируем дедуплицированные события по типу события в количество общих пользователей
        stream.selectKey((key, value) -> "active-users")
            .groupByKey(Grouped.with(Serdes.String(), activityEventSerde))
            .aggregate(() -> 0L, (key, value, aggregate) -> 
                aggregate + 
                switch(value.getType()){
                    case CONNECT -> 1;
                    case DISCONNECT -> -1;
                    default -> 0;
                },
                Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("users-online-store")
                    .withKeySerde(Serdes.String())
                    .withValueSerde(Serdes.Long())
            )
            .toStream()
            .to(
                "users.online",
                Produced.with(
                    Serdes.String(), 
                    Serdes.Long()
                )
            );
        return stream;
    }

    /**
     * Container factories for batch requests on listeners
     */
    @Bean
    ConcurrentKafkaListenerContainerFactory<String, byte[]> kafkaUserAggregationListenerContainerFactory(ConsumerFactory<String, byte[]> cf){
        var factory = new ConcurrentKafkaListenerContainerFactory<String, byte[]>();
        var configProps = new HashMap<String, Object>(cf.getConfigurationProperties());
        configProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 300);
        var customFactory = new DefaultKafkaConsumerFactory<String, byte[]>(configProps);
        factory.setConsumerFactory(customFactory);
        factory.setBatchListener(true);
        factory.setConcurrency(1);
        factory.getContainerProperties().setAckMode(AckMode.BATCH);
        factory.getContainerProperties().setPollTimeout(3000);
        return factory;
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, byte[]> kafkaUserFollowListenerContainerFactory(ConsumerFactory<String, byte[]> cf){
        var factory = new ConcurrentKafkaListenerContainerFactory<String, byte[]>();
        var configProps = new HashMap<String, Object>(cf.getConfigurationProperties());
        configProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 300);
        var customFactory = new DefaultKafkaConsumerFactory<String, byte[]>(configProps);
        factory.setConsumerFactory(customFactory);
        factory.setBatchListener(true);
        factory.setConcurrency(1);
        factory.getContainerProperties().setAckMode(AckMode.BATCH);
        factory.getContainerProperties().setPollTimeout(3000);
        return factory;
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, byte[]> kafkaUserPresenseContainerFactory(ConsumerFactory<String, byte[]> cf){
        var factory = new ConcurrentKafkaListenerContainerFactory<String, byte[]>();
        var configProps = new HashMap<String, Object>(cf.getConfigurationProperties());
        configProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 300);
        var customFactory = new DefaultKafkaConsumerFactory<String, byte[]>(configProps);
        factory.setConsumerFactory(customFactory);
        factory.setBatchListener(true);
        factory.setConcurrency(1);
        factory.getContainerProperties().setAckMode(AckMode.BATCH);
        factory.getContainerProperties().setPollTimeout(3000);
        return factory;
    }
}
