package com.example.fcproject.service;

import com.example.fcproject.model.StrikerStats;
import java.util.List;

/**
 * 射手榜服务接口，定义射手榜统计的业务方法
 */
public interface StrikerService {
    
    /**
     * 获取所有球员的射手榜统计（按进球数降序排列）
     * @return 射手榜统计列表
     */
    List<StrikerStats> getAllStrikerStats();
    
    /**
     * 获取球员的射手榜统计（按进球数降序排列）
     * @param userId 用户ID
     * @return 射手榜统计对象
     */
    StrikerStats getStrikerStatsByUserId(Integer userId);
}