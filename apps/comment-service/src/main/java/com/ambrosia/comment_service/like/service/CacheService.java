package com.ambrosia.comment_service.like.service;

import java.util.concurrent.TimeUnit;

public interface CacheService<F, R> {
    void push(R r, long ttl, TimeUnit timeUnit);
    R get(F f);
}
