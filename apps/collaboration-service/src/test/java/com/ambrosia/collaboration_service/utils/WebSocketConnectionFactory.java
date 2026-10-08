package com.ambrosia.collaboration_service.utils;

import java.net.URI;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;

public class WebSocketConnectionFactory {
    public static String createToken(UUID userId){
        return new PlainJWT(
        new JWTClaimsSet.Builder()
            .subject(userId.toString())
            .claim("realm_access", Map.of(
                "roles", List.of("user")
            ))
            .claim("preferred_username", "TestUsername")
            .claim("given_name", "name")
            .claim("family_name", "name")
            .issueTime(new Date())
            .expirationTime(Date.from(Instant.now().plusSeconds(2048)))
            .build()
        ).serialize();
    }

    public static StandardWebSocketClient createClient(){
        return new StandardWebSocketClient();
    }

    public static WebSocketHttpHeaders createHeaders(UUID userId){
        var headers = new WebSocketHttpHeaders();
        headers.setBearerAuth(createToken(userId));
        return headers;
    }

    public static URI buildUri(Long postId, int port){
        return URI.create("ws://localhost:"+port+"/ws/post/"+postId);
    }
}
