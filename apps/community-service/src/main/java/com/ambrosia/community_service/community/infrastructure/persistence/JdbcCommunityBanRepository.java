package com.ambrosia.community_service.community.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.ambrosia.community_service.community.domain.entity.CommunityBan;
import com.ambrosia.community_service.community.domain.entity.keys.CommunityBanKey;
import com.ambrosia.community_service.community.domain.repository.CommunityBanRepository;

public interface JdbcCommunityBanRepository extends 
        CommunityBanRepository, 
        CrudRepository<CommunityBan, CommunityBanKey> {
    
    @Modifying 
    @Query("""
    DELETE FROM community_ban
    WHERE user_id = :userId
    AND community_id = :communityId
    AND (
        before_date IS NULL OR
        before_date > CURRENT_TIMESTAMP
    )
    """)
    void unban(UUID userId, Long communityId);

    @Query("""
    SELECT * FROM community_ban cb
    JOIN community c ON c.id = cb.community_id
    WHERE cb.user_id = :userId
    AND cb.community_id = c.id
    AND (
        cb.before_date IS NULL OR
        cb.before_date > CURRENT_TIMESTAMP
    )
    """)
    Optional<CommunityBan> findById(UUID userId, Long communityId);
}
