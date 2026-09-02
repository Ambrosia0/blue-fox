package com.ambrosia.content_service.kafka.producer;

import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.ambrosia.content_service.kafka_events.UserFollowEvent;
import com.ambrosia.library_core.dto.Topics;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class UserFollowEventProducer {
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    @EventListener
    public void on(UserFollowEvent userFollowEvent){
        var id = switch(userFollowEvent.getEventCase()){
            case FOLLOWED -> userFollowEvent.getFollowed().getFollowedUserId();
            case UNFOLLOWED -> userFollowEvent.getUnfollowed().getFollowedUserId();
            default -> throw new RuntimeException("Unrecognized type!");
        };
        kafkaTemplate.send(
            Topics.USER_FOLLOW_EVENT, 
            id,
            userFollowEvent.toByteArray());
    }
}
