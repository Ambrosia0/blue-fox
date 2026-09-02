package com.ambrosia.community_service.core;

import org.jspecify.annotations.NonNull;

public interface IndexService<T> {
    void index(T t);
    void reIndex(T t);
    void removeFromIndex(@NonNull Long id);
}
