package com.ambrosia.content_service.follow.repository.custom;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.ambrosia.content_service.follow.model.dto.UserFollowResponse;
import com.ambrosia.content_service.follow.model.entity.UserFollow;
import com.ambrosia.content_service.follow.model.entity.keys.UserFollowKey;

public interface CustomUserFollowRepository {
    Optional<UserFollow> optionalSave(UserFollow userFollow);
    Slice<UserFollowResponse> findByUserId(UUID userId, Pageable pageable);
    int returningDelete(UserFollowKey userFollowKey);
}
