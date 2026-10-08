package com.ambrosia.community_service.community.utils;

import java.security.SecureRandom;
import java.time.Instant;

import org.springframework.stereotype.Component;

@Component
public class AvatarIdGenerator {
    private final SecureRandom random = new SecureRandom();
    
    public String generateAvatarId(){
        return Long.toString(
            Instant.now().toEpochMilli())+
            "_"+
            random.nextLong(1L, Long.MAX_VALUE);
    }
}
