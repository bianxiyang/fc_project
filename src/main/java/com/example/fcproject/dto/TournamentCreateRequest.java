package com.example.fcproject.dto;

import java.util.List;

/**
 * 创建锦标赛请求DTO
 */
public class TournamentCreateRequest {
    
    // 锦标赛名称
    private String name;
    
    // 参与者用户ID列表
    private List<Integer> userIds;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Integer> userIds) {
        this.userIds = userIds;
    }
}