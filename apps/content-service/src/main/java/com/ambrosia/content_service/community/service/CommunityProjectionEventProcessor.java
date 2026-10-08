package com.ambrosia.content_service.community.service;

import com.ambrosia.community_service.kafka_events.CommunityEvent;

public interface CommunityProjectionEventProcessor {
    void process(CommunityEvent communityEvent);
}
