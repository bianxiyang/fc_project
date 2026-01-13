package com.example.fcproject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 锦标赛参与者实体类，用于存储参与淘汰赛的用户信息
 */
@Entity
@Table(name = "tournament_participant")
public class TournamentParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    // 关联的锦标赛
    @ManyToOne
    @JoinColumn(name = "tournament_id", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private Tournament tournament;

    // 参与者用户
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private FcUser user;

    // 排名
    @Column(name = "rank")
    private Integer rank;

    // 当前状态：0-参赛中，1-已晋级，2-已淘汰
    @Column(name = "status")
    private Short status = 0;

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

    public FcUser getUser() {
        return user;
    }

    public void setUser(FcUser user) {
        this.user = user;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
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