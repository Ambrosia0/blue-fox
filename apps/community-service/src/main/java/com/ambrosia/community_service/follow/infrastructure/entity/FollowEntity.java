package com.ambrosia.community_service.follow.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.ambrosia.community_service.follow.infrastructure.entity.key.CommunityFollowKey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Table(name = "community_follow")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FollowEntity implements Persistable<CommunityFollowKey>{
    @Id
    private CommunityFollowKey id;

    @ReadOnlyProperty
    @Column("followed_at")
    private Instant followedAt;
    
    @Transient
    private boolean isNew = true;

    public static FollowEntity create(UUID userId, Long communityId){
        return new FollowEntity(CommunityFollowKey.create(userId, communityId), null, true);
    }
}
