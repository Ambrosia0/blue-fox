package com.ambrosia.comment_service.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import com.ambrosia.comment_service.comment.model.entity.Comment;
import com.ambrosia.comment_service.comment.repository.CommentRepository;
import com.ambrosia.comment_service.utils.factory.CommentFactory;

@TestComponent
public class CommentCreator {
    @Autowired CommentRepository commentRepository;

    @Autowired PostProjectionCreator postProjectionCreator;

    public Comment create(Long postId, Long parentCommentId){
        return commentRepository.save(
            CommentFactory.create(postId, parentCommentId)
        );
    }

    public Comment create(Long postId){
        return commentRepository.save(
            CommentFactory.create(postId, null)
        );
    }

    public Comment createFromSchratch(){
        var post = postProjectionCreator.createFromScratch();
        return commentRepository.save(
            CommentFactory.create(post.getId(), null)
        );
    }
}
