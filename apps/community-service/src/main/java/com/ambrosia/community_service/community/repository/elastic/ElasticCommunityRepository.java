package com.ambrosia.community_service.community.repository.elastic;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.ambrosia.community_service.community.model.entity.elastic.ElasticCommunity;
import com.ambrosia.community_service.community.repository.elastic.custom.CustomElasticCommunityRepository;


public interface ElasticCommunityRepository extends 
    ElasticsearchRepository<ElasticCommunity, String>,
    CustomElasticCommunityRepository{}
