package com.ambrosia.community_service.infrastructure.kafka.producer;

import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.ambrosia.community_service.kafka_events.CommunityBanEvent;
import com.ambrosia.library_core.dto.Topics;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class CommunityBanEventProducer {
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(CommunityBanEvent event){
        var id = switch(event.getEventCase()){
            case BAN -> event.getBan().getCommunityId()+"_"+event.getBan().getUserId();
            case UNBAN -> event.getUnban().getCommunityId()+"_"+event.getUnban().getUserId();
            default -> throw new RuntimeException("Unknown body content!");
        };
        kafkaTemplate.send(
            Topics.COMMUNITY_BAN_EVENT,
            id,
            event.toByteArray()
        );
    }
}
