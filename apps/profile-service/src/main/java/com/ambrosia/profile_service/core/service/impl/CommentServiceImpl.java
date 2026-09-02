package com.ambrosia.profile_service.core.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.ambrosia.profile_service.core.service.CommentService;
import com.ambrosia.profile_service.exception.internal.CommentServiceUnavailableException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService{
    private final RestClient restClient;

    @Override
    public boolean isExists(long commentId) {
        return restClient.get()
            .uri("http://comment-service/internal/comment/{id}", commentId)
            .retrieve()
            .onStatus(t -> !t.is2xxSuccessful(), (req, resp) ->{
                log.error("Comment service unavailable! {} {}", 
                    resp.getStatusCode(), new String(resp.getBody().readAllBytes()));
                throw new CommentServiceUnavailableException();
            })
            .toEntity(Boolean.class)
            .getBody();
    }
}
