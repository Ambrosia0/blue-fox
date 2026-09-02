package com.ambrosia.content_service.search.service;

import java.util.List;
import java.util.UUID;

import com.ambrosia.content_service.post.model.dto.response.PreviewWithScoreResponse;
import com.ambrosia.content_service.search.model.dto.EventFilter;

import jakarta.annotation.Nullable;

public interface PostSearchService {
    List<PreviewWithScoreResponse> search(
        EventFilter eventFilter, 
        @Nullable UUID requestingUser, 
        int pageSize,
        @Nullable List<UUID> blacklist
    );
}
