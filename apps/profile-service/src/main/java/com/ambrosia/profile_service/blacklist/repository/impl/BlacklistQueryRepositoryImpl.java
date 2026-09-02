package com.ambrosia.profile_service.blacklist.repository.impl;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.ambrosia.profile_service.blacklist.model.dto.response.BlacklistResponse;
import com.ambrosia.profile_service.blacklist.repository.BlacklistQueryRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class BlacklistQueryRepositoryImpl implements BlacklistQueryRepository{
    private final JdbcClient jdbcClient;
    
    @Override
    public Slice<BlacklistResponse> getBlacklistedUsers(UUID userId, Pageable pageable) {
        var sql = """
        SELECT
            su.id,
            su.username,
            su.first_name,
            su.last_name,
            su.avatar_id,
            b.reason
        FROM blacklist b
        JOIN service_user su ON su.id = b.user_id
        WHERE b.user_id = :userId
        LIMIT :pageSize
        OFFSET :offset
        """;
        var resp = jdbcClient
            .sql(sql)
            .param("userId", userId)
            .param("pageSize", pageable.getPageSize() + 1)
            .param("offset", pageable.getOffset())
            .query(BlacklistResponse.class)
            .list();
        var hasNext = resp.size() > pageable.getPageSize();
        if(hasNext)
            resp.remove(resp.size());
        return new SliceImpl<>(resp, pageable, hasNext);
    }

    @Override
    public int getBlacklistCount(UUID userId) {
        var sql = """
        SELECT blacklist_count 
        FROM service_user su 
        WHERE su.id = :userId        
        """;
        return jdbcClient
            .sql(sql)
            .param("userId", userId)
            .query(Integer.class)
            .single();
    }
}
