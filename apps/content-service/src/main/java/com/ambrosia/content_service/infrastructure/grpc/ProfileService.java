package com.ambrosia.content_service.infrastructure.grpc;

import java.util.List;
import java.util.UUID;

public interface ProfileService{
    List<UUID> getBlacklist(UUID userId);
}
