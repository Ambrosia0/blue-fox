package com.ambrosia.report_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig{

    @Profile("dev")
    @Bean
    JwtDecoder jwtDecoder(@Value ("${OIDC_ISSUER_URL}") String issuer){
        var decoder = NimbusJwtDecoder.withJwkSetUri(issuer + "/protocol/openid-connect/certs").build();
        var withTimestamp = new JwtTimestampValidator();
        decoder.setJwtValidator(withTimestamp);
        return decoder;
    }

    @Bean
    SecurityFilterChain security(HttpSecurity http, ApplicationContext applicationContext) throws Exception{
        var jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        var security = http
            .csrf(csrf -> csrf.disable())
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))   
            )
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.POST,"/api/v1/report").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/v1/report/reason").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/v1/report").hasRole("admin")
                .requestMatchers("/api/v1/report/**").hasRole("admin")
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form.disable())
            .httpBasic(httpBasic -> httpBasic.disable())
            .logout(logout -> logout.disable());
        return security.build();
    }
}