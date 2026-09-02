package com.ambrosia.profile_service.user.repository.impl;

import java.util.List;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.ambrosia.profile_service.user.model.dto.response.UserSearch;
import com.ambrosia.profile_service.user.repository.UserSearchRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class UserSearchRepositoryImpl implements UserSearchRepository{
    private final JdbcClient jdbcClient;
    
    private String sql = """
    SELECT   
        su.id, 
        su.username,
        su.first_name,
        su.last_name,
        su.avatar_id, 
        (word_similarity(:searchString, su.username) * 1.0 +
        word_similarity(:searchString, su.first_name) * 0.3 +
        word_similarity(:searchString, su.last_name) * 0.3)
            as rank
    FROM service_user su
    WHERE :searchString <% su.username OR :searchString <% su.first_name OR :searchString <% su.last_name
    ORDER BY rank DESC, su.username
    LIMIT :pageSize
    """;

    @Override
    public List<UserSearch> search(String searchString, int pageSize) {
        return jdbcClient
            .sql(sql)
            .param("searchString", searchString)
            .param("pageSize", pageSize)
            .query(UserSearch.class)
            .list();
    }
}
