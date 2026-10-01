package com.ambrosia.content_service.post.utils.policy;

import java.util.UUID;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;

@AllArgsConstructor 
public abstract class AbstractPolicy {
    protected UUID userId;

    public @Nullable UUID userId(){
        return userId;
    };
}
