package com.ambrosia.report_service.user.service;

import java.util.UUID;

import com.ambrosia.profile_service.kafka_events.UserCreated;

public interface UserProjectionService {
    void create(UserCreated userCreated, UUID eventId);
    boolean exist(UUID id);
}
