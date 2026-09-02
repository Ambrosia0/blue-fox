package com.ambrosia.comment_service.like.repository;

import org.springframework.data.repository.CrudRepository;

import com.ambrosia.comment_service.like.model.entity.CommentLike;
import com.ambrosia.comment_service.like.model.entity.keys.CommentLikeKey;
import com.ambrosia.comment_service.like.repository.custom.CustomLikeRepository;

public interface LikeRepository extends CrudRepository<CommentLike, CommentLikeKey>, CustomLikeRepository{}
