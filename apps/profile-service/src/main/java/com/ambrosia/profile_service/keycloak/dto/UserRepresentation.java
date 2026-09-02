package com.ambrosia.profile_service.keycloak.dto;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @see "https://www.keycloak.org/docs-api/latest/rest-api/index.html#UserRepresentation"
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRepresentation{
    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("id")
    private String id;

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("username")
    private String username;

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("firstName")
    private String firstName;

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("lastName")
    private String lastName;

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("email")
    private String email;

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("emailVerified")
    private Boolean emailVerified;

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("attributes")
    private Map<String, List<String>> attributes;
    
    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("userProfileMetadata")
    private String userProfileMetadata;

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("enabled")
    private Boolean enabled;

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("self")
    private String self;

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("origin")
    private String origin;

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("createdTimestamp")
    private Long createdTimestamp;

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("totp")
    private Boolean totp;

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("federationLink")
    private String federationLink;

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("serviceAccountClientId")
    private String serviceAccountClientId;

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("credentials")
    private List<CredentialRepresentation> credentials;

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("disableableCredentialTypes")
    private Set<String> disableableCredentialTypes;

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("requiredActions")
    private List<String> requiredActions;

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("federatedIdentities")
    List<FederatedIdentityRepresentation> federatedIdentities;

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("realmRoles")
    private List<String> realmRoles;

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("clientRoles")
    private Map<String, List<String>> clientRoles;

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("clientConsents")
    private List<UserConsentRepresentation> clientConsents;
    
    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("notBefore")
    private Integer notBefore;

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("applicationRoles")
    private Map<String, List<String>> applicationRoles;

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("socialLinks")
    private List<SocialLinkRepresentation> socialLinks;

    @JsonProperty("groups")
    @JsonInclude(value = Include.NON_NULL)
    private List<String> groups;

    @JsonInclude(value = Include.NON_NULL)
    @JsonProperty("access")
    private Map<String, Boolean> access;

}