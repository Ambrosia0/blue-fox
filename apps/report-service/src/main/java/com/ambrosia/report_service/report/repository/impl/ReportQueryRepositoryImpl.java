package com.ambrosia.report_service.report.repository.impl;

import java.util.LinkedHashMap;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.ambrosia.report_service.report.model.dto.admin.ReportFilter;
import com.ambrosia.report_service.report.model.dto.admin.ReportResponse;
import com.ambrosia.report_service.report.repository.ReportQueryRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class ReportQueryRepositoryImpl implements ReportQueryRepository{
    private final JdbcClient jdbcClient;

    @Override
    public Slice<ReportResponse> getReports(ReportFilter reportFilter, Pageable pageable) {
        var paramMap = new LinkedHashMap<String, Object>();
        var sql = new StringBuilder("""
        SELECT 
            r.id,
            r.report_reason_id,
            r.report_content,
            r.target_type,
            r.status,
            r.reported_content_key,
            r.resolved_by,
            r.created_at
        FROM report r
        WHERE 1=1 
        """);

        if(reportFilter.status() != null){
            sql.append("AND status = :status ");
            paramMap.put("status", reportFilter.status());
        }

        if(reportFilter.targetType() != null){
            sql.append("AND target_type = :targetType ");
            paramMap.put("targetType", reportFilter.targetType());
        }

        if(reportFilter.direction() != null && reportFilter.direction() == Direction.ASC){
            sql.append("ORDER BY created_at ASC ");
        }else{
            sql.append("ORDER BY created_at DESC ");
        }
        sql.append("LIMIT :pageSize OFFSET :offset");
        paramMap.put("pageSize", pageable.getPageSize() + 1);
        paramMap.put("offset", pageable.getOffset());
        var res = jdbcClient
            .sql(sql.toString())
            .params(paramMap)
            .query(ReportResponse.class)
            .list();
        var hasNext = res.size() > pageable.getPageSize();
        if(hasNext)
            res.remove(res.size());
        return new SliceImpl<>(res, pageable, hasNext);
    }
}
