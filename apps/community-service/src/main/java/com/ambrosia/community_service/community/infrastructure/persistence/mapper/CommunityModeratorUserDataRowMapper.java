package com.ambrosia.community_service.community.infrastructure.persistence.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.ambrosia.community_service.community.utils.ScopeEnum;
import com.ambrosia.community_service.core.domain.policy.entity.CommunityModeratorUserContext;

@Component 
public class CommunityModeratorUserDataRowMapper implements RowMapper<CommunityModeratorUserContext>{
    @Override
    public CommunityModeratorUserContext mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new CommunityModeratorUserContext(
            Stream.of(((Short[])rs.getArray("permissions").getArray()))
                .map(t -> ScopeEnum.fromId(t))
                .collect(Collectors.toSet()),
            rs.getBoolean("is_target_exist"),
            rs.getBoolean("is_target_moderator"),
            rs.getBoolean("is_target_banned")
        );
    }
}
