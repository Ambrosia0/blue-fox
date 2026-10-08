package com.ambrosia.content_service.search.infrastructure.elastic;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.ambrosia.content_service.post.infrastructure.entity.PostElastic;
import com.ambrosia.content_service.search.infrastructure.elastic.custom.ElasticPostCustomRepository;


public interface ElasticPostRepository extends 
    ElasticsearchRepository<PostElastic, String>, 
    ElasticPostCustomRepository{}
