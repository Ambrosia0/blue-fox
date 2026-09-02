package com.ambrosia.content_service.search.repository.elastic.custom;

import java.util.List;
import java.util.UUID;

import org.springframework.data.elasticsearch.core.SearchHits;

import com.ambrosia.content_service.search.model.dto.EventFilter;
import com.ambrosia.content_service.search.model.entity.elastic.PostElastic;

public interface ElasticPostSearchRepository {
    SearchHits<PostElastic> search(
        EventFilter eventFilter, 
        int pageSize,
        List<UUID> userFollows,
        List<Long> communityFollows,
        List<UUID> blacklists
    );
}
