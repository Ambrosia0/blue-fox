package com.ambrosia.comment_service.utils.repository;


import com.ambrosia.comment_service.post.model.entity.PostProjection;
import org.springframework.data.repository.CrudRepository;

public interface PostProjectionCrudRepository extends
    CrudRepository<PostProjection, Long> {}
