package com.ambrosia.community_service.follow.infrastructure.persistence;

import java.util.HashMap;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.ambrosia.community_service.follow.api.dto.request.FollowFilter;
import com.ambrosia.community_service.follow.api.dto.response.CommunityFollowUserResponse;
import com.ambrosia.community_service.follow.application.query.CommunityFollowUserQueryRepository;
import com.ambrosia.community_service.follow.infrastructure.persistence.mapper.CommunityFollowUserResponseRowMapper;

import lombok.RequiredArgsConstructor;

@Repository 
@RequiredArgsConstructor 
public class JdbcCommunityFollowUserQueryRepository implements CommunityFollowUserQueryRepository{
    private final JdbcClient jdbcClient;

    private final CommunityFollowUserResponseRowMapper communityFollowUserResponseRowMapper;
    
    @Override
    public Slice<CommunityFollowUserResponse> getUserFollows(UUID userId, FollowFilter filter, int pageSize) {
        var params = new HashMap<String, Object>(6, 1.0f);
        var sql = new StringBuilder("""
        SELECT * FROM(
            SELECT
                c.id,
                c.slug,
                c.displayed_name,
                c.avatar_id,
                c.follow_count,
                c.is_private,
                c.tags,
                cf.followed_at,
                c.created_at,
                'FOLLOWED' as type
            FROM community_follow cf
            JOIN community c ON c.id = cf.community_id
            WHERE cf.user_id = :userId

            UNION ALL

            SELECT
                c.id,
                c.slug,
                c.displayed_name,
                c.avatar_id,
                c.follow_count,
                c.is_private,
                c.tags,
                c.created_at,
                cfr.created_at as followed_at,
                'REQUESTED' as type
            FROM community_follow_request cfr
            JOIN community c ON c.id = cfr.community_id
            WHERE cfr.user_id = :userId
        ) t 
        WHERE 1=1 
        """);

        if(filter.cursor() != null && filter.cursor().lastInstant() != null && filter.cursor().lastSeenId() != null){
            if(filter.direction() == Direction.ASC){
                sql.append("AND (t.created_at, t.id) < (:lastSeenInstant, :lastSeenId) ");
                sql.append("ORDER BY t.created_at ASC ");
            }else{
                sql.append("AND (t.created_at, t.id) > (:lastSeenInstant, :lastSeenId) ");
                sql.append("ORDER BY t.created_at DESC ");
            }
            params.put("lastSeenInstant", filter.cursor().lastInstant());
            params.put("lastSeenId", filter.cursor().lastSeenId());
        }else{
            if(filter.direction() == Direction.ASC){
                sql.append("ORDER BY t.created_at ASC LIMIT :pageSize");
            }else{
                sql.append("ORDER BY t.created_at DESC LIMIT :pageSize");
            }
        }

        var res = jdbcClient
            .sql(sql.toString())
            .params(params)
            .param("userId", userId)
            .param("pageSize", pageSize + 1)
            .query(communityFollowUserResponseRowMapper)
            .list();
        var hasNext = res.size() > pageSize;
        if(hasNext)
            res.remove(res.size());
        return new SliceImpl<>(res, PageRequest.of(0, pageSize), hasNext);
    }
}
