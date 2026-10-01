package com.ambrosia.collaboration_service.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

import com.ambrosia.library_core.dto.Topics;

@Profile("dev")
@Configuration 
public class KafkaTopics {
    NewTopic collaborationTopic(){
        return TopicBuilder.name(Topics.COLLABORATION_EVENT)
            .partitions(1)
            .replicas(1)
            .build();
    }
}
