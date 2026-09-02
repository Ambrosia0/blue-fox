package com.ambrosia.community_service.community.model.entity.keys;

import java.io.Serializable;
import java.util.UUID;

import org.springframework.data.relational.core.mapping.Column;

public record ScopeLinkKey(
    @Column(value = "user_id") UUID userId,
    @Column(value = "scope_id") Short scopeId,
    @Column(value = "community_id") Long communityId
) implements Serializable {
    public static ScopeLinkKey create(UUID userId, Short scopeId, Long communityId){
        return new ScopeLinkKey(userId, scopeId, communityId);
    }
}
