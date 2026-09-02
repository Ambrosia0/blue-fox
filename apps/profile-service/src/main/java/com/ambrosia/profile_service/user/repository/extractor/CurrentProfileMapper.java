package com.ambrosia.profile_service.user.repository.extractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.ambrosia.profile_service.user.model.dto.response.CurrentUserProfileResponse;
import com.ambrosia.profile_service.user.model.dto.response.ProfileSettingsResponse;
import com.ambrosia.profile_service.user.utils.Status;

@Component
public class CurrentProfileMapper implements RowMapper<CurrentUserProfileResponse>{
    @Override
    public CurrentUserProfileResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new CurrentUserProfileResponse(
            rs.getObject("id", UUID.class), 
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getString("username"),
            rs.getString("about"),
            rs.getString("email"),
            rs.getObject("avatar_id", UUID.class),
            rs.getTimestamp("created_at").toInstant(),
            Status.valueOf(rs.getString("status")),
            rs.getTimestamp("last_activity").toInstant(),
            new ProfileSettingsResponse(
                rs.getBoolean("display_email"),
                rs.getBoolean("display_activity")
            )
        );
    }
}