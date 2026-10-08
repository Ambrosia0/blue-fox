package com.ambrosia.content_service.post.domain.repository;

import java.util.List;
import java.util.UUID;

import com.ambrosia.content_service.post.api.dto.EventFilter;
import com.ambrosia.content_service.post.api.dto.response.PreviewWithScoreResponse;

public interface PostSearchRepository {
    List<PreviewWithScoreResponse> search(
        EventFilter eventFilter, 
        UUID requestingUser, 
        int pageSize,
        List<UUID> blacklist
    );
}
