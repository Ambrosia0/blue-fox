package com.ambrosia.profile_service.blacklist.application;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.ambrosia.profile_service.blacklist.api.dto.request.BlacklistRequest;
import com.ambrosia.profile_service.blacklist.api.dto.response.BlacklistResponse;

public interface UserBlacklistService {
    void blacklistUser(UUID requestingUser, UUID blacklistedUser, BlacklistRequest request);
    void removeFromBlacklist(UUID requestingUser, UUID blacklistedUser);
    Slice<BlacklistResponse> getBlacklistedUsers(UUID requestingUser, Pageable pageable);
}
