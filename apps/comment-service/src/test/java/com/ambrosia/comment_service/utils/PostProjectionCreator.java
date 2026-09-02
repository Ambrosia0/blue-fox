package com.ambrosia.comment_service.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import com.ambrosia.comment_service.post.model.entity.PostProjection;
import com.ambrosia.comment_service.post.repository.PostProjectionRepository;
import com.ambrosia.comment_service.utils.factory.PostProjectionFactory;

@TestComponent
public class PostProjectionCreator {
    @Autowired PostProjectionRepository postProjectionRepository;

    public PostProjection createFromScratch(Long communityId){
        return postProjectionRepository.save(
            PostProjectionFactory.createProjection(communityId)
        );
    }
    public PostProjection createFromScratch(){
        return postProjectionRepository.save(
            PostProjectionFactory.createProjection()
        );
    }
}
