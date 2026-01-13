package com.example.fcproject.dto;

import java.util.List;

/**
 * 赛程生成请求DTO
 */
public class ScheduleGeneratorRequest {
    
    private List<Integer> userIds;
    private boolean includeHomeAway;
    
    // Getters and Setters
    public List<Integer> getUserIds() {
        return userIds;
    }
    
    public void setUserIds(List<Integer> userIds) {
        this.userIds = userIds;
    }
    
    public boolean isIncludeHomeAway() {
        return includeHomeAway;
    }
    
    public void setIncludeHomeAway(boolean includeHomeAway) {
        this.includeHomeAway = includeHomeAway;
    }
}