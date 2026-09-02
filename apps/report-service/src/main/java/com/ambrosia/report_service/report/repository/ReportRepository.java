package com.ambrosia.report_service.report.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ambrosia.report_service.report.model.entity.Report;

public interface ReportRepository extends 
    CrudRepository<Report, UUID>, 
    PagingAndSortingRepository<Report, UUID>{}
