package com.ambrosia.community_service.community.application.query;

import java.util.List;

import com.ambrosia.community_service.community.api.dto.request.CommunityEventFilter;
import com.ambrosia.community_service.community.application.query.model.CommunityScoredPreview;

public interface CommunitySearchRepository {
    List<CommunityScoredPreview> search(CommunityEventFilter eventFilter, int pageSize);
}
