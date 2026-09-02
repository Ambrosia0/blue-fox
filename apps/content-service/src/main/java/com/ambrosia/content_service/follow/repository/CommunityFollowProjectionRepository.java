package com.ambrosia.content_service.follow.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.ambrosia.content_service.follow.model.entity.CommunityFollowProjection;
import com.ambrosia.content_service.follow.model.entity.keys.CommunityFollowKey;
import com.ambrosia.content_service.follow.repository.custom.CustomCommunityFollowProjectionRepository;

public interface CommunityFollowProjectionRepository extends 
    CrudRepository<CommunityFollowProjection, CommunityFollowKey>,
    CustomCommunityFollowProjectionRepository{
        
    @Query("SELECT community_id FROM community_follow_projection WHERE user_id = :userId")
    List<Long> findFollowedByUserId(UUID userId);

    @Query("""
    SELECT EXISTS(
        SELECT 1 FROM community_projection c
        LEFT JOIN community_follow_projection cf ON cf.community_id = c.id AND cf.user_id = :userId
        WHERE c.id = :communityId
        AND (
            c.is_private = false
            OR cf.user_id IS NOT NULL
        )
    )
    """)
    boolean followExistsOnPrivateOrDoesntPrivate(Long communityId, UUID userId);
}
