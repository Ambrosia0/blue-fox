package com.ambrosia.collaboration_service.model.entity;

import jakarta.annotation.Nullable;

public interface Document {
    /**
     * Returns id of the document
     * @return
     */
    Object getId();
    
    /**
     * Returns state of the document
     * @return byte array with state
     */
    @Nullable byte[] getContent();
}
