package com.ambrosia.content_service.community.repository;

import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;

import com.ambrosia.content_service.community.model.entity.CommunityBanProjection;
import com.ambrosia.content_service.community.model.entity.key.CommunityBanKey;
import com.ambrosia.content_service.community.repository.custom.CustomCommunityBanProjectionRepository;

public interface CommunityBanProjectionRepository extends
    Repository<CommunityBanProjection, CommunityBanKey>,
    CustomCommunityBanProjectionRepository{
    
    @Query("""
    SELECT EXISTS(
        SELECT 1 FROM community_ban_projection
        WHERE community_id = :communityId
        AND user_id = :userId
    )     
    """)
    boolean existsByCommunityIdAndUserId(Long communityId, UUID userId);
}
