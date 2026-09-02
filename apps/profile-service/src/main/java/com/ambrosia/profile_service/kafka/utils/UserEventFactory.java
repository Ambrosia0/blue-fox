package com.ambrosia.profile_service.kafka.utils;

import com.ambrosia.profile_service.kafka_events.UserCreated;
import com.ambrosia.profile_service.kafka_events.UserEvent;
import com.ambrosia.profile_service.user.model.entity.User;

import io.github.robsonkades.uuidv7.UUIDv7;

public class UserEventFactory {
    public static UserEvent createdEvent(User user){
        var created = UserCreated.newBuilder()
            .setId(user.getId().toString())
                .setUsername(user.getUsername())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName());
        if(user.getAvatarId() != null)
            created.setAvatarId(user.getAvatarId());

        return UserEvent.newBuilder()
            .setEventId(UUIDv7.randomUUIDString())
            .setCreated(created.build())
            .build();
    }
}
