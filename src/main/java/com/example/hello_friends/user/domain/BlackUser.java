package com.example.hello_friends.user.domain;

import com.example.hello_friends.common.entity.EntityState;
import com.example.hello_friends.common.entity.LogEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "blackUser")
public class BlackUser extends LogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blackuser_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Column(name = "blackuser_state", nullable = false)
    @Enumerated(EnumType.STRING)
    private EntityState state;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "black_start_date")
    private LocalDateTime blackStartDate;

    @Column(name = "black_end_date")
    private LocalDateTime blackEndDate;

    @Column(name = "warning_count")
    private Integer warningCount;

    @Column(name = "admin_memo", length = 1000)
    private String adminMemo;

    public BlackUser(User user, String reason, LocalDateTime blackEndDate) {
        this.user = user;
        this.state = EntityState.ACTIVE;
        this.reason = reason;
        this.blackStartDate = LocalDateTime.now();
        this.blackEndDate = blackEndDate;
        this.warningCount = 1;
    }

    public void updateWarningCount() {
        this.warningCount++;
    }

    public void updateBlackEndDate(LocalDateTime newEndDate) {
        this.blackEndDate = newEndDate;
    }

    public void addAdminMemo(String memo) {
        this.adminMemo = (this.adminMemo != null)
                ? this.adminMemo + "\n" + LocalDateTime.now() + ": " + memo
                : LocalDateTime.now() + ": " + memo;
    }

    public void deactivate(String memo) {
        this.state = EntityState.DELETE;
        addAdminMemo("블랙리스트 해제: " + memo);
    }
}