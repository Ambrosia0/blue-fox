package com.ambrosia.community_service.community.service;

import java.util.List;

import com.ambrosia.community_service.community.model.dto.request.CommunityEventFilter;
import com.ambrosia.community_service.community.model.dto.response.CommunityPreview;

public interface CommunitySearchService {
    List<CommunityPreview> search(CommunityEventFilter communityEventFilter, int pageSize);
}
