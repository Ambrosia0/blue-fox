package com.ambrosia.content_service.follow.infrastructure.persistence;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.ambrosia.content_service.follow.api.dto.UserFollowResponse;
import com.ambrosia.content_service.follow.application.query.UserFollowQueryRepository;
import com.ambrosia.content_service.follow.infrastructure.persistence.mapper.UserFollowResponseRowMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Repository 
public class JdbcUserFollowQueryRepository implements UserFollowQueryRepository{
    private final JdbcClient jdbcClient;

    private final UserFollowResponseRowMapper userFollowResponseRowMapper;

    @Override
    public Slice<UserFollowResponse> findByUserId(UUID userId, Pageable pageable) {
        var sql = """
        SELECT
            up.*,
            uf.followed_at 
        FROM user_follow uf
        JOIN user_projection up ON up.id = uf.followed_user_id
        WHERE uf.user_id = :userId
        ORDER BY uf.followed_at
        LIMIT :pageSize
        OFFSET :offset
        """;
        var res = jdbcClient
            .sql(sql)
            .param("userId", userId)
            .param("pageSize", pageable.getPageSize()+1)
            .param("offset", pageable.getOffset())
            .query(userFollowResponseRowMapper)
            .list();
        var hasNext = res.size() > pageable.getPageSize();
        if(hasNext){
            res.remove(res.size() - 1);
        }
        return new SliceImpl<>(res, pageable, hasNext);
    }
}
