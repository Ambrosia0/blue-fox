package com.ambrosia.content_service.post.service.user.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.ambrosia.content_service.community.service.CommunityPrivacyService;
import com.ambrosia.content_service.exception.api.NotEnoughPermissionsException;
import com.ambrosia.content_service.follow.service.CommunityFollowProjectionService;
import com.ambrosia.content_service.grpc.ProfileService;
import com.ambrosia.content_service.kafka.utils.PreviewEventFactory;
import com.ambrosia.content_service.kafka.utils.ViewEventFactory;
import com.ambrosia.content_service.like.service.LikeUserService;
import com.ambrosia.content_service.post.model.dto.response.PostContentResponse;
import com.ambrosia.content_service.post.model.dto.response.PreviewWithScoreResponse;
import com.ambrosia.content_service.post.repository.PostRepository;
import com.ambrosia.content_service.post.service.PostQueryService;
import com.ambrosia.content_service.post.service.user.PostUserService;
import com.ambrosia.content_service.search.model.dto.EventFilter;
import com.ambrosia.content_service.search.service.PostSearchService;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class PostUserServiceImpl implements PostUserService{
    private final PostRepository postRepository;
  
    private final PostQueryService postQueryService;
    
    private final CommunityPrivacyService communityPrivacyService;

    private final LikeUserService likeUserService;

    private final PostSearchService postSearchService;

    private final ApplicationEventPublisher eventPublisher;

    private final CommunityFollowProjectionService communityFollowProjectionService;

    private final ProfileService profileService;

    // possibly get unupdated value in redis\
    @Override
    public PostContentResponse getPost(long id, @Nullable UUID requestingUser) {
        var post = postQueryService.getPublishedPostWithCommunity(id);
        if(post.getCommunityId() != null){
            if(requestingUser != null && !communityFollowProjectionService.isFollowedOnPrivateOrDoesntPrivate(post.getCommunityId(), requestingUser)){
                throw new NotEnoughPermissionsException();
            }else if(communityPrivacyService.isPrivate(post.getCommunityId()).get()){
                throw new NotEnoughPermissionsException();
            }
        }
        eventPublisher.publishEvent(ViewEventFactory.from(post));
        if(requestingUser == null)
            return post;
        if(likeUserService.isLiked(id, requestingUser)){
            post.setIsLiked(true);
        }
        else{
            post.setIsLiked(false);
        }
        return post;
    }

    @Override
    public List<PreviewWithScoreResponse> search(EventFilter eventFilter, @Nullable UUID requestingUser, int pageSize) {
        if(eventFilter.communityId() != null && 
            !communityFollowProjectionService.isFollowedOnPrivateOrDoesntPrivate(eventFilter.communityId(), requestingUser))
            throw new NotEnoughPermissionsException();

        List<UUID> blacklist = null;
        if(requestingUser != null)
            blacklist = profileService.getBlacklist(requestingUser);
        var posts = postSearchService.search(eventFilter, requestingUser, pageSize, blacklist);
        eventPublisher.publishEvent(PreviewEventFactory.from(posts));
        return posts;
    }

    @Override
    public boolean isAuthor(long postId, UUID userId) {
        return postRepository.existsByIdAndAuthorId(postId, userId);
    }

    @Override
    public boolean isExists(long postId) {
        return postRepository.existsByIdAndVisibleIsTrueAndPublishedIsTrue(postId);
    }
}