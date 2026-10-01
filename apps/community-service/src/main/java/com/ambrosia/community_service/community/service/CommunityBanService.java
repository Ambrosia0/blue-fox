package com.ambrosia.community_service.community.service;

import java.util.Set;
import java.util.UUID;

public interface CommunityBanService {
    boolean isAnyBanned(Set<UUID> ids);
}
