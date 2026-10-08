package com.ambrosia.community_service.follow.infrastructure.persistence;

import java.util.HashMap;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.ambrosia.community_service.follow.api.dto.request.FollowFilter;
import com.ambrosia.community_service.follow.api.dto.response.CommunityFollowResponse;
import com.ambrosia.community_service.follow.application.query.CommunityFollowQueryRepository;
import com.ambrosia.community_service.follow.infrastructure.persistence.mapper.CommunityFollowResponseRowMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Repository 
public class JdbcCommunityFollowQueryRepository implements CommunityFollowQueryRepository{
    private final JdbcClient jdbcClient;

    private final CommunityFollowResponseRowMapper communityFollowResponseRowMapper;
    
    @Override
    public Slice<CommunityFollowResponse> getFollows(Long communityId, FollowFilter filter, int pageSize) {
        var params = new HashMap<String, Object>(6, 1.0f);
        var builder = new StringBuilder(
            switch(filter.type()){
                case FOLLOWED -> """
                SELECT
                    up.id as user_id,
                    up.username,
                    up.first_name,
                    up.last_name,
                    up.avatar_id,
                    followed_at as created_at,
                    'FOLLOWED' as type
                FROM community_follow
                JOIN user_projection up ON up.id = user_id
                WHERE community_id = :communityId
                """;
                case REQUESTED -> """
                SELECT
                    up.id as user_id,
                    up.username,
                    up.first_name,
                    up.last_name,
                    up.avatar_id,
                    created_at,
                    'REQUESTED' as type
                FROM community_follow_request
                JOIN user_projection up ON up.id = user_id
                WHERE community_id = :communityId 
                """;
                default -> throw new IllegalArgumentException();
            }
        );

        if(filter.cursor() != null && 
                filter.cursor().lastInstant() != null && 
                filter.cursor().lastSeenId() != null
        ){
            if(filter.direction() == Direction.DESC){
                builder.append("AND (created_at, id) < (:lastSeenInstant, :lastSeenId) ");
                builder.append("ORDER BY created_at DESC ");
            }else{
                builder.append("AND (created_at, id) > (:lastSeenInstant, :lastSeenId) ");
                builder.append("ORDER BY created_at ASC ");
            }
            params.put("lastSeenInstant", filter.cursor().lastInstant());
            params.put("lastSeenId", filter.cursor().lastSeenId());
        }
        builder.append("LIMIT :pageSize");
        var res = jdbcClient
            .sql(builder.toString())
            .params(params)
            .param("communityId", communityId)
            .param("pageSize", pageSize + 1)
            .query(communityFollowResponseRowMapper)
            .list();

        boolean hasNext = res.size() > pageSize;
        if(hasNext)
            res.remove(res.size());
        return new SliceImpl<>(res, PageRequest.of(0, pageSize), hasNext);
    }
}
