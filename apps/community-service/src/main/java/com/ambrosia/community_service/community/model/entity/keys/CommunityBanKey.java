package com.ambrosia.community_service.community.model.entity.keys;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import org.springframework.data.relational.core.mapping.Column;

public record CommunityBanKey(
    @Column(value = "user_id")
    UUID userId,

    @Column(value = "community_id")
    Long communityId,

    @Column(value = "before_date")
    Instant beforeDate
) implements Serializable{
}
