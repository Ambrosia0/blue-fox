package com.ambrosia.report_service.util.repository;

import com.ambrosia.report_service.comment.entity.CommentProjection;
import org.springframework.data.repository.CrudRepository;

public interface CommentProjectionCrudRepository extends CrudRepository<CommentProjection, Long> {
}
