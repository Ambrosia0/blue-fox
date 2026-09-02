package com.ambrosia.report_service.report.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.report_service.report.model.dto.ReportReasonResponse;
import com.ambrosia.report_service.report.model.dto.ReportRequest;
import com.ambrosia.report_service.report.service.UserReportService;
import com.ambrosia.report_service.report.utils.Iso6391;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;


@RequiredArgsConstructor
@RequestMapping("/api/user/report")
@RestController
public class UserReportController {
    private final UserReportService userReportService;

    @GetMapping("/reason")
    public List<ReportReasonResponse> getReasons(
        @RequestParam(defaultValue = "en") @Iso6391 String lang) {
        return userReportService.getReportReasons(lang);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping
    public void createReport(
        @RequestBody ReportRequest reportRequest,
        @AuthenticationPrincipal Jwt jwt) {
        userReportService.createReport(reportRequest, UUID.fromString(jwt.getSubject()));
    }
}
