package com.ambrosia.content_service.search.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ambrosia.content_service.community.service.CommunitySearchService;
import com.ambrosia.content_service.exception.api.CommunityDoesntExistException;
import com.ambrosia.content_service.follow.service.FollowSnapshotProvider;
import com.ambrosia.content_service.post.model.dto.response.PostViewResponse;
import com.ambrosia.content_service.post.model.dto.response.PreviewWithScoreResponse;
import com.ambrosia.content_service.post.model.entity.Post;
import com.ambrosia.content_service.post.service.PostQueryService;
import com.ambrosia.content_service.post.utils.TextExtractor;
import com.ambrosia.content_service.search.model.dto.EventFilter;
import com.ambrosia.content_service.search.model.entity.elastic.CommunityElastic;
import com.ambrosia.content_service.search.model.entity.elastic.PostElastic;
import com.ambrosia.content_service.search.repository.elastic.custom.ElasticPostSearchRepository;
import com.ambrosia.content_service.search.service.PostIndexService;
import com.ambrosia.content_service.search.service.PostSearchService;
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

    private final PostQueryService postQueryService;

    private final TextExtractor textExtractor;

    private final CommunitySearchService communitySearchService;

    private final SearchIndexOutboxService searchIndexOutboxService;

    @Override
    public void index(Post post) {
        var builder = convert(post)
            .isNew(true);
        if(post.getCommunityId() != null){
            var community = communitySearchService.findById(post.getCommunityId())
                .orElseThrow(() -> new CommunityDoesntExistException());
            builder.community(
                CommunityElastic.create(community.getId(), community.isPrivate())
            );
        }
        searchIndexOutboxService.put(builder.build());
    }

    @Override
    public void reIndex(Post post) {
        var builder = convert(post)
            .isNew(false);
        if(post.getCommunityId() != null){
            var community = communitySearchService.findById(post.getCommunityId())
                .orElseThrow(() -> new CommunityDoesntExistException());
            builder.community(
                CommunityElastic.create(community.getId(), community.isPrivate())
            );
        }
        searchIndexOutboxService.put(builder.build());
    }

    @Override
    public void deleteFromIndex(Long id) {
        Assert.notNull(id, "Id must not be null!");
        searchIndexOutboxService.put(PostElastic.builder()
            .esid(id.toString())
            .build()
        );
    }

    // to optimize
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
        var ids = hits.stream()
                .map(SearchHit::getId)
                .map(Long::parseLong)
                .toList();
        var previews = (
            requestingUser != null?
                postQueryService.getPostPreviewsByIdsWithLike(ids, requestingUser):
                postQueryService.getPostPreviewsByIds(ids))
            .stream()
            .collect(Collectors.toMap(PostViewResponse::id, v -> v));
        return hits.stream().map(hit ->
            new PreviewWithScoreResponse(
                previews.get(Long.parseLong(hit.getId())),
                hit.getScore()
            )
        ).toList();
    }

    private PostElastic.PostElasticBuilder convert(Post post){
        return PostElastic.builder()
                .id(post.getId())
                .esid(post.getId().toString())
                .content(textExtractor.extractText(post.getContent()))   
                .tags(post.getTags())
                .authorId(post.getAuthorId().toString())
                .likeCount(post.getLikeCount())
                .title(post.getTitle())
                .publishedAt(post.getPublishedAt())
                .version(post.getVersion())
                .visible(post.isVisible());
    }
}
