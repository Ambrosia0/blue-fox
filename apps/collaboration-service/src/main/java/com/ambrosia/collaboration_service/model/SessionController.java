package com.ambrosia.collaboration_service.model;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.Assert;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import com.ambrosia.collaboration_service.model.dto.SessionInit;
import com.ambrosia.collaboration_service.model.entity.Document;
import com.ambrosia.collaboration_service.model.entity.Session;
import com.ambrosia.collaboration_service.model.entity.User;
import com.ambrosia.collaboration_service.model.entity.message.UserContextMessage;
import com.ambrosia.collaboration_service.model.entity.message.CollaborationMessage;
import com.ambrosia.collaboration_service.model.entity.message.Message;
import com.ambrosia.collaboration_service.utils.MessageCodes;
import com.ambrosia.collaboration_service.utils.ServerCloseStatus;
import com.ambrosia.collaboration_service.utils.WebSocketStatusCode;

import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
public class SessionController implements SessionControllerInterface{
    private Session session;

    private Map<UUID, ConcurrentWebSocketSessionDecorator> connections;

    private SessionController(
            SessionInit sessionInit,
            Document document
        ){
        Assert.notNull(sessionInit, "sessionInit must not be null!");
        Assert.notNull(document, "Document must not be null!");
        this.session = new Session(
            sessionInit.author(), 
            sessionInit.collaborators(),
            document
        );
        this.connections = new ConcurrentHashMap<>();
    }

    public void handleMessage(UUID userId, Message message){
        switch (message.code()) {
            case SYNC -> { // one of the clients requested sync, sending every other sync message
                broadcastExclude(
                    UserContextMessage.create(
                        message.code(), 
                        userId, 
                        new byte[0]
                    ).serialize(), 
                    userId
                );
            }
            case NOTIFICATION -> { // notifications are broadcasted to every user
                broadcast(message.serialize());
            }
            case SYNC_RESP -> { // sync response goes to specific user
                var msg = (UserContextMessage) message;
                send(msg.userId(), msg.serialize());
            }
            case MESSAGE, CURSOR, CHAT_MESSAGE -> {  // broadcasted messages from user
                broadcastExclude(
                    message.serialize(), 
                    userId
                );
            }
            default -> {} // the rest is ignored
        }
    }
    
    public void addConnection(WebSocketSession webSocketSession){
        var userId = UUID.fromString(
            ((JwtAuthenticationToken)webSocketSession.getPrincipal()).getToken().getSubject()
        );
        var old = connections.put(userId, decorate(webSocketSession));
        if(old == null){
            try {
                webSocketSession.sendMessage(CollaborationMessage
                    .create(MessageCodes.STATE, session.serialize())
                    .serialize()
                );
            } catch (IOException | IllegalStateException e) {
                close(webSocketSession, CloseStatus.PROTOCOL_ERROR, null);
                return;
            }
            session.addActiveUser(userId);
            notifyJoin(userId);
            return;
        }else{
            close(old, CloseStatus.POLICY_VIOLATION, ServerCloseStatus.DEDUPLICATED);
        }
    }

    public SessionController cleanConnectionState(UUID userId){
        connections.remove(userId);
        session.removeActiveUser(userId);
        if(connections.size() == 0){
            return null;
        }
        notifyLeave(userId);
        return this;
    }

    // called with holded mutex on session
    public SessionController changeCollaborators(Set<User> collaborators){
        session.changeCollaborators(collaborators)
            .stream()
            .forEach(t -> {
                var conn = connections.remove(t);
                notifyLeave(t);
                if(conn != null){
                    close(
                        conn, 
                        WebSocketStatusCode.FORBIDDEN.withReason("Access was closed!"), 
                        ServerCloseStatus.FORBIDDEN
                    );
                }
            });
        if(connections.size() != 0){
            notifyStateChange();
            return this;
        }else{
            return null;
        }
    }

    // called with holded mutex on session
    @Override
    public void closeSession(CloseStatus closeStatus) {
        connections.forEach((t, u) -> {
            close(u, closeStatus, ServerCloseStatus.ABORT);
        });
    }

    // called with holded mutex on session
    @Override
    public void closeConnection(UUID userId, CloseStatus closeStatus) {
        var conn = connections.get(userId);
        if(conn != null)
            close(conn, closeStatus, null);
    }

    @Override
    public SessionView getSession() {
        return new SessionWrapper(session);
    }

    private void notifyLeave(UUID userId){
        broadcast(UserContextMessage
                .create(MessageCodes.USER_LEAVE, userId, new byte[0])
                .serialize()
        );
    }

    private void notifyJoin(UUID userId){
        broadcastExclude(
                UserContextMessage
                    .create(MessageCodes.USER_JOIN, userId, session.getUser(userId).serialize())
                    .serialize(),
                userId
        );
    }

    private void notifyStateChange(){
        broadcast(CollaborationMessage
            .create(MessageCodes.STATE, session.serialize())
            .serialize()
        );
    }
    
    private void send(UUID userId, BinaryMessage binaryMessage){
        var connection = connections.get(userId);
        try {
            connection.sendMessage(binaryMessage);
        } catch (IOException | IllegalStateException e) {}
    }

    private void broadcast(BinaryMessage binaryMessage){
        var it = connections.entrySet().iterator();
        while(it.hasNext()){
            var curr = it.next();
            try {
                curr.getValue().sendMessage(binaryMessage);
            } catch (IOException | IllegalStateException e) {}
        }
    }

    private void broadcastExclude(BinaryMessage binaryMessage, UUID userId){
        var it = connections.entrySet().iterator();
        while(it.hasNext()){
            var curr = it.next();
            if(!curr.getKey().equals(userId)){
                try {
                    curr.getValue().sendMessage(binaryMessage);
                } catch (IOException | IllegalStateException e) {}
            }
        }
    }
 
    private ConcurrentWebSocketSessionDecorator decorate(WebSocketSession webSocketSessions){
        return new ConcurrentWebSocketSessionDecorator(webSocketSessions, 10000, 10 * 1024);
    }

    private void close(WebSocketSession session, CloseStatus closeStatus, @Nullable ServerCloseStatus serverCloseStatus){
        try {
            if(serverCloseStatus != null){
                session.getAttributes().put(
                    ServerCloseStatus.ATTR_CLOSED_BY_SERVER, 
                    serverCloseStatus
                );
            }
            session.close(closeStatus);
        } catch (Exception e) {}
    }

    public static SessionController create(
            SessionInit sessionInit,
            Document document
    ){
        return new SessionController(
            sessionInit, 
            document
        );
    }

    public record SessionWrapper(
        Session session
    ) implements SessionView{
        @Override
        public Collection<User> getCollaborators() {
            return session.getCollaborators();
        }

        @Override
        public boolean hasActiveUser(UUID userId) {
            return session.hasActiveUser(userId);
        }

        @Override
        public boolean hasCollaborator(UUID userId) {
            return session.hasCollaborationUser(userId);
        }
    }
}
