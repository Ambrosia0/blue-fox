package com.ambrosia.collaboration_service.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

@Profile("dev")
@Configuration 
public class KafkaTopics {
    NewTopic collaborationTopic(){
        return TopicBuilder.name("blog.post.collaboration")
            .partitions(1)
            .replicas(1)
            .build();
    }
}
