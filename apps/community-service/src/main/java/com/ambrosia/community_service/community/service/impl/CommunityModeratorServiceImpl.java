package com.ambrosia.community_service.community.service.impl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.ambrosia.community_service.community.model.entity.CommunityBan;
import com.ambrosia.community_service.community.repository.CommunityBanRepository;
import com.ambrosia.community_service.community.service.CommunityModeratorService;
import com.ambrosia.community_service.community.service.ScopeLinkService;
import com.ambrosia.community_service.community.utils.ScopeEnum;
import com.ambrosia.community_service.exception.community.NotEnoughPermissionsException;
import com.ambrosia.community_service.exception.community.UserDoesntBannedException;
import com.ambrosia.community_service.exception.community.UserDoesntExistException;
import com.ambrosia.community_service.exception.community.UserIsModeratorException;
import com.ambrosia.community_service.kafka.utils.CommunityBanEventFactory;
import com.ambrosia.community_service.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommunityModeratorServiceImpl implements CommunityModeratorService{
    private final ScopeLinkService scopeLinkService;

    private final CommunityBanRepository communityBanRepository;

    private final UserService userService;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void banUser(long communityId, UUID requestingUser, UUID userToBan, Instant beforeDate) {
        var hasScope = scopeLinkService.hasScope(communityId, ScopeEnum.USER_BAN, requestingUser);
        if(!hasScope)
            throw new NotEnoughPermissionsException();
        if(!userService.isUserExist(userToBan))
            throw new UserDoesntExistException();
        if(scopeLinkService.hasAnyScope(communityId, userToBan))
            throw new UserIsModeratorException();
        communityBanRepository.save(CommunityBan.create(userToBan, communityId, beforeDate));
        eventPublisher.publishEvent(
            CommunityBanEventFactory.createBan(communityId, userToBan)
        );

    }

    @Override
    public void unbanUser(long communityId, UUID requestingUser, UUID userToUnban) {
        var hasScope = scopeLinkService.hasScope(communityId, ScopeEnum.USER_UNBAN, requestingUser);
        if(!hasScope)
            throw new NotEnoughPermissionsException();
        if(communityBanRepository.unban(userToUnban, communityId) == 0)
            throw new UserDoesntBannedException();
        eventPublisher.publishEvent(
            CommunityBanEventFactory.createUnban(communityId, userToUnban)
        );
    }
}
