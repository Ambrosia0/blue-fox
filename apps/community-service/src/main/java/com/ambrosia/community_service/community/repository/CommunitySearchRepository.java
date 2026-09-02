package com.ambrosia.community_service.community.repository;

import java.util.List;

import com.ambrosia.community_service.community.model.dto.request.CommunityEventFilter;
import com.ambrosia.community_service.community.model.dto.response.CommunityPreview;

public interface CommunitySearchRepository {
    List<CommunityPreview> search(CommunityEventFilter eventFilter, int pageSize);
}
