package com.ambrosia.community_service.community.repository;

import java.util.Collection;
import java.util.UUID;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.ambrosia.community_service.community.model.entity.ScopeLink;
import com.ambrosia.community_service.community.model.entity.keys.ScopeLinkKey;
import com.ambrosia.community_service.community.repository.custom.CustomScopeLinkRepository;

@CacheConfig(cacheNames = "scopes")
public interface ScopeLinkRepository extends 
        CrudRepository<ScopeLink, ScopeLinkKey>,
        CustomScopeLinkRepository{
    @Query("SELECT COUNT(DISTINCT(user_id)) FROM scope_link WHERE community_id = :communityId AND user_id IN (:userIds)")
    long countPermittedUsers(long communityId, Collection<UUID> userIds);

    @Modifying
    @Query("DELETE FROM scope_link WHERE community_id = :communityId AND user_id NOT IN (:excludedIds)")
    void cleanScopes(long communityId, Collection<UUID> excludedIds);

    @Modifying
    @Query("DELETE FROM scope_link WHERE community_id = :communityId")
    void cleanScopes(long communityId);

    @Modifying
    @Query("DELETE FROM scope_link WHERE community_id = :communityId AND user_id = :userId")
    int cleanScopesForUser(long communityId, UUID userId);

    @Query("SELECT EXISTS(SELECT 1 FROM scope_link WHERE community_id = :communityId AND user_id = :userId)")
    boolean isModerator(long communityId, UUID userId);

    @Override
    @Cacheable(key = "#id")
    boolean existsById(ScopeLinkKey id);

}
