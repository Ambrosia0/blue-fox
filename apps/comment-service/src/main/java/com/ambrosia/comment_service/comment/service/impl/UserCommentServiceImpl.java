package com.ambrosia.comment_service.comment.service.impl;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.ambrosia.comment_service.attachment.service.AttachmentService;
import com.ambrosia.comment_service.attachment.utils.AttachmentIdGenerator;
import com.ambrosia.comment_service.comment.model.dto.request.CreateComment;
import com.ambrosia.comment_service.comment.model.dto.response.CreateCommentResponse;
import com.ambrosia.comment_service.comment.model.entity.Comment;
import com.ambrosia.comment_service.comment.repository.CommentRepository;
import com.ambrosia.comment_service.comment.service.UserCommentService;
import com.ambrosia.comment_service.community.service.CommunityPermissionService;
import com.ambrosia.comment_service.exceptions.api.CommentOrPostDoesntExistException;
import com.ambrosia.comment_service.exceptions.api.PostDoesntExistException;
import com.ambrosia.comment_service.kafka.utils.CommentMessageFactory;
import com.ambrosia.comment_service.post.service.PostProjectionService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserCommentServiceImpl implements UserCommentService {

    private final CommentRepository commentRepository;

    private final ApplicationEventPublisher applicationEventPublisher;
    
    private final PostProjectionService postProjectionService;

    private final CommunityPermissionService communityPermissionService;

    private final AttachmentService attachmentService;

    @Override
    public CreateCommentResponse createComment(UUID userId, CreateComment request) {
        var post = postProjectionService.findProjectionById(request.postId())
            .orElseThrow(() -> new PostDoesntExistException());

        if(post.getCommunityId() != null){
            communityPermissionService.validateCommentCreate(userId, post.getId());
        }

        var savedComment = commentRepository.insert(Comment.builder()
                .postId(request.postId())
                .userId(userId)
                .content(request.content())
                .parentCommentId(request.parentComment())
                .build())
            .orElseThrow(() -> new CommentOrPostDoesntExistException());

        if(request.fileMetadata() == null){
            applicationEventPublisher.publishEvent(
                CommentMessageFactory.createOperation(savedComment)
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
    public CreateCommentResponse confirmAttachmentUpload(UUID userId, long commentId, String attachmentId) {
        var comment = commentRepository.findCreateProjection(commentId, userId)
            .orElseThrow(() -> new CommentOrPostDoesntExistException());
        attachmentService.confirmAttachmentUpload(commentId, attachmentId);
        comment.setAttachmentId(attachmentId);
        applicationEventPublisher.publishEvent(
            CommentMessageFactory.createOperation(comment)
        );
        return comment;
    }

    @Override
    public boolean isExists(long commentId) {
        return commentRepository.existsByIdAndIsVisibleIsTrue(commentId);
    }
    
}
