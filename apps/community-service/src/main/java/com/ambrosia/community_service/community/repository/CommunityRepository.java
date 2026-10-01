package com.ambrosia.community_service.community.repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

import com.ambrosia.community_service.community.model.dto.response.CommunityScopeResponse;
import com.ambrosia.community_service.community.model.entity.Community;
import com.ambrosia.community_service.community.repository.custom.CustomCommunityRepository;

public interface CommunityRepository extends 
        CrudRepository<Community, Long>, 
        ListPagingAndSortingRepository<Community, Long>,
        CustomCommunityRepository{

    @Query("SELECT COUNT(*) FROM community WHERE owner_id = :userId")
    long countOwned(UUID userId);

    @Query("SELECT is_private FROM community WHERE id = :communityId")
    Optional<Boolean> findIsCommunityPrivate(long communityId);

    @Modifying
    @Query("DELETE FROM scope_link WHERE community_id = :communityId AND user_id = :userId")
    int cleanScopesForUser(long communityId, UUID userId);

    @Query("SELECT EXISTS(SELECT 1 FROM scope_link WHERE community_id = :communityId AND user_id = :userId)")
    boolean isModerator(long communityId, UUID userId);

    @Query("""
    SELECT EXISTS(
        SELECT 1 FROM scope_link
        WHERE user_id = :userId
        AND community_id = :communityId
        AND scope_id = :scopeId
    )
    """)
    boolean scopeExists(Long communityId, Short scopeId, UUID userId);

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

    boolean existsBySlug(String slug);
}
