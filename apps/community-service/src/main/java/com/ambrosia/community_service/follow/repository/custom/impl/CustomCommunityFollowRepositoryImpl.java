package com.ambrosia.community_service.follow.repository.custom.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.ambrosia.community_service.follow.model.entity.CommunityFollow;
import com.ambrosia.community_service.follow.model.entity.key.CommunityFollowKey;
import com.ambrosia.community_service.follow.repository.custom.CustomCommunityFollowRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class CustomCommunityFollowRepositoryImpl implements CustomCommunityFollowRepository{
    private final JdbcClient jdbcClient;

    @Override
    public Optional<CommunityFollow> optionalSave(CommunityFollow communityFollow) {
        var sql = """
            INSERT INTO community_follow 
            SELECT :userId, id FROM (SELECT :communityId AS id) c
            WHERE EXISTS(SELECT 1 FROM community WHERE id = c.id)
            ON CONFLICT (user_id, community_id) DO NOTHING 
            RETURNING *
        """;
        return jdbcClient
            .sql(sql)
            .param("userId", communityFollow.getId().userId())
            .param("communityId", communityFollow.getId().communityId())
            .query(CommunityFollow.class)
            .optional();
    }

    //     @Query("SELECT * FROM community_follow WHERE user_id = :userId LIMIT :#{pageable.getPageSize} OFFSET :#{pageable.getOffset}")
    // Slice<CommunityFollow> findByUserId(@Param("userId") UUID userId, Pageable pageable);
    
    @Override
    public Slice<CommunityFollow> findByUserId(UUID userId, Pageable pageable) {
        var sql = """
        SELECT * FROM community_follow
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
            .query(CommunityFollow.class)
            .list();
        var hasNext = res.size() > pageable.getPageSize();
        if(hasNext)
            res.remove(res.size());
        return new SliceImpl<>(res, pageable, hasNext);
    }


    @Override
    public int returningDelete(CommunityFollowKey communityFollowKey) {
        var sql = "DELETE FROM community_follow WHERE user_id = :userId AND community_id = :communityId";
        return jdbcClient
            .sql(sql)
            .param("userId", communityFollowKey.userId())
            .param("communityId", communityFollowKey.communityId())
            .update();
    }

    @Override
    public List<UUID> findByCommunityId(Long communityId, Pageable pageable) {
        var sql = """
        SELECT user_id FROM community_follow 
        WHERE community_id = :communityId
        ORDER BY followed_at ASC
        LIMIT :pageSize
        OFFSET :offset
        """;
        return jdbcClient
            .sql(sql)
            .param("communityId", communityId)
            .param("pageSize", pageable.getPageSize())
            .param("offset", pageable.getOffset())
            .query(UUID.class)
            .list();
    }
}
