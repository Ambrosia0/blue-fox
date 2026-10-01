package com.ambrosia.community_service.user.service;

import java.util.Set;
import java.util.UUID;

public interface  UserService {
    boolean isUsersExist(Set<UUID> userIds);
    boolean isUserExist(UUID userId);
}
