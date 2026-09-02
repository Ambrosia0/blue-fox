package com.ambrosia.content_service.community.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ambrosia.community_service.kafka_events.CommunityEvent;
import com.ambrosia.content_service.community.model.entity.CommunityProjection;
import com.ambrosia.content_service.community.repository.CommunityProjectionRepository;
import com.ambrosia.content_service.community.service.CommunityProjectionService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CommunityProjectionServiceImpl implements CommunityProjectionService{

    private final CommunityProjectionRepository communityProjectionRepository;

    @Override
    public void create(CommunityEvent communityCreate) {
        var builder = CommunityProjection.builder()
            .id(communityCreate.getCreate().getId())
            .name(communityCreate.getCreate().getName())
            .isNew(true);
        if(communityCreate.getCreate().hasAvatarId())
            builder.avatarId(communityCreate.getCreate().getAvatarId());
        communityProjectionRepository.insert(
            builder.build(),
            UUID.fromString(communityCreate.getEventId())
        );
    }

    @Override
    public void update(CommunityEvent communityUpdate) {
        var builder = CommunityProjection.builder()
            .id(communityUpdate.getUpdate().getId())
            .name(communityUpdate.getUpdate().getName())
            .isNew(false);
        if(communityUpdate.getUpdate().hasAvatarId())
            builder.avatarId(communityUpdate.getUpdate().getAvatarId());
        communityProjectionRepository.update(
            builder.build(),
            UUID.fromString(communityUpdate.getEventId())
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
