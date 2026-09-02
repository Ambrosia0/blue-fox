package com.ambrosia.content_service.post.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ambrosia.content_service.post.model.dto.response.PostEditorViewResponse;
import com.ambrosia.content_service.post.model.entity.Post;
import com.ambrosia.content_service.post.repository.custom.CustomPostRepository;
import com.ambrosia.content_service.post.model.DeletionProjection;

public interface PostRepository extends 
        CrudRepository<Post, Long>, 
        PagingAndSortingRepository<Post, Long>, 
        CustomPostRepository {
    Optional<Post> findByAuthorIdAndIdAndPublishedIsFalse(UUID authorId, long id);
    Page<PostEditorViewResponse> findByAuthorIdAndPublishedIsFalseAndVisibleIsTrue(UUID authorId, Pageable pageable);
    
    boolean existsByIdAndAuthorId(long postId, UUID userId);
    boolean existsByIdAndVisibleIsTrueAndPublishedIsTrue(long postId);

    @Query("SELECT community_id FROM post WHERE id = :postId")
    Optional<Long> findCommunityId(long postId);


    @Query("""
    SELECT id, community_id, author_id, published
    FROM post
    WHERE id = :postId
    """)
    Optional<DeletionProjection> findDeletionProjectionById(Long postId);

    Optional<Post> findByAuthorIdAndId(UUID authorId, long postId);
    
}
