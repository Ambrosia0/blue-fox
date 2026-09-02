package com.ambrosia.community_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

import com.ambrosia.library_core.dto.Topics;

@Profile({"dev", "test"})
@Configuration
public class KafkaTopics {
    @Bean
    NewTopic localCommunityFollowAggregation(){
        return TopicBuilder.name(Topics.COMMUNITY_FOLLOW_AGGREGATION)
            .partitions(2)
            .replicas(1)
            .build();
    }

    @Bean
    NewTopic communityFollow(){
        return TopicBuilder.name(Topics.COMMUNITY_FOLLOW_EVENT)
            .partitions(2)
            .replicas(1)
            .build();
    }

    @Bean
    NewTopic postEvent(){
        return TopicBuilder.name(Topics.POST_EVENT)
            .partitions(2)
            .replicas(1)
            .build();
    }

    @Bean
    NewTopic community(){
        return TopicBuilder.name(Topics.COMMUNITY)
            .partitions(2)
            .replicas(1)
            .build();
    }

    @Bean
    NewTopic postCountEvent(){
        return TopicBuilder.name(Topics.COMMUNITY_POST_COUNT_AGGREGATION)
            .partitions(2)
            .replicas(1)
            .build();
    }
}
