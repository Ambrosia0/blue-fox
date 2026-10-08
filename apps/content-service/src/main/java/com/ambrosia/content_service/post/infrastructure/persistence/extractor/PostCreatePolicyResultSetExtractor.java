package com.ambrosia.content_service.post.infrastructure.persistence.extractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.ambrosia.content_service.post.domain.policy.entity.CreateCommunityData;
import com.ambrosia.content_service.post.domain.policy.entity.CreatePostData;
import com.ambrosia.content_service.post.domain.policy.entity.PostCreatePolicyData;

@Component 
public class PostCreatePolicyResultSetExtractor implements ResultSetExtractor<PostCreatePolicyData>{
    @Override
    public PostCreatePolicyData extractData(ResultSet rs) throws SQLException, DataAccessException {
        if(rs.next())
            return new PostCreatePolicyData(
                rs.getObject("posted_community_id", Long.class) != null?
                    Optional.of(
                        new CreateCommunityData(
                            rs.getLong("posted_community_id"),
                            rs.getBoolean("posted_is_moderator"),
                            rs.getBoolean("posted_is_followed"),
                            rs.getBoolean("posted_is_banned"),
                            rs.getBoolean("posted_is_private")
                        )
                    ):
                    Optional.empty(),

                rs.getObject("replying_post_id", Long.class) != null?
                    Optional.of(
                        new CreatePostData(
                            rs.getObject("replying_community_id") != null?
                                Optional.of(
                                    new CreateCommunityData(
                                        rs.getLong("replying_community_id"),
                                        rs.getBoolean("posted__is_moderator"),
                                        rs.getBoolean("replying_is_followed"),
                                        rs.getBoolean("replying_is_banned"),
                                        rs.getBoolean("replying_is_private")
                                    )
                                ):
                                Optional.empty()
                        )
                    ):
                    Optional.empty()
            );
        else
            return new PostCreatePolicyData(Optional.empty(), Optional.empty());
    }
}
