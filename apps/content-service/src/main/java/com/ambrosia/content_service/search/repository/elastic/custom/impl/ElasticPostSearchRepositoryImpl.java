package com.ambrosia.content_service.search.repository.elastic.custom.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.stereotype.Repository;

import com.ambrosia.content_service.search.model.dto.EventFilter;
import com.ambrosia.content_service.search.model.dto.EventFilter.SortField;
import com.ambrosia.content_service.search.model.entity.elastic.PostElastic;
import com.ambrosia.content_service.search.repository.elastic.custom.ElasticPostSearchRepository;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.FieldValueFactorModifier;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionBoostMode;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionScoreMode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Profile("!es-disabled")
@Repository
public class ElasticPostSearchRepositoryImpl implements ElasticPostSearchRepository{
    private final ElasticsearchOperations elasticsearchOperations;

    private final float minimalRank = 0.05f;
    private List<String> weightenedSeachFields = List.of("content^1", "title^3", "tags^4");

    public SearchHits<PostElastic> search(
            EventFilter eventFilter, 
            int pageSize, 
            List<UUID> userFollows,
            List<Long> communityFollows,
            List<UUID> blacklist
        ) {
        return switch (eventFilter.searchType()) {
            case BEST -> findBest(eventFilter, pageSize);
            case POPULAR -> findPopular(eventFilter, pageSize);
            case RELEVANCY -> findRelevant(eventFilter, pageSize);
            case LATEST -> findLatest(eventFilter, pageSize);
            case PERSONALIZED -> findPersonalized(eventFilter, pageSize, userFollows, communityFollows, blacklist);
        };
    }

    private SearchHits<PostElastic> findPersonalized(
            EventFilter eventFilter, 
            int pageSize, 
            List<UUID> userFollows, 
            List<Long> communityFollows, 
            List<UUID> blacklist){
        var bool = visibilityFilter();
        personalFilter(eventFilter, bool, userFollows, communityFollows, blacklist);
        if(eventFilter.sortField() == SortField.SCORE)
            popularityFilter(eventFilter, bool);
            
        var query = NativeQuery.builder()
            .withSourceFilter(new FetchSourceFilter(false, null, null))
            .withQuery(q -> q.bool(bool.build()))
            .withTrackScores(true)
            .withMaxResults(pageSize);

        if(eventFilter.sortField() == SortField.SCORE){
            query
                .withSort(s -> s.field(fs -> fs.field("_score").order(SortOrder.Desc)))
                .withSort(s -> s.field(fs -> fs.field("id").order(SortOrder.Desc)));
            if(eventFilter.lastScore() != null && eventFilter.lastSeenId() != null)
                query.withSearchAfter(List.of(eventFilter.lastScore(), eventFilter.lastSeenId()));
        } else{
            query
                .withSort(s -> s.field(fs -> fs.field("publishedAt").order(SortOrder.Desc)))
                .withSort(s -> s.field(fs -> fs.field("id").order(SortOrder.Desc)));
            if(eventFilter.lastSeenInstant() != null && eventFilter.lastSeenId() != null)
                query.withSearchAfter(List.of(eventFilter.lastSeenInstant(), eventFilter.lastSeenId()));
        }
        return elasticsearchOperations.search(query.build(), PostElastic.class);
    }

    private BoolQuery.Builder personalFilter(
            EventFilter eventFilter, 
            BoolQuery.Builder builder, 
            List<UUID> userFollows, 
            List<Long> communityFollows,
            List<UUID> blacklist
        ){
        builder
            .should(f -> f
                .terms(t -> t
                    .field("communityId")
                    .terms(tt -> tt
                        .value(communityFollows.stream().map(FieldValue::of).toList())
                    )
                )
            )
            .should(f -> f
                .terms(t -> t
                    .field("authorId")
                    .terms(tt -> tt
                        .value(userFollows.stream().map(FieldValue::of).toList())
                    )
                )
            );
        if(blacklist != null && !blacklist.isEmpty())
            builder.mustNot(f -> f
                .terms(t -> t
                    .field("authorId")
                    .terms(tt -> tt
                        .value(blacklist.stream().map(FieldValue::of).toList())
                    )
                )
            );
        return builder;
    }

    private SearchHits<PostElastic> findRelevant(EventFilter eventFilter, int pageSize){
        var bool = visibilityFilter();
        tagFilter(eventFilter, bool);
        authorFilter(eventFilter, bool);
        communityFilter(eventFilter, bool);
        searchStringFilter(eventFilter, bool);
        var query = NativeQuery.builder()
            .withSourceFilter(new FetchSourceFilter(false, null, null))
            .withQuery(q -> q.bool(bool.build()))
            .withMaxResults(pageSize)
            .withMinScore(minimalRank)
            .withTrackScores(true)
            .withSort(s -> s.field(f -> f.field("_score").order(toSortOrder(eventFilter.direction()))))
            .withSort(s -> s.field(f -> f.field("id").order(toSortOrder(eventFilter.direction()))));
        if(eventFilter.lastScore() != null && eventFilter.lastSeenInstant() != null)
            query.withSearchAfter(List.of(eventFilter.lastScore(), eventFilter.lastSeenInstant()));
        return elasticsearchOperations.search(query.build(), PostElastic.class);
    }

