package com.ambrosia.comment_service.comment.service.impl;

import org.springframework.stereotype.Service;

import com.ambrosia.comment_service.comment.repository.CommentRepository;
import com.ambrosia.comment_service.comment.service.AdminCommentService;
import com.ambrosia.comment_service.exceptions.api.CommentDoesntExistException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AdminCommentServiceImpl implements AdminCommentService {
    private final CommentRepository commentRepository;
    
    @Override
    public void deleteComment(long commentId) {
        var res = commentRepository.hideCommentById(commentId);
        if(res == 0)
            throw new CommentDoesntExistException();
    }
}
