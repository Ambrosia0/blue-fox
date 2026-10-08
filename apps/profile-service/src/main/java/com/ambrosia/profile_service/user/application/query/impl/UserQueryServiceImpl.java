package com.ambrosia.profile_service.user.application.query.impl;

import org.springframework.stereotype.Service;

import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.Actor.Role;
import com.ambrosia.profile_service.user.api.dto.response.CurrentUserProfileResponse;
import com.ambrosia.profile_service.user.api.dto.response.PublicUserProfileResponse;
import com.ambrosia.profile_service.user.application.query.CurrentUserProfileCache;
import com.ambrosia.profile_service.user.application.query.PersonalProfileInformationCache;
import com.ambrosia.profile_service.user.application.query.PublicUserProfileCache;
import com.ambrosia.profile_service.user.application.query.UserQueryService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserQueryServiceImpl implements UserQueryService{
    private final CurrentUserProfileCache currentUserProfileCache;

    private final PersonalProfileInformationCache personalProfileInformationCache;
    
    private final PublicUserProfileCache publicUserProfileCache;

    @Override
    public CurrentUserProfileResponse getProfile(Actor actor) {
        return currentUserProfileCache.getById(actor.id());
    }

    @Override
    public PublicUserProfileResponse getPublicProfile(String username, Actor actor) {
        var resp = publicUserProfileCache.getByUsername(username);
        if(actor.role().equals(Role.ANONYMOUS))
            return resp;
        resp.setUserData(
            personalProfileInformationCache.getById(actor.id(), resp.getId())
        );
        return resp;
    }
}
