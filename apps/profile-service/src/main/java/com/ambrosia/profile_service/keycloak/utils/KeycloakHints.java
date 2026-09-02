package com.ambrosia.profile_service.keycloak.utils;

import org.jspecify.annotations.Nullable;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

import com.ambrosia.profile_service.kafka.dto.AdminEvent;
import com.ambrosia.profile_service.kafka.dto.UserEvent;
import com.ambrosia.profile_service.keycloak.dto.ClientMappingsRepresentation;
import com.ambrosia.profile_service.keycloak.dto.CredentialRepresentation;
import com.ambrosia.profile_service.keycloak.dto.FederatedIdentityRepresentation;
import com.ambrosia.profile_service.keycloak.dto.MappingsRepresentation;
import com.ambrosia.profile_service.keycloak.dto.RoleRepresentation;
import com.ambrosia.profile_service.keycloak.dto.SocialLinkRepresentation;
import com.ambrosia.profile_service.keycloak.dto.TokenResponse;
import com.ambrosia.profile_service.keycloak.dto.UserConsentRepresentation;
import com.ambrosia.profile_service.keycloak.dto.UserRepresentation;

public class KeycloakHints implements RuntimeHintsRegistrar{
    @Override
    public void registerHints(RuntimeHints hints, @Nullable ClassLoader classLoader) {
        hints.reflection().registerType(ClientMappingsRepresentation.class, 
            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, 
            MemberCategory.INVOKE_DECLARED_METHODS, 
            MemberCategory.ACCESS_DECLARED_FIELDS
        );
        hints.reflection().registerType(MappingsRepresentation.class, 
            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, 
            MemberCategory.INVOKE_DECLARED_METHODS, 
            MemberCategory.ACCESS_DECLARED_FIELDS
        );
        hints.reflection().registerType(RoleRepresentation.class, 
            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, 
            MemberCategory.INVOKE_DECLARED_METHODS, 
            MemberCategory.ACCESS_DECLARED_FIELDS
        );
        hints.reflection().registerType(UserRepresentation.class,
            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, 
            MemberCategory.INVOKE_DECLARED_METHODS, 
            MemberCategory.ACCESS_DECLARED_FIELDS
        );
        hints.reflection().registerType(TokenResponse.class, 
            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, 
            MemberCategory.INVOKE_DECLARED_METHODS, 
            MemberCategory.ACCESS_DECLARED_FIELDS
        );
        hints.reflection().registerType(UserConsentRepresentation.class, 
            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, 
            MemberCategory.INVOKE_DECLARED_METHODS, 
            MemberCategory.ACCESS_DECLARED_FIELDS
        );
        hints.reflection().registerType(SocialLinkRepresentation.class, 
            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, 
            MemberCategory.INVOKE_DECLARED_METHODS, 
            MemberCategory.ACCESS_DECLARED_FIELDS
        );
        hints.reflection().registerType(FederatedIdentityRepresentation.class, 
            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, 
            MemberCategory.INVOKE_DECLARED_METHODS, 
            MemberCategory.ACCESS_DECLARED_FIELDS
        );
        hints.reflection().registerType(CredentialRepresentation.class, 
            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, 
            MemberCategory.INVOKE_DECLARED_METHODS, 
            MemberCategory.ACCESS_DECLARED_FIELDS
        );
        hints.reflection().registerType(UserEvent.class, 
            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, 
            MemberCategory.INVOKE_DECLARED_METHODS, 
            MemberCategory.ACCESS_DECLARED_FIELDS
        );
        hints.reflection().registerType(UserEvent.EventType.class, 
            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, 
            MemberCategory.INVOKE_DECLARED_METHODS, 
            MemberCategory.ACCESS_DECLARED_FIELDS
        );
        hints.reflection().registerType(UserEvent.UserDetails.class, 
            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, 
            MemberCategory.INVOKE_DECLARED_METHODS, 
            MemberCategory.ACCESS_DECLARED_FIELDS
        );
        hints.reflection().registerType(AdminEvent.class, 
            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, 
            MemberCategory.INVOKE_DECLARED_METHODS, 
            MemberCategory.ACCESS_DECLARED_FIELDS
        );
        hints.reflection().registerType(AdminEvent.OperationType.class, 
            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, 
            MemberCategory.INVOKE_DECLARED_METHODS, 
            MemberCategory.ACCESS_DECLARED_FIELDS
        );
    }
}