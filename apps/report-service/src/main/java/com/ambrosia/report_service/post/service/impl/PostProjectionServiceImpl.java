package com.ambrosia.report_service.post.service.impl;

import org.springframework.stereotype.Service;

import com.ambrosia.report_service.post.repository.PostProjectionRepository;
import com.ambrosia.report_service.post.service.PostProjectionService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PostProjectionServiceImpl implements PostProjectionService{
    private final PostProjectionRepository postProjectionRepository;

    @Override
    public boolean exist(Long id) {
        return postProjectionRepository.existsById(id);
    }
}
