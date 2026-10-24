package com.ambrosia.community_service.user.infrastructure.persistence;

import com.ambrosia.community_service.user.model.entity.UserProjection;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;
import java.util.UUID;

public interface UserRepository extends
        CrudRepository<UserProjection, UUID>,
        CustomUserRepository{
    @Query("""
    SELECT COUNT(*) FROM user_projection up
    WHERE up.id IN (:userIds)     
    """)
    int count(Set<UUID> userIds);
}