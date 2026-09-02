package com.ambrosia.community_service.follow.repository.custom.impl;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.ambrosia.community_service.follow.model.entity.CommunityFollowRequest;
import com.ambrosia.community_service.follow.repository.custom.CustomCommunityFollowRequestRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class CustomCommunityFollowRequestRepositoryImpl implements CustomCommunityFollowRequestRepository{
    private final JdbcClient jdbcClient;
    
    @Override
    public List<CommunityFollowRequest> findRequests(long communityId, Pageable pageable) {
        var sql = """
        SELECT * FROM community_follow_request 
        WHERE community_id = :communityId
        ORDER BY created_at ASC
        LIMIT :limit
        OFFSET :offset
        """;
        return jdbcClient
            .sql(sql)
            .param("communityId", communityId)
            .param("limit", pageable.getPageSize())
            .param("offset", pageable.getOffset())
            .query(CommunityFollowRequest.class)
            .list();
    }
}
