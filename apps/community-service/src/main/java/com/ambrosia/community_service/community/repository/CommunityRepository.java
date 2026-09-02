package com.ambrosia.community_service.community.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.ambrosia.community_service.community.model.dto.response.CommunityPreview;
import com.ambrosia.community_service.community.model.dto.response.CommunityResponse;
import com.ambrosia.community_service.community.model.entity.Community;
import com.ambrosia.community_service.community.repository.custom.CustomCommunityRepository;

public interface CommunityRepository extends 
    CrudRepository<Community, Long>, 
    ListPagingAndSortingRepository<Community, Long>,
    CustomCommunityRepository{
    @Modifying
    @Query("""
        UPDATE community 
        SET avatar_url = :avatarUrl, description = :description, tags = :tags 
        WHERE id = :id AND owner_id = :requestingUser
        RETURNING *
            """)
    Optional<CommunityResponse> save(Community community, @Param("requestingUser") UUID requestingUser);

    @Query("SELECT * FROM community WHERE id = :id")
    Optional<CommunityResponse> findByIdProjected(long id);

    Page<CommunityPreview> findBy(Pageable pageable);

    @Modifying
    @Query("UPDATE community SET follow_count = follow_count + :count WHERE id = :communityId")
    void incrementFollowCount(@Param("communityId") long communityId, @Param("count") long count);

    @Query("SELECT COUNT(*) FROM community WHERE owner_id = :userId")
    long countOwned(UUID userId);

    @Modifying
    @Query("DELETE FROM community WHERE id = :communityId")
    int returningDelete(long communityId);

    @Query("SELECT is_private FROM community WHERE id = :communityId")
    Optional<Boolean> findIsCommunityPrivate(long communityId);

    boolean existsBySlug(String slug);

    @Modifying
    @Query("""
    UPDATE community
    SET avatar_id = :avatarId
    WHERE id = :communityId
    """)
    int updateAvatar(long communityId, String avatarId);
}
