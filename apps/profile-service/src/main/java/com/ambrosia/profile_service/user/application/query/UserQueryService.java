package com.ambrosia.profile_service.user.application.query;

import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.profile_service.user.api.dto.response.CurrentUserProfileResponse;
import com.ambrosia.profile_service.user.api.dto.response.PublicUserProfileResponse;

public interface UserQueryService {
    PublicUserProfileResponse getPublicProfile(String username, Actor actor);
    CurrentUserProfileResponse getProfile(Actor actor);
}
