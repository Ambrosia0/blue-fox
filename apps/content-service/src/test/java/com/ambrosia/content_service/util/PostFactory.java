package com.ambrosia.content_service.util;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import com.ambrosia.content_service.post.model.entity.Post;

public class PostFactory {
    public static Post createTestPost() {
        return Post.builder()
            .authorId(UUID.randomUUID())
            .content(PostTemplate.template)
            .published(true)
            .publishedAt(Instant.now())
            .visible(true)
            .isNew(true)
            .title("Test Title" + ThreadLocalRandom.current().nextLong())
            .build();
    }
}
