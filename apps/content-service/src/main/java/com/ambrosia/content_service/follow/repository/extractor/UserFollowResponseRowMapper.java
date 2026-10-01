package com.ambrosia.content_service.follow.repository.extractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.ambrosia.content_service.follow.model.dto.UserFollowResponse;
import com.ambrosia.content_service.post.model.dto.response.UserResponse;

@Component 
public class UserFollowResponseRowMapper implements RowMapper<UserFollowResponse>{
    @Override
    public UserFollowResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new UserFollowResponse(
            new UserResponse(
                rs.getObject("id", UUID.class),
                rs.getString("username"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("avatar_id")
            ),
            rs.getTimestamp("followed_at").toInstant()
        );
    }
}
