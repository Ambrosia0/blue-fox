
package com.ambrosia.community_service.community.infrastructure.elastic;

import java.util.List;

import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.ambrosia.community_service.community.api.dto.request.CommunityEventFilter;
import com.ambrosia.community_service.community.application.CommunityIndexService;
import com.ambrosia.community_service.community.application.query.CommunitySearchService;
import com.ambrosia.community_service.community.application.query.model.CommunityScoredPreview;
import com.ambrosia.community_service.community.domain.entity.Community;
import com.ambrosia.community_service.community.domain.entity.elastic.ElasticCommunity;
import com.ambrosia.outbox.elastic.SearchIndexOutboxService;

import lombok.RequiredArgsConstructor;

@Profile({"!es-disabled"})
@Primary
@RequiredArgsConstructor
@Service
public class ElasticCommunityIndexService implements CommunityIndexService, CommunitySearchService {
    private final ElasticCommunitySearchRepository elasticCommunitySearchRepository;
    
    private final SearchIndexOutboxService searchIndexOutboxService;

    @Override
    public void index(Community community) {
        searchIndexOutboxService.put(convert(community)
            .isNew(true)
            .build()
        );
    }

    @Override
    public void reIndex(Community community) {
        searchIndexOutboxService.put(convert(community)
            .isNew(false)
            .build()
        );
    }

    @Override
    public void removeFromIndex(@NonNull Long id, Long version) {
        searchIndexOutboxService.put(ElasticCommunity.builder()
            .id(id.toString())
            .version(version)
            .build()
        );
    }

    @Override
    public List<CommunityScoredPreview> search(CommunityEventFilter communityEventFilter, int pageSize) {
        return elasticCommunitySearchRepository.search(communityEventFilter, pageSize);
    }

    private ElasticCommunity.ElasticCommunityBuilder convert(Community community){
        return ElasticCommunity.builder()
            .id(community.getId().toString())
            .displayedName(community.getDisplayedName())
            .slug(community.getSlug())
            .avatarId(community.getAvatarId())
            .followCount(community.getFollowCount())
            .createdAt(community.getCreatedAt())
            .tags(community.getTags() != null?
                community.getTags().toArray(String[]::new):
                null
            )
            .version(community.getVersion());
    }
}
