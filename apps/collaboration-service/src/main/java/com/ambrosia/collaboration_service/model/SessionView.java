package com.ambrosia.collaboration_service.model;

import java.util.Collection;
import java.util.UUID;

import com.ambrosia.collaboration_service.model.entity.User;

public interface SessionView {
    boolean hasActiveUser(UUID userId);
    boolean hasCollaborator(UUID userId);
    Collection<User> getCollaborators();
}
