package com.ambrosia.profile_service.user.infrastructure.persistence;

import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.ambrosia.profile_service.user.application.policy.PolicyDataRepository;
import com.ambrosia.profile_service.user.domain.policy.entity.UsernameChangePolicyData;

import lombok.RequiredArgsConstructor;

@Repository 
@RequiredArgsConstructor 
public class JdbcPolicyDataRepository implements PolicyDataRepository{
    private final JdbcClient jdbcClient;

    @Override
    public UsernameChangePolicyData loadChangeData(UUID userId, String username) {
        var sql = """
        SELECT
            EXISTS (
                SELECT 1 FROM service_user
                WHERE username = :username
            ) as is_username_claimed,
            (
                SELECT MAX(changed_at) 
                FROM username_history
                WHERE user_id = :userId
            ) as last_change_instant
        """;

        return jdbcClient
            .sql(sql)
            .param("userId", userId)
            .param("username", username)
            .query(UsernameChangePolicyData.class)
            .single();
    }
}
