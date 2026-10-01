package com.ambrosia.collaboration_service;


import java.time.Instant;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import com.ambrosia.collaboration_service.config.KeycloakRoleConverter;

import com.nimbusds.jwt.PlainJWT;

@Primary 
@TestConfiguration 
@EnableWebSocket
public class TestSecurityConfiguration{

    @Bean 
    @Primary 
    JwtDecoder jwtDecoder(){
        return token -> {
            return parseJwt(token);
        };
    }

    @Bean 
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity){
        var jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        return httpSecurity
            .formLogin(t -> t.disable())
            .httpBasic(t -> t.disable())
            .formLogin(t -> t.disable())
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(t -> t.decoder(jwtDecoder())))
            .csrf(t -> t.disable())
            .build();
    }

    private Jwt parseJwt(String token){
        try {
            var parsed = PlainJWT.parse(token);
            var claims = parsed.getJWTClaimsSet();
            return Jwt.withTokenValue(token)
                .subject(claims.getSubject())
                .claim("realm_access", claims.getJSONObjectClaim("realm_access"))
                .claim("preferred_username", claims.getStringClaim("preferred_username"))
                .claim("given_name", claims.getStringClaim("given_name"))
                .claim("family_name", claims.getStringClaim("family_name"))
                .header("alg", "none")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(2048))
                .build();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
