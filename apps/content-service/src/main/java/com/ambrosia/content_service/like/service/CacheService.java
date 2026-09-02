package com.ambrosia.content_service.like.service;

import reactor.core.publisher.Mono;

public interface CacheService<K, V> {
    public Mono<Void> cache(V cacheable);
    public Mono<V> getFromCache(K key);
}
