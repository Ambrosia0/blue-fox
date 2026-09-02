package com.ambrosia.content_service.post.service.user.impl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.ambrosia.content_service.post.utils.policy.PostOwnershipPolicy;
import com.ambrosia.content_service.search.service.PostIndexService;
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

    @Override
    public void deletePost(long postId, PostOwnershipPolicy policy) {
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
    public PostEditorViewResponse createPost(UUID authorId, PostCreateRequest postCreateRequest) {
        if(postCreateRequest.communityId() != null)
            communityPermissionService.validatePostCreate(authorId, postCreateRequest.communityId());
        var post = postRepository.save(Post.builder()
            .authorId(authorId)
            .title(postCreateRequest.title())
            .communityId(postCreateRequest.communityId())
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
    public void publishPost(UUID userId, long postId) {
        var post = postRepository.findByAuthorIdAndIdAndPublishedIsFalse(userId, postId)
            .orElseThrow(() -> new PostDoesntExistException());
        if(post.getCommunityId() != null)
            communityPermissionService.validatePostCreate(userId, postId);
        post.setPublishedAt(Instant.now());
        post.setPublished(true);
        
        post = postRepository.save(post);
        postIndexService.index(post);

        var event = PostMessageFactory.createOperation(post);
        kafkaOutboxService.put(event);
        applicationEventPublisher.publishEvent(
            PostMessageFactory.createOperation(post)
        );
    }
    
    @Override
    public Page<PostEditorViewResponse> getUnpublishedPosts(UUID authorId, Pageable pageable) {
        return postRepository.findByAuthorIdAndPublishedIsFalseAndVisibleIsTrue(authorId, pageable);
    }
}
