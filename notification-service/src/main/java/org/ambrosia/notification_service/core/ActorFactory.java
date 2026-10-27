package org.ambrosia.notification_service.core;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.ambrosia.notification_service.core.policy.Actor;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.UUID;

@ApplicationScoped
public class ActorFactory {
    @Inject
    SecurityIdentity securityIdentity;
    
    @Inject
    JsonWebToken token;
    
    public Actor get(){
        if(securityIdentity.isAnonymous())
            return new Actor(null, Actor.Role.ANONYMOUS);
        return new Actor(
            UUID.fromString(token.getSubject()),
            Actor.Role.from(securityIdentity.getRoles())
        );
    }
}
