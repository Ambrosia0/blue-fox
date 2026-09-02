package com.ambrosia.community_service.follow.model.entity;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.ambrosia.community_service.follow.model.entity.key.CommunityFollowKey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Table(name = "community_follow")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommunityFollow implements Persistable<CommunityFollowKey>{
    @Id
    private CommunityFollowKey id;

    @ReadOnlyProperty
    @Column("followed_at")
    private Instant followedAt;
    
    @Transient
    private boolean isNew = true;

    public static CommunityFollow create(UUID userId, Long communityId){
        return new CommunityFollow(CommunityFollowKey.create(userId, communityId), null, true);
    }
}
