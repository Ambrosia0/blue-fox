package com.ambrosia.profile_service.user.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.ambrosia.notification_service.kafka_events.ActivityEvent;

import com.ambrosia.profile_service.user.model.dto.UserFollowIncrement;

public interface CustomUserRepository {
    int batchIncrementFollowCount(Collection<UserFollowIncrement> toIncrement);
    boolean existsByIds(Collection<UUID> ids);
    void batchUpdatePresense(List<ActivityEvent> batch);
}
