package com.ambrosia.collaboration_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Profile("!test")
@Configuration 
@EnableWebSecurity  
public class WebSocketSecurityConfiguration {
    
    @Profile("dev")
    @Bean
    JwtDecoder jwtDecoder(@Value ("${OIDC_ISSUER_URL}") String issuer){
        var decoder = NimbusJwtDecoder.withJwkSetUri(issuer + "/protocol/openid-connect/certs").build();
        var withTimestamp = new JwtTimestampValidator();
        decoder.setJwtValidator(withTimestamp);
        return decoder;
    }

    @Bean 
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity){
        var jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());

        return httpSecurity
            .csrf(csrf -> csrf.disable())
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
            )
            .authorizeHttpRequests(authorize -> 
                authorize.requestMatchers("/ws/**").authenticated()   
            )
            .formLogin(t -> t.disable())
            .httpBasic(httpBasic -> httpBasic.disable())
            .logout(logout -> logout.disable())
            .build();
    }
}
