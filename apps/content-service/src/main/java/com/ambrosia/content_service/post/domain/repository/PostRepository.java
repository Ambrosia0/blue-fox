package com.ambrosia.content_service.post.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.ambrosia.content_service.post.domain.entity.DeletionProjection;
import com.ambrosia.content_service.post.domain.entity.Post;

public interface PostRepository {
    Optional<Post> findByAuthorIdAndIdAndPublishedIsFalse(UUID authorId, long id);
    Optional<Post> findByAuthorIdAndIdAndPublishedIsTrue(UUID authorId, long id);

    Post save(Post post);

    boolean existsByIdAndAuthorId(long postId, UUID userId);
    boolean existsByIdAndPublishedIsTrue(long postId);
    Optional<Long> findCommunityId(long postId);

    int deleteByAuthorIdAndIdAndPublishedIsFalse(UUID authorId, long id);

    Optional<DeletionProjection> deletePublished(Long postId);

    Optional<Post> findByAuthorIdAndId(UUID authorId, long postId);
    Optional<Post> findById(Long postId);
}
