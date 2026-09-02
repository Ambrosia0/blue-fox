package com.ambrosia.content_service.search.repository;

import java.util.List;
import java.util.UUID;

import com.ambrosia.content_service.post.model.dto.response.PreviewWithScoreResponse;
import com.ambrosia.content_service.search.model.dto.EventFilter;

public interface PostSearchRepository {
    List<PreviewWithScoreResponse> search(
        EventFilter eventFilter, 
        UUID requestingUser, 
        int pageSize,
        List<UUID> blacklist
    );
}
