package com.ambrosia.content_service.search.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ambrosia.content_service.follow.service.FollowSnapshotProvider;
import com.ambrosia.content_service.post.model.dto.response.PreviewWithScoreResponse;
import com.ambrosia.content_service.post.service.PostViewQueryService;
import com.ambrosia.content_service.search.model.dto.EventFilter;
import com.ambrosia.content_service.search.model.dto.PostIndex;
import com.ambrosia.content_service.search.model.entity.elastic.PostElastic;
import com.ambrosia.content_service.search.repository.elastic.custom.ElasticPostSearchRepository;
import com.ambrosia.content_service.search.service.PostIndexService;
import com.ambrosia.content_service.search.service.PostSearchService;
import com.ambrosia.content_service.search.service.mappers.PostIndexMapper;
import com.ambrosia.outbox.elastic.SearchIndexOutboxService;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;

@Primary
@Profile({"!es-disabled"})
@AllArgsConstructor
@Service
public class ElasticPostIndexServiceImpl implements PostIndexService, PostSearchService {
    private final ElasticPostSearchRepository elasticPostSearchRepository;

    private final FollowSnapshotProvider followSnapshotProvider;

    private final PostViewQueryService postViewQueryService;

    private final SearchIndexOutboxService searchIndexOutboxService;

    private final PostIndexMapper postIndexMapper;

    @Override
    public void index(PostIndex postIndex) {
        searchIndexOutboxService.put(
            postIndexMapper.toEntity(postIndex)
        );
    }

    @Override
    public void reIndex(PostIndex postIndex) {
        searchIndexOutboxService.put(
            postIndexMapper.toEntity(postIndex)
        );
    }

    @Override
    public void deleteFromIndex(Long id, Long version) {
        Assert.notNull(id, "Id must not be null!");
        Assert.notNull(version, "Version must no be null!");
        searchIndexOutboxService.put(PostElastic.builder()
            .esid(id.toString())
            .version(version)
            .isNew(false)
            .build()
        );
    }

    @Override
    public List<PreviewWithScoreResponse> search(
            EventFilter eventFilter, 
            UUID requestingUser, 
            int pageSize, 
            @Nullable List<UUID> blacklist) {
        var follows = requestingUser != null? followSnapshotProvider.get(requestingUser): null;

        var hits = elasticPostSearchRepository.search(
            eventFilter, 
            pageSize, 
            requestingUser != null? follows.followedUsers(): null, 
            requestingUser != null? follows.followedCommunities(): null,
            blacklist
        );
        if(hits == null || hits.isEmpty())
            return List.of();

        var hitsMap = hits.stream()
            .collect(Collectors.toMap(k -> Long.parseLong(k.getId()), v -> v.getScore()));

        var previews = postViewQueryService.getPostPreviews(
                hitsMap.keySet(), 
                requestingUser
        );
        return previews.stream()
            .map(preview -> new PreviewWithScoreResponse(
                    preview,
                    hitsMap.get(preview.id())
                )
            )
            .toList();
    }
}
