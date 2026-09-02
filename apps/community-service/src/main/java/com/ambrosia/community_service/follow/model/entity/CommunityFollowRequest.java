package com.ambrosia.community_service.follow.model.entity;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.ambrosia.community_service.follow.model.entity.key.CommunityFollowRequestKey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "community_follow_request")
public class CommunityFollowRequest implements Persistable<CommunityFollowRequestKey>{
    @Id
    private CommunityFollowRequestKey id;

    @ReadOnlyProperty
    @Column("created_at")
    private Instant createdAt;

    @Transient
    @Builder.Default
    private boolean isNew = false;

    public static CommunityFollowRequest create(UUID userId, Long communityId){
        return new CommunityFollowRequest(
            new CommunityFollowRequestKey(
                userId,
                communityId
            ), 
            null, 
            true
        );
    }
}
