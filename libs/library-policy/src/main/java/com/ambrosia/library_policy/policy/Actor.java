package com.ambrosia.library_policy.policy;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.Assert;

import jakarta.annotation.Nullable;

public record Actor(
    UUID id,
    Role role
) implements PolicyActor{
    public enum Role{
        ADMIN,
        USER,
        ANONYMOUS;

        private static Map<String, Role> map = Arrays.asList(Role.values())
            .stream()
            .collect(Collectors.toUnmodifiableMap(k -> k.toString().toLowerCase(), v -> v));

        public static Role fromCollection(Collection<? extends GrantedAuthority> roles){
            var it = roles.iterator();
            while(it.hasNext()){
                var cur = map.get(it.next().getAuthority().toLowerCase());
                if(cur != null)
                    return cur;
            }
            return USER;
        }
    }

    public static Actor from(Authentication authentication){
        if(authentication instanceof AnonymousAuthenticationToken)
            return new Actor(null, Role.ANONYMOUS);

        if(authentication instanceof JwtAuthenticationToken jwt){
            return new Actor(
                UUID.fromString(jwt.getToken().getSubject()),
                Role.fromCollection(authentication.getAuthorities())
            );
        }

        throw new IllegalArgumentException("Unsupported authentication type: "+authentication.getClass());
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder{
        private UUID id;
        private Role role;

        public Builder id(@Nullable UUID id){
            this.id = id;
            return this;
        }

        public Builder role(Role role){
            this.role = role;
            return this;
        }

        public Actor build(){
            Assert.notNull(role, "Role must be not null!");
            if(role != Role.ANONYMOUS && id == null)
                throw new IllegalArgumentException("Non-anonymous should have id!");
            if(role == Role.ANONYMOUS && id != null)
                throw new IllegalArgumentException("Anonymous user should not have an id!");
            return new Actor(
                id,
                role
            );
        }
    }
}
