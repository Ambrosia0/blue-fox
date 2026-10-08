package com.ambrosia.comment_service.user.repository;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;

import com.ambrosia.comment_service.user.model.entity.UserProjection;

public interface UserRepository extends Repository<UserProjection, UUID> {
    @Query("""
    WITH inserted AS (
        INSERT INTO processed_events(id) VALUES (:eventId)
        ON CONFLICT(id) DO NOTHING
        RETURNING id
    )
    INSERT INTO user_projection(id, username, first_name, last_name, avatar_id)
    SELECT 
        :#{#userProjection.id}, 
        :#{#userProjection.username},
        :#{#userProjection.firstName},
        :#{#userProjection.lastName},
        :#{#userProjection.avatarId}
    FROM inserted
    RETURNING *
    """
    )
    UserProjection insert(UserProjection userProjection, UUID eventId);

    @Query("""
    WITH inserted AS (
        INSERT INTO processed_events(id) VALUES (:eventId)
        ON CONFLICT(id) DO NOTHING
        RETURNING id
    )
    UPDATE user_projection up
    SET username = :#{#userProjection.username},
        first_name = :#{#userProjection.firstName},
        last_name = :#{#userProjection.lastName},
        avatar_id = :#{#userProjection.avatarId}
    FROM inserted
    WHERE up.id = :#{#userProjection.id}
    RETURNING up.*
    """
    )
    UserProjection update(UserProjection userProjection, UUID eventId);

    @Query("""
    WITH inserted AS (
        INSERT INTO processed_events(id) VALUES (:eventId)
        ON CONFLICT(id) DO NOTHING
        RETURNING id
    )
    DELETE FROM user_projection up
    USING inserted i
    WHERE up.id = :userId
    """)
    void delete(UUID userId, UUID eventId);

    @Query("""
    SELECT COUNT(*) FROM user_projection up
    WHERE up.id IN (:userIds)     
    """)
    int count(Set<UUID> userIds);

    boolean existsById(UUID id);
}
