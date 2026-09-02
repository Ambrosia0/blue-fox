package com.ambrosia.content_service.config;

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
    NewTopic postPublished(){
        return TopicBuilder.name(Topics.POST_EVENT)
            .partitions(2)
            .replicas(1)
            .build();
    }

    @Bean
    NewTopic postView(){
        return TopicBuilder.name(Topics.VIEW_EVENT)
            .partitions(2)
            .replicas(1)
            .build();
    }

    @Bean
    NewTopic previewEvent(){
        return TopicBuilder.name(Topics.PREVIEW_EVENT)
            .partitions(2)
            .replicas(1)
            .build();
    }

    @Bean
    NewTopic viewAggregation(){
        return TopicBuilder.name(Topics.VIEW_AGGREGATION)
            .partitions(2)
            .replicas(1)
            .build();
    }

    @Bean
    NewTopic previewAggregaion(){
        return TopicBuilder.name(Topics.PREVIEW_AGGREGATION)
            .partitions(2)
            .replicas(1)
            .build();
    }

    @Bean
    NewTopic communityBan(){
        return TopicBuilder.name(Topics.COMMUNITY_BAN_EVENT)
            .partitions(2)
            .replicas(1)
            .build();
    }

    @Bean
    NewTopic comment(){
        return TopicBuilder.name(Topics.COMMENT_EVENT)
            .partitions(2)
            .replicas(1)
            .build();
    }
}
