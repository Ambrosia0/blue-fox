package com.ambrosia.comment_service.user.service;

import com.ambrosia.profile_service.kafka_events.UserEvent;

public interface UserProjectionEventProcessor {
    void process(UserEvent userEvent);
}
