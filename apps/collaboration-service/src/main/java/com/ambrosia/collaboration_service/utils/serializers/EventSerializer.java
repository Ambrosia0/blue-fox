package com.ambrosia.collaboration_service.utils.serializers;

public interface EventSerializer<T> {
    byte[] serialize(T obj);
    Class<T> type();
}
