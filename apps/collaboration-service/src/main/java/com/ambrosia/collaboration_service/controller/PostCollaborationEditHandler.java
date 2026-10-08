package com.ambrosia.collaboration_service.controller;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.springframework.context.event.EventListener;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.ambrosia.collaboration_service.grpc.ContentService;
import com.ambrosia.collaboration_service.model.SessionController;
import com.ambrosia.collaboration_service.model.dto.SessionInit;
import com.ambrosia.collaboration_service.model.entity.message.Message;
import com.ambrosia.collaboration_service.model.entity.post.PostDocument;
import com.ambrosia.collaboration_service.utils.SerializerRegistry;
import com.ambrosia.collaboration_service.utils.ServerCloseStatus;
import com.ambrosia.collaboration_service.utils.WebSocketStatusCode;
import com.ambrosia.collaboration_service.utils.mapper.UserMapper;
import com.ambrosia.content_service.kafka_events.collaboration.CollaborationEvent;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
@RequiredArgsConstructor 
@Component 
public class PostCollaborationEditHandler implements WebSocketHandler{
    private Map<Long, SessionController> sessions = new ConcurrentHashMap<>(128, 0.8f);

    private final ContentService contentService;

    private final UserMapper userMapper;

    private final SerializerRegistry serializerRegistry;

    @Override
    public void afterConnectionEstablished(WebSocketSession newWebSession) throws Exception {
        var auth = (JwtAuthenticationToken)newWebSession.getPrincipal();
        var currentDocId = (Long) newWebSession.getAttributes().get("postId");

        var userId = UUID.fromString(auth.getToken().getSubject());

        var sessionController = sessions.computeIfAbsent(currentDocId, postId -> {
            var postOpt = contentService.getCollaborationContent(postId);
            if(postOpt.isEmpty() || postOpt.get().getCollaborationUsersCount() == 0){
                close(
                    newWebSession, 
                    WebSocketStatusCode.BAD_REQUEST, 
                    ServerCloseStatus.BAD_REQUEST
                );
                return null;
            }
            var post = postOpt.get();

            var authorId = UUID.fromString(post.getAuthor().getId());
            var collaborationUserIds = post
                    .getCollaborationUsersList()
                    .stream()
                    .map(t -> UUID.fromString(t.getId()))
                    .collect(Collectors.toSet());
            
            if(!collaborationUserIds.contains(userId) && !authorId.equals(userId)){
                return null;
            } else{
                return SessionController.create(
                    new SessionInit(
                        userMapper.toUser(post.getAuthor()),
                        post.getCollaborationUsersList()
                            .stream()
                            .map(userMapper::toUser)
                            .toList()
                    ), 
                    new PostDocument(
                        postId,
                        new AtomicReference<byte[]>(post.getContentBytes().toByteArray())
                    )
                );
            }
        });
        if(sessionController != null && sessionController.getSession().hasCollaborator(userId)){
            sessionController.addConnection(newWebSession);
        }else{
            close(
                newWebSession,
                WebSocketStatusCode.FORBIDDEN,
                ServerCloseStatus.FORBIDDEN
            );
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSession, CloseStatus closeStatus) throws Exception {
        var postId = webSession.getAttributes().get("postId");

        var skipStateCleanup = webSession.getAttributes().get(ServerCloseStatus.ATTR_CLOSED_BY_SERVER) != null;
        
        // Ignores close if state should not be cleaned up
        if(skipStateCleanup){
            return;
        }

        // Clears connection state
        sessions.compute(
            (Long)postId,
            (key, value) -> {
                var auth = (JwtAuthenticationToken)webSession.getPrincipal();
                var id = UUID.fromString(auth.getToken().getSubject());
                return value.cleanConnectionState(id);
            }
        );
    }

    @Override
    public void handleMessage(WebSocketSession webSession, WebSocketMessage<?> message) throws Exception {
        if(!(message instanceof BinaryMessage)){
            return;
        }
        var binaryMessage = (BinaryMessage)message;

        var auth = (JwtAuthenticationToken)webSession.getPrincipal();
        var userId = UUID.fromString(auth.getToken().getSubject());

        var postId = (Long)webSession.getAttributes().get("postId");

        Message serializedMessage = null;
        try {
            serializedMessage = Message.deserialize(binaryMessage.getPayload());
        } catch (IllegalArgumentException e) {
            sessions.get(postId).closeConnection(userId, CloseStatus.PROTOCOL_ERROR);
            return;
        }

        if(serializedMessage == null)
            return;

        sessions.get(postId).handleMessage(
            userId,
            serializedMessage
        );

        
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {}

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    @EventListener 
    public void on(CollaborationEvent collaborationEvent){
        processEvent(collaborationEvent);
    }

    private void processEvent(CollaborationEvent collaborationEvent){
        switch (collaborationEvent.getEventCase()) {
            case ATTACHMENT_DELETE -> {
                var sessionController = sessions.get(collaborationEvent.getPostId());
                if(sessionController == null)
                    return;
                sessionController.handleMessage(
                    null, 
                    serializerRegistry.serialize(collaborationEvent.getAttachmentDelete())
                );
            }
            case ATTACHMENT_CREATE -> {
                var sessionController = sessions.get(collaborationEvent.getPostId());
                if(sessionController == null)
                    return;
                sessionController.handleMessage(
                    null,
                    serializerRegistry.serialize(collaborationEvent.getAttachmentCreate())
                );
            }
            case DELETE -> {
                sessions.computeIfPresent(
                    collaborationEvent.getPostId(), 
                    (key, value) -> {
                        value.closeSession(WebSocketStatusCode.RESOURCE_DELETED);
                        return null;
                    }
                );
            }
            case UPDATE -> {
                sessions.computeIfPresent(
                    collaborationEvent.getPostId(), 
                    (key, value) -> {
                        var event = collaborationEvent.getUpdate();
                        if(event.getIsPublished()){
                            value.closeSession(WebSocketStatusCode.RESOURCE_PUBLISHED);
                            return null;
                        }else{
                            return value.changeCollaborators(event.getCollaborationUsersList()
                                .stream()
                                .map(userMapper::toUser)
                                .collect(Collectors.toSet())
                            );
                        }
                    }
                );
            }
            default -> {}
        }
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

    Map<Long, SessionController> getSessionControllers(){
        return Map.copyOf(sessions);
    }
}
