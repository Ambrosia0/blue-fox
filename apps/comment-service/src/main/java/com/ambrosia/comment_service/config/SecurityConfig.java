package com.ambrosia.comment_service.config;

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
import org.springframework.web.client.RestClient;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
    @Bean
    public RestClient restClient(){
        return RestClient.create();
    }

    @Profile("dev")
    @Bean
    public JwtDecoder jwtDecoder(@Value("${OIDC_ISSUER_URL}") String issuer){
        var decoder = NimbusJwtDecoder.withIssuerLocation(issuer).build();
        var withTimestamp = new JwtTimestampValidator();
        decoder.setJwtValidator(withTimestamp);
        return decoder;
    }

    @Bean
    SecurityFilterChain apiFilterChain(HttpSecurity http){
        var jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        return http
            .csrf(csrf -> csrf.disable())
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
            )
            .authorizeHttpRequests(
                authorize -> authorize
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/user/**").authenticated()
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated()
            )
            .httpBasic(basic -> basic.disable())
            .logout(logout -> logout.disable())
            .formLogin(login -> login.disable())
            .build();
        }
}
