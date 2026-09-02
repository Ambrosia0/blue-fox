package com.ambrosia.comment_service.utils.factory;


import com.ambrosia.comment_service.attachment.model.dto.request.FileMetadata;
import com.ambrosia.comment_service.comment.model.dto.request.CreateComment;

public class CommentRequestFactory {
    public static CreateComment createCommentRequest(Long postId){
        return new CreateComment(
            postId,
            "Test content",
            null,
            null
        );
    }

    public static CreateComment createCommentRequest(Long postId, Long parentComment, FileMetadata fileMetadata){
        return new CreateComment(
            postId,
            "Test content",
            parentComment,
            fileMetadata
        );
    }
}
