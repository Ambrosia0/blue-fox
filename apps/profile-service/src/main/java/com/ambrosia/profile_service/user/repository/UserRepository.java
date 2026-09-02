package com.ambrosia.profile_service.user.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.profile_service.core.UserInfo;
import com.ambrosia.profile_service.user.model.dto.response.PublicUserProfileResponse;
import com.ambrosia.profile_service.user.model.entity.User;

public interface UserRepository extends 
        CrudRepository<User, UUID>, 
        PagingAndSortingRepository<User, UUID>,
        CustomUserRepository {
    Optional<User> findByUsernameIgnoreCase(String username);
    boolean existsByUsernameOrEmail(String username, String email);
    boolean existsByUsername(String username);
    Optional<User> findByIdAndIsActiveIsTrue(UUID id);

    @Modifying
    @Transactional
    @Query("UPDATE service_user SET avatar_id = :avatarId WHERE id = :userId")
    void updateAvatar(String avatarId, UUID userId);

    @Modifying
    @Transactional
    @Query("UPDATE service_user SET username = :username WHERE id = :userId")
    void updateUsername(UUID userId, String username);
    
    @Query("""
    SELECT
        su.id,
        su.username,
        su.first_name,
        su.last_name,
        su.avatar_id,
        CASE
            WHEN us.display_activity = true THEN su.status
            ELSE NULL
        END as status
    FROM service_user su
    JOIN user_settings us ON us.user_id = su.id
    WHERE id IN (:userIds)
    """)
    List<UserInfo> findByIdIn(Collection<UUID> userIds);

    @Query("""
        INSERT INTO service_user(id, username, user_role, email) 
            VALUES(:userId, :username, :userRole, :email)
        ON CONFLICT (id) DO NOTHING
        """)
    int saveIfNotPresent(UUID userId, String username, String userRole, String email);

    @Modifying
    @Query("UPDATE service_user SET follow_count = follow_count + :count WHERE id = :userId")
    void incrementFollowCount(long userId, long count);
    
    boolean existsByIdAndIsEnabledIsTrue(UUID id);


    @Query("""
    SELECT
        su.id,
        su.username,
        su.first_name,
        su.last_name,
        su.about,
        CASE
            WHEN us.display_email = true THEN su.email
            ELSE NULL
        END as email,
        su.avatar_id,
        su.follow_count,
        CASE 
            WHEN us.display_activity = true THEN su.status
            ELSE NULL
        END as status,
        CASE WHEN us.display_activity = true THEN su.last_activity
            ELSE NULL
        END as last_activity,
        su.created_at
    FROM service_user su
    JOIN user_settings us ON us.user_id = su.id
    WHERE su.id = :userId
    """)
    Optional<PublicUserProfileResponse> findPublicProfileById(UUID userId);

    @Query("""
    SELECT
        su.id,
        su.username,
        su.first_name,
        su.last_name,
        su.about,
        CASE
            WHEN us.display_email = true THEN su.email
            ELSE NULL
        END as email,
        su.avatar_id,
        su.follow_count,
        CASE 
            WHEN us.display_activity = true THEN su.status
            ELSE NULL
        END as status,
        CASE WHEN us.display_activity = true THEN su.last_activity
            ELSE NULL
        END as last_activity,
        su.created_at
    FROM service_user su
    JOIN user_settings us ON us.user_id = su.id
    WHERE su.username = LOWER(:username)     
    """)
    Optional<PublicUserProfileResponse> findPublicProfileByUsername(String username);
}
