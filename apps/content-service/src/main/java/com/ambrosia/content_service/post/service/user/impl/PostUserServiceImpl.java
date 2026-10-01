package com.ambrosia.content_service.post.service.user.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.content_service.community.service.PostPermissionService;
import com.ambrosia.content_service.grpc.ProfileService;
import com.ambrosia.content_service.kafka.utils.PostEventFactory;
import com.ambrosia.content_service.kafka.utils.PreviewEventFactory;
import com.ambrosia.content_service.kafka.utils.ViewEventFactory;
import com.ambrosia.content_service.like.service.LikeUserService;
import com.ambrosia.content_service.post.exception.PostDoesntExistException;
import com.ambrosia.content_service.post.model.dto.response.PostContentResponse;
import com.ambrosia.content_service.post.model.dto.response.PreviewWithScoreResponse;
import com.ambrosia.content_service.post.repository.PostRepository;
import com.ambrosia.content_service.post.service.PostViewQueryService;
import com.ambrosia.content_service.post.service.user.PostUserService;
import com.ambrosia.content_service.post.utils.policy.PostPolicy;
import com.ambrosia.content_service.search.model.dto.EventFilter;
import com.ambrosia.content_service.search.service.PostIndexService;
import com.ambrosia.content_service.search.service.PostSearchService;
import com.ambrosia.outbox.kafka.KafkaOutboxService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class PostUserServiceImpl implements PostUserService{
    private final PostRepository postRepository;
  
    private final PostViewQueryService postViewQueryService;

    private final LikeUserService likeUserService;

    private final PostSearchService postSearchService;

    private final ApplicationEventPublisher eventPublisher;

    private final ProfileService profileService;

    private final PostPermissionService postPermissionService;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final KafkaOutboxService kafkaOutboxService;

    private final PostIndexService postIndexService;
    
    @Override
    public PostContentResponse getPost(long id, PostPolicy policy) {
        var post = postViewQueryService.getPost(id);
        if(post.getCommunity() != null){
            postPermissionService.validateViewCommunity(policy, id);
        }
        eventPublisher.publishEvent(ViewEventFactory.from(post));
        if(policy.userId() == null)
            return post;
        if(likeUserService.isLiked(id, policy.userId())){
            post.setIsLiked(true);
        }
        else{
            post.setIsLiked(false);
        }
        return post;
    }

    @Override
    public List<PreviewWithScoreResponse> search(EventFilter eventFilter, PostPolicy policy, int pageSize) {
        if(eventFilter.communityId() != null){
            postPermissionService.validateViewCommunity(policy, eventFilter.communityId());
        }

        List<UUID> blacklist = null;
        if(policy.userId() != null)
            blacklist = profileService.getBlacklist(policy.userId());
        var posts = postSearchService.search(eventFilter, policy.userId(), pageSize, blacklist);
        
        eventPublisher.publishEvent(PreviewEventFactory.from(posts));
        return posts;
    }

    @Transactional 
    @CacheEvict(cacheNames = "posts", key = "#postId")
    @Override
    public void deletePost(long postId, PostPolicy policy) {
        postPermissionService.validatePostDelete(policy, postId);
        var deleted = postRepository.returningDeletePublished(postId)
            .orElseThrow(() -> new PostDoesntExistException());

        postIndexService.deleteFromIndex(deleted.id(), deleted.version());
        
        var event = PostEventFactory.deleteOperation(deleted);
        kafkaOutboxService.put(event);
        applicationEventPublisher.publishEvent(event);
    }
}