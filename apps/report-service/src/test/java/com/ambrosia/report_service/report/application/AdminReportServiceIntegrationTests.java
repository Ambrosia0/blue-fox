package com.ambrosia.report_service.report.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.report_service.BaseIntegrationTest;
import com.ambrosia.report_service.exception.report.ReportAlreadyClosedException;
import com.ambrosia.report_service.exception.report.ReportDoesntExistException;
import com.ambrosia.report_service.exception.report.ReportTranslationDoesntExistException;
import com.ambrosia.report_service.report.api.dto.ReportTranslationCreate;
import com.ambrosia.report_service.report.domain.entity.Report.Status;
import com.ambrosia.report_service.report.domain.entity.Report.TargetType;
import com.ambrosia.report_service.report.domain.repository.ReportReasonI18nRepository;
import com.ambrosia.report_service.report.domain.repository.ReportRepository;
import com.ambrosia.report_service.report.infrastructure.entity.key.ReportReasonI18nKey;
import com.ambrosia.report_service.util.ReportCreator;

@Transactional
public class AdminReportServiceIntegrationTests extends BaseIntegrationTest{
    @Autowired AdminReportService adminReportService;
    @Autowired ReportRepository reportRepository;
    @Autowired ReportReasonI18nRepository reportReasonI18nRepository;

    @Autowired ReportCreator reportCreator;

    @Test
    void shouldThrowReportDoesntExistException(){
        assertThrows(
            ReportDoesntExistException.class,
            () -> adminReportService.closeReport(UUID.randomUUID(), UUID.randomUUID())
        );
    }

    @Test
    void shouldThrowReportAlreadyClosedException(){
        var report = reportCreator.create(Status.CLOSE, TargetType.USER);
        assertThrows(
            ReportAlreadyClosedException.class,
            () -> adminReportService.closeReport(report.getId(), UUID.randomUUID())
        );
    }

    @Test
    void shouldCloseReport(){
        var report = reportCreator.create(Status.OPEN, TargetType.USER);
        var user = reportCreator.createUser();
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
        var reason = reportCreator.createReason();
        reportCreator.createTranslation(reason.getId(), "ru");
        assertDoesNotThrow(
            () -> adminReportService.deleteTranslation(reason.getId(), "ru")
        );
    }

    @Test
    void shouldReturnTranslationForReason(){
        var reason = reportCreator.createReason();
        reportCreator.createTranslation(reason.getId(), "ru");
        reportCreator.createTranslation(reason.getId(), "de");
        assertEquals(2, adminReportService.getTranslations(reason.getId()).size());
    }

    @Test
    void shouldReturnReasons(){
        assertNotEquals(
            0,
             adminReportService.getReasons().size()
        );
    }


    private ReportTranslationCreate createTranslationRequestWithReason(String lang){
        var reason = reportCreator.createReason();
        return new ReportTranslationCreate(
            reason.getId(), "Test title", lang);
    }

}
