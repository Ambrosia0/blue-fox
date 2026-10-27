package com.ambrosia.report_service.report.application.impl;

import java.util.List;
import java.util.UUID;

import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicyContext;
import com.ambrosia.report_service.report.domain.policy.ReportCreatePolicy;
import org.springframework.stereotype.Service;

import com.ambrosia.report_service.comment.service.CommentProjectionService;
import com.ambrosia.report_service.community.service.CommunityProjectionService;
import com.ambrosia.report_service.exception.report.InvalidReportTargetKeyException;
import com.ambrosia.report_service.exception.report.ReportReasonDoesntExistException;
import com.ambrosia.report_service.exception.report.ReportTargetDoesntExistException;
import com.ambrosia.report_service.post.service.PostProjectionService;
import com.ambrosia.report_service.report.api.dto.ReportReasonResponse;
import com.ambrosia.report_service.report.api.dto.ReportRequest;
import com.ambrosia.report_service.report.application.UserReportService;
import com.ambrosia.report_service.report.domain.entity.Report;
import com.ambrosia.report_service.report.domain.repository.ReportReasonRepository;
import com.ambrosia.report_service.report.domain.repository.ReportRepository;
import com.ambrosia.report_service.user.service.UserProjectionService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserReportServiceImpl implements UserReportService{
    private final ReportRepository reportRepository;

    private final ReportReasonRepository reportReasonRepository;

    private final GenericPolicyContext policyContext;

    @Override
    public void createReport(ReportRequest reportRequest, Actor actor) {
        policyContext.evaluate(actor, ReportCreatePolicy.class, reportRequest);
        reportRepository.save(Report.builder()
            .reportContent(reportRequest.reportContent())
            .reportReasonId(reportRequest.reportReasonId())
            .targetType(reportRequest.targetType())
            .reportedContentKey(reportRequest.reportContentKey())
            .userId(actor.id())
            .build()
        );
    }

    @Override
    public List<ReportReasonResponse> getReportReasons(String lang) {
        return reportReasonRepository.findByLang(lang);
    }
}
