package com.ambrosia.content_service.follow.repository.custom;

import java.util.Optional;

import com.ambrosia.content_service.follow.model.entity.UserFollow;
import com.ambrosia.content_service.follow.model.entity.keys.UserFollowKey;

public interface CustomUserFollowRepository {
    Optional<UserFollow> optionalSave(UserFollow userFollow);
    int returningDelete(UserFollowKey userFollowKey);
}
