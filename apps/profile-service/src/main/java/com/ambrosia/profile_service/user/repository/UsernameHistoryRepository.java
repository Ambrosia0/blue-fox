package com.ambrosia.profile_service.user.repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.ambrosia.profile_service.user.model.entity.UsernameHistory;

public interface UsernameHistoryRepository extends CrudRepository<UsernameHistory, UUID>{
    Optional<UsernameHistory> findFirstByUserIdOrderByChangedAtDesc(UUID userId);

    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM username_history
            WHERE user_id = :userId
            AND changed_at < :threshold
            ORDER BY changed_at DESC
        )
        """)
    boolean allowedToChange(UUID userId, Instant threshold);
}
