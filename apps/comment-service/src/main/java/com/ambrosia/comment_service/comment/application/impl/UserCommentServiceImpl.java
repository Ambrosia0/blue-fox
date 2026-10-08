package com.ambrosia.comment_service.comment.application.impl;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.ambrosia.comment_service.attachment.application.AttachmentService;
import com.ambrosia.comment_service.attachment.utils.AttachmentIdGenerator;
import com.ambrosia.comment_service.comment.api.dto.request.CreateComment;
import com.ambrosia.comment_service.comment.api.dto.response.CreateCommentResponse;
import com.ambrosia.comment_service.comment.api.mapper.CommentMapper;
import com.ambrosia.comment_service.comment.application.UserCommentService;
import com.ambrosia.comment_service.comment.application.query.CommentQueryService;
import com.ambrosia.comment_service.comment.domain.policy.CommentDeletePolicy;
import com.ambrosia.comment_service.comment.domain.policy.PostCommentCreatePolicy;
import com.ambrosia.comment_service.comment.domain.policy.TreeCommentCreatePolicy;
import com.ambrosia.comment_service.comment.domain.repository.CommentRepository;
import com.ambrosia.comment_service.exceptions.api.CommentOrPostDoesntExistException;
import com.ambrosia.comment_service.infrastructure.kafka.utils.CommentMessageFactory;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicyContext;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserCommentServiceImpl implements UserCommentService {

    private final CommentRepository commentRepository;

    private final ApplicationEventPublisher applicationEventPublisher;
    
    private final AttachmentService attachmentService;

    private final CommentMapper commentMapper;

    private final CommentQueryService commentQueryService;

    private final GenericPolicyContext genericPolicyContext;

    @Override
    public CreateCommentResponse createComment(Actor actor, CreateComment request) {
        genericPolicyContext.evaluate(
            actor, 
            request.parentComment() != null?
                TreeCommentCreatePolicy.class:
                PostCommentCreatePolicy.class,
            request.parentComment() != null?
                request.parentComment():
                request.postId()
        );

        var savedComment = commentRepository.insert(commentMapper.toEntity(actor.id(), request))
            .orElseThrow(() -> new CommentOrPostDoesntExistException());

        if(request.fileMetadata() == null){
            applicationEventPublisher.publishEvent(
                CommentMessageFactory.createOperation(
                    commentQueryService.getComment(savedComment.getId(), actor)
                )
            );
            return savedComment;
        }

        var attachmentId = AttachmentIdGenerator.generateAttachmentId(savedComment.getId());
        savedComment.setAttachmentUploadResponse(
            attachmentService.attachMedia(
                attachmentId,
                request.fileMetadata()
            )
        );
        return savedComment;
    }

    @Override
    public CreateCommentResponse confirmAttachmentUpload(Actor actor, long commentId, String attachmentId) {
        var comment = commentRepository.findCreateProjection(commentId, actor.id())
            .orElseThrow(() -> new CommentOrPostDoesntExistException());
        attachmentService.confirmAttachmentUpload(commentId, attachmentId);
        comment.setAttachmentId(attachmentId);
        applicationEventPublisher.publishEvent(
            CommentMessageFactory.createOperation(
                commentQueryService.getComment(commentId, actor)
            )
        );
        return comment;
    }

    @Override
    public void deleteComment(long commentId, Actor actor) {
        genericPolicyContext.evaluate(actor, CommentDeletePolicy.class, commentId);
        commentRepository.hideCommentById(commentId);
    }

    @Override
    public boolean isExists(long commentId) {
        return commentRepository.existsByIdAndIsVisibleIsTrue(commentId);
    }
    
}
