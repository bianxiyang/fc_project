package com.example.fcproject.init;

import com.example.fcproject.model.FcUser;
import com.example.fcproject.model.Match;
import com.example.fcproject.repository.FcUserRepository;
import com.example.fcproject.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MatchDataInitializer {

    @Autowired
    private FcUserRepository fcUserRepository;

    @Autowired
    private MatchRepository matchRepository;

    @PostConstruct
    public void initializeData() {
        // 初始化用户数据
        initializeUsers();
        
        // 初始化赛程数据
        initializeMatches();
    }

    private void initializeUsers() {
        // 检查是否已有用户数据
        if (fcUserRepository.count() == 0) {
            // 创建所有参赛者
            FcUser user1 = new FcUser();
            user1.setId(1);
            user1.setName("克里斯");
            user1.setUsername("chris");
            user1.setPassword("123456");
            user1.setRole("USER");
            user1.setWin((short) 0);
            user1.setTie((short) 0);
            user1.setLose((short) 0);
            user1.setPowerful(1000);
            fcUserRepository.save(user1);

            FcUser user2 = new FcUser();
            user2.setId(2);
            user2.setName("的的");
            user2.setUsername("didi");
            user2.setPassword("123456");
            user2.setRole("USER");
            user2.setWin((short) 0);
            user2.setTie((short) 0);
            user2.setLose((short) 0);
            user2.setPowerful(1000);
            fcUserRepository.save(user2);

            FcUser user3 = new FcUser();
            user3.setId(3);
            user3.setName("1辰");
            user3.setUsername("yichen");
            user3.setPassword("123456");
            user3.setRole("USER");
            user3.setWin((short) 0);
            user3.setTie((short) 0);
            user3.setLose((short) 0);
            user3.setPowerful(1000);
            fcUserRepository.save(user3);

            FcUser user4 = new FcUser();
            user4.setId(4);
            user4.setName("Ramos");
            user4.setUsername("ramos");
            user4.setPassword("123456");
            user4.setRole("USER");
            user4.setWin((short) 0);
            user4.setTie((short) 0);
            user4.setLose((short) 0);
            user4.setPowerful(1000);
            fcUserRepository.save(user4);

            FcUser user5 = new FcUser();
            user5.setId(5);
            user5.setName("煎饼果子烤冷面");
            user5.setUsername("jianbing");
            user5.setPassword("123456");
            user5.setRole("USER");
            user5.setWin((short) 0);
            user5.setTie((short) 0);
            user5.setLose((short) 0);
            user5.setPowerful(1000);
            fcUserRepository.save(user5);

            FcUser user6 = new FcUser();
            user6.setId(6);
            user6.setName("洋");
            user6.setUsername("yang");
            user6.setPassword("123456");
            user6.setRole("USER");
            user6.setWin((short) 0);
            user6.setTie((short) 0);
            user6.setLose((short) 0);
            user6.setPowerful(1000);
            fcUserRepository.save(user6);

            FcUser user7 = new FcUser();
            user7.setId(7);
            user7.setName("Tohwa");
            user7.setUsername("tohwa");
            user7.setPassword("123456");
            user7.setRole("USER");
            user7.setWin((short) 0);
            user7.setTie((short) 0);
            user7.setLose((short) 0);
            user7.setPowerful(1000);
            fcUserRepository.save(user7);

            // 创建管理员用户
            FcUser admin = new FcUser();
            admin.setId(8);
            admin.setName("管理员");
            admin.setUsername("admin");
            admin.setPassword("admin123");
            admin.setRole("ADMIN");
            admin.setWin((short) 0);
            admin.setTie((short) 0);
            admin.setLose((short) 0);
            admin.setPowerful(1000);
            fcUserRepository.save(admin);

            // 创建超级管理员用户
            FcUser root = new FcUser();
            root.setId(9);
            root.setName("超级管理员");
            root.setUsername("root");
            root.setPassword("root123");
            root.setRole("ROOT");
            root.setWin((short) 0);
            root.setTie((short) 0);
            root.setLose((short) 0);
            root.setPowerful(1000);
            fcUserRepository.save(root);
        }
    }

    private void initializeMatches() {
        // 检查是否已有比赛数据
        if (matchRepository.count() == 0) {
            // 获取所有用户
            List<FcUser> users = fcUserRepository.findAll();
            if (users.size() < 2) {
                System.out.println("用户数量不足，跳过赛程初始化");
                return;
            }

            // 初始化比赛时间
            LocalDateTime baseTime = LocalDateTime.now().minusDays(30);

            // 生成所有可能的配对
            int matchId = 1;
            int roundCounter = 1;
            
            // 单循环赛制：生成所有可能的配对
            for (int i = 0; i < users.size() - 1; i++) {
                for (int j = i + 1; j < users.size(); j++) {
                    FcUser homeTeam = users.get(i);
                    FcUser awayTeam = users.get(j);
                    
                    // 生成比赛对象
                    Match match = new Match();
                    match.setId(matchId++);
                    match.setMatchNo("Match" + String.format("%03d", matchId));
                    match.setRound(1);
                    match.setHomeTeam(homeTeam);
                    match.setAwayTeam(awayTeam);
                    match.setHomeScore(null);
                    match.setAwayScore(null);
                    match.setStatus((short)0);
                    match.setMatchTime(baseTime.plusDays(roundCounter - 1));
                    match.setLocation("FC竞技场");
                    match.setCreatedAt(LocalDateTime.now());
                    match.setUpdatedAt(LocalDateTime.now());
                    
                    matchRepository.save(match);
                    roundCounter++;
                }
            }
            
            System.out.println("赛程数据初始化完成，共创建 " + (matchId - 1) + " 场比赛");
        }
    }


}