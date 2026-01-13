package com.example.fcproject.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 锦标赛实体类，用于存储淘汰赛的基本信息
 */
@Entity
@Table(name = "tournament")
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    // 锦标赛名称
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    // 锦标赛状态：0-未开始，1-进行中，2-已结束
    @Column(name = "status")
    private Short status = 0;

    // 参与者数量
    @Column(name = "participant_count")
    private Integer participantCount;

    // 创建时间
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 更新时间
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 比赛列表
    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TournamentMatch> matches;

    // 参与者列表
    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TournamentParticipant> participants;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public Integer getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(Integer participantCount) {
        this.participantCount = participantCount;
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

    public List<TournamentMatch> getMatches() {
        return matches;
    }

    public void setMatches(List<TournamentMatch> matches) {
        this.matches = matches;
    }

    public List<TournamentParticipant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<TournamentParticipant> participants) {
        this.participants = participants;
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