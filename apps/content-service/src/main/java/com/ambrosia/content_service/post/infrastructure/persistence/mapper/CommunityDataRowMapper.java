package com.ambrosia.content_service.post.infrastructure.persistence.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.ambrosia.content_service.post.domain.policy.entity.CommunityData;

@Component 
public class CommunityDataRowMapper implements RowMapper<CommunityData>{
    @Override
    public CommunityData mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new CommunityData(
            rs.getBoolean("is_moderator"),
            rs.getBoolean("is_followed"),
            rs.getBoolean("is_banned"),
            rs.getBoolean("is_private")
        );
    }
}
