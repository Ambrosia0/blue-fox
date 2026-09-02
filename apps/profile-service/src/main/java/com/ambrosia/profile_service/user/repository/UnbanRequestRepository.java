package com.ambrosia.profile_service.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.ambrosia.profile_service.user.model.dto.response.UnbanRequestResponse;
import com.ambrosia.profile_service.user.model.entity.UnbanRequest;

public interface UnbanRequestRepository extends CrudRepository<UnbanRequest, Long>, PagingAndSortingRepository<UnbanRequest, Long> {
    Optional<UnbanRequestResponse> findRequestByUserId(@Param("userId") UUID id);

    @Query("""
            SELECT * FROM unban_request
            WHERE user_id = :userId
            """)
    Optional<UnbanRequest> findByUserId(@Param("userId") UUID id);
    Page<UnbanRequestResponse> findByIsViewedIsFalse(Pageable pageable);
}
