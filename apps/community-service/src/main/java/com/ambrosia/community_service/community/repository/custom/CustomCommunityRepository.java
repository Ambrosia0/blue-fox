package com.ambrosia.community_service.community.repository.custom;

import java.util.Collection;

import com.ambrosia.community_service.community.model.dto.CommunityFollowIncrement;
import com.ambrosia.community_service.kafka_events.PostCountAggregation;

public interface CustomCommunityRepository {
    int batchIncrementFollowCount(Collection<CommunityFollowIncrement> toIncrement);
    int batchIncrementPostCount(Collection<PostCountAggregation> toIncrement);
}
