package com.ambrosia.comment_service.comment.infrastructure.persistence.extractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.ambrosia.comment_service.comment.domain.policy.entity.CommentPolicyData;
import com.ambrosia.comment_service.comment.domain.policy.entity.CommunityData;

@Component 
public class CommentPolicyRowMapper implements RowMapper<CommentPolicyData> {
    @Override
    public CommentPolicyData mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new CommentPolicyData(
                rs.getObject("community_id") != null?
                    Optional.of(new CommunityData(
                        rs.getBoolean("is_banned"),
                        rs.getBoolean("is_moderator"), 
                        rs.getBoolean("is_private"), 
                        rs.getBoolean("is_followed")
                    )):
                    Optional.empty()
            );
    }
}
