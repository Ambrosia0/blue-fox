package com.ambrosia.content_service.follow.model.entity;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import com.ambrosia.content_service.follow.model.entity.keys.CommunityFollowKey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "community_follow_projection")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommunityFollowProjection implements Persistable<CommunityFollowKey>{
    @Id
    private CommunityFollowKey id;
    
    @Transient
    private boolean isNew = true;

    public static CommunityFollowProjection create(UUID userId, Long communityId){
        return new CommunityFollowProjection(CommunityFollowKey.create(userId, communityId),true);
    }
}
