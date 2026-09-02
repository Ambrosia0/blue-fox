package com.ambrosia.profile_service.user.repository.elastic.custom.impl;

import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Repository;

import com.ambrosia.profile_service.user.repository.elastic.custom.CustomElasticUserRepository;

import lombok.RequiredArgsConstructor;

@Profile("!es-disabled")
@RequiredArgsConstructor
@Repository
public class CustomElasticUserRepositoryImpl implements CustomElasticUserRepository{
    private final ElasticsearchTemplate elasticsearchTemplate;
    
    @Override
    public void updateAvatarId(UUID id, String avatarId) {
        var query = UpdateQuery.builder(id.toString())
            .withDocument(
                Document.create()
                    .append("avatarId", avatarId)
            )
            .build();
        elasticsearchTemplate.update(query, IndexCoordinates.of("user"));
    }
}
