package com.ambrosia.content_service.follow.domain.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

import com.ambrosia.content_service.community.repository.custom.CustomUserFollowRepository;
import com.ambrosia.content_service.follow.infrastructure.entity.UserFollow;
import com.ambrosia.content_service.follow.infrastructure.entity.keys.UserFollowKey;

public interface UserFollowRepository extends 
    CrudRepository<UserFollow, UserFollowKey>, 
    ListPagingAndSortingRepository<UserFollow, UserFollowKey>,
    CustomUserFollowRepository {

    @Query("SELECT followed_user_id FROM user_follow WHERE user_id = :userId ")
    List<UUID> findFollowsByUserId(UUID userId);
}