    private SearchHits<PostElastic> findBest(EventFilter eventFilter, int pageSize){
        var bool = visibilityFilter();
        tagFilter(eventFilter, bool);
        authorFilter(eventFilter, bool);
        communityFilter(eventFilter, bool);
        searchStringFilter(eventFilter, bool);
        var query = NativeQuery.builder()
            .withSourceFilter(new FetchSourceFilter(false, null, null))
            .withQuery(q -> q.bool(bool.build()))
            .withMaxResults(pageSize)
            .withTrackScores(true)
            .withSort(s -> s.field(f -> f.field("likeCount").order(toSortOrder(eventFilter.direction()))))
            .withSort(s -> s.field(f -> f.field("id").order(toSortOrder(eventFilter.direction()))));
        if(eventFilter.lastSeenLikeCount() != null && eventFilter.lastScore() != null)
            query.withSearchAfter(List.of(eventFilter.lastSeenLikeCount(), eventFilter.lastScore()));
        return elasticsearchOperations.search(query.build(), PostElastic.class);
    }
    
    private SearchHits<PostElastic> findPopular(EventFilter eventFilter, int pageSize){
        var bool = visibilityFilter();
        tagFilter(eventFilter, bool);
        authorFilter(eventFilter, bool);
        communityFilter(eventFilter, bool);
        searchStringFilter(eventFilter, bool);
        popularityFilter(eventFilter, bool);
        var query = NativeQuery.builder()
            .withSourceFilter(new FetchSourceFilter(false, null, null))
            .withQuery(q -> q.bool(bool.build()))
            .withMaxResults(pageSize)
            .withTrackScores(true)
            .withSort(s -> s.field(f -> f.field("_score").order(toSortOrder(eventFilter.direction()))))
            .withSort(s -> s.field(f -> f.field("id").order(toSortOrder(eventFilter.direction()))));
        if(eventFilter.lastScore() != null && eventFilter.lastSeenInstant() != null)
            query.withSearchAfter(List.of(eventFilter.lastScore(), eventFilter.lastSeenInstant()));
        return elasticsearchOperations.search(query.build(), PostElastic.class);
    }

    private SearchHits<PostElastic> findLatest(EventFilter eventFilter, int pageSize){
        var bool = visibilityFilter();
        tagFilter(eventFilter, bool);
        authorFilter(eventFilter, bool);
        communityFilter(eventFilter, bool);
        searchStringFilter(eventFilter, bool);
        var query = NativeQuery.builder()
            .withSourceFilter(new FetchSourceFilter(false, null, null))
            .withQuery(q -> q.bool(bool.build()))
            .withMaxResults(pageSize)
            .withTrackScores(true)
            .withSort(s -> s.field(f -> f.field("publishedAt").order(toSortOrder(eventFilter.direction()))))
            .withSort(s -> s.field(f -> f.field("id").order(toSortOrder(eventFilter.direction()))));
        if(eventFilter.lastSeenInstant() != null && eventFilter.lastSeenId() != null)
            query.withSearchAfter(List.of(eventFilter.lastSeenInstant(), eventFilter.lastSeenId()));
        return elasticsearchOperations.search(query.build(), PostElastic.class);
    }

    private BoolQuery.Builder tagFilter(EventFilter eventFilter, BoolQuery.Builder builder){
        if(eventFilter.tags() == null)
            return builder;
        return builder.must(m -> m
                .terms(mt -> mt
                    .field("tags.raw")
                    .terms(mtt -> mtt.value(eventFilter.tags().stream().map(FieldValue::of).toList()))
                )
            );
    }

    private BoolQuery.Builder authorFilter(EventFilter eventFilter, BoolQuery.Builder builder){
        if(eventFilter.authorId() == null)
            return builder;
        return builder.must(m -> m
                .term(mt -> mt
                    .field("authorId")
                    .value(eventFilter.authorId().toString())
                )
        );
    }

    private BoolQuery.Builder communityFilter(EventFilter eventFilter, BoolQuery.Builder builder){
        if(eventFilter.communityId() == null)
            return builder.filter(f -> f.bool(b -> b
                .should(s -> s.term(fm -> fm.field("community.isPrivate").value(false)))
                .should(s -> s.bool(bb -> bb.mustNot(mn -> mn.exists(e -> e.field("community")))))
                .minimumShouldMatch("1")
            ));
        return builder.must(m -> m
            .term(mt -> mt
                .field("communityId")
                .value(eventFilter.communityId())
            )
        );
    }

    private BoolQuery.Builder searchStringFilter(EventFilter eventFilter, BoolQuery.Builder builder){
        if(eventFilter.searchString() == null)
            return builder;
        return builder.must(m -> m
            .multiMatch(mm -> mm
                .query(eventFilter.searchString())
                .fields(weightenedSeachFields)
            )
        );
    }

    private BoolQuery.Builder popularityFilter(EventFilter eventFilter, BoolQuery.Builder builder){
        return builder.must(m -> m
            .functionScore(fs -> fs
                .query(fq -> fq
                    .bool(bl -> bl
                        .filter(f -> f
                            .range(r -> r
                                .date(d -> d
                                    .field("publishedAt")
                                    .gte(Instant.now().minus(7, ChronoUnit.DAYS).toString())
                                )
                            )
                        )
                    )
                )
                .functions(f -> f
                    .fieldValueFactor(fvf -> fvf
                        .field("likeCount").modifier(FieldValueFactorModifier.Log1p).factor(1.0).missing(0.0)
                    )
                )
                .functions(f -> f
                    .gauss(g -> g
                        .date(bg -> bg
                            .field("publishedAt")
                            .placement(pl -> pl
                                .decay(0.5)
                                .origin("now")
                                .scale(Time.of(tb -> tb.time("7d")))
                            )
                        )
                    )
                )
                .scoreMode(FunctionScoreMode.Sum)
                .boostMode(FunctionBoostMode.Replace)
            )
        );
    }

    private BoolQuery.Builder visibilityFilter(){
        return new BoolQuery.Builder()
            .filter(f -> f.term(ft -> ft.field("visible").value(true)));
    }

    private SortOrder toSortOrder(Direction direction){
        return direction == Direction.DESC?
            SortOrder.Desc:
            SortOrder.Asc;
    }
}
