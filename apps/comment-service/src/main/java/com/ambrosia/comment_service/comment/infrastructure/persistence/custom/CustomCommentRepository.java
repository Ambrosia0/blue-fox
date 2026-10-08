package com.ambrosia.comment_service.comment.infrastructure.persistence.custom;

import java.util.Map.Entry;

public interface CustomCommentRepository {
    long incrementAll(Iterable<Entry<Long, Integer>> iterable);
}
