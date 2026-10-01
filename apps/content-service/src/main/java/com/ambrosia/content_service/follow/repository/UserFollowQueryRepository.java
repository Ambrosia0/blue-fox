package com.ambrosia.content_service.follow.repository;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.ambrosia.content_service.follow.model.dto.UserFollowResponse;

public interface  UserFollowQueryRepository {
    Slice<UserFollowResponse> findByUserId(UUID userId, Pageable pageable);
}
