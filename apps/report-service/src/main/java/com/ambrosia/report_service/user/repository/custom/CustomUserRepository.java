package com.ambrosia.report_service.user.repository.custom;

import com.ambrosia.profile_service.kafka_events.UserEvent;

import java.util.List;

public interface CustomUserRepository {
    void process(List<UserEvent> toUpsert, List<UserEvent> toDelete);
}
