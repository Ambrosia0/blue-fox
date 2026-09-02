package com.ambrosia.report_service.report.model.entity;

import java.time.Instant;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "report")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Report {
    @Id
    private UUID id;

    @Column("user_id")
    private UUID userId;

    @Column("report_reason_id")
    private Short reportReasonId;

    @Column("report_content")
    private String reportContent;

    @Column("target_type")
    private TargetType targetType;

    @Builder.Default
    @Column("status")
    private Status status = Status.OPEN;

    @Column("reported_content_key")
    private String reportedContentKey;

    @Column("resolved_by")
    private UUID resolvedBy;

    @ReadOnlyProperty
    @Column("created_at")
    private Instant createdAt;

    public enum TargetType {
        POST(Long::parseLong),
        USER(UUID::fromString),
        COMMUNITY(Long::parseLong),
        COMMENT(Long::parseLong);

        private TargetType(Function<String, ?> function){
            this.function = function;
        }

        private final Function<String, ?> function;
        
        public boolean isValid(String key){
            try {
                function.apply(key);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    public enum Status {
        OPEN,
        CLOSE;
    }
}
