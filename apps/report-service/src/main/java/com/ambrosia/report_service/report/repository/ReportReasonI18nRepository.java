package com.ambrosia.report_service.report.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.ambrosia.report_service.report.model.dto.ReportReasonTranslation;
import com.ambrosia.report_service.report.model.entity.ReportReasonI18n;
import com.ambrosia.report_service.report.model.entity.key.ReportReasonI18nKey;


public interface ReportReasonI18nRepository extends 
    CrudRepository<ReportReasonI18n, ReportReasonI18nKey> {

    @Query("""
        INSERT INTO report_reason_i18n(report_reason_id, title, lang) 
        VALUES (:reasonId, :title, :lang)
        ON CONFLICT (report_reason_id, lang)
        DO UPDATE SET title = EXCLUDED.title
        RETURNING report_reason_id, lang, title
        """)
    Optional<ReportReasonTranslation> upsert(Short reasonId, String title, String lang);

    @Modifying
    @Query("DELETE FROM report_reason_i18n WHERE report_reason_id = :reasonId AND lang = :lang")
    int returningDelete(Short reasonId, String lang);

    @Query("SELECT report_reason_id, lang, title FROM report_reason_i18n WHERE report_reason_id = :reasonId")
    List<ReportReasonTranslation> findByReasonId(Short reasonId);
}
