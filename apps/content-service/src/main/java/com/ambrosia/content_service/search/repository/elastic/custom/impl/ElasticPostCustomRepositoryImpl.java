package com.ambrosia.content_service.search.repository.elastic.custom.impl;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Repository;

import com.ambrosia.content_service.search.model.entity.elastic.PostElastic;
import com.ambrosia.content_service.search.repository.elastic.custom.ElasticPostCustomRepository;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Profile("!es-disabled")
@Repository
public class ElasticPostCustomRepositoryImpl implements ElasticPostCustomRepository{
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public void incrementLikeCount(Iterable<Entry<Long, Long>> likes) {
        var updateList = new ArrayList<UpdateQuery>();
        likes.forEach(entry -> updateList.add(UpdateQuery
            .builder(entry.getKey().toString())
            .withScript("ctx._source.likeCounter += params.incr")
            .withParams(Map.of("incr", entry.getValue().intValue()))
            .build()
        ));
        elasticsearchOperations.bulkUpdate(updateList, PostElastic.class);
    }
}
