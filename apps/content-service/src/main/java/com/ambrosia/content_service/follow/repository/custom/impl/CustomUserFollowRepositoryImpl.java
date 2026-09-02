package com.ambrosia.content_service.follow.repository.custom.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.ambrosia.content_service.follow.model.dto.UserFollowResponse;
import com.ambrosia.content_service.follow.model.entity.UserFollow;
import com.ambrosia.content_service.follow.model.entity.keys.UserFollowKey;
import com.ambrosia.content_service.follow.repository.custom.CustomUserFollowRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class CustomUserFollowRepositoryImpl implements CustomUserFollowRepository{
    private final JdbcClient jdbcClient;
    
    @Override
    public Optional<UserFollow> optionalSave(UserFollow userFollow) {
        var sql = """
        INSERT INTO user_follow VALUES (:userId, :followedUserId) 
        ON CONFLICT (user_id, followed_user_id) DO NOTHING
        RETURNING *
        """;
        return jdbcClient
            .sql(sql)
            .param("userId", userFollow.getId().userId())
            .param("followedUserId", userFollow.getId().followedUserId())
            .query(UserFollow.class)
            .optional();
    }

    @Override
    public int returningDelete(UserFollowKey userFollowKey) {
        var sql = "DELETE FROM user_follow WHERE user_id = :userId AND followed_user_id = :followedUserId";
        return jdbcClient
            .sql(sql)
            .param("userId", userFollowKey.userId())
            .param("followedUserId", userFollowKey.followedUserId())
            .update();
    }

    @Override
    public Slice<UserFollowResponse> findByUserId(UUID userId, Pageable pageable) {
        var sql = """
        SELECT * FROM user_follow
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
            .query(UserFollowResponse.class)
            .list();
        var hasNext = res.size() > pageable.getPageSize();
        if(hasNext){
            res.remove(res.size() - 1);
        }
        return new SliceImpl<>(res, pageable, hasNext);
    }

    //     @Query("SELECT * FROM user_follow WHERE user_id = :userId LIMIT :#{pageable.getPageSize} OFFSET :#{pageable.getOffset}")
    // List<UserFollow> findByUserId(UUID userId, Pageable pageable);
}
