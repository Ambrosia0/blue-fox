package com.ambrosia.comment_service.community.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ambrosia.comment_service.community.service.CommunityProjectionService;
import com.ambrosia.comment_service.community.service.mapper.CommunityProjectionMapper;
import com.ambrosia.community_service.kafka_events.CommunityEvent;
import com.ambrosia.comment_service.community.repository.CommunityProjectionRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommunityProjectionServiceImpl implements CommunityProjectionService{
    private final CommunityProjectionRepository communityProjectionRepository;

    private final CommunityProjectionMapper communityProjectionMapper;

    @Override
    public void process(CommunityEvent communityEvent) {
        var eventId = UUID.fromString(communityEvent.getEventId());
        var communityId = communityEvent.getId();
        switch (communityEvent.getEventCase()) {
            case CREATE -> {
                communityProjectionRepository.insert(
                    communityProjectionMapper.toEntity(communityId, communityEvent.getCreate()), 
                    eventId
                );
            }
            case DELETE -> {
                communityProjectionRepository.delete(communityId, eventId);
            }
            case UPDATE -> {
                communityProjectionRepository.update(
                    communityProjectionMapper.toEntity(communityId, communityEvent.getUpdate()), 
                    eventId
                );
            }
            default -> {}
        }
    }
}
