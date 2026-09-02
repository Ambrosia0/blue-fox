package com.ambrosia.community_service.follow.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ambrosia.community_service.exception.follow.AlreadyRequestedFollowException;
import com.ambrosia.community_service.follow.repository.CommunityFollowRequestRepository;
import com.ambrosia.community_service.follow.service.CommunityFollowRequestService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommunityFollowRequestServiceImpl implements CommunityFollowRequestService{
    private final CommunityFollowRequestRepository communityFollowRequestRepository;
    
    @Override
    public void createFollowRequest(UUID userId, Long communityId) {
        var res = communityFollowRequestRepository.save(userId, communityId);
        if(!res)
            throw new AlreadyRequestedFollowException();
    }
}
