package com.ambrosia.content_service.post.infrastructure.persistence.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.ambrosia.content_service.post.domain.policy.entity.CreateCommunityData;
import com.ambrosia.content_service.post.domain.policy.entity.PostPublishPolicyData;

@Component 
public class PostPublishPolicyDataRowMapper implements RowMapper<PostPublishPolicyData>{
    @Override
    public PostPublishPolicyData mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new PostPublishPolicyData(
            rs.getObject("author_id", UUID.class),
            rs.getObject("community_id", Long.class) != null?
                Optional.of(new CreateCommunityData(
                    rs.getLong("community_id"),
                    rs.getBoolean("is_moderator"),
                    rs.getBoolean("is_followed"),
                    rs.getBoolean("is_banned"),
                    rs.getBoolean("is_private")
                )):
                Optional.empty(),
            rs.getObject("replying_community_id", Long.class) != null?
                Optional.of(new CreateCommunityData(
                    rs.getLong("replying_community_id"),
                    rs.getBoolean("replying_is_moderator"),
                    rs.getBoolean("replying_is_followed"),
                    rs.getBoolean("replying_is_banned"),
                    rs.getBoolean("replying_is_private")
                )):
                Optional.empty()
        );
    }
}
