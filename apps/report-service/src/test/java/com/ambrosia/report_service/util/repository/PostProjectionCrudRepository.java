package com.ambrosia.report_service.util.repository;

import com.ambrosia.report_service.post.entity.PostProjection;
import org.springframework.data.repository.CrudRepository;

public interface PostProjectionCrudRepository extends CrudRepository<PostProjection, Long> {
}
