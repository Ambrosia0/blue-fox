package com.ambrosia.profile_service.keycloak.service.impl;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import com.ambrosia.profile_service.exception.internal.IdpServiceUnavailableException;
import com.ambrosia.profile_service.keycloak.dto.MappingsRepresentation;
import com.ambrosia.profile_service.keycloak.dto.TokenResponse;
import com.ambrosia.profile_service.keycloak.dto.UserRepresentation;
import com.ambrosia.profile_service.keycloak.service.KeycloakAdminClient;
import com.ambrosia.profile_service.keycloak.utils.KeycloakConfiguration;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of Keycloak Admin REST API service with OIDC-authorization
 * Provides CRUD operations for IdP users {@link UserRepresentation}
 */
@Slf4j
@Service
public final class KeycloakServiceAccountAuthServiceImpl implements KeycloakAdminClient{
    private RestClient restClient;
    private String authInfo;
    private AtomicReference<TokenResponse> accessToken;
    private final KeycloakConfiguration appConfiguration;

    public KeycloakServiceAccountAuthServiceImpl(
            KeycloakConfiguration appConfiguration, 
            RestClient keycloakRestClient) throws Exception{
        this.appConfiguration = appConfiguration;
        this.restClient = keycloakRestClient;

        // credentials for openid token request
        this.authInfo = String.format(
            "grant_type=client_credentials&client_id=%s&client_secret=%s",
            appConfiguration.getClientId(),
            appConfiguration.getSecret());
        this.accessToken = new AtomicReference<>(requestToken());
        
        if(accessToken.get().expiresIn() < appConfiguration.getAuthKeyRotationTime()*60)
            log.warn("Key rotation time is less than token expiration time!");
        log.info("Successfully instantiated keycloak service!");
    }

    @CircuitBreaker(name = "keycloak", fallbackMethod = "fallback")
    @Override
    public void register(UserRepresentation userRepresentation){
        withAuthRetry(() -> registerUser(userRepresentation));
    }

    @CircuitBreaker(name = "keycloak", fallbackMethod = "fallback")
    @Override
    public void update(UserRepresentation userRepresentation) {
        withAuthRetry(() -> updateUser(userRepresentation));
    }

