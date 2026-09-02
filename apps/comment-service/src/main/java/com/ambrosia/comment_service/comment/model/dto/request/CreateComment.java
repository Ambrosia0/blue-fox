package com.ambrosia.comment_service.comment.model.dto.request;

import com.ambrosia.comment_service.attachment.model.dto.request.FileMetadata;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateComment(
    @NotNull Long postId,

    @Size(min = 3, max = 1000)
    String content,

    Long parentComment,

    @Nullable @Valid FileMetadata fileMetadata
) {}
