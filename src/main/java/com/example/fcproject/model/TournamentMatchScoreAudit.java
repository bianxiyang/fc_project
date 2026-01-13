package com.example.fcproject.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tournament_match_score_audit")
public class TournamentMatchScoreAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "match_id", nullable = false)
    private TournamentMatch match;

    @Column(name = "home_score")
    private Integer homeScore;

    @Column(name = "away_score")
    private Integer awayScore;

    @Column(name = "status")
    private Short status;

    @ManyToOne
    @JoinColumn(name = "submitted_by", nullable = false)
    private FcUser submittedBy;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @Column(name = "audit_status", nullable = false, length = 20) // PENDING, APPROVED, REJECTED
    private String auditStatus = "PENDING";

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private FcUser approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "reject_reason", length = 255)
    private String rejectReason;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TournamentMatch getMatch() {
        return match;
    }

    public void setMatch(TournamentMatch match) {
        this.match = match;
    }

    public Integer getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(Integer homeScore) {
        this.homeScore = homeScore;
    }

    public Integer getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(Integer awayScore) {
        this.awayScore = awayScore;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public FcUser getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(FcUser submittedBy) {
        this.submittedBy = submittedBy;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public FcUser getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(FcUser approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }
}