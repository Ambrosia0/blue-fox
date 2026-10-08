package com.ambrosia.content_service.search.application;

import java.util.List;
import java.util.UUID;

import com.ambrosia.content_service.post.api.dto.EventFilter;
import com.ambrosia.content_service.post.api.dto.response.PreviewWithScoreResponse;

import jakarta.annotation.Nullable;

public interface PostSearchService {
    List<PreviewWithScoreResponse> search(
        EventFilter eventFilter, 
        @Nullable UUID requestingUser, 
        int pageSize,
        @Nullable List<UUID> blacklist
    );
}
