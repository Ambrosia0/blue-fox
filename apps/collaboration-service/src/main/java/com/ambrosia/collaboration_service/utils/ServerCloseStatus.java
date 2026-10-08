package com.ambrosia.collaboration_service.utils;

public enum ServerCloseStatus {
    DEDUPLICATED,
    FORBIDDEN,
    BAD_REQUEST,
    ABORT;

    public static final String ATTR_CLOSED_BY_SERVER = "closed";
}
