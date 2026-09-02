package com.ambrosia.content_service.post.service.user;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ambrosia.content_service.post.model.dto.request.PostCreateRequest;
import com.ambrosia.content_service.post.model.dto.request.PostEditRequest;
import com.ambrosia.content_service.post.model.dto.response.PostEditorContentResponse;
import com.ambrosia.content_service.post.model.dto.response.PostEditorViewResponse;
import com.ambrosia.content_service.post.utils.policy.PostOwnershipPolicy;

public interface PostEditorService {
    void editPost(UUID requestingUser, long postId, PostEditRequest postEditRequest);
    PostEditorViewResponse createPost(UUID authorId, PostCreateRequest postCreateRequest);
    void publishPost(UUID userId, long postId);
    void deletePost(long postId, PostOwnershipPolicy policy);
    PostEditorContentResponse getContent(long postId, UUID userId);
    Page<PostEditorViewResponse> getUnpublishedPosts(UUID authorId, Pageable pageable);
}
