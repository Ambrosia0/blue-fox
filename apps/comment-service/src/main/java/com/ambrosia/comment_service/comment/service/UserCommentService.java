package com.ambrosia.comment_service.comment.service;

import java.util.List;

import com.ambrosia.comment_service.comment.model.dto.EventFilter;
import com.ambrosia.comment_service.comment.model.dto.request.CreateComment;
import com.ambrosia.comment_service.comment.model.dto.response.CommentData;
import com.ambrosia.comment_service.comment.model.dto.response.CreateCommentResponse;
import com.ambrosia.comment_service.comment.model.dto.response.ScoredCommentData;
import com.ambrosia.comment_service.community.utils.CommentPolicy;

public interface UserCommentService {
    CreateCommentResponse createComment(
        CommentPolicy commentPolicy, 
        CreateComment request
    );
    CreateCommentResponse confirmAttachmentUpload(
        CommentPolicy commentPolicy, 
        long commentId, 
        String attachmentId
    );
    void deleteComment(
        long commentId,
        CommentPolicy commentPolicy
    );
    List<ScoredCommentData> getCommentsForPost(
        long postId, 
        EventFilter eventFilter, 
        CommentPolicy commentPolicy
    );
    List<ScoredCommentData> getCommentTree(
        long commentId, 
        CommentPolicy commentPolicy
    );
    CommentData getComment(
        long commentId, 
        CommentPolicy commentPolicy
    );
    boolean isExists(long commentId);
}
