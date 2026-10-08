package com.ambrosia.content_service.community.repository.custom;

import java.util.Optional;

import com.ambrosia.content_service.follow.infrastructure.entity.UserFollow;
import com.ambrosia.content_service.follow.infrastructure.entity.keys.UserFollowKey;

public interface CustomUserFollowRepository {
    Optional<UserFollow> optionalSave(UserFollow userFollow);
    int returningDelete(UserFollowKey userFollowKey);
}
