package com.ambrosia.content_service.user.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.ambrosia.profile_service.kafka_events.UserEvent;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.ambrosia.content_service.user.model.entity.UserProjection;

public interface UserRepository extends
        CrudRepository<UserProjection, UUID>,
        CustomUserRepository{
    @Query("""
    SELECT COUNT(*) FROM user_projection up
    WHERE up.id IN (:userIds)
    """)
    int count(Set<UUID> userIds);
    
    @Query("SELECT * FROM user_projection WHERE id = :id AND deleted_at IS NULL")
    Optional<UserProjection> findById(UUID id);
}