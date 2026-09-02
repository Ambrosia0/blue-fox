package com.ambrosia.report_service.community.service;

import java.util.UUID;

import com.ambrosia.community_service.kafka_events.CommunityCreate;
import com.ambrosia.community_service.kafka_events.CommunityDelete;

public interface CommunityProjectionService {
    void create(CommunityCreate communityCreate, UUID eventId);
    void delete(CommunityDelete communityDelete, UUID eventId);
    boolean exist(Long id);
}
