package com.ambrosia.report_service.report.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ambrosia.report_service.report.model.dto.ReportReasonResponse;
import com.ambrosia.report_service.report.model.entity.ReportReason;


public interface ReportReasonRepository extends 
    CrudRepository<ReportReason, Short>,
    PagingAndSortingRepository<ReportReason, Short>{
    @Query("""
        SELECT 
            rr.id, 
            rr.code,
            rri.title,
            rri.lang
        FROM report_reason rr
        JOIN report_reason_i18n rri ON rri.report_reason_id = rr.id
        WHERE rri.lang = :lang
    """)
    List<ReportReasonResponse> findByLang(String lang);

    List<ReportReason> findBy();
}
