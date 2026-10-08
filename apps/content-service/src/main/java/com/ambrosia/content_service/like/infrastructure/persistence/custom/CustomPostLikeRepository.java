package com.ambrosia.content_service.like.infrastructure.persistence.custom;

import java.util.Collection;
import java.util.Map;

import com.ambrosia.content_service.like.infrastructure.entity.PostLikeKey;

public interface CustomPostLikeRepository {
    Map<Long, Long> batchSaveAll(Collection<PostLikeKey> iterable);
    Map<Long, Long> batchDeleteAll(Collection<PostLikeKey> iterable);
}
