package com.ambrosia.report_service.integration.report;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.report_service.BaseIntegrationTest;
import com.ambrosia.report_service.exception.report.ReportAlreadyClosedException;
import com.ambrosia.report_service.exception.report.ReportDoesntExistException;
import com.ambrosia.report_service.exception.report.ReportTranslationDoesntExistException;
import com.ambrosia.report_service.report.model.dto.ReportTranslationCreate;
import com.ambrosia.report_service.report.model.dto.admin.ReportFilter;
import com.ambrosia.report_service.report.model.entity.Report;
import com.ambrosia.report_service.report.model.entity.ReportReason;
import com.ambrosia.report_service.report.model.entity.ReportReasonI18n;
import com.ambrosia.report_service.report.model.entity.Report.Status;
import com.ambrosia.report_service.report.model.entity.Report.TargetType;
import com.ambrosia.report_service.report.model.entity.key.ReportReasonI18nKey;
import com.ambrosia.report_service.report.repository.ReportReasonI18nRepository;
import com.ambrosia.report_service.report.repository.ReportReasonRepository;
import com.ambrosia.report_service.report.repository.ReportRepository;
import com.ambrosia.report_service.report.service.AdminReportService;
import com.ambrosia.report_service.user.entity.UserProjection;
import com.ambrosia.report_service.user.repository.UserProjectionRepository;
import com.ambrosia.report_service.util.UserFactory;

@Transactional
public class AdminReportServiceIntegrationTests extends BaseIntegrationTest{
    @Autowired AdminReportService adminReportService;
    @Autowired ReportRepository reportRepository;
    @Autowired ReportReasonRepository reportReasonRepository;
    @Autowired ReportReasonI18nRepository reportReasonI18nRepository;

    @Autowired UserProjectionRepository userProjectionRepository;

    @Test
    void shouldThrowReportDoesntExistException(){
        assertThrows(
            ReportDoesntExistException.class,
            () -> adminReportService.closeReport(UUID.randomUUID(), UUID.randomUUID())
        );
    }

    @Test
    void shouldThrowReportAlreadyClosedException(){
        var report = createReport(Status.CLOSE, TargetType.USER);
        assertThrows(
            ReportAlreadyClosedException.class,
            () -> adminReportService.closeReport(report.getId(), UUID.randomUUID())
        );
    }

    @Test
    void shouldCloseReport(){
        var report = createReport(Status.OPEN, TargetType.USER);
        var user = createUser();
        assertEquals(Status.OPEN, report.getStatus());
        assertDoesNotThrow(() -> adminReportService.closeReport(report.getId(), user.getId()));
        assertEquals(Status.CLOSE, reportRepository.findById(report.getId()).get().getStatus());
    }

    @Test
    void shouldCreateReportTranslation(){
        var request = createTranslationRequestWithReason("ru");
        assertDoesNotThrow(() -> adminReportService.createTranslation(request));
        assertDoesNotThrow(() -> reportReasonI18nRepository.findById(
            ReportReasonI18nKey.create(request.reasonId(), request.lang())).get());
    }

    @Test
    void shouldUpdateReportTranslation(){
        var request = createTranslationRequestWithReason("de");
        assertDoesNotThrow(() -> adminReportService.createTranslation(request));
        var testTitle = "new test title";
        assertNotEquals(testTitle, request.title());
        assertDoesNotThrow(() -> adminReportService.createTranslation(
            new ReportTranslationCreate(request.reasonId(), testTitle, request.lang())
        ));
        assertEquals(testTitle, 
            reportReasonI18nRepository.findById(
                ReportReasonI18nKey.create(request.reasonId(), request.lang())
            ).get().getTitle()
        );
    }

    @Test
    void shouldThrowReportTranslationDoesntExist(){
        assertThrows(
            ReportTranslationDoesntExistException.class,
            () -> adminReportService.deleteTranslation(
                (short)ThreadLocalRandom.current().nextLong(), 
                "ru"
            )
        );
    }

    @Test
    void shouldDeleteTranslation(){
        var reason = createReason();
        createTranslation(reason.getId(), "ru");
        assertDoesNotThrow(
            () -> adminReportService.deleteTranslation(reason.getId(), "ru")
        );
    }

    @Test
    void shouldReturnTranslationForReason(){
        var reason = createReason();
        createTranslation(reason.getId(), "ru");
        createTranslation(reason.getId(), "de");
        assertEquals(2, adminReportService.getTranslations(reason.getId()).size());
    }

    @Test
    void shouldReturnReports(){
        createReport(Status.OPEN, TargetType.POST);
        createReport(Status.OPEN, TargetType.USER);
        createReport(Status.CLOSE, TargetType.COMMENT);
        assertEquals(
            3,
             adminReportService.getReports(
                new ReportFilter(null, null, null),
                PageRequest.of(0, 10)
             ).getContent().size()
        );
    }

    @Test
    void shouldReturnReasons(){
        assertNotEquals(
            0,
             adminReportService.getReasons().size()
        );
    }

    private UserProjection createUser(){
        return userProjectionRepository.save(UserFactory.create());
    }

    private ReportReason createReason(){
        return reportReasonRepository.save(ReportReason.builder()
            .code("TestCode"+ThreadLocalRandom.current().nextLong())
            .build()
        );
    }

    private ReportReasonI18n createTranslation(short reasonId, String lang){
        return reportReasonI18nRepository.save(ReportReasonI18n.create(
            reasonId,
            lang, 
            "Test title"
        ));
    }

    private ReportTranslationCreate createTranslationRequestWithReason(String lang){
        var reason = createReason();
        return new ReportTranslationCreate(
            reason.getId(), "Test title", lang);
    }

    private Report createReport(Status status, TargetType targetType){
        var user = createUser();
        var reason = createReason();
        return reportRepository.save(Report.builder()
            .reportReasonId(reason.getId())
            .userId(user.getId())
            .reportContent("Test Content")
            .reportedContentKey("random string")
            .status(status)
            .targetType(targetType)
            .build()
        );
    }
}
