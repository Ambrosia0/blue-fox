package com.ambrosia.community_service.community.repository.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.ambrosia.community_service.community.model.dto.response.CommunityResponse;
import com.ambrosia.community_service.user.model.entity.UserResponse;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor 
@Component
public class CommunityResponseRowMapper implements RowMapper<CommunityResponse>{
    private final ObjectMapper objectMapper;

    @Override
    public CommunityResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new CommunityResponse(
                rs.getLong("id"),
                rs.getString("slug"),
                rs.getString("displayed_name"),
                rs.getObject("owner_id", UUID.class) != null?
                    new UserResponse(
                        rs.getObject("owner_id", UUID.class),
                        rs.getObject("username", String.class),
                        rs.getObject("first_name", String.class),
                        rs.getObject("last_name", String.class),
                        rs.getObject("avatar_id", String.class)
                    ):
                    null,
                rs.getObject("avatar_id", String.class),
                rs.getObject("description", String.class),
                rs.getArray("rules") != null?
                    (String[])rs.getArray("rules").getArray():
                    null, 
                rs.getArray("tags") != null?
                    (String[])rs.getArray("tags").getArray():
                    null, 
                rs.getObject("community_moderators", String.class) != null?
                    objectMapper.readValue(
                        rs.getString("community_moderators"), 
                        UserResponse[].class
                    ):
                    null, 
                rs.getLong("post_count"),
                rs.getLong("follow_count"),
                rs.getBoolean("is_private"),
                null,
                rs.getTimestamp("created_at").toInstant()
        );
    }
}
