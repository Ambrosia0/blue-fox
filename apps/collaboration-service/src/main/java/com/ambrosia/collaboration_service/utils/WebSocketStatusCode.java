package com.ambrosia.collaboration_service.utils;

import org.springframework.web.socket.CloseStatus;

public final class WebSocketStatusCode {
    public static final CloseStatus FORBIDDEN = new CloseStatus(4001);
    public static final CloseStatus BAD_REQUEST = new CloseStatus(4002);
    public static final CloseStatus RESOURCE_DELETED = new CloseStatus(4003)
        .withReason("Resource is deleted!");
    public static final CloseStatus RESOURCE_PUBLISHED = new CloseStatus(4004)
        .withReason("Resource is published!");
}
