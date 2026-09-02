package com.ambrosia.comment_service.comment.repository.custom;

import java.util.Map.Entry;

public interface CustomCommentRepository {
    long incrementAll(Iterable<Entry<Long, Integer>> iterable);
}
