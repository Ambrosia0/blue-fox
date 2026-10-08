package com.ambrosia.community_service.follow.infrastructure.persistence.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.ambrosia.community_service.core.infrastructure.persistence.mapper.UserResponseRowMapper;
import com.ambrosia.community_service.follow.api.dto.request.FollowFilter;
import com.ambrosia.community_service.follow.api.dto.response.CommunityFollowResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Component 
public class CommunityFollowResponseRowMapper implements RowMapper<CommunityFollowResponse>{
    private final UserResponseRowMapper userResponseRowMapper;

    @Override
    public CommunityFollowResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new CommunityFollowResponse(
            userResponseRowMapper.mapRow(rs, rowNum),
            rs.getTimestamp("created_at").toInstant(),
            FollowFilter.Type.valueOf(rs.getString("type"))
        );
    }
}
