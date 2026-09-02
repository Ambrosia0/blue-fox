package com.ambrosia.content_service.kafka;

import java.time.Duration;
import java.util.HashMap;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Suppressed;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Suppressed.BufferConfig;
import org.apache.kafka.streams.state.Stores;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import com.ambrosia.content_service.kafka.serde.AggregatedPreviewSerde;
import com.ambrosia.content_service.kafka.serde.AggregatedViewSerde;
import com.ambrosia.content_service.kafka.serde.CommentEventSerde;
import com.ambrosia.content_service.kafka.serde.PostDeltaEventSerde;
import com.ambrosia.content_service.kafka.serde.PostPreviewEventSerde;
import com.ambrosia.content_service.kafka.serde.PostViewEventSerde;
import com.ambrosia.content_service.kafka_events.AggregatedPreviewEvent;
import com.ambrosia.content_service.kafka_events.AggregatedViewEvent;
import com.ambrosia.content_service.kafka_events.PostDelta;
import com.ambrosia.content_service.kafka_events.PostPreviewEvent;
import com.ambrosia.library_core.dto.Topics;

@EnableKafkaStreams
@Configuration
public class KafkaStreams {
    @Bean
    KStream<Long, AggregatedViewEvent> kStreamViewAggregation(StreamsBuilder builder){
        var viewEventSerde = new PostViewEventSerde();
        var viewAggregationSerde = new AggregatedViewSerde();
        var viewAggregation = builder.stream(Topics.VIEW_EVENT, Consumed.with(Serdes.String(), viewEventSerde))
            .groupBy((key, value) -> value.getPostId(), Grouped.with(Serdes.Long(), viewEventSerde))
            .windowedBy(TimeWindows.ofSizeAndGrace(Duration.ofSeconds(10), Duration.ofSeconds(5)))
            .count()
            .suppress(Suppressed.untilWindowCloses(BufferConfig.unbounded()))
            .toStream()
            .map((key, value) -> KeyValue.pair(
                key.key(),
                AggregatedViewEvent.newBuilder()
                    .setPostId(key.key())
                    .setDelta(value.intValue())
                    .build()
            ));
        viewAggregation.to(
            Topics.VIEW_AGGREGATION,
            Produced.with(Serdes.Long(), viewAggregationSerde)
        );
        return viewAggregation;
    }

    @Bean
    KStream<Long, PostDelta> kStreamCommentCountAggregation(StreamsBuilder builder){
        var commentEventSerde = new CommentEventSerde();
        var postDeltaSerde = new PostDeltaEventSerde();
        builder.addStateStore(
            Stores.keyValueStoreBuilder(
                Stores.inMemoryKeyValueStore("comment-count-aggregation-store"),
                Serdes.Long(),
                Serdes.Integer()
            )
        );
        var stream = builder.stream(Topics.COMMENT_EVENT, Consumed.with(Serdes.String(), commentEventSerde))
            .filter((key, value) -> value.hasCreated() || value.hasDeleted())
            .selectKey((key, value) -> switch (value.getEventCase()) {
                case CREATED -> value.getCreated().getPostId();
                case DELETED -> value.getDeleted().getPostId();
                default -> -1L;
            })
            .mapValues(value -> switch (value.getEventCase()) {
                case CREATED -> 1;
                case DELETED -> -1;
                default -> 0;
            })
            .process(
                () -> new BatchAggregationProcessor<Long>("comment-count-aggregation-store"), 
                Named.as("comment-count-aggregation-processor"),
                "comment-count-aggregation-store"
            )
            .mapValues((readOnlyKey, value) -> PostDelta.newBuilder()
                .setPostId(readOnlyKey)
                .setDelta(value)
                .build()
            );

        stream.to(
            Topics.POST_COMMENT_COUNT_AGGREGATE,
            Produced.with(Serdes.Long(), postDeltaSerde)
        );

        return stream;
    }

    @Bean
    KStream<Long, AggregatedPreviewEvent> kStreamPreviewAggregation(StreamsBuilder builder){
        var previewEventSerde = new PostPreviewEventSerde();
        var previewAggregationSerde = new AggregatedPreviewSerde();
        var previewAggregation = builder.stream(Topics.PREVIEW_EVENT, Consumed.with(Serdes.String(), previewEventSerde))
            .flatMap(new KeyValueMapper<String,PostPreviewEvent,Iterable<? extends KeyValue<String, Long>>>() {
                public java.lang.Iterable<? extends KeyValue<String, Long>> apply(String key, PostPreviewEvent value) {
                    return value.getPostIdList()
                        .stream()
                        .map(t -> KeyValue.pair(key, t))
                        .toList();
                }
            })
            .groupBy((key, value) -> value, Grouped.with(Serdes.Long(), Serdes.Long()))
            .windowedBy(TimeWindows.ofSizeAndGrace(Duration.ofSeconds(10), Duration.ofSeconds(5)))
            .count()
            .suppress(Suppressed.untilWindowCloses(BufferConfig.unbounded()))
            .toStream()
            .map((key, value) -> KeyValue.pair(
                key.key(),
                AggregatedPreviewEvent.newBuilder()
                    .setPostId(key.key())
                    .setDelta(value.intValue())
                    .build()
            ));
        previewAggregation.to(
            Topics.PREVIEW_AGGREGATION,
            Produced.with(Serdes.Long(), previewAggregationSerde)
        );
        return previewAggregation;
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<Long, byte[]> kafkaCommentCountAggregationListenerContainerFactory(ConsumerFactory<String, byte[]> cf){
        var factory = new ConcurrentKafkaListenerContainerFactory<Long, byte[]>();
        var configProps = new HashMap<String, Object>(cf.getConfigurationProperties());
        configProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 300);
        var customFactory = new DefaultKafkaConsumerFactory<Long, byte[]>(configProps);
        customFactory.setKeyDeserializer(new LongDeserializer());
        factory.setConsumerFactory(customFactory);
        factory.setBatchListener(true);
        factory.setConcurrency(1);
        factory.getContainerProperties().setAckMode(AckMode.BATCH);
        factory.getContainerProperties().setPollTimeout(5000);
        return factory;
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<Long, byte[]> kafkaViewAggregationListenerContainerFactory(ConsumerFactory<String, byte[]> cf){
        var factory = new ConcurrentKafkaListenerContainerFactory<Long, byte[]>();
        var configProps = new HashMap<String, Object>(cf.getConfigurationProperties());
        configProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 300);
        var customFactory = new DefaultKafkaConsumerFactory<Long, byte[]>(configProps);
        customFactory.setKeyDeserializer(new LongDeserializer());
        factory.setConsumerFactory(customFactory);
        factory.setBatchListener(true);
        factory.setConcurrency(1);
        factory.getContainerProperties().setAckMode(AckMode.BATCH);
        factory.getContainerProperties().setPollTimeout(3000);
        return factory;
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, byte[]> kafkaCommunityFollowListenerContainerFactory(ConsumerFactory<String, byte[]> cf){
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
    DeadLetterPublishingRecoverer recoverer(KafkaTemplate<String, byte[]> template){
        return new DeadLetterPublishingRecoverer(template);
    }

    @Bean
    DefaultErrorHandler errorHandler(DeadLetterPublishingRecoverer recoverer){
        return new DefaultErrorHandler(recoverer, new FixedBackOff(0L, 2L));
    }
    
    @Bean
    KafkaListenerErrorHandler serializationErrorHandler(){
        return (message, exception) ->{
            return exception;
        };
    }

}
