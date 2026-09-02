package com.ambrosia.content_service.util;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import com.ambrosia.content_service.community.model.entity.CommunityProjection;
import com.ambrosia.content_service.post.model.entity.Post;

public class Factory {
    public static Post createTestPost() {
        return Post.builder()
            .authorId(UUID.randomUUID())
            .content(PostTemplate.template)
            .published(true)
            .publishedAt(Instant.now())
            .visible(true)
            .title("Test Title" + ThreadLocalRandom.current().nextLong())
            .build();
    }

    public static CommunityProjection createPrivateCommunity(){
        var id = ThreadLocalRandom.current().nextLong(20L, 999_999_999L);
        return CommunityProjection.builder()
            .isPrivate(true)
            .isNew(true)
            .id(id)
            .name("TestCommunity"+id)
            .build();
    }
}
