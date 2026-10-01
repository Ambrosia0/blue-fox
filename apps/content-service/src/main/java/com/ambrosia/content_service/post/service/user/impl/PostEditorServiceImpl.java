package com.ambrosia.content_service.post.service.user.impl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.content_service.community.model.dto.CommunityUserData;
import com.ambrosia.content_service.community.service.PostPermissionService;
import com.ambrosia.content_service.core.PostValidator;
import com.ambrosia.content_service.exception.api.CollaborationApiEditException;
import com.ambrosia.content_service.exception.api.InvalidCollaboratorInfoException;
import com.ambrosia.content_service.exception.api.InvalidContentException;
import com.ambrosia.content_service.exception.api.InvalidPostVersionException;
import com.ambrosia.content_service.exception.api.UserBannedException;
import com.ambrosia.content_service.kafka.utils.PostEventFactory;
import com.ambrosia.content_service.post.exception.PostDoesntExistException;
import com.ambrosia.content_service.post.model.dto.request.PostCreateRequest;
import com.ambrosia.content_service.post.model.dto.request.PostEditRequest;
import com.ambrosia.content_service.post.model.dto.request.PostEditorFilter;
import com.ambrosia.content_service.post.model.dto.response.PostEditorContentResponse;
import com.ambrosia.content_service.post.model.dto.response.PostEditorViewResponse;
import com.ambrosia.content_service.post.model.entity.Post;
import com.ambrosia.content_service.post.repository.PostRepository;
import com.ambrosia.content_service.post.service.PostEditorQueryService;
import com.ambrosia.content_service.post.service.PostViewQueryService;
import com.ambrosia.content_service.post.service.mappers.PostMapper;
import com.ambrosia.content_service.post.service.user.PostEditorService;
import com.ambrosia.content_service.post.utils.policy.PostPolicy;
import com.ambrosia.content_service.search.service.PostIndexService;
import com.ambrosia.content_service.search.service.mappers.PostIndexMapper;
import com.ambrosia.content_service.user.service.UserService;
import com.ambrosia.outbox.kafka.KafkaOutboxService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PostEditorServiceImpl implements PostEditorService {
    private final PostRepository postRepository;

    private final PostIndexService postIndexService;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final PostValidator postValidator;

    private final PostPermissionService communityPermissionService;

    private final KafkaOutboxService kafkaOutboxService;

    private final PostIndexMapper postIndexMapper;

    private final PostMapper postMapper;

    private final UserService userService;

    private final PostEditorQueryService postEditorQueryService;

    private final PostViewQueryService postViewQueryService;

    @Override
    public void deleteDraftPost(long postId, PostPolicy policy) {
        var deleted = postRepository.deleteByAuthorIdAndIdAndPublishedIsFalse(policy.userId(), postId);
        if(deleted == 0)
            throw new PostDoesntExistException();
        // policy.validateDelete(deletionProjection.authorId());
        // postRepository.deleteById(postId);
        // if(deletionProjection.published()){
        //     applicationEventPublisher.publishEvent(
        //         PostMessageFactory.deleteOperation(deletionProjection)
        //     );
        //     postIndexService.deleteFromIndex(postId);
        // }
    }


    @Override
    public PostEditorViewResponse createPost(UUID authorId, PostPolicy policy, PostCreateRequest postCreateRequest) {
        if(postCreateRequest.replyId() != null && !postRepository.existsById(postCreateRequest.replyId()))
            throw new PostDoesntExistException();

        if(postCreateRequest.collaborators() != null && !postCreateRequest.collaborators().isEmpty()){
            if(!userService.isUsersExist(postCreateRequest.collaborators()) 
                || postCreateRequest.collaborators().contains(authorId))
                throw new InvalidCollaboratorInfoException();
        }

        CommunityUserData userData = null;
        if(postCreateRequest.replyId() != null)
            userData = communityPermissionService.validatePostToReply(
                    policy, 
                    postCreateRequest.replyId(), 
                    postCreateRequest.communityId()
            );
        else if(postCreateRequest.communityId() != null)
            userData = communityPermissionService.validatePostInCommunity(
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
        return postEditorQueryService.getPostPreview(post.getId(), authorId).get();
    }

    @Override
    public void editPost(UUID requestingUser, long postId, PostEditRequest editRequest) {
        var post = postRepository.findByAuthorIdAndId(requestingUser, postId)
            .orElseThrow(() -> new PostDoesntExistException("Editable post doesn't exist!"));
            
        if(!post.getCollaborationUsers().isEmpty())
            throw new CollaborationApiEditException();

        if(post.getVersion() != editRequest.version())
            throw new InvalidPostVersionException();

        if(!postValidator.isValid(editRequest.post()))
            throw new InvalidContentException();

        postRepository.save(postMapper.apply(post, editRequest));
    }

    @Override
    public PostEditorContentResponse getContent(long postId, UUID userId) {
        return postEditorQueryService.getPostContent(postId, userId)
            .orElseThrow(() -> new PostDoesntExistException());
    }

    @Transactional(noRollbackFor = UserBannedException.class)
    @Override
    public void publishPost(PostPolicy policy, long postId) {
        var post = postRepository.findByAuthorIdAndIdAndPublishedIsFalse(policy.userId(), postId)
            .orElseThrow(() -> new PostDoesntExistException());
        
        CommunityUserData userData = null;
        if(post.getReplyId() != null){
            userData = communityPermissionService.validatePostToReply(
                policy, 
                postId, 
                post.getReplyId().getId()
            );
        }
        else if(post.getCommunityId() != null){
            userData = communityPermissionService.validatePostInCommunity(policy, postId);
        }

        post = postRepository.save(postMapper.toPublishedState(post));
        postIndexService.index(postIndexMapper.toIndex(post, userData));

        var event = post.isRepublished()?
            PostEventFactory.updateOperation(post):
            PostEventFactory.createOperation(
                postViewQueryService.getPostPreview(postId, null)
            );

        kafkaOutboxService.put(event);
        applicationEventPublisher.publishEvent(event);
    }
    
    @Transactional 
    @Override
    public void unpublishPost(UUID authorId, long postId) {
        var post = postRepository.findByAuthorIdAndIdAndPublishedIsTrue(authorId, postId)
            .orElseThrow(() -> new PostDoesntExistException());
        post.setPublished(false);
        
        var versionToDelete = post.getVersion();

        post = postRepository.save(postMapper.toUnpublishedState(post));

        var event = PostEventFactory.updateOperation(post);
        kafkaOutboxService.put(event);
        postIndexService.deleteFromIndex(post.getId(), versionToDelete);
    }
    
    @Override
    public Slice<PostEditorViewResponse> getUnpublishedPosts(UUID authorId, PostEditorFilter filter, Pageable pageable) {
        return postEditorQueryService.getPostsPreview(authorId, filter, pageable);
    }
}
