package com.ambrosia.content_service.community.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ambrosia.community_service.kafka_events.CommunityEvent;
import com.ambrosia.content_service.community.repository.CommunityProjectionRepository;
import com.ambrosia.content_service.community.service.CommunityProjectionEventProcessor;
import com.ambrosia.content_service.community.service.mapper.CommunityMapper;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CommunityProjectionEventProcessorImpl implements CommunityProjectionEventProcessor{

    private final CommunityProjectionRepository communityProjectionRepository;

    private final CommunityMapper communityMapper;


    @Override
    public void process(CommunityEvent communityEvent) {
        var eventId = UUID.fromString(communityEvent.getEventId());
        var communityId = communityEvent.getId();
        switch (communityEvent.getEventCase()) {
            case CREATE ->{
                communityProjectionRepository.insert(
                    communityMapper.toEntity(communityId, communityEvent.getCreate()),
                    eventId
                );
            }
            case UPDATE -> {
                communityProjectionRepository.update(
                    communityMapper.toEntity(communityId, communityEvent.getUpdate()), 
                    eventId
                );
            }
            case DELETE ->{
                communityProjectionRepository.delete(communityId, eventId);
            }
            default -> {
                throw new RuntimeException("Unknown event!");
            }
        }
    }
}
