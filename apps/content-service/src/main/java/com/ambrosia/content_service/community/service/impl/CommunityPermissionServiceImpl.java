package com.ambrosia.content_service.community.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ambrosia.content_service.community.repository.CommunityQueryRepository;
import com.ambrosia.content_service.community.service.CommunityPermissionService;
import com.ambrosia.content_service.exception.api.CommunityDoesntExistException;
import com.ambrosia.content_service.exception.api.DoesntFollowedException;
import com.ambrosia.content_service.exception.api.UserBannedException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommunityPermissionServiceImpl implements CommunityPermissionService{
    private final CommunityQueryRepository communityQueryRepository;

    @Override
    public void validatePostCreate(UUID userId, long communityId) {
        var userCommunityDataOpt = communityQueryRepository
            .findCommunityUserDataByCommunityId(communityId, userId);
        if(userCommunityDataOpt.isEmpty())
            throw new CommunityDoesntExistException();
        var userCommunityData = userCommunityDataOpt.get();
        if(userCommunityData.isBanned())
            throw new UserBannedException();
        if(!userCommunityData.isFollowed())
            throw new DoesntFollowedException();
    }
}
