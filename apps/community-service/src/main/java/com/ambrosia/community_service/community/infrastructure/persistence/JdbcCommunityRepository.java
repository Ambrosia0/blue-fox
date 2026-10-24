package com.ambrosia.community_service.community.infrastructure.persistence;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import com.ambrosia.community_service.community.domain.entity.CommunityDelete;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.ambrosia.community_service.community.api.dto.response.CommunityScopeResponse;
import com.ambrosia.community_service.community.domain.entity.Community;
import com.ambrosia.community_service.community.domain.repository.CommunityRepository;

public interface JdbcCommunityRepository extends 
        CrudRepository<Community, Long>,
        CustomCommunityRepository,
        CommunityRepository{

    @Query("SELECT COUNT(DISTINCT(user_id)) FROM scope_link WHERE community_id = :communityId AND user_id IN (:userIds)")
    long countPermittedUsers(long communityId, Collection<UUID> userIds);

    @Query("""
    SELECT user_id, array_agg(scope_id)
    FROM scope_link
    WHERE community_id = :communityId
    AND user_id = :userId
    GROUP BY user_id
    """)
    Optional<CommunityScopeResponse> findUserScopes(long communityId, UUID userId);
    
    @Query("""
    DELETE FROM community
        WHERE id = :id
    RETURNING id, avatar_id, version
    """)
    Optional<CommunityDelete> delete(Long id);
    
    boolean existsBySlug(String slug);
}
