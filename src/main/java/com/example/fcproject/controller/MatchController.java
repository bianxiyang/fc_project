package com.example.fcproject.controller;

import com.example.fcproject.dto.ScheduleGeneratorRequest;
import com.example.fcproject.model.Match;
import com.example.fcproject.model.FcUser;
import com.example.fcproject.model.MatchScoreAudit;
import com.example.fcproject.repository.MatchRepository;
import com.example.fcproject.repository.FcUserRepository;
import com.example.fcproject.repository.MatchScoreAuditRepository;
import com.example.fcproject.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private FcUserRepository fcUserRepository;
    
    @Autowired
    private MatchScoreAuditRepository matchScoreAuditRepository;

    // 获取所有比赛
    @GetMapping
    public ResponseEntity<ApiResponse> getAllMatches(Authentication authentication) {
        String username = authentication.getName();
        FcUser currentUser = fcUserRepository.findByUsername(username);
        List<Match> matches;
        
        // 检查用户角色，如果是USER角色，只返回该用户参与的比赛
        boolean isUserRole = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER"));
        
        if (isUserRole) {
            matches = matchRepository.findByHomeTeamOrAwayTeam(currentUser, currentUser);
        } else {
            matches = matchRepository.findAll();
        }
        
        return ResponseEntity.ok(
                ApiResponse.success("获取比赛成功", matches)
        );
    }

    // 根据ID获取比赛
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getMatchById(@PathVariable Integer id, Authentication authentication) {
        Optional<Match> matchOptional = matchRepository.findById(id);
        if (matchOptional.isPresent()) {
            Match match = matchOptional.get();
            
            // 检查用户角色，如果是USER角色，验证该比赛是否与用户相关
            boolean isUserRole = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER"));
            
            if (isUserRole) {
                String username = authentication.getName();
                FcUser currentUser = fcUserRepository.findByUsername(username);
                
                // 验证比赛的主队或客队是否是当前用户
                if (!match.getHomeTeam().equals(currentUser) && !match.getAwayTeam().equals(currentUser)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                            ApiResponse.error("无权访问该比赛")
                    );
                }
            }
            
            return ResponseEntity.ok(
                    ApiResponse.success("获取比赛成功", match)
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.error("比赛不存在")
            );
        }
    }

    // 更新比赛比分
    @PutMapping("/{id}/score")
    public ResponseEntity<ApiResponse> updateMatchScore(@PathVariable Integer id, @RequestBody MatchScoreRequest scoreRequest, Authentication authentication) {
        Optional<Match> matchOptional = matchRepository.findById(id);
        if (matchOptional.isPresent()) {
            Match match = matchOptional.get();
            
            // 检查比赛是否已经结束
            if (match.getStatus() == 2) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        ApiResponse.error("已完成的比赛不能编辑")
                );
            }
            
            // 检查是否已有待审核的比分修改记录
            List<MatchScoreAudit> pendingAudits = matchScoreAuditRepository.findByMatchIdAndAuditStatus(id, "PENDING");
            if (!pendingAudits.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        ApiResponse.error("该比赛已有待审核的比分修改请求，请等待审核完成后再提交")
                );
            }
            
            // 获取当前登录用户
            String username = authentication.getName();
            FcUser currentUser = fcUserRepository.findByUsername(username);
            
            // 检查用户角色，如果是USER角色，验证该比赛是否与用户相关
            boolean isUserRole = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER"));
            
            if (isUserRole) {
                // 验证比赛的主队或客队是否是当前用户
                if (!match.getHomeTeam().equals(currentUser) && !match.getAwayTeam().equals(currentUser)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                            ApiResponse.error("无权修改该比赛的比分")
                    );
                }
            }
            
            // 创建审核记录
            MatchScoreAudit audit = new MatchScoreAudit();
            audit.setMatch(match);
            audit.setHomeScore(scoreRequest.getHomeScore());
            audit.setAwayScore(scoreRequest.getAwayScore());
            audit.setStatus(scoreRequest.getStatus());
            audit.setSubmittedBy(currentUser);
            audit.setSubmittedAt(LocalDateTime.now());
            audit.setAuditStatus("PENDING");
            
            // 保存审核记录
            matchScoreAuditRepository.save(audit);
            
            return ResponseEntity.ok(
                    ApiResponse.success("比分修改已提交审核", null)
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.error("比赛不存在")
            );
        }
    }
    
    /**
     * 根据比赛结果更新用户统计数据
     * 现在只需要保存比赛结果，积分会在查询时动态计算
     */
    private void updateUserStats(Match match) {
        // 不需要手动更新用户统计数据，积分会在查询时动态计算
        // 只需要确保比赛结果已保存，统计数据会在下次查询时自动更新
    }

    // 生成赛程API
    @PostMapping("/generate-schedule")
    public ResponseEntity<ApiResponse> generateSchedule(@RequestBody ScheduleGeneratorRequest request) {
        // 验证输入参数
        if (request.getUserIds() == null || request.getUserIds().size() < 2) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponse.error("至少需要选择2个用户")
            );
        }
        
        try {
            // 获取选中的用户
            List<FcUser> selectedUsers = fcUserRepository.findAllById(request.getUserIds());
            if (selectedUsers.size() != request.getUserIds().size()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        ApiResponse.error("部分用户不存在")
                );
            }
            
            // 生成赛程
            List<Match> generatedMatches = generateMatches(selectedUsers, request.isIncludeHomeAway());
            
            // 保存到数据库
            List<Match> savedMatches = matchRepository.saveAll(generatedMatches);
            
            return ResponseEntity.ok(
                    ApiResponse.success("赛程生成成功", savedMatches)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error("赛程生成失败：" + e.getMessage())
            );
        }
    }
    
    /**
     * 生成比赛列表
     */
    private List<Match> generateMatches(List<FcUser> users, boolean includeHomeAway) {
        List<Match> matches = new ArrayList<>();
        int userCount = users.size();
        
        // 获取最大比赛ID，用于生成比赛编号
        Integer maxId = matchRepository.findAll().stream()
                .map(Match::getId)
                .max(Integer::compare)
                .orElse(0);
        
        // 生成比赛时间基准
        LocalDateTime baseMatchTime = LocalDateTime.now().plusDays(1);
        
        // 单循环赛制：生成所有可能的配对，确保每个人都和其他人对战一次
        int roundCounter = 1;
        
        // 对于不包含主客场的情况，使用双循环算法确保生成所有必要的配对
        // 对于包含主客场的情况，我们仍然需要在第二轮交换主客场
        
        // 第一轮：生成所有可能的配对（A vs B，A vs C，B vs C等）
        for (int i = 0; i < userCount - 1; i++) {
            for (int j = i + 1; j < userCount; j++) {
                FcUser homeTeam = users.get(i);
                FcUser awayTeam = users.get(j);
                
                // 生成比赛对象
                Match match = new Match();
                match.setId(++maxId);
                match.setMatchNo("MATCH" + String.format("%04d", maxId));
                match.setRound(1); // 所有比赛都在第一轮
                match.setHomeTeam(homeTeam);
                match.setAwayTeam(awayTeam);
                match.setHomeScore(null);
                match.setAwayScore(null);
                match.setStatus((short) 0); // 未开始
                match.setMatchTime(baseMatchTime.plusDays(roundCounter - 1)); // 每天安排比赛
                match.setLocation("FC竞技场");
                match.setCreatedAt(LocalDateTime.now());
                match.setUpdatedAt(LocalDateTime.now());
                
                matches.add(match);
                roundCounter++;
            }
        }
        
        // 如果需要主客场，再生成一轮交换主客场的比赛
        if (includeHomeAway) {
            roundCounter = 1;
            for (int i = 0; i < userCount - 1; i++) {
                for (int j = i + 1; j < userCount; j++) {
                    FcUser homeTeam = users.get(j); // 交换主客场
                    FcUser awayTeam = users.get(i);
                    
                    // 生成比赛对象
                    Match match = new Match();
                    match.setId(++maxId);
                    match.setMatchNo("MATCH" + String.format("%04d", maxId));
                    match.setRound(2); // 主客场第二轮
                    match.setHomeTeam(homeTeam);
                    match.setAwayTeam(awayTeam);
                    match.setHomeScore(null);
                    match.setAwayScore(null);
                    match.setStatus((short) 0); // 未开始
                    match.setMatchTime(baseMatchTime.plusDays(matches.size() + roundCounter - 1)); // 安排在所有第一轮比赛之后
                    match.setLocation("FC竞技场");
                    match.setCreatedAt(LocalDateTime.now());
                    match.setUpdatedAt(LocalDateTime.now());
                    
                    matches.add(match);
                    roundCounter++;
                }
            }
        }
        
        return matches;
    }
    
    /**
     * 生成第一轮比赛配对
     */
    private List<List<FcUser>> generateFirstRoundMatches(List<FcUser> users) {
        List<List<FcUser>> matches = new ArrayList<>();
        int userCount = users.size();
        
        // 创建用户副本并打乱顺序
        List<FcUser> shuffledUsers = new ArrayList<>(users);
        Collections.shuffle(shuffledUsers, new Random(System.currentTimeMillis()));
        
        // 处理轮空情况
        if (userCount % 2 != 0) {
            // 奇数个用户，每个轮次有一个用户轮空
            for (int i = 0; i < userCount - 1; i += 2) {
                List<FcUser> pair = new ArrayList<>();
                pair.add(shuffledUsers.get(i));
                pair.add(shuffledUsers.get(i + 1));
                matches.add(pair);
            }
            // 最后一个用户在第一轮轮空
        } else {
            // 偶数个用户，正常配对
            for (int i = 0; i < userCount / 2; i++) {
                List<FcUser> pair = new ArrayList<>();
                pair.add(shuffledUsers.get(i));
                pair.add(shuffledUsers.get(userCount - 1 - i));
                matches.add(pair);
            }
        }
        
        return matches;
    }
    
    /**
     * 生成下一轮比赛配对（使用标准的循环赛轮转法）
     */
    private List<List<FcUser>> generateNextRoundMatches(List<List<FcUser>> currentRound, List<FcUser> allUsers) {
        List<List<FcUser>> nextRound = new ArrayList<>();
        int userCount = allUsers.size();
        boolean hasBye = userCount % 2 != 0;
        
        // 创建用户列表的副本并保持原始顺序
        List<FcUser> users = new ArrayList<>(allUsers);
        
        // 处理奇数用户情况
        if (hasBye) {
            // 对于奇数用户，我们需要找到轮空用户
            // 先收集所有参与当前轮次的用户
            Set<FcUser> playedUsers = new HashSet<>();
            for (List<FcUser> match : currentRound) {
                playedUsers.add(match.get(0));
                playedUsers.add(match.get(1));
            }
            
            // 找到轮空的用户
            FcUser byeUser = null;
            for (FcUser user : allUsers) {
                if (!playedUsers.contains(user)) {
                    byeUser = user;
                    break;
                }
            }
            
            // 创建一个临时列表，不包含轮空用户
            List<FcUser> tempUsers = new ArrayList<>();
            for (FcUser user : users) {
                if (!user.equals(byeUser)) {
                    tempUsers.add(user);
                }
            }
            
            // 应用标准的循环赛轮转算法（偶数用户）
            // 固定第一个用户，其他用户逆时针轮转
            FcUser firstUser = tempUsers.get(0);
            List<FcUser> rotatedTemp = new ArrayList<>();
            rotatedTemp.add(firstUser); // 固定第一个用户
            rotatedTemp.add(tempUsers.get(tempUsers.size() - 1)); // 最后一个用户放到第二位
            // 中间的用户依次后移
            for (int i = 1; i < tempUsers.size() - 1; i++) {
                rotatedTemp.add(tempUsers.get(i));
            }
            
            // 为轮转后的列表生成配对（第一个用户与最后一个配对，第二个与倒数第二个配对，以此类推）
            int n = rotatedTemp.size();
            for (int i = 0; i < n / 2; i++) {
                List<FcUser> pair = new ArrayList<>();
                pair.add(rotatedTemp.get(i));
                pair.add(rotatedTemp.get(n - 1 - i));
                nextRound.add(pair);
            }
        } else {
            // 偶数用户，应用标准的循环赛轮转算法
            // 固定第一个用户，其他用户逆时针轮转
            FcUser firstUser = users.get(0);
            List<FcUser> rotatedUsers = new ArrayList<>();
            rotatedUsers.add(firstUser); // 固定第一个用户
            rotatedUsers.add(users.get(userCount - 1)); // 最后一个用户放到第二位
            // 中间的用户依次后移
            for (int i = 1; i < userCount - 1; i++) {
                rotatedUsers.add(users.get(i));
            }
            
            // 为轮转后的列表生成配对（第一个用户与最后一个配对，第二个与倒数第二个配对，以此类推）
            for (int i = 0; i < userCount / 2; i++) {
                List<FcUser> pair = new ArrayList<>();
                pair.add(rotatedUsers.get(i));
                pair.add(rotatedUsers.get(userCount - 1 - i));
                nextRound.add(pair);
            }
        }
        
        return nextRound;
    }
    
    // 获取指定轮次的比赛
    @GetMapping("/round/{round}")
    public ResponseEntity<ApiResponse> getMatchesByRound(@PathVariable Integer round) {
        List<Match> matches = matchRepository.findByRound(round);
        return ResponseEntity.ok(
                ApiResponse.success("获取指定轮次的比赛成功", matches)
        );
    }

    // 获取指定状态的比赛
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse> getMatchesByStatus(@PathVariable Short status) {
        List<Match> matches = matchRepository.findByStatus(status);
        return ResponseEntity.ok(
                ApiResponse.success("获取指定状态比赛成功", matches)
        );
    }

    // 创建新比赛
    @PostMapping
    public ResponseEntity<ApiResponse> createMatch(@RequestBody Match match) {
        // 验证主队和客队是否存在
        if (match.getHomeTeam() != null && match.getHomeTeam().getId() != null) {
            FcUser homeTeam = fcUserRepository.findById(match.getHomeTeam().getId()).orElse(null);
            if (homeTeam == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        ApiResponse.error("主队不存在")
                );
            }
            match.setHomeTeam(homeTeam);
        }
        
        if (match.getAwayTeam() != null && match.getAwayTeam().getId() != null) {
            FcUser awayTeam = fcUserRepository.findById(match.getAwayTeam().getId()).orElse(null);
            if (awayTeam == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        ApiResponse.error("客队不存在")
                );
            }
            match.setAwayTeam(awayTeam);
        }
        
        match.setCreatedAt(LocalDateTime.now());
        match.setUpdatedAt(LocalDateTime.now());
        
        Match savedMatch = matchRepository.save(match);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("创建比赛成功", savedMatch)
        );
    }

    // 删除比赛
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteMatch(@PathVariable Integer id) {
        Optional<Match> matchOptional = matchRepository.findById(id);
        if (matchOptional.isPresent()) {
            matchRepository.deleteById(id);
            return ResponseEntity.ok(
                    ApiResponse.success("删除比赛成功")
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.error("比赛不存在")
            );
        }
    }

    // 获取所有待审核的比分修改记录
    @GetMapping("/audit/pending")
    public ResponseEntity<ApiResponse> getPendingAudits() {
        List<MatchScoreAudit> audits = matchScoreAuditRepository.findByAuditStatus("PENDING");
        return ResponseEntity.ok(
                ApiResponse.success("获取待审核记录成功", audits)
        );
    }
    
    // 批准比分修改
    @PutMapping("/audit/{id}/approve")
    public ResponseEntity<ApiResponse> approveAudit(@PathVariable Integer id, Authentication authentication) {
        Optional<MatchScoreAudit> auditOptional = matchScoreAuditRepository.findById(id);
        if (auditOptional.isPresent()) {
            MatchScoreAudit audit = auditOptional.get();
            Match match = audit.getMatch();
            
            // 更新比赛比分
            match.setHomeScore(audit.getHomeScore());
            match.setAwayScore(audit.getAwayScore());
            match.setStatus(audit.getStatus());
            match.setUpdatedAt(LocalDateTime.now());
            
            // 只有当比赛状态为已结束(2)时，才更新用户数据
            if (audit.getStatus() == 2) {
                updateUserStats(match);
            }
            
            // 保存更新后的比赛信息
            matchRepository.save(match);
            
            // 更新审核记录状态
            String username = authentication.getName();
            FcUser currentUser = fcUserRepository.findByUsername(username);
            audit.setAuditStatus("APPROVED");
            audit.setApprovedBy(currentUser);
            audit.setApprovedAt(LocalDateTime.now());
            matchScoreAuditRepository.save(audit);
            
            return ResponseEntity.ok(
                    ApiResponse.success("批准比分修改成功", null)
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.error("审核记录不存在")
            );
        }
    }
    
    // 拒绝比分修改
    @PutMapping("/audit/{id}/reject")
    public ResponseEntity<ApiResponse> rejectAudit(@PathVariable Integer id, @RequestBody RejectAuditRequest request, Authentication authentication) {
        Optional<MatchScoreAudit> auditOptional = matchScoreAuditRepository.findById(id);
        if (auditOptional.isPresent()) {
            MatchScoreAudit audit = auditOptional.get();
            
            // 更新审核记录状态
            String username = authentication.getName();
            FcUser currentUser = fcUserRepository.findByUsername(username);
            audit.setAuditStatus("REJECTED");
            audit.setRejectReason(request.getRejectReason());
            audit.setApprovedBy(currentUser);
            audit.setApprovedAt(LocalDateTime.now());
            matchScoreAuditRepository.save(audit);
            
            return ResponseEntity.ok(
                    ApiResponse.success("拒绝比分修改成功", null)
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.error("审核记录不存在")
            );
        }
    }
    
    // 比分更新请求类
    public static class MatchScoreRequest {
        private Integer homeScore;
        private Integer awayScore;
        private Short status;

        public Integer getHomeScore() {
            return homeScore;
        }

        public void setHomeScore(Integer homeScore) {
            this.homeScore = homeScore;
        }

        public Integer getAwayScore() {
            return awayScore;
        }

        public void setAwayScore(Integer awayScore) {
            this.awayScore = awayScore;
        }

        public Short getStatus() {
            return status;
        }

        public void setStatus(Short status) {
            this.status = status;
        }
    }
    
    // 拒绝审核请求类
    public static class RejectAuditRequest {
        private String rejectReason;

        public String getRejectReason() {
            return rejectReason;
        }

        public void setRejectReason(String rejectReason) {
            this.rejectReason = rejectReason;
        }
    }
}