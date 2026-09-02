package com.ambrosia.community_service.community.repository.elastic.impl;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Repository;

import com.ambrosia.community_service.community.model.dto.request.CommunityEventFilter;
import com.ambrosia.community_service.community.model.dto.response.CommunityPreview;
import com.ambrosia.community_service.community.model.entity.elastic.ElasticCommunity;
import com.ambrosia.community_service.community.repository.elastic.ElasticCommunitySearchRepository;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.FieldValueFactorModifier;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionBoostMode;
import lombok.RequiredArgsConstructor;

@Profile("!es-disabled")
@RequiredArgsConstructor
@Repository
public class ElasticCommunitySearchRepositoryImpl implements ElasticCommunitySearchRepository{
    private final ElasticsearchOperations elasticsearchOperations;

    private List<String> weightenedSearchFields = List.of("tags^1", "displayedName^2", "slug^3");

    
    @Override
    public List<CommunityPreview> search(CommunityEventFilter eventFilter, int pageSize) {
        if(eventFilter.searchString() != null)
            return findRelevant(eventFilter, pageSize);
        else
            return findLatest(eventFilter, pageSize);
    }

    private List<CommunityPreview> findLatest(CommunityEventFilter eventFilter, int pageSize) {
        var query = NativeQuery.builder()
            .withQuery(q -> eventFilter.tags() != null?
                q.terms(qt -> qt.field("tags.raw").terms(qtt -> qtt.value(eventFilter.tags().stream().map(FieldValue::of).toList()))):
                q.matchAll(ma -> ma)
            )
            .withMaxResults(pageSize)
            .withTrackScores(true)
            .withSort(s -> s.field(ss -> ss.field("_score").order(toSortOrder(eventFilter.direction()))))
            .withSort(s -> s.field(ss -> ss.field("createdAt").order(toSortOrder(eventFilter.direction()))));
        if(eventFilter.lastSeenScore() != null && eventFilter.lastSeenInstant() != null)
            query.withSearchAfter(List.of(eventFilter.lastSeenScore(), eventFilter.lastSeenInstant()));
        return elasticsearchOperations.search(query.build(), ElasticCommunity.class)
            .map(CommunityPreview::from)
            .toList();
    }

    private List<CommunityPreview> findRelevant(CommunityEventFilter eventFilter, int pageSize) {
        var bool = new BoolQuery.Builder();
        if(eventFilter.tags() != null)
            bool.filter(f -> f.terms(t -> t.field("tags.raw").terms(tt -> tt.value(eventFilter.tags().stream().map(FieldValue::of).toList()))));
        var query = NativeQuery.builder()
            .withQuery(q -> q.bool(bool
                .must(m -> m
                    .functionScore(fs -> fs
                       .query(fsq -> fsq
                           .multiMatch(mm -> mm
                               .query(eventFilter.searchString())
                               .fields(weightenedSearchFields)
                           )
                       )
                       .functions(f -> f
                           .fieldValueFactor(fvf -> fvf
                               .field("followCount")
                               .factor(0.1)
                               .modifier(FieldValueFactorModifier.Log1p)
                           )
                       )
                       .boostMode(FunctionBoostMode.Sum)
                    )
                )
                .build())
            )
            .withMaxResults(pageSize)
            .withTrackScores(true)
            .withSort(s -> s.field(ss -> ss.field("_score").order(toSortOrder(eventFilter.direction()))))
            .withSort(s -> s.field(ss -> ss.field("createdAt").order(toSortOrder(eventFilter.direction()))));
        if(eventFilter.lastSeenScore() != null && eventFilter.lastSeenInstant() != null)
            query.withSearchAfter(List.of(eventFilter.lastSeenScore(), eventFilter.lastSeenInstant()));
        return elasticsearchOperations.search(query.build(), ElasticCommunity.class)
            .map(CommunityPreview::from)
            .toList();
    }

    private SortOrder toSortOrder(Direction direction){
        return direction == Direction.DESC?
            SortOrder.Desc:
            SortOrder.Asc;
    }
}
