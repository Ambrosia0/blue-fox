package com.ambrosia.profile_service.user.repository.elastic;

import java.util.UUID;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.ambrosia.profile_service.user.model.entity.elastic.ElasticUser;
import com.ambrosia.profile_service.user.repository.elastic.custom.CustomElasticUserRepository;

public interface ElasticUserRepository extends 
    ElasticsearchRepository<ElasticUser, UUID>,
    CustomElasticUserRepository{}
