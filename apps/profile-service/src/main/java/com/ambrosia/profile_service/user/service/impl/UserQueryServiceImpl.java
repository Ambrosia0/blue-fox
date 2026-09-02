package com.ambrosia.profile_service.user.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ambrosia.profile_service.user.model.dto.response.CurrentUserProfileResponse;
import com.ambrosia.profile_service.user.model.dto.response.PublicUserProfileResponse;
import com.ambrosia.profile_service.user.service.UserQueryService;
import com.ambrosia.profile_service.user.service.cache.CurrentUserProfileCache;
import com.ambrosia.profile_service.user.service.cache.PersonalProfileInformationCache;
import com.ambrosia.profile_service.user.service.cache.PublicUserProfileCache;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserQueryServiceImpl implements UserQueryService{
    private final CurrentUserProfileCache currentUserProfileCache;

    private final PersonalProfileInformationCache personalProfileInformationCache;
    
    private final PublicUserProfileCache publicUserProfileCache;

    @Override
    public CurrentUserProfileResponse getProfile(UUID id) {
        return currentUserProfileCache.getById(id);
    }

    @Override
    public PublicUserProfileResponse getPublicProfile(String username, @Nullable UUID requestingUser) {
        var resp = publicUserProfileCache.getByUsername(username);
        if(requestingUser == null)
            return resp;
        resp.setUserData(
            personalProfileInformationCache.getById(requestingUser, resp.getId())
        );
        return resp;
    }
}
