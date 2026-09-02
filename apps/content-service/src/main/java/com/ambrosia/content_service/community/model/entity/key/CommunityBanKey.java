package com.ambrosia.content_service.community.model.entity.key;

import java.util.UUID;

import org.springframework.data.relational.core.mapping.Column;

public record CommunityBanKey(
    @Column("community_id") Long communityId,
    @Column("user_id") UUID userId
) {}
