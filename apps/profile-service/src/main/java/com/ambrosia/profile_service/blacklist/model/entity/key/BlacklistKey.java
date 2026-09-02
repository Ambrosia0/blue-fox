package com.ambrosia.profile_service.blacklist.model.entity.key;

import java.util.UUID;

import org.springframework.data.relational.core.mapping.Column;

public record BlacklistKey(
    @Column("user_id") UUID userId,
    @Column("blacklisted_user_id") UUID blacklistedUserId
) {
    public static BlacklistKey from(UUID userId, UUID blacklistedUserId){
        return new BlacklistKey(userId, blacklistedUserId);
    }
}
