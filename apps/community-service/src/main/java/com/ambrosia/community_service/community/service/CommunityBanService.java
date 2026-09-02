package com.ambrosia.community_service.community.service;

import java.util.List;
import java.util.UUID;

public interface CommunityBanService {
    boolean isAnyBanned(List<UUID> ids);
}
