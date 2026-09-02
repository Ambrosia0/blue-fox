package com.ambrosia.profile_service.config;

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
    NewTopic keycloakTopic(){
       return TopicBuilder.name(Topics.KEYCLOAK_EVENT)
           .partitions(2)
           .replicas(1)
           .build();
    }

    @Bean
    NewTopic keycloakAdminTopic(){
       return TopicBuilder.name(Topics.KEYCLOAK_EVENT_ADMIN)
           .partitions(2)
           .replicas(1)
           .build();
    }

    @Bean
    NewTopic postTopic(){
       return TopicBuilder.name(Topics.POST_EVENT)
            .partitions(2)
            .replicas(1)
            .build();
    }

    @Bean
    NewTopic commentTopic(){
       return TopicBuilder.name(Topics.COMMENT_EVENT)
            .partitions(2)
            .replicas(1)
            .build();
    }

    @Bean
    NewTopic aggr(){
       return TopicBuilder.name(Topics.NOTIFICATION_AGGR)
            .partitions(2)
            .replicas(1)
            .build();
    }

    @Bean
    NewTopic userFollow(){
       return TopicBuilder.name(Topics.USER_FOLLOW_EVENT)
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
    NewTopic localUserAggregation(){
        return TopicBuilder.name(Topics.USER_AGGREGATION)
             .partitions(2)
             .replicas(1)
             .build();
     }

    @Bean
    NewTopic localUserFollowAggregation(){
        return TopicBuilder.name(Topics.USER_FOLLOW_AGGREGATION)
            .partitions(2)
            .replicas(1)
            .build();
    }

    @Bean
    NewTopic userStatus(){
        return TopicBuilder.name(Topics.USER_STATUS)
			.partitions(2)
			.replicas(1)
			.build();
    }

    @Bean
    NewTopic userActivity(){
        return TopicBuilder.name(Topics.USER_ACTIVITY)
			.partitions(2)
			.replicas(1)
			.build();
    }

    @Bean
    NewTopic userEvent(){
        return TopicBuilder.name(Topics.USER_EVENT)
			.partitions(2)
			.replicas(1)
			.build();
    }
}
