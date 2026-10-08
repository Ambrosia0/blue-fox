package com.ambrosia.comment_service.community.service;

import com.ambrosia.community_service.kafka_events.CommunityEvent;

public interface CommunityProjectionService {
    void process(CommunityEvent communityEvent);
}
