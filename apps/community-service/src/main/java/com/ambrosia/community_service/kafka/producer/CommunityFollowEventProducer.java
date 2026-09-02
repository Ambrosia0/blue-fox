package com.ambrosia.community_service.kafka.producer;

import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.ambrosia.community_service.kafka_events.CommunityFollowEvent;
import com.ambrosia.library_core.dto.Topics;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CommunityFollowEventProducer {
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    @EventListener
    public void on(CommunityFollowEvent communityFollowEvent){
        kafkaTemplate.send(
            Topics.COMMUNITY_FOLLOW_EVENT, 
            communityFollowEvent.getRequestingUser()+communityFollowEvent.getCommunityId(),
            communityFollowEvent.toByteArray());
    }
}
