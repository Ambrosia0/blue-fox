package com.ambrosia.community_service.community.repository.elastic.custom.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Repository;

import com.ambrosia.community_service.community.repository.elastic.custom.CustomElasticCommunityRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Profile("!es-disabled")
@Repository
public class CustomElasticCommunityRepositoryImpl implements CustomElasticCommunityRepository{
    private final ElasticsearchTemplate elasticsearchTemplate;
    
    @Override
    public void updateAvatar(Long communityId, String avatarId) {
        var query = UpdateQuery.builder(Long.toString(communityId))
            .withDocument(
                Document.create()
                    .append("avatarId", avatarId)
            )
            .build();
        elasticsearchTemplate.update(query, IndexCoordinates.of("community"));
    }
}
