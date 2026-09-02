package com.ambrosia.profile_service.core.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.ambrosia.profile_service.core.service.ContentService;
import com.ambrosia.profile_service.exception.internal.ContentServiceUnavailableException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequiredArgsConstructor
@Service
public class ContentServiceImpl implements ContentService{
    private final RestClient restClient;
    
    @Override
    public boolean isExists(Long postId) {
        return restClient
            .get()
            .uri("http://content-service/internal/post/{id}", postId)
            .retrieve()
            .onStatus(t -> !t.is2xxSuccessful(), (req, resp) ->{
                log.error(
                    "Can't access content service: {} {}", 
                    resp.getStatusCode(), new String(resp.getBody().readAllBytes()));
                throw new ContentServiceUnavailableException();
            })
            .toEntity(Boolean.class)
            .getBody();
    }
}
