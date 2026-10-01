package com.ambrosia.comment_service.comment.service.impl;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ambrosia.comment_service.attachment.service.AttachmentService;
import com.ambrosia.comment_service.attachment.utils.AttachmentIdGenerator;
import com.ambrosia.comment_service.comment.model.dto.EventFilter;
import com.ambrosia.comment_service.comment.model.dto.request.CreateComment;
import com.ambrosia.comment_service.comment.model.dto.response.CommentData;
import com.ambrosia.comment_service.comment.model.dto.response.CreateCommentResponse;
import com.ambrosia.comment_service.comment.model.dto.response.ScoredCommentData;
import com.ambrosia.comment_service.comment.repository.CommentRepository;
import com.ambrosia.comment_service.comment.service.CommentQueryService;
import com.ambrosia.comment_service.comment.service.UserCommentService;
import com.ambrosia.comment_service.comment.service.mapper.CommentMapper;
import com.ambrosia.comment_service.community.service.CommentPermissionService;
import com.ambrosia.comment_service.community.utils.CommentPolicy;
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

    private final CommentPermissionService communityPermissionService;

    private final AttachmentService attachmentService;

    private final CommentMapper commentMapper;

    private final CommentQueryService commentQueryService;

    @Override
    public CreateCommentResponse createComment(CommentPolicy policy, CreateComment request) {
        var post = postProjectionService.findProjectionById(request.postId())
            .orElseThrow(() -> new PostDoesntExistException());

        if(post.getCommunityId() != null){
            communityPermissionService.validateCommentCreate(policy, post.getId());
        }

        var savedComment = commentRepository.insert(commentMapper.toEntity(policy.id(), request))
            .orElseThrow(() -> new CommentOrPostDoesntExistException());

        if(request.fileMetadata() == null){
            applicationEventPublisher.publishEvent(
                CommentMessageFactory.createOperation(
                    commentQueryService.getComment(savedComment.getId(), null)
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
    public CreateCommentResponse confirmAttachmentUpload(CommentPolicy policy, long commentId, String attachmentId) {
        var comment = commentRepository.findCreateProjection(commentId, policy.id())
            .orElseThrow(() -> new CommentOrPostDoesntExistException());
        attachmentService.confirmAttachmentUpload(commentId, attachmentId);
        comment.setAttachmentId(attachmentId);
        applicationEventPublisher.publishEvent(
            CommentMessageFactory.createOperation(
                commentQueryService.getComment(commentId, null)
            )
        );
        return comment;
    }

    @Override
    public void deleteComment(long commentId, CommentPolicy commentPolicy) {
        communityPermissionService.validateCommentDelete(commentPolicy, commentId);
        commentRepository.hideCommentById(commentId);
    }
    @Override
    public List<ScoredCommentData> getCommentTree(long commentId, CommentPolicy commentPolicy) {
        Assert.notNull(commentPolicy, "Policy must no be null!");
        communityPermissionService.validateCommentView(commentPolicy, commentId);
        return commentQueryService.getCommentTree(commentId, commentPolicy.id());
    }

    @Override
    public List<ScoredCommentData> getCommentsForPost(long postId, EventFilter eventFilter, CommentPolicy commentPolicy) {
        Assert.notNull(commentPolicy, "Policy must no be null!");
        communityPermissionService.validateCommentView(commentPolicy, postId);
        return commentQueryService.getCommentsForPost(postId, eventFilter, commentPolicy.id());
    }

    @Override
    public CommentData getComment(long commentId, CommentPolicy commentPolicy) {
        Assert.notNull(commentPolicy, "Policy must no be null!");
        communityPermissionService.validateCommentView(commentPolicy, commentId);
        var data = commentQueryService.getComment(commentId, commentPolicy.id());
        return data;
    }

    @Override
    public boolean isExists(long commentId) {
        return commentRepository.existsByIdAndIsVisibleIsTrue(commentId);
    }
    
}
