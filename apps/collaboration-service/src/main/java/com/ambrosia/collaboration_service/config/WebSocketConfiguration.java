package com.ambrosia.collaboration_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.ambrosia.collaboration_service.config.interceptors.PostInterceptor;
import com.ambrosia.collaboration_service.controller.PostCollaborationEditHandler;

import lombok.RequiredArgsConstructor;

import org.springframework.web.socket.config.annotation.EnableWebSocket;

@RequiredArgsConstructor 
@Configuration 
@EnableWebSocket 
public class WebSocketConfiguration implements WebSocketConfigurer{

    private final PostCollaborationEditHandler postCollaborationEditHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
            .addHandler(postCollaborationEditHandler, "/ws/post/*")
            .addInterceptors(new PostInterceptor());
    }
}
