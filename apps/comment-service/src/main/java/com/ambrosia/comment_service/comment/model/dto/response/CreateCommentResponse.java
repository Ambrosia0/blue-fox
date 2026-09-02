package com.ambrosia.comment_service.comment.model.dto.response;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Transient;

import com.ambrosia.comment_service.attachment.model.dto.response.AttachmentUploadResponse;
import com.ambrosia.comment_service.comment.model.entity.Comment;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateCommentResponse{
    long id;
    long postId;
    UUID userId;
    String content;

    @JsonInclude(value = Include.NON_NULL)
    Long parentComment;
    Instant createdAt;

    /**
     * Attachment id to return after attachment upload confirmation
     */
    @Transient
    @JsonInclude(value = Include.NON_NULL)
    String attachmentId;

    /**
     * Information required to upload attachment to external store
     */
    @Transient
    @JsonInclude(value = Include.NON_NULL)
    AttachmentUploadResponse attachmentUploadResponse;

    public static CreateCommentResponse create(
            Comment comment,
            String attachmentId,
            AttachmentUploadResponse attachmentUploadResponse){
        return new CreateCommentResponse(
            comment.getId(),
            comment.getPostId(),
            comment.getUserId(),
            comment.getContent(), 
            comment.getParentCommentId(), 
            comment.getCreatedAt(),
            attachmentId,
            attachmentUploadResponse
        );
    }
}
