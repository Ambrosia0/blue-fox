package com.ambrosia.content_service.post.application.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.content_service.infrastructure.grpc.ProfileService;
import com.ambrosia.content_service.infrastructure.kafka.utils.PostEventFactory;
import com.ambrosia.content_service.infrastructure.kafka.utils.PreviewEventFactory;
import com.ambrosia.content_service.infrastructure.kafka.utils.ViewEventFactory;
import com.ambrosia.content_service.like.application.LikeUserService;
import com.ambrosia.content_service.post.api.dto.EventFilter;
import com.ambrosia.content_service.post.api.dto.response.PostContentResponse;
import com.ambrosia.content_service.post.api.dto.response.PreviewWithScoreResponse;
import com.ambrosia.content_service.post.application.PostUserService;
import com.ambrosia.content_service.post.application.query.PostViewQueryService;
import com.ambrosia.content_service.post.domain.policy.CommunityViewPolicy;
import com.ambrosia.content_service.post.domain.policy.PostDeletePolicy;
import com.ambrosia.content_service.post.domain.policy.PostViewPolicy;
import com.ambrosia.content_service.post.domain.repository.PostRepository;
import com.ambrosia.content_service.post.exception.PostDoesntExistException;
import com.ambrosia.content_service.search.application.PostSearchService;
import com.ambrosia.content_service.search.infrastructure.PostIndexService;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicyContext;
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

    private final ApplicationEventPublisher applicationEventPublisher;

    private final KafkaOutboxService kafkaOutboxService;

    private final PostIndexService postIndexService;

    private final GenericPolicyContext policyContext;
    
    @Override
    public PostContentResponse getPost(long id, Actor actor) {
        var post = postViewQueryService.getPost(id);
        if(post.getCommunity() != null){
            policyContext.evaluate(actor, PostViewPolicy.class, id);
        }

        eventPublisher.publishEvent(ViewEventFactory.from(post));
        if(actor.id() == null)
            return post;
        if(likeUserService.isLiked(id, actor.id())){
            post.setIsLiked(true);
        }
        else{
            post.setIsLiked(false);
        }
        return post;
    }

    @Override
    public List<PreviewWithScoreResponse> search(EventFilter eventFilter, Actor actor, int pageSize) {
        if(eventFilter.communityId() != null){
            policyContext.evaluate(actor, CommunityViewPolicy.class, eventFilter.communityId());
        }

        List<UUID> blacklist = null;
        if(actor.id() != null)
            blacklist = profileService.getBlacklist(actor.id());
        var posts = postSearchService.search(eventFilter, actor.id(), pageSize, blacklist);
        
        eventPublisher.publishEvent(PreviewEventFactory.from(posts));
        return posts;
    }

    @Transactional 
    @CacheEvict(cacheNames = "posts", key = "#postId")
    @Override
    public void deletePost(long postId, Actor actor) {
        policyContext.evaluate(actor, PostDeletePolicy.class, postId);

        var deleted = postRepository.deletePublished(postId)
            .orElseThrow(() -> new PostDoesntExistException());

        postIndexService.deleteFromIndex(deleted.id(), deleted.version());
        
        var event = PostEventFactory.deleteOperation(deleted);
        kafkaOutboxService.put(event);
        applicationEventPublisher.publishEvent(event);
    }
}