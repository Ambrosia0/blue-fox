package com.ambrosia.profile_service.blacklist.domain.repository;

import java.util.List;
import java.util.UUID;

public interface BlacklistRepository {
    List<UUID> findByUserId(UUID userId);
    void add(UUID userId, UUID blacklistedUser, String reason);
    void remove(UUID userId, UUID blacklistedUser);
}
