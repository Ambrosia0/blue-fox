package com.ambrosia.profile_service.user.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.ambrosia.profile_service.user.api.dto.response.PublicUserProfileResponse;
import com.ambrosia.profile_service.user.domain.entity.User;

public interface UserRepository {
    Optional<User> findByUsernameIgnoreCase(String username);
    boolean existsByUsernameOrEmail(String username, String email);
    boolean existsByUsername(String username);
    Optional<User> findByIdAndIsActiveIsTrue(UUID id);
    void updateAvatar(String avatarId, UUID userId);
    void updateUsername(UUID userId, String username);
    boolean existsByIdAndIsEnabledIsTrue(UUID id);
    Optional<PublicUserProfileResponse> findPublicProfileById(UUID userId);
    Optional<PublicUserProfileResponse> findPublicProfileByUsername(String username);
    Optional<User> findById(UUID id);
    boolean existsById(UUID id);
    User save(User user);
}
