package com.ambrosia.content_service.post.application.impl;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.content_service.core.PostValidator;
import com.ambrosia.content_service.core.PreviewConverter;
import com.ambrosia.content_service.exception.api.CollaborationApiEditException;
import com.ambrosia.content_service.exception.api.InvalidCollaboratorInfoException;
import com.ambrosia.content_service.exception.api.InvalidContentException;
import com.ambrosia.content_service.exception.api.InvalidPostVersionException;
import com.ambrosia.content_service.exception.api.UserBannedException;
import com.ambrosia.content_service.infrastructure.kafka.utils.PostEventFactory;
import com.ambrosia.content_service.post.api.dto.request.PostCreateRequest;
import com.ambrosia.content_service.post.api.dto.request.PostEditRequest;
import com.ambrosia.content_service.post.api.dto.request.PostEditorFilter;
import com.ambrosia.content_service.post.api.dto.response.PostEditorContentResponse;
import com.ambrosia.content_service.post.api.dto.response.PostEditorViewResponse;
import com.ambrosia.content_service.post.application.PostEditorService;
import com.ambrosia.content_service.post.application.policy.handlers.CreateHandlerArg;
import com.ambrosia.content_service.post.application.query.PostEditorQueryService;
import com.ambrosia.content_service.post.application.query.PostViewQueryService;
import com.ambrosia.content_service.post.domain.entity.Post;
import com.ambrosia.content_service.post.domain.policy.PostCreatePolicy;
import com.ambrosia.content_service.post.domain.policy.PostPublishPolicy;
import com.ambrosia.content_service.post.domain.policy.entity.PostPublishPolicyData;
import com.ambrosia.content_service.post.domain.repository.PostRepository;
import com.ambrosia.content_service.post.exception.PostDoesntExistException;
import com.ambrosia.content_service.post.utils.ConvertedDoc;
import com.ambrosia.content_service.search.infrastructure.PostIndexService;
import com.ambrosia.content_service.search.infrastructure.mappers.PostIndexMapper;
import com.ambrosia.content_service.user.service.UserService;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicyContext;
import com.ambrosia.outbox.kafka.KafkaOutboxService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PostEditorServiceImpl implements PostEditorService {
    private final PostRepository postRepository;

    private final PostIndexService postIndexService;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final PostValidator postValidator;

    private final KafkaOutboxService kafkaOutboxService;

    private final PostIndexMapper postIndexMapper;

    private final UserService userService;

    private final PostEditorQueryService postEditorQueryService;

    private final PostViewQueryService postViewQueryService;

    private final GenericPolicyContext policyContext;

    private final PreviewConverter previewConverter;

    @Override
    public void deleteDraftPost(long postId, Actor actor) {
        var deleted = postRepository.deleteByAuthorIdAndIdAndPublishedIsFalse(actor.id(), postId);
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
    public PostEditorViewResponse createPost(Actor actor, PostCreateRequest postCreateRequest) {
        if(postCreateRequest.collaborators() != null && !postCreateRequest.collaborators().isEmpty()){
            if(!userService.isUsersExist(postCreateRequest.collaborators()) 
                || postCreateRequest.collaborators().contains(actor.id()))
                throw new InvalidCollaboratorInfoException();
        }

        policyContext.evaluate(
                actor, 
                PostCreatePolicy.class, 
                CreateHandlerArg.create(postCreateRequest.replyId(), postCreateRequest.communityId())
            );

        var post = postRepository.save(Post.builder()
            .authorId(actor.id())
            .title(postCreateRequest.title())
            .communityId(postCreateRequest.communityId())
            .replyId(postCreateRequest.replyId())
            .build()
        );
        return postEditorQueryService.getPostPreview(post.getId(), actor.id()).get();
    }

    @Override
    public void editPost(Actor actor, long postId, PostEditRequest editRequest) {
        var post = postRepository.findByAuthorIdAndId(actor.id(), postId)
            .orElseThrow(() -> new PostDoesntExistException("Editable post doesn't exist!"));
            
        if(!post.getCollaborationUsers().isEmpty())
            throw new CollaborationApiEditException();

        if(post.getVersion() != editRequest.version())
            throw new InvalidPostVersionException();

        if(!postValidator.isValid(editRequest.post()))
            throw new InvalidContentException();

        post.edit(ConvertedDoc
            .from(editRequest.post(), previewConverter.convert(editRequest.post()))
        );
        postRepository.save(post);
    }

    @Override
    public PostEditorContentResponse getContent(long postId, Actor actor) {
        return postEditorQueryService.getPostContent(postId, actor.id())
            .orElseThrow(() -> new PostDoesntExistException());
    }

    @Transactional(noRollbackFor = UserBannedException.class)
    @Override
    public void publishPost(Actor actor, long postId) {
        var post = postRepository.findByAuthorIdAndIdAndPublishedIsFalse(actor.id(), postId)
            .orElseThrow(() -> new PostDoesntExistException());
        

        var data = (PostPublishPolicyData)policyContext.evaluate(
            actor, 
            PostPublishPolicy.class, 
            post.getId()
        );

        post.publish();

        post = postRepository.save(post);

        postIndexService.index(postIndexMapper.toIndex(post, data));

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
    public void unpublishPost(Actor actor, long postId) {
        var post = postRepository.findByAuthorIdAndIdAndPublishedIsTrue(actor.id(), postId)
            .orElseThrow(() -> new PostDoesntExistException());
        
        var versionToDelete = post.getVersion();

        post.unpublish();

        post = postRepository.save(post);

        var event = PostEventFactory.updateOperation(post);
        kafkaOutboxService.put(event);
        postIndexService.deleteFromIndex(post.getId(), versionToDelete);
    }
    
    @Override
    public Slice<PostEditorViewResponse> getUnpublishedPosts(Actor actor, PostEditorFilter filter, Pageable pageable) {
        return postEditorQueryService.getPostsPreview(actor.id(), filter, pageable);
    }
}
