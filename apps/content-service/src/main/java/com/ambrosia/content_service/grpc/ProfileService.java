package com.ambrosia.content_service.grpc;

import java.util.List;
import java.util.UUID;

public interface ProfileService{
    boolean isUserExist(UUID userId);
    List<UUID> getBlacklist(UUID userId);
}
