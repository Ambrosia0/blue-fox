package com.ambrosia.community_service.follow.repository;

import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ambrosia.community_service.follow.model.entity.CommunityFollowRequest;
import com.ambrosia.community_service.follow.model.entity.key.CommunityFollowRequestKey;
import com.ambrosia.community_service.follow.repository.custom.CustomCommunityFollowRequestRepository;

public interface CommunityFollowRequestRepository extends 
    CrudRepository<CommunityFollowRequest, CommunityFollowRequestKey>,
    PagingAndSortingRepository<CommunityFollowRequest, CommunityFollowRequestKey>,
    CustomCommunityFollowRequestRepository{
    
    @Query("""
    WITH inserted AS (
        INSERT INTO community_follow_request(user_id, community_id) 
        VALUES (:userId, :communityId)
        ON CONFLICT DO NOTHING
        RETURNING 1
    )
    SELECT EXISTS(SELECT 1 FROM inserted)
    """)
    boolean save(UUID userId, Long communityId);

    @Query("SELECT EXISTS(SELECT 1 FROM community_follow_request WHERE community_id = :communityId AND user_id = :userId)")
    boolean existsByCommunityIdAndUserId(UUID userId, Long communityId);

    @Modifying
    @Query("DELETE FROM community_follow_request WHERE community_id = :communityId AND user_id = :userId")
    int returningDelete(UUID userId, Long communityId);

}
