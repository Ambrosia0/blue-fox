package com.ambrosia.report_service.report.application.query.impl;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.ambrosia.report_service.report.api.dto.ReportFilter;
import com.ambrosia.report_service.report.api.dto.ReportResponse;
import com.ambrosia.report_service.report.application.query.ReportQueryRepository;
import com.ambrosia.report_service.report.application.query.ReportQueryService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Service 
public class ReportQueryServiceImpl implements ReportQueryService{
    private final ReportQueryRepository reportQueryRepository;
    
    @Override
    public Slice<ReportResponse> getReports(ReportFilter reportFilter, Pageable pageable) {
        return reportQueryRepository.getReports(reportFilter, pageable);
    }
}
