package com.example.fcproject.model;

/**
 * 射手榜统计DTO，用于显示球员进球数据
 */
public class StrikerStats {
    
    private Integer userId;
    private String userName;
    private Integer goals;
    private Integer goalsConceded; // 失球数
    private Integer matches;
    
    // 构造函数
    public StrikerStats() {
    }
    
    public StrikerStats(Integer userId, String userName, Integer goals, Integer goalsConceded, Integer matches) {
        this.userId = userId;
        this.userName = userName;
        this.goals = goals;
        this.goalsConceded = goalsConceded;
        this.matches = matches;
    }
    
    // 计算进球率（每场平均进球数）
    public Double getGoalsPerMatch() {
        if (matches == null || matches == 0) {
            return 0.0;
        }
        return (double) goals / matches;
    }
    
    // 计算失球率（每场平均失球数）
    public Double getGoalsConcededPerMatch() {
        if (matches == null || matches == 0) {
            return 0.0;
        }
        return (double) goalsConceded / matches;
    }
    
    // 计算净胜球
    public Integer getGoalDifference() {
        if (goals == null || goalsConceded == null) {
            return 0;
        }
        return goals - goalsConceded;
    }
    
    // Getters and Setters
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public Integer getGoals() {
        return goals;
    }
    
    public void setGoals(Integer goals) {
        this.goals = goals;
    }
    
    public Integer getMatches() {
        return matches;
    }
    
    public void setMatches(Integer matches) {
        this.matches = matches;
    }
    
    public Integer getGoalsConceded() {
        return goalsConceded;
    }
    
    public void setGoalsConceded(Integer goalsConceded) {
        this.goalsConceded = goalsConceded;
    }
}