package com.ambrosia.profile_service.config;

import java.io.IOException;
import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.ambrosia.profile_service.user.repository.UserRepository;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
@ConditionalOnProperty(
    name = "app.profile-lazy-creation",
    havingValue = "true",
    matchIfMissing = false
)
public class LazyUserCreationFilter implements Filter{
    private final UserRepository userRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
        throws IOException, ServletException{
        var req = (HttpServletRequest)request;
        if(!req.getContextPath().startsWith("/api/public")){
            var auth = (Jwt)req.getUserPrincipal();
            userRepository.saveIfNotPresent(
                    UUID.fromString(auth.getSubject()), 
                    auth.getClaimAsString("username"), 
                    auth.getClaimAsStringList("realm_role").getFirst(), 
                    auth.getClaimAsString("email")
            );
        }
        chain.doFilter(request, response);
    }
}
