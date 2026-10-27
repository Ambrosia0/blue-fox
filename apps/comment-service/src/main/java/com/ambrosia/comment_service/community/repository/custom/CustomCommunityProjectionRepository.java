package com.ambrosia.comment_service.community.repository.custom;

import java.util.List;
import java.util.UUID;

import com.ambrosia.comment_service.community.model.entity.CommunityProjection;
import com.ambrosia.community_service.kafka_events.CommunityEvent;

public interface  CustomCommunityProjectionRepository {
    void process(List<CommunityEvent> toUpsert, List<CommunityEvent> toDelete);
}
