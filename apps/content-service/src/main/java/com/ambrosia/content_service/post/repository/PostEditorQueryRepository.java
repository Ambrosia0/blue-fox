package com.ambrosia.content_service.post.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.ambrosia.content_service.post.model.dto.request.PostEditorFilter;
import com.ambrosia.content_service.post.model.dto.response.PostCollaborationContentResponse;
import com.ambrosia.content_service.post.model.dto.response.PostEditorContentResponse;
import com.ambrosia.content_service.post.model.dto.response.PostEditorViewResponse;

public interface PostEditorQueryRepository {
    Optional<PostEditorContentResponse> findUnpublishedByPostIdAndUserId(long postId, UUID userId);
    Optional<PostCollaborationContentResponse> findCollaborationPostById(long postId);
    Slice<PostEditorViewResponse> findUnpublishedPreviewsByUserId(UUID userId, PostEditorFilter filter, Pageable pageable);
    Optional<PostEditorViewResponse> findUnpublishedViewByPostIdAndUserId(long postId, UUID userId);
}
