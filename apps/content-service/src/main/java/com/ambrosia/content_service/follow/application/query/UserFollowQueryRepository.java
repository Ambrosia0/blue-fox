package com.ambrosia.content_service.follow.application.query;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.ambrosia.content_service.follow.api.dto.UserFollowResponse;

public interface UserFollowQueryRepository {
    Slice<UserFollowResponse> findByUserId(UUID userId, Pageable pageable);
}
