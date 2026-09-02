package com.ambrosia.profile_service.core.idp;

import java.util.UUID;

public interface IdpAdminService {
    void banUser(UUID userId);
    void unbanUser(UUID userId);
}
