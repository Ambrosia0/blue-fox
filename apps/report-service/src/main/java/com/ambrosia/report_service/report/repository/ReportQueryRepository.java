package com.ambrosia.report_service.report.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.ambrosia.report_service.report.model.dto.admin.ReportFilter;
import com.ambrosia.report_service.report.model.dto.admin.ReportResponse;

public interface ReportQueryRepository {
    Slice<ReportResponse> getReports(ReportFilter reportFilter, Pageable pageable);
}
