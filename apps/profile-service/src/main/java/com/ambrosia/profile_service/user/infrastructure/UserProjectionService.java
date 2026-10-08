package com.ambrosia.profile_service.user.infrastructure;

import java.util.UUID;

import com.ambrosia.profile_service.user.api.dto.UserProjection;


/**
 * Service responsible for creating, updating and deleting local user projections
 */
public interface UserProjectionService{
    void create(UserProjection userProjection);
    void update(UserProjection userProjection);
    void delete(UUID id);
}
