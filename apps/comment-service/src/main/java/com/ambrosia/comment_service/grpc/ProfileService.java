package com.ambrosia.comment_service.grpc;

import java.util.Collection;
import java.util.UUID;

public interface ProfileService{
    boolean isUserExist(UUID userId);
    boolean isUsersExist(Collection<UUID> userIds);
}
