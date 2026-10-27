package com.ambrosia.report_service.comment.service.impl;

import org.springframework.stereotype.Service;

import com.ambrosia.report_service.comment.repository.CommentProjectionRepository;
import com.ambrosia.report_service.comment.service.CommentProjectionService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommentProjectionServiceImpl implements CommentProjectionService{
    private final CommentProjectionRepository commentProjectionRepository;

    @Override
    public boolean exist(Long id) {
        return commentProjectionRepository.existsById(id);
    }
}
