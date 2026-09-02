package com.ambrosia.content_service.search.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.ambrosia.content_service.search.model.entity.elastic.PostElastic;
import com.ambrosia.content_service.search.repository.elastic.ElasticPostRepository;
import com.ambrosia.outbox.elastic.ElasticsearchOutboxHandler;
import com.ambrosia.outbox.entity.SearchIndexOutbox;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Profile("!es-disabled")
@Component
@RequiredArgsConstructor
public class PostOutboxConverter implements ElasticsearchOutboxHandler<PostElastic>{
    private final ObjectMapper objectMapper;

    private final ElasticPostRepository elasticPostRepository;

    private String ENTITY_TYPE = "post";

    @Override
    public SearchIndexOutbox convert(Object source) {
        Assert.notNull(source, "Object must not be null!");
        var casted = (PostElastic) source;
        return SearchIndexOutbox.from(
            casted.getId(), 
            ENTITY_TYPE, 
            objectMapper.writeValueAsString(casted)
        );
    }

    @Override
    public String getName() {
        return ENTITY_TYPE;
    }

    @Override
    public Class<PostElastic> getSourceType() {
        return PostElastic.class;
    }

    @Override
    public void process(List<String> entities) {
        var partitioned = entities
            .stream()
            .map(t -> objectMapper.readValue(t, PostElastic.class))
            .collect(Collectors.partitioningBy(t -> t.getTitle() == null));
        var toDelete = partitioned.get(true);
        try {
            if(!toDelete.isEmpty())
                elasticPostRepository.deleteAll(toDelete);

            var toUpsert = partitioned.get(false);
            if(!toUpsert.isEmpty())
                elasticPostRepository.saveAll(toUpsert);
        } catch (OptimisticLockingFailureException e) {
            // TODO: handle exception
        }
    }
}
