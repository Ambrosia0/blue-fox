package com.ambrosia.community_service.community.repository.elastic.custom;

public interface CustomElasticCommunityRepository {
    void updateAvatar(Long communityId, String avatarId);
}
