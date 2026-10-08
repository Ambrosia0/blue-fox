package com.ambrosia.report_service.report.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.report_service.BaseIntegrationTest;
import com.ambrosia.report_service.comment.entity.CommentProjection;
import com.ambrosia.report_service.comment.repository.CommentProjectionRepository;
import com.ambrosia.report_service.exception.report.InvalidReportTargetKeyException;
import com.ambrosia.report_service.exception.report.ReportReasonDoesntExistException;
import com.ambrosia.report_service.exception.report.ReportTargetDoesntExistException;
import com.ambrosia.report_service.post.entity.PostProjection;
import com.ambrosia.report_service.post.repository.PostProjectionRepository;
import com.ambrosia.report_service.report.api.dto.ReportRequest;
import com.ambrosia.report_service.report.domain.entity.Report.TargetType;
import com.ambrosia.report_service.report.domain.repository.ReportReasonRepository;
import com.ambrosia.report_service.user.repository.UserProjectionRepository;
import com.ambrosia.report_service.util.CommentFactory;
import com.ambrosia.report_service.util.PostFactory;
import com.ambrosia.report_service.util.ReportCreator;

@Transactional
public class UserReportServiceIntegrationTests extends BaseIntegrationTest {
    @Autowired UserReportService userReportService;
    @Autowired ReportReasonRepository reportReasonRepository;

    @Autowired UserProjectionRepository userProjectionRepository;
    @Autowired CommentProjectionRepository commentProjectionRepository;
    @Autowired PostProjectionRepository postProjectionRepository;

    @Autowired ReportCreator reportCreator;

    @Test
    void shouldThrowInvalidReportTargetKey(){
        var request = createRequest((short)0, TargetType.USER, null);
        assertThrows(
            InvalidReportTargetKeyException.class,
            () -> userReportService.createReport(request, UUID.randomUUID())
        );
    }

    @Test
    void shouldThrowReportReasonDoesntExist(){
        var request = createRequest((short)0, TargetType.USER, UUID.randomUUID().toString());
        assertThrows(
            ReportReasonDoesntExistException.class,
            () -> userReportService.createReport(request, UUID.randomUUID())
        );
    }

    @Test
    void shouldThrowReportTargetDoesntExistOnCommentReport(){
        var reason = reportCreator.createReason();
        var request = createRequest(reason.getId(), TargetType.COMMENT, Long.toString(ThreadLocalRandom.current().nextLong()));
        assertThrows(
            ReportTargetDoesntExistException.class,
            () -> userReportService.createReport(request, UUID.randomUUID())
        );
    }

    @Test
    void shouldThrowReportTargetDoesntExistOnPostReport(){
        var reason = reportCreator.createReason();
        var request = createRequest(reason.getId(), TargetType.POST, Long.toString(ThreadLocalRandom.current().nextLong()));
        assertThrows(
            ReportTargetDoesntExistException.class,
            () -> userReportService.createReport(request, UUID.randomUUID())
        );
    }

    @Test
    void shouldThrowReportTargetDoesntExistOnUserReport(){
        var reason = reportCreator.createReason();
        var request = createRequest(reason.getId(), TargetType.USER, UUID.randomUUID().toString());
        assertThrows(
            ReportTargetDoesntExistException.class,
            () -> userReportService.createReport(request, UUID.randomUUID())
        );
    }

    @Test
    void shouldCreateCommentReport(){
        var user = reportCreator.createUser();
        var comment = createComment();
        var reason = reportCreator.createReason();
        var request = createRequest(reason.getId(), TargetType.COMMENT, Long.toString(comment.getId()));
        assertDoesNotThrow(
            () -> userReportService.createReport(request, user.getId())
        );
    }

    @Test
    void shouldCreatePostReport(){
        var user = reportCreator.createUser();
        var reason = reportCreator.createReason();
        var post = createPost();
        var request = createRequest(reason.getId(), TargetType.POST, Long.toString(post.getId()));
        assertDoesNotThrow(
            () -> userReportService.createReport(request, user.getId())
        );
    }

    @Test
    void shouldThrowInvalidReportTargetKeyOnSameUserId(){
        var user = reportCreator.createUser();
        var reason = reportCreator.createReason();
        var request = createRequest(reason.getId(), TargetType.USER, user.getId().toString());
        assertThrows(
            InvalidReportTargetKeyException.class,
            () -> userReportService.createReport(request, user.getId())
        );
    }

    @Test
    void shouldCreateUserReport(){
        var reportedUser = reportCreator.createUser();
        var user = reportCreator.createUser();
        var reason = reportCreator.createReason();
        var request = createRequest(reason.getId(), TargetType.USER, reportedUser.getId().toString());
        assertDoesNotThrow(
            () -> userReportService.createReport(request, user.getId())
        );
    }

    private CommentProjection createComment(){
        return commentProjectionRepository.save(CommentFactory.create());
    }

    private PostProjection createPost(){
        return postProjectionRepository.save(PostFactory.create());
    }

    private ReportRequest createRequest(short reasonId, TargetType targetType, String contentKey){
        return new ReportRequest(
            reasonId, targetType, "Test content", contentKey
        );
    }
}
