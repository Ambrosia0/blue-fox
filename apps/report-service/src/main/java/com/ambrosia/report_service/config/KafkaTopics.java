package com.ambrosia.report_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

import com.ambrosia.library_core.dto.Topics;

/**
 * Auto-creation of topics for testing
 */
@Profile({"dev", "test"})
@Configuration
public class KafkaTopics {
    @Bean
    NewTopic userEvent(){
       return TopicBuilder.name(Topics.USER_EVENT)
           .partitions(2)
           .replicas(1)
           .build();
    }
    @Bean
    NewTopic commentEvent(){
       return TopicBuilder.name(Topics.POST_EVENT)
           .partitions(2)
           .replicas(1)
           .build();
    }
    @Bean
    NewTopic postEvent(){
       return TopicBuilder.name(Topics.COMMENT_EVENT)
           .partitions(2)
           .replicas(1)
           .build();
    }
    @Bean
    NewTopic communityEvent(){
       return TopicBuilder.name(Topics.COMMUNITY)
           .partitions(2)
           .replicas(1)
           .build();
    }
}
