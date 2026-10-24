package com.ambrosia.comment_service.user.infrastructure.persistence;

import com.ambrosia.profile_service.kafka_events.UserEvent;

import java.util.List;

public interface CustomUserRepository {
    void process(List<UserEvent> toUpsert, List<UserEvent> toDelete);
}
