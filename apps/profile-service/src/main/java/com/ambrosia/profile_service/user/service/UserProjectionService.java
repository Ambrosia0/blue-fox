package com.ambrosia.profile_service.user.service;

import java.util.UUID;

import com.ambrosia.profile_service.core.UserExistencePort;
import com.ambrosia.profile_service.user.model.dto.UserProjection;
/**
 * Service responsible for creating, updating and deleting local user projections
 */
public interface UserProjectionService extends UserExistencePort{
    void create(UserProjection userProjection);
    void update(UserProjection userProjection);
    void delete(UUID id);
}
