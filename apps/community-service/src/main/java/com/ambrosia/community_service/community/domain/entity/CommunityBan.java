package com.ambrosia.community_service.community.domain.entity;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import com.ambrosia.community_service.community.domain.entity.keys.CommunityBanKey;

@Table(name = "community_ban")
@Data 
@Builder
@AllArgsConstructor(
    onConstructor_ = @PersistenceCreator,
    access = AccessLevel.PACKAGE
)
public class CommunityBan implements Persistable<CommunityBanKey>, Serializable{
    @Id
    private CommunityBanKey id;

    @Builder.Default
    @Transient
    private boolean isNew = true;

    public static CommunityBan create(UUID userId, long communityId, Instant beforeDate){
        if(ChronoUnit.MINUTES.between(Instant.now(), beforeDate) < 1)
            throw new IllegalArgumentException("Ban must be at least 1 minute long");
        return new CommunityBan(
            new CommunityBanKey(userId, communityId, beforeDate),
            true
        );
    }
}
