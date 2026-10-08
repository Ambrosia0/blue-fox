package com.ambrosia.report_service.report.application.query;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.ambrosia.report_service.report.api.dto.ReportFilter;
import com.ambrosia.report_service.report.api.dto.ReportResponse;

public interface ReportQueryService {
    Slice<ReportResponse> getReports(ReportFilter reportFilter, Pageable pageable);
}
