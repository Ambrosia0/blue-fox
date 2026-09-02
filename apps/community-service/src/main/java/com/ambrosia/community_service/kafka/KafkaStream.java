package com.ambrosia.community_service.kafka;

import java.util.HashMap;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;
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

import com.ambrosia.community_service.kafka.serde.CommunityFollowAggregationSerde;
import com.ambrosia.community_service.kafka.serde.CommunityFollowEventSerde;
import com.ambrosia.community_service.kafka.serde.PostCountAggregationSerde;
import com.ambrosia.community_service.kafka.serde.PostEventSerde;
import com.ambrosia.community_service.kafka_events.CommunityFollowAggregation;
import com.ambrosia.community_service.kafka_events.CommunityFollowEvent;
import com.ambrosia.community_service.kafka_events.PostCountAggregation;
import com.ambrosia.library_core.dto.Topics;

@Configuration
@EnableKafkaStreams
public class KafkaStream {
    private CommunityFollowEventSerde communityFollowEventSerde = new CommunityFollowEventSerde(); 

    @Bean
    KStream<String, CommunityFollowEvent> communityFollowStream(StreamsBuilder builder){
        builder.addStateStore(
            Stores.keyValueStoreBuilder(
                Stores.inMemoryKeyValueStore("community-follow-store"),
                Serdes.Long(),
                Serdes.Integer()
            )
        );
        return builder.stream(Topics.COMMUNITY_FOLLOW_EVENT, Consumed.with(Serdes.String(), communityFollowEventSerde));
    }

    @Bean
    KStream<Long, CommunityFollowAggregation> kStreamCommunityFollowAggregation(KStream<String, CommunityFollowEvent> communityFollowStream){
        var communityFollowAggregationSerde = new CommunityFollowAggregationSerde();
        
        var communityStream = communityFollowStream
            .selectKey((key, value) -> value.getCommunityId())
            .mapValues(value -> value.getFollowed()? 1: -1)
            .process(
                () -> new BatchAggregationProcessor<Long>("community-follow-store"), 
                Named.as("community-follow-aggregation-processor"),
                "community-follow-store"
            )
            .mapValues((readOnlyKey, value) -> CommunityFollowAggregation.newBuilder()
                .setCommunityId(readOnlyKey)
                .setDelta(value)
                .build()
            );
        communityStream.to(
            Topics.COMMUNITY_FOLLOW_AGGREGATION,
            Produced.with(
                Serdes.Long(), 
                communityFollowAggregationSerde
            )
        );
        return communityStream;
    }

    @Bean
    KStream<Long, PostCountAggregation> kStreamPostCount(StreamsBuilder builder){
        var postEventSerde = new PostEventSerde();
        var postCountSerde = new PostCountAggregationSerde();
        builder.addStateStore(
            Stores.keyValueStoreBuilder(
                Stores.inMemoryKeyValueStore("post-count-store"), 
                Serdes.Long(), 
                Serdes.Integer()
            )
        );

        var postCountStream = builder.stream(Topics.POST_EVENT, Consumed.with(Serdes.String(), postEventSerde))
            .filter((key, value) -> {
                if((value.hasCreated() && value.getCreated().hasCommunityId())
                    || (value.hasDeleted() && value.getDeleted().hasCommunityId())){
                    return true;     
                }
                return false;
            })
            .selectKey((key, value) -> switch (value.getEventCase()) {
                    case CREATED -> value.getCreated().getCommunityId();
                    case DELETED -> value.getDeleted().getCommunityId();
                    default -> null;
            })
            .mapValues(value -> switch (value.getEventCase()) {
                    case CREATED -> 1;
                    case DELETED -> -1;
                    default -> 0;
                }
            )
            .process(
                () -> new BatchAggregationProcessor<Long>("post-count-store"),
                Named.as("post-count-processor"),
                "post-count-store"
            )
            .filter((key, value) -> value != 0)
            .mapValues((readOnlyKey, value) -> PostCountAggregation.newBuilder()
                    .setCommunityId(readOnlyKey)
                    .setDelta(value)
                    .build()
            );

        postCountStream.to(
            Topics.COMMUNITY_POST_COUNT_AGGREGATION,
            Produced.with(Serdes.Long(), postCountSerde)
        );

        return postCountStream;
    }
    
    /**
     * Container factory for batch requests on listeners
     */
    @Bean
    ConcurrentKafkaListenerContainerFactory<Long, byte[]> kafkaCommunityFollowListenerContainerFactory(ConsumerFactory<String, byte[]> cf){
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
    ConcurrentKafkaListenerContainerFactory<Long, byte[]> kafkaCommunityPostCountListenerContainerFactory(ConsumerFactory<String, byte[]> cf){
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
