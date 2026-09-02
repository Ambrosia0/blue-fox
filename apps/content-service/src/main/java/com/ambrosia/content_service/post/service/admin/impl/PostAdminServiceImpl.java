package com.ambrosia.content_service.post.service.admin.impl;

import org.springframework.stereotype.Service;

import com.ambrosia.content_service.post.model.dto.response.PostContentResponse;
import com.ambrosia.content_service.post.service.PostQueryService;
import com.ambrosia.content_service.post.service.admin.PostAdminService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PostAdminServiceImpl implements PostAdminService {
    private final PostQueryService postQueryService;

    @Override
    public PostContentResponse getPost(long id) {
        return postQueryService.getPublishedPostWithCommunity(id);
    }

    // @Transactional
    // @Override
    // public void hidePost(long id) {
    //     var post = postRepository.findById(id)
    //         .orElseThrow(() -> new PostDoesntExistException());
    //     if(!post.isPublished())
    //         throw new PostDoesntExistException();
    //     post.setVisible(false);
    //     postRepository.save(post);
    //     applicationEventPublisher.publishEvent(
    //         PostMessageFactory.deleteOperation(post.getId())
    //     );
    // }

    // @Override
    // public void showPost(long id) {
    //     var post = postRepository.findById(id)
    //         .orElseThrow(() -> new PostDoesntExistException());
    //     if(post.isVisible())
    //         throw new PostDoesntExistException();
    //     post.setVisible(true);
    //     postRepository.save(post);
    //     applicationEventPublisher.publishEvent(
    //         PostMessageFactory.createOperation(post)
    //     );
    // }
}
