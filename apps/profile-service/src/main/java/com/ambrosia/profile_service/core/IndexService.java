package com.ambrosia.profile_service.core;

public interface IndexService<T> {
    void index(T t);
    void reIndex(T t);
    void removeFromIndex(String id);
}
