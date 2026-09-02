package com.ambrosia.comment_service.community.repository;

import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.ambrosia.comment_service.community.model.entity.CommunityBanProjection;
import com.ambrosia.comment_service.community.model.entity.key.CommunityBanKey;

public interface CommunityBanRepository extends
        CrudRepository<CommunityBanProjection, CommunityBanKey>,
        CustomCommunityBanRepository{

    @Query("""
    SELECT EXISTS(
        SELECT 1 FROM community_ban_projection
        WHERE community_id = :communityId
        AND user_id = :userId
    )
    """)
    boolean existsByCommunityIdAndUserId(Long communityId, UUID userId);
}
