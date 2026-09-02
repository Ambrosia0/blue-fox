package com.ambrosia.community_service.follow.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.community_service.community.service.ScopeLinkService;
import com.ambrosia.community_service.community.utils.ScopeEnum;
import com.ambrosia.community_service.exception.community.NotEnoughPermissionsException;
import com.ambrosia.community_service.exception.follow.FollowRequestDoesntExist;
import com.ambrosia.community_service.follow.model.entity.CommunityFollow;
import com.ambrosia.community_service.follow.model.entity.CommunityFollowRequest;
import com.ambrosia.community_service.follow.repository.CommunityFollowRepository;
import com.ambrosia.community_service.follow.repository.CommunityFollowRequestRepository;
import com.ambrosia.community_service.follow.service.CommunityModeratorFollowService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommunityModeratorFollowServiceImpl implements CommunityModeratorFollowService{
    private final CommunityFollowRequestRepository communityFollowRequestRepository;

    private final CommunityFollowRepository communityFollowRepository;

    private final ScopeLinkService scopeLinkService;

    @Transactional
    @Override
    public void approveRequest(Long communityId, UUID approvedUser, UUID requestingUser) {
        if(!scopeLinkService.hasScope(communityId, ScopeEnum.FOLLOW_MANAGE, requestingUser))
            throw new NotEnoughPermissionsException();
        var res = communityFollowRequestRepository.returningDelete(approvedUser, communityId);
        if(res == 0)
            throw new FollowRequestDoesntExist();
        communityFollowRepository.save(CommunityFollow.create(approvedUser, communityId));
    }

    @Override
    public void declineRequest(Long communityId, UUID declinedUser, UUID requestingUser) {
        if(!scopeLinkService.hasScope(communityId, ScopeEnum.FOLLOW_MANAGE, requestingUser))
            throw new NotEnoughPermissionsException();
        var res = communityFollowRequestRepository.returningDelete(declinedUser, communityId);
        if(res == 0)
            throw new FollowRequestDoesntExist();
    }

    @Override
    public List<CommunityFollowRequest> getRequests(Long communityId, UUID requestingUser, int page) {
        if(!scopeLinkService.hasScope(communityId, ScopeEnum.FOLLOW_MANAGE, requestingUser))
            throw new NotEnoughPermissionsException();
        return communityFollowRequestRepository.findRequests(communityId, Pageable.ofSize(10).withPage(page));
    }
}
