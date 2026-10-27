package com.ambrosia.report_service.user.service;

import java.util.UUID;

import com.ambrosia.profile_service.kafka_events.UserEvent;

public interface UserProjectionService {
    boolean exist(UUID id);
}
