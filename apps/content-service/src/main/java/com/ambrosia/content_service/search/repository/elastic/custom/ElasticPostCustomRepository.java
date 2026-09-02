package com.ambrosia.content_service.search.repository.elastic.custom;

import java.util.Map.Entry;

public interface ElasticPostCustomRepository {
    void incrementLikeCount(Iterable<Entry<Long, Long>> likes);
}
