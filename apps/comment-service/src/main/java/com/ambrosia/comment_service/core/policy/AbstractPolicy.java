package com.ambrosia.comment_service.core.policy;

import java.util.UUID;

import jakarta.annotation.Nullable;

public abstract class AbstractPolicy {
    protected UUID userId;

    public AbstractPolicy(@Nullable UUID userId){
        this.userId = userId;
    }

    public @Nullable UUID id(){
        return userId;
    }
}
