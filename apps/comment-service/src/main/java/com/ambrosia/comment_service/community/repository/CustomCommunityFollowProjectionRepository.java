package com.ambrosia.comment_service.community.repository;

import java.util.Collection;

import com.ambrosia.community_service.kafka_events.CommunityFollowEvent;

public interface CustomCommunityFollowProjectionRepository {
    void batchModify(Collection<CommunityFollowEvent> toInsert, Collection<CommunityFollowEvent> toDelete);
}
