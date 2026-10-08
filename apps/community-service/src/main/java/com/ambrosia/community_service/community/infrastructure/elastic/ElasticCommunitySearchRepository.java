package com.ambrosia.community_service.community.infrastructure.elastic;

import java.util.List;

import com.ambrosia.community_service.community.api.dto.request.CommunityEventFilter;
import com.ambrosia.community_service.community.application.query.model.CommunityScoredPreview;

public interface ElasticCommunitySearchRepository {
    List<CommunityScoredPreview> search(CommunityEventFilter communityEventFilter, int pageSize);
}
