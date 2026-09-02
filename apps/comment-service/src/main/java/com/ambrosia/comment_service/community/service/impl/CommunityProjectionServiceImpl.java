package com.ambrosia.comment_service.community.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ambrosia.comment_service.community.service.CommunityProjectionService;
import com.ambrosia.community_service.kafka_events.CommunityEvent;
import com.ambrosia.comment_service.community.model.entity.CommunityProjection;
import com.ambrosia.comment_service.community.repository.CommunityProjectionRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommunityProjectionServiceImpl implements CommunityProjectionService{
    private final CommunityProjectionRepository communityProjectionRepository;

    @Override
    public void create(CommunityEvent communityCreate) {
        communityProjectionRepository.insert(CommunityProjection.builder()
            .id(communityCreate.getCreate().getId())
            .isPrivate(communityCreate.getCreate().getIsPrivate())
            .isNew(true)
            .build(),
            UUID.fromString(communityCreate.getEventId())
        );
    }

    @Override
    public void delete(CommunityEvent communityDelete) {
        communityProjectionRepository.delete(
            communityDelete.getDelete().getId(),
            UUID.fromString(communityDelete.getEventId())
        );
    }
}
