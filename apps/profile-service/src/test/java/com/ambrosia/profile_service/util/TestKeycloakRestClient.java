package com.ambrosia.profile_service.util;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;

import com.ambrosia.profile_service.user.infrastructure.keycloak.utils.KeycloakConfiguration;

@TestConfiguration 
public class TestKeycloakRestClient {
    private static final Logger log = LoggerFactory.getLogger(TestKeycloakRestClient.class);

    @Bean
    @Primary
    RestClient keycloakRestClient(KeycloakConfiguration appConfiguration){
        return RestClient.builder()
            .requestFactory(new BufferingClientHttpRequestFactory(
                new SimpleClientHttpRequestFactory()
            ))
            .requestInterceptor((req, body, execution) ->{
                log.info("Rest client request: {} {}", req.getMethod(), req.getURI());
                if(body.length > 0)
                    log.info("Rest client request: body {}", new String(body));
                var resp = execution.execute(req, body);
                var respBody = StreamUtils.copyToString(
                    resp.getBody(), 
                    StandardCharsets.UTF_8
                );
                log.info("Rest client response: {}", resp.getStatusCode());
                log.info("Rest client response: body {}", respBody);
                return resp; 
            })
            .baseUrl(appConfiguration.getBaseUrl())
            .build();
    }
}
