package com.ambrosia.content_service.post.repository.extractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.ambrosia.content_service.post.model.dto.response.CommunityResponse;
import com.ambrosia.content_service.post.model.dto.response.PostCollaborationContentResponse;
import com.ambrosia.content_service.post.model.dto.response.UserResponse;

import lombok.RequiredArgsConstructor;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor 
@Component 
public class PostCollaborationRowMapper implements RowMapper<PostCollaborationContentResponse>{
    private final ObjectMapper objectMapper;

    private TypeReference<List<UserResponse>> typeReference = new TypeReference<>() {};

    @Override
    public PostCollaborationContentResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new PostCollaborationContentResponse(
            rs.getObject("id", Long.class),
            rs.getObject("author_id", UUID.class) != null?
                new UserResponse(
                    rs.getObject("author_id", UUID.class),
                    rs.getObject("username", String.class),
                    rs.getObject("fist_name", String.class),
                    rs.getObject("last_name", String.class),
                    rs.getObject("avatar_id", String.class)
                ):
                null,
            rs.getString("title"),
            rs.getString("content"),
            rs.getArray("tags") != null?
                Arrays.asList((String[])rs.getArray("tags").getArray()):
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
            rs.getArray("attachment_ids") != null?
                Arrays.asList((String[])rs.getArray("attachment_ids").getArray()):
                null,
            rs.getTimestamp("updated_at").toInstant()
        );
    }
}
