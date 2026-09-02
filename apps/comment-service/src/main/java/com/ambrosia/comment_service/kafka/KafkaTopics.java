package com.ambrosia.comment_service.kafka;

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
    public NewTopic commentCreate(){
        return TopicBuilder.name(Topics.COMMENT_EVENT)
            .partitions(2)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic commentLikeUpdate(){
        return TopicBuilder.name(Topics.COMMENT_LIKE_UPDATE)
            .partitions(2)
            .replicas(1)
            .build();
    }

    @Bean
    NewTopic aggregationTopic(){
        return TopicBuilder.name(Topics.POST_COMMENT_COUNT_AGGREGATE)
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
    NewTopic communityFollow(){
        return TopicBuilder.name(Topics.COMMUNITY_FOLLOW_EVENT)
            .partitions(2)
            .replicas(1)
            .build();
    }
}
