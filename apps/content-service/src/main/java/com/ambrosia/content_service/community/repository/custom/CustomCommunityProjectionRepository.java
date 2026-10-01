package com.ambrosia.content_service.community.repository.custom;

import java.util.UUID;

import com.ambrosia.content_service.community.model.entity.CommunityProjection;

public interface CustomCommunityProjectionRepository {
    void insert(CommunityProjection communityProjection, UUID eventId);
    void update(CommunityProjection communityProjection, UUID eventId);
}
