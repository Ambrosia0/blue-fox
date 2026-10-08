package com.ambrosia.content_service.post.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;

import com.ambrosia.content_service.post.domain.entity.DeletionProjection;
import com.ambrosia.content_service.post.domain.entity.Post;
import com.ambrosia.content_service.post.domain.repository.PostRepository;
import com.ambrosia.content_service.post.infrastructure.persistence.custom.CustomPostRepository;

public interface JdbcPostRepository extends 
        Repository<Post, Long>, 
        CustomPostRepository,
        PostRepository {
    Optional<Post> findByAuthorIdAndIdAndPublishedIsFalse(UUID authorId, long id);
    Optional<Post> findByAuthorIdAndIdAndPublishedIsTrue(UUID authorId, long id);
    
    boolean existsByIdAndAuthorId(long postId, UUID userId);
    boolean existsByIdAndPublishedIsTrue(long postId);

    @Query ("SELECT community_id FROM post WHERE id = :postId")
    Optional<Long> findCommunityId(long postId);

    @Query("""
    DELETE FROM post 
    WHERE author_id = :authorId 
    AND published = 'false'
    AND id = :id
    """)
    @Modifying 
    int deleteByAuthorIdAndIdAndPublishedIsFalse(UUID authorId, long id);

    @Query("""
    DELETE FROM post
    WHERE id = :postId AND published = 'true'
    RETURNING id, community_id, author_id, version
    """)
    Optional<DeletionProjection> deletePublished(Long postId);

    Optional<Post> findByAuthorIdAndId(UUID authorId, long postId);
    
    void deleteAll();
}
