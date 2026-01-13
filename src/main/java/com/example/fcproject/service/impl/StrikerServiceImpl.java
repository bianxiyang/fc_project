package com.example.fcproject.service.impl;

import com.example.fcproject.model.FcUser;
import com.example.fcproject.model.Match;
import com.example.fcproject.model.StrikerStats;
import com.example.fcproject.repository.FcUserRepository;
import com.example.fcproject.repository.MatchRepository;
import com.example.fcproject.service.StrikerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 射手榜服务实现类，实现射手榜统计的业务逻辑
 */
@Service
public class StrikerServiceImpl implements StrikerService {

    @Autowired
    private FcUserRepository fcUserRepository;
    
    @Autowired
    private MatchRepository matchRepository;
    
    @Override
    public List<StrikerStats> getAllStrikerStats() {
        List<StrikerStats> strikerStatsList = new ArrayList<>();
        
        // 获取所有非ROOT用户
        List<FcUser> allUsers = fcUserRepository.findByRoleNotOrderByIdAsc("ROOT");
        
        for (FcUser user : allUsers) {
            StrikerStats stats = calculateUserGoals(user);
            strikerStatsList.add(stats);
        }
        
        // 按进球数降序排列，如果进球数相同则按用户ID升序排列
        strikerStatsList.sort((s1, s2) -> {
            int goalsComparison = s2.getGoals().compareTo(s1.getGoals());
            if (goalsComparison == 0) {
                return s1.getUserId().compareTo(s2.getUserId());
            }
            return goalsComparison;
        });
        
        return strikerStatsList;
    }
    
    @Override
    public StrikerStats getStrikerStatsByUserId(Integer userId) {
        FcUser user = fcUserRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }
        return calculateUserGoals(user);
    }
    
    /**
     * 计算单个用户的进球统计数据
     * @param user 用户对象
     * @return 射手榜统计对象
     */
    private StrikerStats calculateUserGoals(FcUser user) {
        Integer totalGoals = 0;
        Integer totalGoalsConceded = 0;
        Integer totalMatches = 0;
        
        // 获取该用户参与的所有已结束比赛
        List<Match> userMatches = matchRepository.findByStatus((short) 2);
        
        for (Match match : userMatches) {
            boolean userParticipated = false;
            Integer goalsInThisMatch = 0;
            Integer goalsConcededInThisMatch = 0;
            
            // 检查用户是否参与了这场比赛（作为主队或客队）
            if (match.getHomeTeam() != null && match.getHomeTeam().getId().equals(user.getId())) {
                userParticipated = true;
                goalsInThisMatch = match.getHomeScore() != null ? match.getHomeScore() : 0;
                goalsConcededInThisMatch = match.getAwayScore() != null ? match.getAwayScore() : 0;
            } else if (match.getAwayTeam() != null && match.getAwayTeam().getId().equals(user.getId())) {
                userParticipated = true;
                goalsInThisMatch = match.getAwayScore() != null ? match.getAwayScore() : 0;
                goalsConcededInThisMatch = match.getHomeScore() != null ? match.getHomeScore() : 0;
            }
            
            // 如果用户参与了比赛，统计进球、失球和场次
            if (userParticipated) {
                totalGoals += goalsInThisMatch;
                totalGoalsConceded += goalsConcededInThisMatch;
                totalMatches++;
            }
        }
        
        return new StrikerStats(user.getId(), user.getName(), totalGoals, totalGoalsConceded, totalMatches);
    }
}