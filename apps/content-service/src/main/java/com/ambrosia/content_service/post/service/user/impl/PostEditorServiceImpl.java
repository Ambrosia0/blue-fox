package com.ambrosia.content_service.post.service.user.impl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.content_service.community.model.dto.CommunityUserData;
import com.ambrosia.content_service.community.service.CommunityPermissionService;
import com.ambrosia.content_service.core.PostValidator;
import com.ambrosia.content_service.core.PreviewConverter;
import com.ambrosia.content_service.exception.api.InvalidContentException;
import com.ambrosia.content_service.exception.api.InvalidPostVersionException;
import com.ambrosia.content_service.exception.api.UserBannedException;
import com.ambrosia.content_service.kafka.utils.PostMessageFactory;
import com.ambrosia.content_service.post.exception.PostDoesntExistException;
import com.ambrosia.content_service.post.model.dto.request.PostCreateRequest;
import com.ambrosia.content_service.post.model.dto.request.PostEditRequest;
import com.ambrosia.content_service.post.model.dto.response.PostEditorContentResponse;
import com.ambrosia.content_service.post.model.dto.response.PostEditorViewResponse;
import com.ambrosia.content_service.post.model.entity.Post;
import com.ambrosia.content_service.post.repository.PostRepository;
import com.ambrosia.content_service.post.service.mapper.PostMapper;
import com.ambrosia.content_service.post.service.user.PostEditorService;
import com.ambrosia.content_service.post.utils.policy.PostPolicy;
import com.ambrosia.content_service.search.service.PostIndexService;
import com.ambrosia.content_service.search.service.mappers.PostIndexMapper;
import com.ambrosia.outbox.kafka.KafkaOutboxService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PostEditorServiceImpl implements PostEditorService {
    private final PostRepository postRepository;

    private final PostMapper postMapper;

    private final PreviewConverter previewConverter;

    private final PostIndexService postIndexService;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final PostValidator postValidator;

    private final CommunityPermissionService communityPermissionService;

    private final KafkaOutboxService kafkaOutboxService;

    private final PostIndexMapper postIndexMapper;

    @Override
    public void deletePost(long postId, PostPolicy policy) {
        var deletionProjection = postRepository.findDeletionProjectionById(postId)
            .orElseThrow(() -> new PostDoesntExistException("Editable post doesn't exist!"));
        policy.validatePostOwnership(deletionProjection.authorId());
        postRepository.deleteById(postId);
        if(deletionProjection.published()){
            applicationEventPublisher.publishEvent(
                PostMessageFactory.deleteOperation(deletionProjection)
            );
            postIndexService.deleteFromIndex(postId);
        }
    }

    @Override
    public PostEditorViewResponse createPost(UUID authorId, PostPolicy policy, PostCreateRequest postCreateRequest) {
        if(postCreateRequest.replyId() != null && !postRepository.existsById(postCreateRequest.replyId()))
            throw new PostDoesntExistException();

        CommunityUserData userData = null;
        if(postCreateRequest.replyId() != null)
            userData = communityPermissionService.validatePostToReply(
                    authorId, 
                    policy, 
                    postCreateRequest.replyId(), 
                    postCreateRequest.communityId()
            );
        else if(postCreateRequest.communityId() != null)
            userData = communityPermissionService.validatePostInCommunity(
                authorId, 
                policy, 
                postCreateRequest.communityId()
            );

        var post = postRepository.save(Post.builder()
            .authorId(authorId)
            .title(postCreateRequest.title())
            .communityId(userData != null? 
                AggregateReference.to(userData.communityId()): 
                null
            )
            .replyId(postCreateRequest.replyId() != null? 
                    AggregateReference.to(postCreateRequest.replyId()): 
                    null
            )
            .isNew(true)
            .updatedAt(Instant.now())
            .build()
        );
        return postMapper.toDto(post);
    }

    @Override
    public void editPost(UUID requestingUser, long postId, PostEditRequest editRequest) {
        var post = postRepository.findByAuthorIdAndId(requestingUser, postId)
            .orElseThrow(() -> new PostDoesntExistException("Editable post doesn't exist!"));
        if(post.getVersion() != editRequest.version())
            throw new InvalidPostVersionException();

        if(!postValidator.isValid(editRequest.post()))
            throw new InvalidContentException();

        var preview = previewConverter.convert(editRequest.post());
        
        post.setTitle(editRequest.title());
        post.setPreview(preview);
        post.setContent(editRequest.post());
        if(editRequest.tags() != null)
            post.setTags(editRequest.tags());
        post.setUpdatedAt(Instant.now());

        postRepository.save(post);
    }

    @Override
    public PostEditorContentResponse getContent(long postId, UUID userId) {
        var post = postRepository.findByAuthorIdAndIdAndPublishedIsFalse(userId, postId)
            .orElseThrow(() -> new PostDoesntExistException());
        return PostEditorContentResponse.from(post);
    }

    @Transactional(noRollbackFor = UserBannedException.class)
    @Override
    public void publishPost(UUID userId, PostPolicy policy, long postId) {
        var post = postRepository.findByAuthorIdAndIdAndPublishedIsFalse(userId, postId)
            .orElseThrow(() -> new PostDoesntExistException());
        
        CommunityUserData userData = null;
        if(post.getReplyId() != null){
            userData = communityPermissionService.validatePostToReply(
                userId, 
                policy, 
                postId, 
                post.getReplyId().getId()
            );
        }
        else if(post.getCommunityId() != null){
            userData = communityPermissionService.validatePostInCommunity(userId, policy, postId);
        }

        post.setPublishedAt(Instant.now());
        post.setPublished(true);
        
        post = postRepository.save(post);
        postIndexService.index(postIndexMapper.toIndex(post, userData));

        var event = PostMessageFactory.createOperation(
            post, 
            userData != null? 
                !userData.isCommunityPrivate(): 
                true
            );

        kafkaOutboxService.put(event);
        applicationEventPublisher.publishEvent(event);
    }
    
    @Override
    public Page<PostEditorViewResponse> getUnpublishedPosts(UUID authorId, Pageable pageable) {
        return postRepository.findByAuthorIdAndPublishedIsFalseAndVisibleIsTrue(authorId, pageable);
    }
}
