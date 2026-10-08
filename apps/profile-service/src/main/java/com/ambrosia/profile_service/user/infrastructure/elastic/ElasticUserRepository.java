package com.ambrosia.profile_service.user.infrastructure.elastic;

import java.util.UUID;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.ambrosia.profile_service.user.infrastructure.elastic.custom.CustomElasticUserRepository;
import com.ambrosia.profile_service.user.infrastructure.entity.ElasticUser;

public interface ElasticUserRepository extends 
    ElasticsearchRepository<ElasticUser, UUID>,
    CustomElasticUserRepository{}
