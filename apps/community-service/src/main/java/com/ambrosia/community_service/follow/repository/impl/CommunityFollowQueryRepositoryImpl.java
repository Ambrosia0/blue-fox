package com.ambrosia.community_service.follow.repository.impl;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.ambrosia.community_service.follow.model.dto.response.CommunityFollowResponse;
import com.ambrosia.community_service.follow.repository.CommunityFollowQueryRepository;
import com.ambrosia.community_service.follow.repository.extractor.CommunityFollowResponseRowMapper;

import lombok.RequiredArgsConstructor;

@Repository 
@RequiredArgsConstructor 
public class CommunityFollowQueryRepositoryImpl implements CommunityFollowQueryRepository{
    private final JdbcClient jdbcClient;

    private final CommunityFollowResponseRowMapper communityFollowResponseRowMapper;
    
    @Override
    public Slice<CommunityFollowResponse> findByUserId(UUID userId, Pageable pageable) {
        var sql = """
        SELECT
            c.id,
            c.slug,
            c.displayed_name,
            c.avatar_id,
            c.is_private,
            c.tags,
            c.created_at,
            c.follow_count,
            cf.followed_at 
        FROM community_follow cf
        JOIN community c ON c.id = cf.community_id
        WHERE user_id = :userId
        ORDER BY followed_at
        LIMIT :pageSize
        OFFSET :offset
        """;
        var res = jdbcClient
            .sql(sql)
            .param("userId", userId)
            .param("pageSize", pageable.getPageSize()+1)
            .param("offset", pageable.getOffset())
            .query(communityFollowResponseRowMapper)
            .list();
        var hasNext = res.size() > pageable.getPageSize();
        if(hasNext)
            res.remove(res.size());
        return new SliceImpl<>(res, pageable, hasNext);
    }
}
