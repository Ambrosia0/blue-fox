package com.ambrosia.community_service.core.infrastructure.persistence.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.ambrosia.community_service.user.model.entity.UserResponse;

@Component 
public class UserResponseRowMapper implements RowMapper<UserResponse>{
    @Override
    public UserResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new UserResponse(
            rs.getObject("user_id", UUID.class),
            rs.getObject("username", String.class),
            rs.getObject("first_name", String.class),
            rs.getObject("last_name", String.class),
            rs.getObject("avatar_id", String.class)
        );
    }
}
