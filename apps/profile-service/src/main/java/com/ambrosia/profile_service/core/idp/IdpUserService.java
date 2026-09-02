package com.ambrosia.profile_service.core.idp;

import java.util.UUID;

import com.ambrosia.profile_service.user.model.dto.request.FirstLastName;
import com.ambrosia.profile_service.user.model.entity.User;

import jakarta.annotation.Nullable;

/**
 * Service for managing users in external IdPs (identity providers)
 */
public interface IdpUserService {
    void updateAvatar(UUID userId, @Nullable String avatarId);
    void updateFirstLastName(UUID userId, FirstLastName firstLastName);
    void updateUsername(UUID userId, String username);
    void registerUser(User user);
}
