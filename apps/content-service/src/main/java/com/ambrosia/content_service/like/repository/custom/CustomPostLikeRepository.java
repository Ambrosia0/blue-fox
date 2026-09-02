package com.ambrosia.content_service.like.repository.custom;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import com.ambrosia.content_service.like.model.entity.PostLikeKey;

public interface CustomPostLikeRepository {
    Map<Long, Long> batchSaveAll(Collection<PostLikeKey> iterable);
    Map<Long, Long> batchDeleteAll(Collection<PostLikeKey> iterable);
    int saveWithoutCheck(UUID userId, long postId);
    int returningDelete(UUID userId, long postId);
}
