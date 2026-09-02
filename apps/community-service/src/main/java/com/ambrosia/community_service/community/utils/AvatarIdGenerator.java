package com.ambrosia.community_service.community.utils;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

public class AvatarIdGenerator {
    public static String generateAvatarId(){
        return Long.toString(
            Instant.now().toEpochMilli())+
            "_"+
            ThreadLocalRandom.current().nextLong(1L, 999_999_999_999L);
    }
}
