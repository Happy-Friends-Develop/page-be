package com.example.hello_friends.report.domain;

import com.example.hello_friends.common.entity.LogEntity;
import com.example.hello_friends.report.application.reportEnum.ReportStatus;
import com.example.hello_friends.report.application.reportEnum.ReportType;
import com.example.hello_friends.user.domain.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "report")
public class Report extends LogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 신고한 사람
    @JsonBackReference("reporter-reports") // 순환참조 방지 고유 이름 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_user_id")
    private User reporter;

    // 신고당한 사람
    @JsonBackReference("reported-reports") // 순환참조 방지 고유 이름 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_user_id")
    private User reportedUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportType reportType;

    @Column(name = "content_id", nullable = false)
    private Long contentId;

    @Column(nullable = false, length = 1000)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status;

    @Column(name = "admin_memo", length = 1000)
    private String adminMemo;

    public Report(User reporter, User reportedUser, ReportType reportType, Long contentId, String reason) {
        this.reporter = reporter;
        this.reportedUser = reportedUser;
        this.reportType = reportType;
        this.contentId = contentId;
        this.reason = reason;
        this.status = ReportStatus.PENDING;
    }

    public void process(ReportStatus newStatus, String adminMemo) {
        this.status = newStatus;
        this.adminMemo = adminMemo;
    }
}