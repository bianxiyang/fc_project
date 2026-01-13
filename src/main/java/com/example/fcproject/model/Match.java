package com.example.fcproject.model;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 赛程实体类，用于存储比赛信息
 */
@Entity
@Table(name = "fc_match")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    // 比赛编号
    @Column(name = "match_no", length = 50)
    private String matchNo;

    // 比赛轮次
    @Column(name = "round")
    private Integer round;

    // 主队
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "home_team_id", referencedColumnName = "id", nullable = true)
    private FcUser homeTeam;

    // 客队
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "away_team_id", referencedColumnName = "id", nullable = true)
    private FcUser awayTeam;

    // 主队得分
    @Column(name = "home_score")
    private Integer homeScore;

    // 客队得分
    @Column(name = "away_score")
    private Integer awayScore;

    // 比赛结果状态：0-未开始，1-进行中，2-已结束
    @Column(name = "status")
    private Short status = 0;

    // 比赛时间
    @Column(name = "match_time")
    private LocalDateTime matchTime;

    // 比赛地点
    @Column(name = "location", length = 255)
    private String location;

    // 创建时间
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 更新时间
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMatchNo() {
        return matchNo;
    }

    public void setMatchNo(String matchNo) {
        this.matchNo = matchNo;
    }

    public Integer getRound() {
        return round;
    }

    public void setRound(Integer round) {
        this.round = round;
    }

    public FcUser getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(FcUser homeTeam) {
        this.homeTeam = homeTeam;
    }

    public FcUser getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(FcUser awayTeam) {
        this.awayTeam = awayTeam;
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

    public LocalDateTime getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(LocalDateTime matchTime) {
        this.matchTime = matchTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // 生命周期回调，用于设置创建和更新时间
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}