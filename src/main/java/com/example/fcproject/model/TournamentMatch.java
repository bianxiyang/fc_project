package com.example.fcproject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 锦标赛比赛实体类，用于存储淘汰赛的比赛信息
 */
@Entity
@Table(name = "tournament_match")
public class TournamentMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    // 关联的锦标赛
    @ManyToOne
    @JoinColumn(name = "tournament_id", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private Tournament tournament;

    // 比赛轮次（如1/8决赛、1/4决赛、半决赛、决赛等）
    @Column(name = "round")
    private Integer round;

    // 该轮次中的比赛编号
    @Column(name = "match_number")
    private Integer matchNumber;

    // 该场比赛的局数（1、2或3，用于3局2胜制）
    @Column(name = "game_number")
    private Integer gameNumber;

    // 主队
    @ManyToOne
    @JoinColumn(name = "home_team_id", referencedColumnName = "id")
    private FcUser homeTeam;

    // 客队
    @ManyToOne
    @JoinColumn(name = "away_team_id", referencedColumnName = "id")
    private FcUser awayTeam;

    // 主队得分
    @Column(name = "home_score")
    private Integer homeScore;

    // 客队得分
    @Column(name = "away_score")
    private Integer awayScore;

    // 获胜者
    @ManyToOne
    @JoinColumn(name = "winner_id", referencedColumnName = "id")
    private FcUser winner;

    // 比赛状态：0-未开始，1-进行中，2-已结束
    @Column(name = "status")
    private Short status = 0;

    // 该场比赛的结果（0-主队胜，1-客队胜）
    @Column(name = "match_result")
    private Short matchResult;

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

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public Integer getRound() {
        return round;
    }

    public void setRound(Integer round) {
        this.round = round;
    }

    public Integer getMatchNumber() {
        return matchNumber;
    }

    public void setMatchNumber(Integer matchNumber) {
        this.matchNumber = matchNumber;
    }

    public Integer getGameNumber() {
        return gameNumber;
    }

    public void setGameNumber(Integer gameNumber) {
        this.gameNumber = gameNumber;
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

    public FcUser getWinner() {
        return winner;
    }

    public void setWinner(FcUser winner) {
        this.winner = winner;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public Short getMatchResult() {
        return matchResult;
    }

    public void setMatchResult(Short matchResult) {
        this.matchResult = matchResult;
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

    // 生命周期回调
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