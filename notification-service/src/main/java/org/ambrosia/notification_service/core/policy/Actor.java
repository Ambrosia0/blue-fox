package org.ambrosia.notification_service.core.policy;

import jakarta.annotation.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class Actor {
    private UUID id;
    private Role role;
    
    public Actor(UUID id, Role role){
        this.id = id;
        this.role = role;
    }
    
    public static Actor from(@Nullable UUID id, Role role){
        if(role == null)
            throw new IllegalArgumentException("Role must not be null!");
        return new Actor(id, role);
    }
    
    public Role role(){
        return role;
    }
    
    public UUID id(){
        return id;
    }
    
    public enum Role{
        USER,
        ADMIN,
        ANONYMOUS;
        
        private static Map<String, Role> map = Arrays.asList(Role.values())
            .stream()
            .collect(Collectors.toUnmodifiableMap(k -> k.toString().toLowerCase(), v -> v));
        
        public static Role from(Set<String> roles){
            for(String role: roles){
                var mappedRole = map.get(role.toLowerCase());
                if(mappedRole != null)
                    return mappedRole;
                
            };
            return ANONYMOUS;
        }
    }
}
