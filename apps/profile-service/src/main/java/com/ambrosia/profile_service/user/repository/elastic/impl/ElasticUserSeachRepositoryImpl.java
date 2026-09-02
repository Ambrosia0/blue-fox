package com.ambrosia.profile_service.user.repository.elastic.impl;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Repository;

import com.ambrosia.profile_service.user.model.dto.response.UserSearch;
import com.ambrosia.profile_service.user.repository.elastic.ElasticUserSearchRepository;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.FieldValueFactorModifier;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionBoostMode;
import lombok.RequiredArgsConstructor;

@Profile("!es-disabled")
@RequiredArgsConstructor
@Repository
public class ElasticUserSeachRepositoryImpl implements ElasticUserSearchRepository{
    private final ElasticsearchOperations elasticsearchOperations;

    private List<String> weightenedSearchFields = List.of("username^2", "firstName^1", "lastName^1");
    
    @Override
    public List<UserSearch> search(String searchString, int pageSize) {
        var query = NativeQuery.builder()
            .withQuery(q -> q
                .functionScore(fs -> fs
                    .query(fsq -> fsq
                        .multiMatch(mm -> mm
                            .query(searchString)
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
            .withMaxResults(pageSize)
            .withSort(s -> s.score(ss -> ss.order(SortOrder.Desc)))
            .build();

        return elasticsearchOperations.search(query, UserSearch.class, IndexCoordinates.of("user"))
            .stream()
            .map(SearchHit::getContent)
            .toList();
    }
}
