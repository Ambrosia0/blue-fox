package com.ambrosia.community_service.community.domain.repository;

import java.util.UUID;

import com.ambrosia.community_service.community.domain.entity.CommunityBan;

public interface CommunityBanRepository{
    CommunityBan save(CommunityBan communityBan);
    void unban(UUID userId, Long communityId);
}