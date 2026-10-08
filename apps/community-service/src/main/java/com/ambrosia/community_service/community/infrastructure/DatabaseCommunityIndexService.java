package com.ambrosia.community_service.community.infrastructure;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ambrosia.community_service.community.api.dto.request.CommunityEventFilter;
import com.ambrosia.community_service.community.application.CommunityIndexService;
import com.ambrosia.community_service.community.application.query.CommunitySearchRepository;
import com.ambrosia.community_service.community.application.query.CommunitySearchService;
import com.ambrosia.community_service.community.application.query.model.CommunityScoredPreview;
import com.ambrosia.community_service.community.domain.entity.Community;

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
    public void removeFromIndex(Long id, Long version) {}

    @Override
    public List<CommunityScoredPreview> search(CommunityEventFilter communityEventFilter, int pageSize) {
        return communitySearchRepository.search(communityEventFilter, pageSize);
    }
}
