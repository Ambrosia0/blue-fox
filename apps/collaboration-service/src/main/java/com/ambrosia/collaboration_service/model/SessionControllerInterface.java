package com.ambrosia.collaboration_service.model;

import java.util.Set;
import java.util.UUID;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import com.ambrosia.collaboration_service.model.entity.User;
import com.ambrosia.collaboration_service.model.entity.message.Message;

import jakarta.annotation.Nullable;

/**
 * Controls connections and the state of collaboration session
 */
public interface SessionControllerInterface {
    /**
     * Handles incoming message
     * @param userId user who sends message, or {@code null} if sender cannot be identified
     * @param message partially deserialized binary message
     */
    void handleMessage(@Nullable UUID userId, Message message);

    /**
     * Adds connection to local map
     * @param webSocketSession connection to add
     */
    void addConnection(WebSocketSession webSocketSession);
    
    /**
     * Removes the user's connection and updates session state
     * <p> The caller must hold the controller mutex.</p>
     * @param userId user whose connection is being removed
     * @return current state of the session, or {@code null} if the session 
     *          doesn't contains active users after change
     */
    @Nullable SessionController cleanConnectionState(UUID userId);

    /**
     * Changes current collaborators.
     * <p> The caller must hold the controller mutex.</p>
     * @param collaborators new set of collabooration users
     * @return current state of the session, or {@code null} if the session 
     *          doesn't contains active users after change
     */
    @Nullable SessionController changeCollaborators(Set<User> collaborators);

    /**
     * Closes all current connections.
     * <p> The caller must hold the controller mutex.</p>
     * @param closeStatus status used to close connections
     */
    void closeSession(CloseStatus closeStatus);

    /**
     * Closes user connection
     * @param userId user whose connection is being removed
     * @param closeStatus status used to close connections
     */
    void closeConnection(UUID userId, CloseStatus closeStatus);

    /**
     * Returns read-only view of current session.
     * @return session view
     */
    SessionView getSession();
}
