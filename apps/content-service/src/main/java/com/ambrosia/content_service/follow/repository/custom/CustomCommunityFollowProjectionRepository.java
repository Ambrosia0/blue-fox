package com.ambrosia.content_service.follow.repository.custom;

import java.util.Collection;

import com.ambrosia.community_service.kafka_events.CommunityFollowEvent;

public interface CustomCommunityFollowProjectionRepository {
    void batchModify(Collection<CommunityFollowEvent> toInsert, Collection<CommunityFollowEvent> toDelete);
}
