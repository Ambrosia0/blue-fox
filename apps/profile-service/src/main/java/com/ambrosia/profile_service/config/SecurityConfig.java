package com.ambrosia.profile_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.grpc.server.GlobalServerInterceptor;
import org.springframework.grpc.server.security.AuthenticationProcessInterceptor;
import org.springframework.grpc.server.security.GrpcSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig{

    @Bean
    RestClient.Builder restClientBuilder(){
        return RestClient.builder();
    }

    @Bean
    RestClient restClient(RestClient.Builder builder){
        return builder
            .build();
    }

    @Profile("dev")
    @Bean
    JwtDecoder jwtDecoder(@Value("${OIDC_ISSUER_URL}") String issuer){
        var decoder = NimbusJwtDecoder.withIssuerLocation(issuer).build();
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
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("admin")
                .requestMatchers("/api/user/**", "/api/me/**").authenticated()
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form.disable())
            .httpBasic(httpBasic -> httpBasic.disable())
            .logout(logout -> logout.disable());
        if(applicationContext.containsBean("lazyUserCreationFilter"))
            security.addFilter(applicationContext.getBean(LazyUserCreationFilter.class));
        return security.build();
    }

    @Bean
    @GlobalServerInterceptor
    AuthenticationProcessInterceptor grpcFilterChain(GrpcSecurity grpc) throws Exception{
        return grpc
            .authorizeRequests(requests -> requests
                .allRequests().permitAll()
            )
            .build();
    }
}