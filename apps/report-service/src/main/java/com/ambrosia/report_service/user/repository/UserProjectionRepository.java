package com.ambrosia.report_service.user.repository;

import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;

import com.ambrosia.report_service.user.entity.UserProjection;

public interface UserProjectionRepository extends Repository<UserProjection, UUID>{
    @Query("""
    WITH inserted AS (
        INSERT INTO processed_events(id) VALUES (:eventId)
        ON CONFLICT(id) DO NOTHING
        RETURNING id
    )
    INSERT INTO user_projection(id)
    SELECT :#{#userProjection.id}
    FROM inserted
    RETURNING id
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
    """
    )
    void delete(UUID userId, UUID eventId);

    boolean existsById(UUID id);
}
