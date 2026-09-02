package com.ambrosia.community_service.community.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ambrosia.community_service.community.model.dto.request.CommunityEventFilter;
import com.ambrosia.community_service.community.model.dto.response.CommunityPreview;
import com.ambrosia.community_service.community.model.entity.Community;
import com.ambrosia.community_service.community.repository.CommunitySearchRepository;
import com.ambrosia.community_service.community.service.CommunitySearchService;
import com.ambrosia.community_service.core.CommunityIndexService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DatabaseCommunityIndexService implements CommunityIndexService, CommunitySearchService{
    private final CommunitySearchRepository communitySearchRepository;

    @Override
    public void index(Community t) {}

    @Override
    public void reIndex(Community t) {}

    @Override
    public void removeFromIndex(Long id) {}

    @Override
    public List<CommunityPreview> search(CommunityEventFilter communityEventFilter, int pageSize) {
        return communitySearchRepository.search(communityEventFilter, pageSize);
    }
}
