package com.ambrosia.profile_service.user.service;

import java.util.UUID;

import com.ambrosia.profile_service.user.model.dto.response.CurrentUserProfileResponse;
import com.ambrosia.profile_service.user.model.dto.response.PublicUserProfileResponse;

import jakarta.annotation.Nullable;

public interface UserQueryService {
    PublicUserProfileResponse getPublicProfile(String username, @Nullable UUID requestingUser);
    CurrentUserProfileResponse getProfile(UUID id);
}
