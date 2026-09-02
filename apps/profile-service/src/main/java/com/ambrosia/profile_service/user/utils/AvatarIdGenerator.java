package com.ambrosia.profile_service.user.utils;

import java.time.Instant;

import com.ambrosia.profile_service.user.model.dto.request.FileMetadata;

public class AvatarIdGenerator {
    public static String generate(FileMetadata fileMetadata){
        return Long.toString(Instant.now().toEpochMilli())+fileMetadata.contentType().getExtension();
    }
}
