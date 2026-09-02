package com.ambrosia.profile_service.kafka.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @see https://github.com/keycloak/keycloak/blob/main/server-spi-private/src/main/java/org/keycloak/events/Event.java
 */
public record UserEvent(
    @JsonProperty("userId")
    String userId,

    @JsonProperty("realmName")
    String realmName,

    /**
     * @see https://github.com/keycloak/keycloak/blob/main/server-spi-private/src/main/java/org/keycloak/events/EventType.java
     */
    @JsonProperty("type")
    String type,

    @JsonProperty("details")
    UserDetails userDetails
) {
    public enum EventType{
        LOGIN(0),
        REGISTER(1),
        LOGOUT(2),
        UPDATE_EMAIL(10),
        UPDATE_PROFILE(11),
        VERIFY_EMAIL(14),
        VERIFY_PROFILE(15),
        DELETE_ACCOUNT(50),
        UPDATE_CREDENTIAL(56),
        REMOVE_CREDENTIAL(57);

        private final int stableIndex;

        private EventType(int stableIndex){
            this.stableIndex = stableIndex;
        }
        
        @JsonGetter
        public int getStableIndex(){
            return this.stableIndex;
        }
    }

    public record UserDetails(
        String email,
        String username,
        String firstName,
        String lastName,
        Boolean enabled,
        Map<String, List<String>> attributes
    ) {}

}
