package com.ambrosia.content_service.follow.application.impl;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.ambrosia.content_service.exception.api.AlreadyFollowedException;
import com.ambrosia.content_service.exception.api.DoesntFollowedException;
import com.ambrosia.content_service.exception.api.UserDoesntExistException;
import com.ambrosia.content_service.follow.api.dto.UserFollowResponse;
import com.ambrosia.content_service.follow.application.UserFollowService;
import com.ambrosia.content_service.follow.application.query.UserFollowQueryRepository;
import com.ambrosia.content_service.follow.domain.repository.UserFollowRepository;
import com.ambrosia.content_service.follow.infrastructure.entity.UserFollow;
import com.ambrosia.content_service.follow.infrastructure.entity.keys.UserFollowKey;
import com.ambrosia.content_service.infrastructure.kafka.utils.UserFollowEventFactory;
import com.ambrosia.content_service.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserFollowServiceImpl implements UserFollowService{
    private final UserFollowRepository userFollowRepository;

    private final UserFollowQueryRepository userFollowQueryRepository;

    private final ApplicationEventPublisher applicationEventPublisher;
    
    private final UserService userService;

    @Override
    public void followUser(UUID requestingUser, UUID followedUser) {
        if(!userService.isUserExist(followedUser))
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
        return userFollowQueryRepository.findByUserId(requestingUser, PageRequest.ofSize(10).withPage(page));
    }
    
}
