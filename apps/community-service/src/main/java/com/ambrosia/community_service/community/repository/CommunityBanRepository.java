package com.ambrosia.community_service.community.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.ambrosia.community_service.community.model.entity.CommunityBan;
import com.ambrosia.community_service.community.model.entity.keys.CommunityBanKey;

public interface CommunityBanRepository extends CrudRepository<CommunityBan, CommunityBanKey>{
    @Query("""
    SELECT EXISTS(
        SELECT 1 FROM community_ban 
        WHERE user_id = :userId 
        AND community_id = :communityId
        AND (
            before_date IS NULL OR
            before_date > CURRENT_TIMESTAMP
        ) 
    )
    """)
    boolean isBanned(UUID userId, long communityId);

    @Query("""
    SELECT COUNT(*) = :size
    FROM community_ban
    WHERE user_id IN ( :ids )
    AND (
        before_date IS NULL OR
        before_date > CURRENT_TIMESTAMP
    ) 
    """)
    boolean isAnyBanned(List<UUID> ids, int size);

    @Modifying
    @Query("""
    DELETE FROM community_ban 
    WHERE user_id = :userId 
    AND community_id = :communityId 
    AND (
        before_date = (
            SELECT before_date FROM community_ban 
            WHERE user_id = :userId 
            AND community_id = :communityId 
            ORDER BY before_date DESC LIMIT 1
        ) 
        OR 
        before_date IS NULL
    )
    """)
    int unban(UUID userId, long communityId);
}