package com.ambrosia.content_service.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import com.ambrosia.content_service.post.model.entity.Post;
import com.ambrosia.content_service.post.repository.PostRepository;

@TestComponent
public class PostCreator {
    @Autowired PostRepository postRepository;

    public Post createFromScratch(){
        return postRepository.save(
            Factory.createTestPost()
        );
    }
}
