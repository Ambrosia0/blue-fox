package com.ambrosia.content_service.search.repository.elastic;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.ambrosia.content_service.search.model.entity.elastic.PostElastic;
import com.ambrosia.content_service.search.repository.elastic.custom.ElasticPostCustomRepository;


public interface ElasticPostRepository extends 
    ElasticsearchRepository<PostElastic, String>, 
    ElasticPostCustomRepository{}
