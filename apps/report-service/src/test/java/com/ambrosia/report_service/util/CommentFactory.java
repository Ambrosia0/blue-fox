package com.ambrosia.report_service.util;

import java.util.concurrent.ThreadLocalRandom;

import com.ambrosia.report_service.comment.entity.CommentProjection;

public class CommentFactory {
    public static CommentProjection create(){
        return new CommentProjection(
            ThreadLocalRandom.current().nextLong(1L, 999_999_999_999L),
            true
        );
    }
}
