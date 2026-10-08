package com.ambrosia.content_service.post.infrastructure.persistence.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.ambrosia.content_service.post.api.dto.response.CommunityResponse;
import com.ambrosia.content_service.post.api.dto.response.PostEditorViewResponse;
import com.ambrosia.content_service.post.api.dto.response.UserResponse;

import lombok.RequiredArgsConstructor;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor 
@Component 
public class PostEditorViewResponseMapper implements RowMapper<PostEditorViewResponse>{
    private final ObjectMapper objectMapper;

    private TypeReference<List<UserResponse>> typeReference = new TypeReference<>() {};
    
    @Override
    public PostEditorViewResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new PostEditorViewResponse(
            rs.getLong("id"),
            rs.getObject("author_id", UUID.class) != null?
                new UserResponse(
                    rs.getObject("author_id", UUID.class), 
                    rs.getString("username"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("avatar_id")
                ):
                null,
            rs.getString("title"),
            rs.getArray("tags") != null?
                Arrays.asList(((String[])rs.getArray("tags").getArray())):
                null,
            rs.getObject("community_id", Long.class) != null?
                new CommunityResponse(
                    rs.getLong("community_id"),
                    rs.getString("name"),
                    rs.getString("slug"),
                    rs.getBoolean("is_private"),
                    rs.getObject("avatar_id", String.class)
                ):
                null,
            rs.getObject("collaborators", String.class) != null?
                objectMapper.readValue(
                    rs.getString("collaborators"),
                    typeReference
                ):
                null,
            rs.getTimestamp("updated_at").toInstant()
        );
    }
}
