package com.ambrosia.collaboration_service.utils;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import com.ambrosia.collaboration_service.model.entity.message.UserContextMessage;
import com.nimbusds.jwt.PlainJWT;

public class TestBinaryWebSocketHandler extends BinaryWebSocketHandler{
    byte[] currentState = null;

    private final static Logger LOGGER = LoggerFactory.getLogger(TestBinaryWebSocketHandler.class);
    
    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        var parsedMessage = TestMessageFactory.deserialize(message.getPayload());
        BinaryMessage toSend = null;

        var uid = UUID.fromString(
            PlainJWT.parse(session.getHandshakeHeaders()
            .get(HttpHeaders.AUTHORIZATION)
            .getFirst()
            .replace("Bearer ", "")
        )
            .getJWTClaimsSet()
            .getSubject()
        );
        
        switch (parsedMessage.code()) {
            case SYNC -> {
                toSend = UserContextMessage.create(
                    MessageCodes.SYNC_RESP,
                    ((UserContextMessage)parsedMessage).userId(),
                    new byte[6]
                ).serialize();
            }
            case STATE -> {
                currentState = parsedMessage.payload();
            }
            case SYNC_RESP -> {
                currentState = parsedMessage.payload();
            }
            default -> {}
        }
        LOGGER.info("Processed {} message uid={} content={}!", parsedMessage.code().name(), uid, new String(parsedMessage.payload()));
        if(toSend != null){
            session.sendMessage(toSend);
        }
        
        session.getAttributes()
            .merge("processed", 1, (oldVal, newVal) -> {
                return (int)oldVal + (int)newVal;
            });

        session.getAttributes()
            .merge(parsedMessage.code().name(), 1, (oldVal, newVal) -> {
                return (int)oldVal + (int)newVal;
            });
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        session.getAttributes().put("processed", 0);
    }
}
