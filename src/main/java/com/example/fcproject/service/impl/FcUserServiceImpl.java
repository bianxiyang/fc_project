package com.example.fcproject.service.impl;

import com.example.fcproject.model.FcUser;
import com.example.fcproject.model.Match;
import com.example.fcproject.repository.FcUserRepository;
import com.example.fcproject.repository.MatchRepository;
import com.example.fcproject.service.FcUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * FcUser服务实现类，实现用户管理的业务逻辑
 */
@Service
public class FcUserServiceImpl implements FcUserService {

    @Autowired
    private FcUserRepository fcUserRepository;
    
    @Autowired
    private MatchRepository matchRepository;
    
    @Override
    public List<FcUser> getAllUsers() {
        // 修改为按ID升序排序，作为默认排序规则
        List<FcUser> users = fcUserRepository.findAllByOrderByIdAsc();
        // 动态计算每个用户的积分
        users.forEach(this::calculateUserPoints);
        return users;
    }
    
    @Override
    public Optional<FcUser> getUserById(Integer id) {
        Optional<FcUser> userOptional = fcUserRepository.findById(id);
        // 如果用户存在，动态计算积分
        userOptional.ifPresent(this::calculateUserPoints);
        return userOptional;
    }
    
    @Override
    public FcUser getUserByName(String name) {
        FcUser user = fcUserRepository.findByName(name);
        // 如果用户存在，动态计算积分
        if (user != null) {
            calculateUserPoints(user);
        }
        return user;
    }
    
    @Override
    public FcUser createUser(FcUser fcUser) {
        // 设置默认值
        if (fcUser.getWin() == null) {
            fcUser.setWin((short) 0);
        }
        if (fcUser.getTie() == null) {
            fcUser.setTie((short) 0);
        }
        if (fcUser.getLose() == null) {
            fcUser.setLose((short) 0);
        }
        if (fcUser.getPowerful() == null) {
            fcUser.setPowerful(1000); // 默认积分
        }
        return fcUserRepository.save(fcUser);
    }
    
    @Override
    public FcUser updateUser(Integer id, FcUser fcUser) {
        // 检查用户是否存在
        Optional<FcUser> existingUser = fcUserRepository.findById(id);
        if (existingUser.isPresent()) {
            // 确保ID一致
            fcUser.setId(id);
            return fcUserRepository.save(fcUser);
        }
        return null;
    }
    
    @Override
    public boolean deleteUser(Integer id) {
        // 检查用户是否存在
        if (fcUserRepository.existsById(id)) {
            fcUserRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    @Override
    public List<FcUser> getUsersByPowerfulDesc() {
        // 只查询非ROOT用户，按积分降序排序
        List<FcUser> users = fcUserRepository.findByRoleNotOrderByPowerfulDesc("ROOT");
        // 动态计算每个用户的积分
        users.forEach(this::calculateUserPoints);
        return users;
    }
    
    @Override
    public List<FcUser> getUsersByIdAsc() {
        List<FcUser> users = fcUserRepository.findAllByOrderByIdAsc();
        // 动态计算每个用户的积分
        users.forEach(this::calculateUserPoints);
        return users;
    }
    
    @Override
    public FcUser getUserByUsername(String username) {
        return fcUserRepository.findByUsername(username);
    }
    
    @Override
    public boolean login(String username, String password) {
        // 根据用户名查找用户
        FcUser user = fcUserRepository.findByUsername(username);
        if (user != null) {
            // 简单的密码验证，后续会使用Spring Security的密码编码器
            return password.equals(user.getPassword());
        }
        return false;
    }
    
    /**
     * 动态计算用户积分
     * @param user 用户对象
     */
    private void calculateUserPoints(FcUser user) {
        // 重置用户的胜平负统计
        short win = 0;
        short tie = 0;
        short lose = 0;
        
        // 查询用户作为主队参加的已结束比赛
        List<Match> homeMatches = matchRepository.findByHomeTeamAndStatus(user, (short) 2);
        // 查询用户作为客队参加的已结束比赛
        List<Match> awayMatches = matchRepository.findByAwayTeamAndStatus(user, (short) 2);
        
        // 处理用户作为主队的比赛
        for (Match match : homeMatches) {
            // 检查比分是否为null，避免空指针异常
            Integer homeScore = match.getHomeScore() != null ? match.getHomeScore() : 0;
            Integer awayScore = match.getAwayScore() != null ? match.getAwayScore() : 0;
            
            if (homeScore > awayScore) {
                win++;
            } else if (homeScore < awayScore) {
                lose++;
            } else {
                tie++;
            }
        }
        
        // 处理用户作为客队的比赛
        for (Match match : awayMatches) {
            // 检查比分是否为null，避免空指针异常
            Integer homeScore = match.getHomeScore() != null ? match.getHomeScore() : 0;
            Integer awayScore = match.getAwayScore() != null ? match.getAwayScore() : 0;
            
            if (awayScore > homeScore) {
                win++;
            } else if (awayScore < homeScore) {
                lose++;
            } else {
                tie++;
            }
        }
        
        // 更新用户的胜平负统计
        user.setWin(win);
        user.setTie(tie);
        user.setLose(lose);
        
        // 计算积分：胜场*3 + 平场*1
        int totalPoints = (win * 3) + tie;
        user.setPowerful(totalPoints);
        
        // 保存更新后的用户数据
        fcUserRepository.save(user);
    }
}