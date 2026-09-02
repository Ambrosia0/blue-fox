package com.ambrosia.profile_service.user.service.elastic;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.ambrosia.outbox.elastic.ElasticsearchOutboxHandler;
import com.ambrosia.outbox.entity.SearchIndexOutbox;
import com.ambrosia.profile_service.user.model.entity.elastic.ElasticUser;
import com.ambrosia.profile_service.user.repository.elastic.ElasticUserRepository;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Component
@Profile("!es-disabled")
@RequiredArgsConstructor
public class UserSearchIndexOutboxConverter implements ElasticsearchOutboxHandler<ElasticUser>{
    private final ObjectMapper objectMapper;

    private final ElasticUserRepository elasticUserRepository;

    private String ENTITY_TYPE = "user";

    @Override
    public SearchIndexOutbox convert(Object source) {
        Assert.notNull(source, "Object must not be null!");
        var casted = (ElasticUser) source;
        return SearchIndexOutbox.from(
            casted.getId().toString(), 
            ENTITY_TYPE, 
            objectMapper.writeValueAsString(casted)
        );
    }

    @Override
    public String getName() {
        return ENTITY_TYPE;
    }

    @Override
    public Class<ElasticUser> getSourceType() {
        return ElasticUser.class;
    }

    @Override
    public void process(List<String> entities) {
        var partitioned = entities
            .stream()
            .map(t -> objectMapper.readValue(t, ElasticUser.class))
            .collect(Collectors.partitioningBy(t -> t.getUsername() == null));
        
            var toDelete = partitioned.get(true);
        try {
            if(!toDelete.isEmpty())
                elasticUserRepository.deleteAll(toDelete);

            var toUpsert = partitioned.get(false);
            if(!toUpsert.isEmpty())
                elasticUserRepository.saveAll(toUpsert);
        } catch (OptimisticLockingFailureException e) {
            // TODO: handle exception
        }
    }
}
