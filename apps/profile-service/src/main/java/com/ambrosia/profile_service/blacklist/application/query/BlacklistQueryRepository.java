package com.ambrosia.profile_service.blacklist.application.query;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.ambrosia.profile_service.blacklist.api.dto.response.BlacklistResponse;

public interface BlacklistQueryRepository {
    Slice<BlacklistResponse> getBlacklistedUsers(UUID userId, Pageable pageable);
    int getBlacklistCount(UUID userId);
}
