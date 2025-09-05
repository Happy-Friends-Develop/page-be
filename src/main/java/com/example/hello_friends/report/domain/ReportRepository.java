package com.example.hello_friends.report.domain;

import com.example.hello_friends.report.application.reportEnum.ReportType;
import com.example.hello_friends.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    boolean existsByReporterAndReportTypeAndContentId(User reporter, ReportType reportType, Long contentId);
}
