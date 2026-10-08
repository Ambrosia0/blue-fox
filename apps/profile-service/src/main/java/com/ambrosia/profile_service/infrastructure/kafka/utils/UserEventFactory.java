package com.ambrosia.profile_service.infrastructure.kafka.utils;

import java.util.UUID;

import com.ambrosia.profile_service.kafka_events.UserCreated;
import com.ambrosia.profile_service.kafka_events.UserDeleted;
import com.ambrosia.profile_service.kafka_events.UserEvent;
import com.ambrosia.profile_service.kafka_events.UserUpdated;
import com.ambrosia.profile_service.user.domain.entity.User;

import io.github.robsonkades.uuidv7.UUIDv7;

public class UserEventFactory {
    public static UserEvent createdEvent(User user){
        var created = UserCreated.newBuilder()
                .setUsername(user.getUsername())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName());
        if(user.getAvatarId() != null)
            created.setAvatarId(user.getAvatarId());

        return UserEvent.newBuilder()
            .setEventId(UUIDv7.randomUUIDString())
            .setUserId(user.getId().toString())
            .setCreated(created.build())
            .build();
    }

    public static UserEvent updatedEvent(User user){
        var updated = UserUpdated.newBuilder()
            .setUsername(user.getUsername())
            .setFirstName(user.getFirstName())
            .setLastName(user.getLastName());
        if(user.getAvatarId() != null)
            updated.setAvatarId(user.getAvatarId());
        return UserEvent.newBuilder()
            .setEventId(UUIDv7.randomUUIDString())
            .setUserId(user.getId().toString())
            .setUpdated(updated)
            .build();
    }

    public static UserEvent deletedEvent(UUID userId){
        var deleted = UserDeleted.newBuilder().build();
        return UserEvent.newBuilder()
            .setEventId(UUIDv7.randomUUIDString())
            .setUserId(userId.toString())
            .setDeleted(deleted)
            .build();
    }
}
