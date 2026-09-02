package com.ambrosia.community_service.community.repository.elastic;

import java.util.List;

import com.ambrosia.community_service.community.model.dto.request.CommunityEventFilter;
import com.ambrosia.community_service.community.model.dto.response.CommunityPreview;

public interface ElasticCommunitySearchRepository {
    List<CommunityPreview> search(CommunityEventFilter communityEventFilter, int pageSize);
}
