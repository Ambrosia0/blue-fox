package com.ambrosia.comment_service.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Import;

import com.ambrosia.comment_service.comment.model.dto.response.CreateCommentResponse;
import com.ambrosia.comment_service.comment.model.entity.Comment;
import com.ambrosia.comment_service.comment.repository.CommentRepository;
import com.ambrosia.comment_service.utils.PostProjectionCreator.PostWithCommunity;

@TestComponent
@Import({UserCreator.class, CommunityCreator.class})
public class CommentCreator {
    @Autowired CommentRepository commentRepository;

    @Autowired UserCreator userCreator;

    @Autowired CommunityCreator communityCreator;

    @Autowired PostProjectionCreator postProjectionCreator;

    @Autowired CommunityFollowCreator communityFollowCreator;

    public CreateCommentResponse create(Long postId, Long parentCommentId){
        var user = userCreator.create();
        return commentRepository.insert(Comment.builder()
            .content("TestContent")
            .userId(user.getId())
            .postId(postId)
            .parentCommentId(parentCommentId)
            .build()
        ).orElse(null);
    }

    public CreateCommentResponse create(Long postId){
        var user = userCreator.create();
        return commentRepository.insert(Comment.builder()
            .content("TestContent")
            .userId(user.getId())
            .postId(postId)
            .build()
        ).orElse(null);
    }

    public CreateCommentResponse createWithPost(){
        var post = postProjectionCreator.create();
        var user = userCreator.create();
        return commentRepository.insert(Comment.builder()
            .content("TestContent")
            .userId(user.getId())
            .postId(post.getId())
            .build()
        ).orElse(null); 
    }

    public CommentContext createWithPostAndCommunity(boolean isCommunityPrivate){
        var postCreateResult = postProjectionCreator.createWithCommunity(isCommunityPrivate);
        var user = userCreator.create();
        communityFollowCreator.create(
            postCreateResult.communityProjection().getId(), 
            user.getId()
        );
        return new CommentContext(
            commentRepository.insert(Comment.builder()
                .content("TestContent")
                .userId(user.getId())
                .postId(postCreateResult.postProjection().getId())
                .build()
            ).orElse(null), 
            postCreateResult
        );
    }

    public void cleanUp(){
        commentRepository.deleteAll();
    }

    public record CommentContext(
        CreateCommentResponse comment,
        PostWithCommunity postAndCommunity
    ) {}
}