    @CircuitBreaker(name = "keycloak", fallbackMethod = "fallback")
    @Override
    public void delete(UUID userId){
        withAuthRetry(() -> deleteUser(userId.toString()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<MappingsRepresentation> getRoleMappings(UUID userId) {
        try {
            return (Optional<MappingsRepresentation>) withAuthRetryWithResult(() -> getMappings(userId));
        } catch (Exception e) {
            throw new IdpServiceUnavailableException();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<UserRepresentation> get(UUID userId) {
        try {
            return (Optional<UserRepresentation>) withAuthRetryWithResult(() -> getUser(userId));
        } catch (Exception e) {
            throw new IdpServiceUnavailableException();
        }
    }


    @Scheduled(fixedRateString = "${app.keycloak.auth-key-rotation-time}", timeUnit = TimeUnit.MINUTES)
    public void refresh(){
        try {
            accessToken.set(requestToken());
        } catch (RestClientResponseException e) {
            log.error("Can't refresh token! Status: {} Body: {}", e.getStatusCode(), e.getCause());
        }
    }

    /**
     * @see "https://openid.net/specs/openid-connect-core-1_0.html#AuthorizationEndpoint"
     * @see "https://datatracker.ietf.org/doc/html/rfc6749#section-4.3.2"
     */
    @CircuitBreaker(name = "keycloak", fallbackMethod = "fallback")
    private TokenResponse requestToken(){
        return restClient
                .post()
                .uri("/realms/{keycloakRealm}/protocol/openid-connect/token", appConfiguration.getRealm())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(authInfo)
                .retrieve()
                .toEntity(TokenResponse.class)
                .getBody();
    }

    /**
     * @see "https://www.keycloak.org/docs-api/latest/rest-api/index.html#_post_adminrealmsrealmusers"
     */
    private ResponseEntity<Void> registerUser(UserRepresentation userRepresentation){
        Assert.notNull(userRepresentation, "User representation must not be null!");
        var token = accessToken.get();
        return restClient.post()
            .uri("/admin/realms/{keycloakRealm}/users", appConfiguration.getRealm())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, token.tokenType()+" "+token.accessToken())
            .body(userRepresentation)
            .retrieve()
            .onStatus(t -> !t.is2xxSuccessful(), (req, resp) ->{
                log.error(
                    "Can't register user in keycloak userId={}! Status: {} Body: {}",
                    userRepresentation.getId(),
                    resp.getStatusCode(), 
                    new String(resp.getBody().readAllBytes()));
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't register!");
            })
            .toBodilessEntity();
    }

    /**
     * @see "https://www.keycloak.org/docs-api/latest/rest-api/index.html#_delete_adminrealmsrealmusersuser_id"
     */
    private ResponseEntity<Void> deleteUser(String userId){
        Assert.notNull(userId, "User id must not be null!");
        var token = accessToken.get();
        return restClient
            .delete()
            .uri("/admin/realms/{keycloakRealm}/users/{userId}", appConfiguration.getRealm(), userId)
            .header(HttpHeaders.AUTHORIZATION, token.tokenType()+" "+token.accessToken())
            .retrieve()
            .onStatus(t -> !t.is2xxSuccessful(), (req, resp) ->{
                log.error(
                    "Can't delete user in keycloak userId={}! Status: {} Body: {}", 
                    userId,
                    resp.getStatusCode(), 
                    new String(resp.getBody().readAllBytes()));
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't delete user!");
            })
            .toBodilessEntity();
    }

    /**
     * @see "https://www.keycloak.org/docs-api/latest/rest-api/index.html#_put_adminrealmsrealmusersuser_id" 
     */
    private ResponseEntity<Void> updateUser(UserRepresentation userRepresentation){
        Assert.notNull(userRepresentation, "User representation must not be null!");
        Assert.notNull(userRepresentation.getId(), "User id must not be null!");
        var token = accessToken.get();
        return restClient.put()
            .uri("/admin/realms/{keycloakRealm}/users/{userId}", appConfiguration.getRealm(), userRepresentation.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, token.tokenType()+" "+token.accessToken())
            .body(userRepresentation)
            .retrieve()
            .onStatus(t -> !t.is2xxSuccessful(), (req, resp) ->{
                log.error(
                    "Can't update user in keycloak userId={}! Status: {} Body: {}",
                    userRepresentation.getId(),
                    resp.getStatusCode(), 
                    new String(resp.getBody().readAllBytes()));
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't update info!");
            })
            .toBodilessEntity();
    }

    /**
     * @see "https://www.keycloak.org/docs-api/latest/rest-api/index.html#_get_adminrealmsrealmusersuser_id"
     */
    private Optional<UserRepresentation> getUser(UUID userId) {
        Assert.notNull(userId, "Id must not be null!");
        var token = accessToken.get();
        return restClient.get()
            .uri("/admin/realms/{keycloakRealm}/users/{id}", appConfiguration.getRealm(), userId)
            .header(HttpHeaders.AUTHORIZATION, token.tokenType()+" "+token.accessToken())
            .accept(MediaType.ALL)
            .exchange((req, resp) ->{
                if(resp.getStatusCode().is2xxSuccessful())
                    return Optional.of(resp.bodyTo(UserRepresentation.class));
                if(resp.getStatusCode() == HttpStatus.NOT_FOUND)
                    return Optional.empty();
                log.error(
                    "Can't get user from keycloak! Status: {} Body: {}", 
                    resp.getStatusCode(),
                    new String(resp.getBody().readAllBytes())
                );
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Can't get user!");
            });
    }

    private Optional<MappingsRepresentation> getMappings(UUID userId){
        Assert.notNull(userId, "Id must not be null!");
        var token = accessToken.get();
        return restClient.get()
            .uri("/admin/realms/{keycloakRealm}/users/{id}/role-mappings", appConfiguration.getRealm(), userId)
            .header(HttpHeaders.AUTHORIZATION, token.tokenType()+" "+token.accessToken())
            .accept(MediaType.ALL)
            .exchange((req, resp) ->{
                if(resp.getStatusCode().is2xxSuccessful())
                    return Optional.of(resp.bodyTo(MappingsRepresentation.class));
                if(resp.getStatusCode() == HttpStatus.NOT_FOUND)
                    return Optional.empty();
                log.error(
                    "Can't get role mappings from keycloak! Status: {} Body: {}", 
                    resp.getStatusCode(),
                    new String(resp.getBody().readAllBytes())
                );
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Can't get role mappings!");
            });
    }

    // retries with token rerequest
    private void withAuthRetry(Supplier<ResponseEntity<?>> requestSupplier){
        try {
            requestSupplier.get();
        } catch (HttpClientErrorException.Unauthorized e) {
            log.error("Unexpected token expiration!", e);
            accessToken.set(requestToken());
            requestSupplier.get();
        }
    }

    private Object withAuthRetryWithResult(Supplier<Object> requestSupplier){
        try {
            return requestSupplier.get();
        } catch (HttpClientErrorException.Unauthorized e) {
            log.error("Unexpected token expiration!", e);
            accessToken.set(requestToken());
            return requestSupplier.get();
        }
    }

    @SuppressWarnings("unused")
    private void fallback(UUID userId, ResourceAccessException e){
        throw new IdpServiceUnavailableException();
    }

    @SuppressWarnings("unused")
    private void fallback(UUID userId, HttpServerErrorException e){
        throw new IdpServiceUnavailableException();
    }

    @SuppressWarnings("unused")
    private void fallback(UUID userId, RuntimeException e){
        throw e;
    }

    @SuppressWarnings("unused")
    private void fallback(UserRepresentation userRepresentation, ResourceAccessException e){
        throw new IdpServiceUnavailableException();
    }

    @SuppressWarnings("unused")
    private void fallback(UserRepresentation userRepresentation, HttpServerErrorException e){
        throw new IdpServiceUnavailableException();
    }

    @SuppressWarnings("unused")
    private void fallback(UserRepresentation userRepresentation, RuntimeException e){
        throw e;
    }
}
