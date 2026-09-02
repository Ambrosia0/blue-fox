package com.ambrosia.library_s3;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;

import com.ambrosia.library_s3.utils.S3ConfigurationProperties;

import software.amazon.awssdk.services.s3.S3Client;

@TestConfiguration
public class TestS3Configuration{
    private static final Logger log = LoggerFactory.getLogger(TestS3Configuration.class);
    
    @Bean
    RestClient testRestClient(){
        return RestClient.builder()
            .requestFactory(new BufferingClientHttpRequestFactory(
                new SimpleClientHttpRequestFactory()
            ))
            .requestInterceptor((req, body, execution) ->{
                log.debug("Rest client request: {} {} {}", req.getMethod(), req.getURI(), req.getHeaders().toString());
                if(body.length > 0)
                    log.debug("Rest client request: body {}", body.length < 10000? new String(body): "[too large body]");
                var resp = execution.execute(req, body);
                var respBody = StreamUtils.copyToString(
                    resp.getBody(), 
                    StandardCharsets.UTF_8
                );
                log.debug("Rest client response: {}", resp.getStatusCode());
                log.debug("Rest client response: body {}", respBody);
                return resp; 
            })
            .build();
    }
    @Bean
    InitializingBean createBucket(S3Client s3Client, S3ConfigurationProperties configurationProperties){
        return () -> {
            try {
                s3Client.createBucket(b -> b
                    .bucket(configurationProperties.getPublicBucket())
                    .build()
                );
            } catch (Exception e) {
                return ;
            }
        };
    }
}