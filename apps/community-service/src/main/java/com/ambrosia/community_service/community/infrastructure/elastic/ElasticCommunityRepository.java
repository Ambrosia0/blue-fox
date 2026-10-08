package com.ambrosia.community_service.community.infrastructure.elastic;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.ambrosia.community_service.community.domain.entity.elastic.ElasticCommunity;

public interface ElasticCommunityRepository extends ElasticsearchRepository<ElasticCommunity, String>{}
