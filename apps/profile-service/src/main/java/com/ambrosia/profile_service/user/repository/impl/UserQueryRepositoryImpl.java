package com.ambrosia.profile_service.user.repository.impl;

import java.util.UUID;

import org.apache.commons.collections4.map.LinkedMap;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.ambrosia.profile_service.user.model.dto.admin.UserFilter;
import com.ambrosia.profile_service.user.model.dto.admin.UserResponse;
import com.ambrosia.profile_service.user.model.dto.response.CurrentUserProfileResponse;
import com.ambrosia.profile_service.user.model.dto.response.ProfileUserData;
import com.ambrosia.profile_service.user.repository.UserQueryRepository;
import com.ambrosia.profile_service.user.repository.extractor.CurrentProfileMapper;
import com.ambrosia.profile_service.user.repository.extractor.UserResponseExtractor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Repository
public class UserQueryRepositoryImpl implements UserQueryRepository{
    private final JdbcClient jdbcClient;
    private final CurrentProfileMapper profileMapper;


    @Override
    public CurrentUserProfileResponse findProfileById(UUID userId) {
        var sql = """
        SELECT
            su.id,
            su.username,
            su.first_name,
            su.last_name,
            su.about,
            su.email,
            su.follow_count,
            su.avatar_id,
            su.created_at,
            su.status,
            su.last_activity,
            us.display_email,
            us.display_activity
        FROM service_user su
        JOIN user_settings us ON us.user_id = su.id
        WHERE su.id = :userId
        """;
        return jdbcClient
            .sql(sql)
            .param("userId", userId)
            .query(profileMapper)
            .single();
    }

    @Override
    public Slice<UserResponse> getUsers(UserFilter userFilter, Pageable pageable) {
        var paramMap = new LinkedMap<String, Object>();
        var sql = new StringBuilder("""
        SELECT
            su.id,
            su.username,
            su.first_name,
            su.last_name,
            su.is_enabled,
            su.email,
            su.avatar_id,
            su.follow_count,
            su.created_at,
            su.status,
            su.last_activity
            uh.id as change_id,
            uh.username as changed_username,
            uh.changed_at
        FROM service_user su
        LEFT JOIN username_history uh ON uh.user_id = su.id
        WHERE 1=1 
        """);
        if(userFilter.email() != null){
            sql.append("AND su.email ILIKE '%' || :email || '%' ");
            paramMap.put("email", userFilter.email());
        }

        if(userFilter.username() != null){
            sql.append("AND su.username ILIKE '%' || :username || '%' ");
            paramMap.put("username", userFilter.username());
        }

        sql.append("ORDER BY created_at LIMIT :pageSize OFFSET :offset");
        paramMap.put("pageSize", pageable.getPageSize() + 1);
        paramMap.put("offset", pageable.getOffset());

        var res = jdbcClient
            .sql(sql.toString())
            .params(paramMap)
            .query(new UserResponseExtractor());
        
        var hasNext = res.size() > pageable.getPageSize();
        if(hasNext)
            res.remove(res.size());

        return new SliceImpl<>(res, pageable, hasNext);
    }

    @Override
    public ProfileUserData findUserData(UUID userId, UUID profileId) {
        var sql = """
        SELECT
            b.blacklisted_user_id IS NOT NULL AS is_blacklisted,
            b.reason
        FROM service_user su
        LEFT JOIN blacklist b ON (b.user_id, b.blacklisted_user_id) = (su.id, :profileId)
        WHERE su.id = :userId
        """;
        return jdbcClient
            .sql(sql)
            .param("userId", userId)
            .param("profileId", profileId)
            .query(ProfileUserData.class)
            .single();
    }
}
