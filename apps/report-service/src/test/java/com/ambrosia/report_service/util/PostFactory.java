package com.ambrosia.report_service.util;

import java.util.concurrent.ThreadLocalRandom;

import com.ambrosia.report_service.post.entity.PostProjection;

public class PostFactory {
    public static PostProjection create(){
        return new PostProjection(
            ThreadLocalRandom.current().nextLong(1L, 999_999_999_999L),
            true
        );
    }
}
