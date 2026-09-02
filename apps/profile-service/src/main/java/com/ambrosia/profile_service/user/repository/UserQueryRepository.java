package com.ambrosia.profile_service.user.repository;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.ambrosia.profile_service.user.model.dto.admin.UserFilter;
import com.ambrosia.profile_service.user.model.dto.admin.UserResponse;
import com.ambrosia.profile_service.user.model.dto.response.CurrentUserProfileResponse;
import com.ambrosia.profile_service.user.model.dto.response.ProfileUserData;

public interface UserQueryRepository {
    CurrentUserProfileResponse findProfileById(UUID userId);
    Slice<UserResponse> getUsers(UserFilter userFilter, Pageable pageable);
    ProfileUserData findUserData(UUID userId, UUID profileId);
}
