package com.ambrosia.community_service.community.infrastructure.elastic;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.ambrosia.community_service.community.domain.entity.elastic.ElasticCommunity;
import com.ambrosia.outbox.elastic.ElasticsearchOutboxHandler;
import com.ambrosia.outbox.entity.SearchIndexOutbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Component
@Slf4j
@Profile("!es-disabled")
@RequiredArgsConstructor
public class ElasticsearchOutboxHandlerImpl implements ElasticsearchOutboxHandler<ElasticCommunity>{
    private final ObjectMapper objectMapper;

    private final ElasticCommunityRepository elasticCommunityRepository;

    private String ENTITY_TYPE = "community";

    @Override
    public SearchIndexOutbox convert(ElasticCommunity source) {
        Assert.notNull(source, "Object must not be null!");
        var casted = (ElasticCommunity) source;
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
    public Class<ElasticCommunity> getSourceType() {
        return ElasticCommunity.class;
    }

    @Override
    public void process(List<String> entities) {
        var partitioned = entities
            .stream()
            .map(t -> objectMapper.readValue(t, ElasticCommunity.class))
            .collect(Collectors.partitioningBy(t -> t.getSlug() == null));
        
        try {
            var toDelete = partitioned.get(true);
            if(!toDelete.isEmpty())
                elasticCommunityRepository.deleteAll(toDelete);
        } catch (OptimisticLockingFailureException e) {
            // TODO: handle exception
        }
        
        try {
            var toUpsert = partitioned.get(false);
            if(!toUpsert.isEmpty())
                elasticCommunityRepository.saveAll(toUpsert);
        } catch (OptimisticLockingFailureException e) {
            // TODO: handle exception
        }
    }
}
