package com.ambrosia.collaboration_service.utils.serializers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ambrosia.collaboration_service.model.entity.message.CollaborationMessage;
import com.ambrosia.collaboration_service.model.entity.message.Message;
import com.ambrosia.collaboration_service.utils.MessageCodes;
import com.ambrosia.collaboration_service.utils.SerializerRegistry;

@Component 
public class SerializerRegistryImpl implements SerializerRegistry{
    private final Map<Class<?>, EventSerializer<?>> serializers;

    public SerializerRegistryImpl(List<EventSerializer<?>> eventSerializers){
        serializers = eventSerializers.stream()
            .collect(Collectors.toUnmodifiableMap(EventSerializer::type, v -> v));
    }

    @Override
    public Message serialize(Object event) {
        var serializer = serializers.get(event.getClass());
        if(serializer == null)
            throw new IllegalStateException(
                "Serializer not found! "+
                event.getClass()
            );
        return new CollaborationMessage(
            MessageCodes.NOTIFICATION,
            ((EventSerializer<Object>) serializer).serialize(event)
        );
    }
}
