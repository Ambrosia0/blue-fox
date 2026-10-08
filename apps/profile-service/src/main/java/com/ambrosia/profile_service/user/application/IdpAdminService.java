package com.ambrosia.profile_service.user.application;

import java.util.UUID;

public interface IdpAdminService {
    void banUser(UUID userId);
    void unbanUser(UUID userId);
}
