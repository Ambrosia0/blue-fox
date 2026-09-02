package com.ambrosia.profile_service.user.repository.extractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.ambrosia.profile_service.user.model.dto.admin.UserResponse;
import com.ambrosia.profile_service.user.model.dto.admin.UsernameChange;
import com.ambrosia.profile_service.user.utils.Status;

public class UserResponseExtractor implements ResultSetExtractor<List<UserResponse>>{
    @Override
    public List<UserResponse> extractData(ResultSet rs) throws SQLException, DataAccessException {
        var map = new LinkedHashMap<UUID, UserResponse>();
        while (rs.next()) {
            var obj = extract(rs);
            var resp = map.computeIfAbsent(
                obj.id(),
                id -> obj
            );

            var change = rs.getObject("change_id", UUID.class);
            if(change != null){
                resp.usernameHistory().add(
                    new UsernameChange(
                        change,
                        rs.getString("changed_username"),
                        rs.getTimestamp("changed_at").toInstant()
                    )
                );
            }
        }
        return new ArrayList<>(map.values());
    }
    
    private UserResponse extract(ResultSet rs) throws SQLException{
        return new UserResponse(
            rs.getObject("id", UUID.class),
            rs.getString("username"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getBoolean("is_enabled"),
            rs.getString("email"),
            rs.getObject("avatar_id", UUID.class),
            rs.getLong("follow_count"),
            rs.getTimestamp("created_at").toInstant(),
            rs.getObject("status", Status.class),
            rs.getTimestamp("last_activity").toInstant(),
            List.of()
        );
    }
}
