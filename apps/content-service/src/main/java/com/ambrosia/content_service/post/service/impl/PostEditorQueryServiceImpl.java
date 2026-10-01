package com.ambrosia.content_service.post.service.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.ambrosia.content_service.post.model.dto.request.PostEditorFilter;
import com.ambrosia.content_service.post.model.dto.response.PostEditorContentResponse;
import com.ambrosia.content_service.post.model.dto.response.PostEditorViewResponse;
import com.ambrosia.content_service.post.repository.PostEditorQueryRepository;
import com.ambrosia.content_service.post.service.PostEditorQueryService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Service 
public class PostEditorQueryServiceImpl implements PostEditorQueryService{
    private final PostEditorQueryRepository postEditorQueryRepository;

    @Override
    public Optional<PostEditorContentResponse> getPostContent(long postId, UUID userId) {
        return postEditorQueryRepository.findUnpublishedByPostIdAndUserId(postId, userId);
    }

    @Override
    public Optional<PostEditorViewResponse> getPostPreview(long postId, UUID userId) {
        return postEditorQueryRepository.findUnpublishedViewByPostIdAndUserId(postId, userId);
    }

    @Override
    public Slice<PostEditorViewResponse> getPostsPreview(UUID userId, PostEditorFilter filter, Pageable pageable) {
        return postEditorQueryRepository.findUnpublishedPreviewsByUserId(userId, filter, pageable);
    }
}
