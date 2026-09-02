package com.ambrosia.community_service.follow.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

import com.ambrosia.community_service.follow.model.entity.CommunityFollow;
import com.ambrosia.community_service.follow.model.entity.key.CommunityFollowKey;
import com.ambrosia.community_service.follow.repository.custom.CustomCommunityFollowRepository;

public interface CommunityFollowRepository extends 
    CrudRepository<CommunityFollow, CommunityFollowKey>, 
    ListPagingAndSortingRepository<CommunityFollow, CommunityFollowKey>,
    CustomCommunityFollowRepository{

    @Query("SELECT community_id FROM community_follow WHERE user_id = :userId")
    List<Long> findFollowedByUserId(UUID userId);
}
