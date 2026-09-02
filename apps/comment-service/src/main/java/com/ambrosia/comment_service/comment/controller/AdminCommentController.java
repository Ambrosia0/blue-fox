package com.ambrosia.comment_service.comment.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.comment_service.comment.service.AdminCommentService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/comment")
public class AdminCommentController {
    private final AdminCommentService adminCommentService;

    @DeleteMapping("/{id}")
    public void deleteComment(
        @PathVariable Long id){
        adminCommentService.deleteComment(id);
    }
}
