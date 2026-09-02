package com.ambrosia.content_service.follow.service.impl;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.ambrosia.content_service.exception.api.AlreadyFollowedException;
import com.ambrosia.content_service.exception.api.DoesntFollowedException;
import com.ambrosia.content_service.exception.api.UserDoesntExistException;
import com.ambrosia.content_service.follow.model.dto.UserFollowResponse;
import com.ambrosia.content_service.follow.model.entity.UserFollow;
import com.ambrosia.content_service.follow.model.entity.keys.UserFollowKey;
import com.ambrosia.content_service.follow.repository.UserFollowRepository;
import com.ambrosia.content_service.follow.service.UserFollowService;
import com.ambrosia.content_service.grpc.ProfileService;
import com.ambrosia.content_service.kafka.utils.UserFollowEventFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserFollowServiceImpl implements UserFollowService{
    private final UserFollowRepository userFollowRepository;

    private final ApplicationEventPublisher applicationEventPublisher;
    
    private final ProfileService profileService;

    @Override
    public void followUser(UUID requestingUser, UUID followedUser) {
        if(!profileService.isUserExist(followedUser))
            throw new UserDoesntExistException();
        userFollowRepository.optionalSave(UserFollow.create(requestingUser, followedUser))
            .orElseThrow(() -> new AlreadyFollowedException());
        applicationEventPublisher.publishEvent(
            UserFollowEventFactory.createFollow(requestingUser, followedUser));
    }
    
    @Override
    public void removeFollow(UUID requestingUser, UUID followedUser) {
        var res = userFollowRepository.returningDelete(UserFollowKey.create(requestingUser, followedUser));
        if(res == 0)
            throw new DoesntFollowedException();
        applicationEventPublisher.publishEvent(
            UserFollowEventFactory.createUnfollow(requestingUser, followedUser));
    }

    @Override
    public Slice<UserFollowResponse> getFollows(UUID requestingUser, int page) {
        return userFollowRepository.findByUserId(requestingUser, PageRequest.ofSize(10).withPage(page));
    }
    
}
