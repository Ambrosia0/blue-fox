package com.ambrosia.community_service.community.model.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import com.ambrosia.community_service.community.model.entity.keys.CommunityBanKey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "community_ban")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommunityBan implements Persistable<CommunityBanKey>, Serializable{
    @Id
    private CommunityBanKey id;

    @Builder.Default
    @Transient
    private boolean isNew = true;

    public static CommunityBan create(UUID userId, long communityId, Instant beforeDate){
        return new CommunityBan(new CommunityBanKey(userId, communityId, beforeDate), true);
    }

    public static CommunityBan create(UUID userId, long communityId, Instant beforeDate, boolean isNew){
        return new CommunityBan(new CommunityBanKey(userId, communityId, beforeDate), isNew);
    }
}
