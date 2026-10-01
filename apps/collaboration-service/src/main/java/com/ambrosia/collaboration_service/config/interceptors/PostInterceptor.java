package com.ambrosia.collaboration_service.config.interceptors;

import java.util.Map;

import org.jspecify.annotations.Nullable;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Component 
public class PostInterceptor implements HandshakeInterceptor{
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {
        try {
            var uri = request.getURI().getPath();
            var prefix = "/ws/post/";
            if(!uri.startsWith(prefix))
                throw new IllegalArgumentException("Invalid WebSocket path");

            var postId = uri.substring(prefix.length());

            if(postId.isBlank() || postId.contains("/")){
                throw new IllegalArgumentException("Invalid post id!");
            }

            attributes.put("postId", Long.parseLong(postId));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
            @Nullable Exception exception) {
        // TODO Auto-generated method stub
    }
}
