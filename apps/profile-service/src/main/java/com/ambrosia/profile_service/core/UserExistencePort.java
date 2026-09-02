package com.ambrosia.profile_service.core;

import java.util.Collection;
import java.util.UUID;

public interface UserExistencePort{
    boolean isExists(Collection<UUID> ids);
    boolean isExists(UUID id);
}