package com.ambrosia.profile_service.user.repository.elastic.custom;

import java.util.UUID;

public interface CustomElasticUserRepository {
    void updateAvatarId(UUID id, String avatarId);
}
