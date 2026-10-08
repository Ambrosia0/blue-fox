package com.ambrosia.community_service.follow.infrastructure.persistence.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.ambrosia.community_service.community.application.query.model.CommunityPreview;
import com.ambrosia.community_service.follow.api.dto.request.FollowFilter;
import com.ambrosia.community_service.follow.api.dto.response.CommunityFollowUserResponse;

@Component 
public class CommunityFollowUserResponseRowMapper implements RowMapper<CommunityFollowUserResponse>{
    @Override
    public CommunityFollowUserResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new CommunityFollowUserResponse(
            new CommunityPreview(
                rs.getLong("id"),
                rs.getString("slug"),
                rs.getString("displayed_name"), 
                rs.getLong("follow_count"), 
                rs.getString("avatar_id"),
                rs.getArray("tags") != null?
                    ((String[])rs.getArray("tags").getArray()):
                    null,
                rs.getTimestamp("created_at").toInstant()
            ),
            rs.getTimestamp("followed_at").toInstant(),
            FollowFilter.Type.valueOf(rs.getString("type"))
        );
    }
}
