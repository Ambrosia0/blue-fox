package com.ambrosia.profile_service.user.application.query;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.ambrosia.profile_service.user.api.dto.UserFilter;
import com.ambrosia.profile_service.user.api.dto.UserResponse;
import com.ambrosia.profile_service.user.api.dto.response.CurrentUserProfileResponse;
import com.ambrosia.profile_service.user.api.dto.response.ProfileUserData;

public interface UserQueryRepository {
    CurrentUserProfileResponse findProfileById(UUID userId);
    Slice<UserResponse> getUsers(UserFilter userFilter, Pageable pageable);
    ProfileUserData findUserData(UUID userId, UUID profileId);
}
