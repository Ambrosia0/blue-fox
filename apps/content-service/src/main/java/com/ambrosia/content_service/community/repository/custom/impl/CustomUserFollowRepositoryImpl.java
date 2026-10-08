package com.ambrosia.content_service.community.repository.custom.impl;

import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.ambrosia.content_service.community.repository.custom.CustomUserFollowRepository;
import com.ambrosia.content_service.follow.infrastructure.entity.UserFollow;
import com.ambrosia.content_service.follow.infrastructure.entity.keys.UserFollowKey;

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

    //     @Query("SELECT * FROM user_follow WHERE user_id = :userId LIMIT :#{pageable.getPageSize} OFFSET :#{pageable.getOffset}")
    // List<UserFollow> findByUserId(UUID userId, Pageable pageable);
}
