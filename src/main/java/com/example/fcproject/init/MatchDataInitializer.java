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
import java.util.HashMap;
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
            Map<String, FcUser> userMap = new HashMap<>();
            fcUserRepository.findAll().forEach(user -> userMap.put(user.getName(), user));

            // 初始化比赛时间
            LocalDateTime baseTime = LocalDateTime.now().minusDays(30);

            // 创建21场比赛
            createMatch(1, "Match001", 1, userMap.get("克里斯"), userMap.get("的的"), baseTime.plusDays(1), (short)0);
            createMatch(2, "Match002", 1, userMap.get("1辰"), userMap.get("Ramos"), baseTime.plusDays(1).plusHours(2), (short)0);
            createMatch(3, "Match003", 1, userMap.get("煎饼果子烤冷面"), userMap.get("的的"), baseTime.plusDays(2), (short)0);
            createMatch(4, "Match004", 1, userMap.get("洋"), userMap.get("Tohwa"), baseTime.plusDays(2).plusHours(2), (short)0);
            createMatch(5, "Match005", 2, userMap.get("1辰"), userMap.get("煎饼果子烤冷面"), baseTime.plusDays(3), (short)0);
            createMatch(6, "Match006", 2, userMap.get("1辰"), userMap.get("Tohwa"), baseTime.plusDays(3).plusHours(2), (short)0);
            createMatch(7, "Match007", 2, userMap.get("洋"), userMap.get("煎饼果子烤冷面"), baseTime.plusDays(4), (short)0);
            createMatch(8, "Match008", 2, userMap.get("煎饼果子烤冷面"), userMap.get("Tohwa"), baseTime.plusDays(4).plusHours(2), (short)0);
            createMatch(9, "Match009", 3, userMap.get("1辰"), userMap.get("克里斯"), baseTime.plusDays(5), (short)0);
            createMatch(10, "Match010", 3, userMap.get("洋"), userMap.get("克里斯"), baseTime.plusDays(5).plusHours(2), (short)0);
            createMatch(11, "Match011", 3, userMap.get("洋"), userMap.get("Ramos"), baseTime.plusDays(6), (short)0);
            createMatch(12, "Match012", 3, userMap.get("洋"), userMap.get("1辰"), baseTime.plusDays(6).plusHours(2), (short)0);
            createMatch(13, "Match013", 4, userMap.get("Ramos"), userMap.get("的的"), baseTime.plusDays(7), (short)0);
            createMatch(14, "Match014", 4, userMap.get("Tohwa"), userMap.get("克里斯"), baseTime.plusDays(7).plusHours(2), (short)0);
            createMatch(15, "Match015", 4, userMap.get("Ramos"), userMap.get("Tohwa"), baseTime.plusDays(8), (short)0);
            createMatch(16, "Match016", 4, userMap.get("Tohwa"), userMap.get("的的"), baseTime.plusDays(8).plusHours(2), (short)0);
            createMatch(17, "Match017", 5, userMap.get("洋"), userMap.get("的的"), baseTime.plusDays(9), (short)0);
            createMatch(18, "Match018", 5, userMap.get("煎饼果子烤冷面"), userMap.get("克里斯"), baseTime.plusDays(9).plusHours(2), (short)0);
            createMatch(19, "Match019", 5, userMap.get("1辰"), userMap.get("的的"), baseTime.plusDays(10), (short)0);
            createMatch(20, "Match020", 5, userMap.get("煎饼果子烤冷面"), userMap.get("Ramos"), baseTime.plusDays(10).plusHours(2), (short)0);
            createMatch(21, "Match021", 6, userMap.get("Ramos"), userMap.get("克里斯"), baseTime.plusDays(11), (short)0);
        }
    }

    private void createMatch(Integer id, String matchNo, Integer round, FcUser homeTeam, FcUser awayTeam, LocalDateTime matchTime, Short status) {
        Match match = new Match();
        match.setId(id);
        match.setMatchNo(matchNo);
        match.setRound(round);
        match.setHomeTeam(homeTeam);
        match.setAwayTeam(awayTeam);
        match.setHomeScore(null);
        match.setAwayScore(null);
        match.setStatus(status);
        match.setMatchTime(matchTime);
        match.setLocation("FC竞技场");
        match.setCreatedAt(LocalDateTime.now());
        match.setUpdatedAt(LocalDateTime.now());
        matchRepository.save(match);
    }
}