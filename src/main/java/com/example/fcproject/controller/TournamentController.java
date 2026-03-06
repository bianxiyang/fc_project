package com.example.fcproject.controller;

import com.example.fcproject.dto.TournamentCreateRequest;
import com.example.fcproject.model.*;
import com.example.fcproject.repository.*;
import com.example.fcproject.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 锦标赛控制器，用于处理淘汰赛相关的请求
 */
@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TournamentParticipantRepository tournamentParticipantRepository;

    @Autowired
    private TournamentMatchRepository tournamentMatchRepository;

    @Autowired
    private FcUserRepository fcUserRepository;
    
    @Autowired
    private TournamentMatchScoreAuditRepository tournamentMatchScoreAuditRepository;

    /**
     * 创建锦标赛并生成赛程
     */
    @PostMapping
    @Transactional
    public ResponseEntity<ApiResponse> createTournament(@RequestBody TournamentCreateRequest request) {
        try {
            // 验证参数
            if (request.getUserIds() == null || request.getUserIds().size() < 2) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        ApiResponse.error("至少需要选择2个用户")
                );
            }

            // 获取参与者用户
            List<FcUser> participants = fcUserRepository.findAllById(request.getUserIds());
            if (participants.size() != request.getUserIds().size()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        ApiResponse.error("部分用户不存在")
                );
            }

            // 创建锦标赛
            Tournament tournament = new Tournament();
            tournament.setName(request.getName());
            tournament.setParticipantCount(participants.size());
            tournament.setStatus((short) 0);
            tournament.setCreatedAt(LocalDateTime.now());
            tournament.setUpdatedAt(LocalDateTime.now());
            tournament = tournamentRepository.save(tournament);

            // 添加参与者
            List<TournamentParticipant> tournamentParticipants = new ArrayList<>();
            for (FcUser user : participants) {
                TournamentParticipant participant = new TournamentParticipant();
                participant.setTournament(tournament);
                participant.setUser(user);
                participant.setStatus((short) 0);
                participant.setCreatedAt(LocalDateTime.now());
                participant.setUpdatedAt(LocalDateTime.now());
                tournamentParticipants.add(participant);
            }
            tournamentParticipantRepository.saveAll(tournamentParticipants);

            // 生成赛程
            generateTournamentSchedule(tournament, participants);

            return ResponseEntity.ok(
                    ApiResponse.success("锦标赛创建成功", tournament)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error("锦标赛创建失败：" + e.getMessage())
            );
        }
    }

    /**
     * 生成锦标赛赛程
     */
    private void generateTournamentSchedule(Tournament tournament, List<FcUser> participants) {
        List<FcUser> remainingParticipants = new ArrayList<>(participants);
        Collections.shuffle(remainingParticipants);

        // 如果参与者数量是奇数，添加一个虚拟轮空用户
        if (remainingParticipants.size() % 2 != 0) {
            remainingParticipants.add(null); // null表示轮空
        }

        int round = 1;
        
        // 生成所有轮次的比赛，直到决赛
        // 对于8个用户，应该生成3轮比赛：8进4、4进2、决赛
        while (remainingParticipants.size() > 1) {
            generateRoundMatches(tournament, round, remainingParticipants);
            
            // 准备下一轮参与者（当前轮次的获胜者位置，初始为null）
            List<FcUser> nextRoundParticipants = new ArrayList<>();
            // 下一轮参与者数量是当前轮次的一半
            for (int i = 0; i < remainingParticipants.size() / 2; i++) {
                nextRoundParticipants.add(null);
            }
            remainingParticipants = nextRoundParticipants;
            round++;
        }
    }

    /**
     * 生成单轮比赛
     */
    private void generateRoundMatches(Tournament tournament, int round, List<FcUser> participants) {
        int matchNumber = 1;

        for (int i = 0; i < participants.size(); i += 2) {
            FcUser homeTeam = participants.get(i);
            FcUser awayTeam = participants.get(i + 1);

            // 为每场比赛生成3局比赛（3局2胜制）
            for (int game = 1; game <= 3; game++) {
                TournamentMatch match = new TournamentMatch();
                match.setTournament(tournament);
                match.setRound(round);
                match.setMatchNumber(matchNumber);
                match.setGameNumber(game);
                match.setHomeTeam(homeTeam);
                match.setAwayTeam(awayTeam);
                match.setCreatedAt(LocalDateTime.now());
                match.setUpdatedAt(LocalDateTime.now());
                
                // 初始状态设置为未开始
                match.setStatus((short) 0); // 未开始
                
                // 只有在第一轮比赛中，当参与者中有实际的轮空情况时（即homeTeam或awayTeam为null），才将比赛状态设置为已结束
                // 对于后续轮次的比赛，即使参与者暂时为null，也应该将状态设置为未开始
                // 或者是在后续轮次中，当有一方已经确定晋级，而另一方还未确定时，也应该将状态设置为未开始
                if (round == 1 && (homeTeam == null || awayTeam == null)) {
                    match.setStatus((short) 2); // 已结束
                    match.setWinner(homeTeam != null ? homeTeam : awayTeam);
                    match.setMatchResult(homeTeam != null ? (short) 0 : (short) 1);
                    // 设置轮空比赛的比分为0:0
                    match.setHomeScore(0);
                    match.setAwayScore(0);
                }
                tournamentMatchRepository.save(match);
            }
            matchNumber++;
        }
    }

    /**
     * 获取锦标赛详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getTournament(@PathVariable Integer id) {
        Optional<Tournament> tournamentOptional = tournamentRepository.findById(id);
        if (tournamentOptional.isPresent()) {
            return ResponseEntity.ok(
                    ApiResponse.success("获取锦标赛成功", tournamentOptional.get())
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.error("锦标赛不存在")
            );
        }
    }

    /**
     * 获取锦标赛所有比赛
     */
    @GetMapping("/{id}/matches")
    public ResponseEntity<ApiResponse> getTournamentMatches(@PathVariable Integer id, Authentication authentication) {
        String username = authentication.getName();
        FcUser currentUser = fcUserRepository.findByUsername(username);
        List<TournamentMatch> matches;
        
        // 检查用户角色，如果是USER角色，只返回该用户参与的比赛
        boolean isUserRole = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER"));
        
        if (isUserRole) {
            List<TournamentMatch> allMatches = tournamentMatchRepository.findByTournamentId(id);
            matches = allMatches.stream()
                    .filter(match -> match.getHomeTeam().equals(currentUser) || match.getAwayTeam().equals(currentUser))
                    .collect(Collectors.toList());
        } else {
            matches = tournamentMatchRepository.findByTournamentId(id);
        }
        
        return ResponseEntity.ok(
                ApiResponse.success("获取锦标赛比赛成功", matches)
        );
    }

    /**
     * 获取锦标赛的比赛全貌（树形结构）
     */
    @GetMapping("/{id}/bracket")
    public ResponseEntity<ApiResponse> getTournamentBracket(@PathVariable Integer id) {
        // 查询所有比赛
        List<TournamentMatch> matches = tournamentMatchRepository.findByTournamentId(id);
        
        // 按轮次和比赛编号分组
        Map<Integer, Map<Integer, List<TournamentMatch>>> bracket = new TreeMap<>();
        
        for (TournamentMatch match : matches) {
            bracket.computeIfAbsent(match.getRound(), k -> new TreeMap<>());
            bracket.get(match.getRound()).computeIfAbsent(match.getMatchNumber(), k -> new ArrayList<>()).add(match);
        }
        
        return ResponseEntity.ok(
                ApiResponse.success("获取锦标赛比赛全貌成功", bracket)
        );
    }

    /**
     * 更新比赛比分
     */
    @PutMapping("/matches/{id}/score")
    public ResponseEntity<ApiResponse> updateMatchScore(@PathVariable Integer id, 
                                                       @RequestBody MatchScoreRequest scoreRequest,
                                                       Authentication authentication) {
        Optional<TournamentMatch> matchOptional = tournamentMatchRepository.findById(id);
        if (matchOptional.isPresent()) {
            TournamentMatch match = matchOptional.get();
            
            // 检查比赛是否已经结束
            if (match.getStatus() == 2) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        ApiResponse.error("已完成的比赛不能编辑")
                );
            }
            
            // 检查是否已有待审核的比分修改记录
            List<TournamentMatchScoreAudit> pendingAudits = tournamentMatchScoreAuditRepository.findByMatchIdAndAuditStatus(id, "PENDING");
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
            TournamentMatchScoreAudit audit = new TournamentMatchScoreAudit();
            audit.setMatch(match);
            audit.setHomeScore(scoreRequest.getHomeScore());
            audit.setAwayScore(scoreRequest.getAwayScore());
            audit.setStatus((short) 2); // 比赛结束
            audit.setSubmittedBy(currentUser);
            audit.setSubmittedAt(LocalDateTime.now());
            audit.setAuditStatus("PENDING");
            
            // 保存审核记录
            tournamentMatchScoreAuditRepository.save(audit);
            
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
     * 检查并处理下一轮比赛
     */
    private void checkAndProcessNextRound(TournamentMatch match) {
        int round = match.getRound();
        int matchNumber = match.getMatchNumber();
        Tournament tournament = match.getTournament();
        
        // 查询该轮次、该比赛编号的所有局数
        List<TournamentMatch> allGames = tournamentMatchRepository.findByTournamentIdAndRoundAndMatchNumber(
                tournament.getId(), round, matchNumber);
        
        // 统计双方获胜局数
        int homeWins = 0;
        int awayWins = 0;
        for (TournamentMatch game : allGames) {
            if (game.getStatus() == 2) {
                if (game.getMatchResult() == 0) {
                    homeWins++;
                } else {
                    awayWins++;
                }
            }
        }
        
        // 判断是否已经分出胜负（3局2胜）
        if (homeWins >= 2 || awayWins >= 2) {
            // 获取获胜者
            FcUser winner = homeWins >= 2 ? match.getHomeTeam() : match.getAwayTeam();
            
            // 更新参与者状态
            TournamentParticipant participant = tournamentParticipantRepository.findByTournamentIdAndUserId(
                    tournament.getId(), winner.getId());
            if (participant != null) {
                participant.setStatus((short) 1); // 已晋级
                tournamentParticipantRepository.save(participant);
            }
            
            // 如果已经分出胜负，将剩余未开始的比赛标记为已结束
            for (TournamentMatch game : allGames) {
                if (game.getStatus() != 2) { // 未结束的比赛
                    game.setStatus((short) 2); // 标记为已结束
                    game.setUpdatedAt(LocalDateTime.now());
                    // 设置获胜者和比赛结果
                    if (homeWins >= 2) {
                        game.setWinner(game.getHomeTeam());
                        game.setMatchResult((short) 0);
                        game.setHomeScore(0);
                        game.setAwayScore(0);
                    } else {
                        game.setWinner(game.getAwayTeam());
                        game.setMatchResult((short) 1);
                        game.setHomeScore(0);
                        game.setAwayScore(0);
                    }
                    tournamentMatchRepository.save(game);
                }
            }
            
            // 生成下一轮比赛
            generateNextRoundMatch(tournament, round, matchNumber, winner);
        }
    }

    /**
     * 生成下一轮比赛
     */
    private void generateNextRoundMatch(Tournament tournament, int currentRound, int matchNumber, FcUser winner) {
        // 计算最大轮次数：log2(参与者数量)，向上取整
        // 对于8个用户，最大轮次数是3（8→4→2→1）
        int maxRounds = (int) Math.ceil(Math.log(tournament.getParticipantCount()) / Math.log(2));
        
        // 如果当前轮次已经是决赛轮次，不需要生成下一轮比赛
        if (currentRound >= maxRounds) {
            return; // 直接返回，不生成下一轮比赛
        }
        
        // 下一轮比赛信息
        int nextRound = currentRound + 1;
        int nextMatchNumber = (matchNumber + 1) / 2;
        
        // 查询下一轮对应的比赛
        List<TournamentMatch> nextRoundMatches = tournamentMatchRepository.findByTournamentIdAndRoundAndMatchNumber(
                tournament.getId(), nextRound, nextMatchNumber);
        
        if (!nextRoundMatches.isEmpty()) {
            // 根据当前比赛编号判断是主场还是客场
            boolean isHomeTeam = matchNumber % 2 == 1;
            
            // 更新下一轮比赛的队伍信息
            for (TournamentMatch nextMatch : nextRoundMatches) {
                if (isHomeTeam) {
                    nextMatch.setHomeTeam(winner);
                } else {
                    nextMatch.setAwayTeam(winner);
                }
                nextMatch.setUpdatedAt(LocalDateTime.now());
                tournamentMatchRepository.save(nextMatch);
            }
        } else {
            // 如果下一轮比赛不存在，创建新的比赛
            // 为每场比赛生成3局比赛（3局2胜制）
            for (int game = 1; game <= 3; game++) {
                TournamentMatch match = new TournamentMatch();
                match.setTournament(tournament);
                match.setRound(nextRound);
                match.setMatchNumber(nextMatchNumber);
                match.setGameNumber(game);
                
                // 根据当前比赛编号判断是主场还是客场
                boolean isHomeTeam = matchNumber % 2 == 1;
                if (isHomeTeam) {
                    match.setHomeTeam(winner);
                    match.setAwayTeam(null); // 客场球队暂时为空，等待另一组比赛结果
                } else {
                    match.setHomeTeam(null); // 主场球队暂时为空，等待另一组比赛结果
                    match.setAwayTeam(winner);
                }
                
                match.setStatus((short) 0); // 未开始
                match.setCreatedAt(LocalDateTime.now());
                match.setUpdatedAt(LocalDateTime.now());
                tournamentMatchRepository.save(match);
            }
        }
    }

    /**
     * 获取所有锦标赛
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getAllTournaments(Authentication authentication) {
        String username = authentication.getName();
        FcUser currentUser = fcUserRepository.findByUsername(username);
        List<Tournament> tournaments;
        
        // 检查用户角色，如果是USER角色，只返回该用户参与的锦标赛
        boolean isUserRole = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER"));
        
        if (isUserRole) {
            // 获取用户参与的所有锦标赛
            List<TournamentParticipant> participants = tournamentParticipantRepository.findByUserId(currentUser.getId());
            List<Integer> tournamentIds = participants.stream()
                    .map(TournamentParticipant::getTournamentId)
                    .collect(Collectors.toList());
            
            if (tournamentIds.isEmpty()) {
                tournaments = new ArrayList<>();
            } else {
                tournaments = tournamentRepository.findAllById(tournamentIds);
            }
        } else {
            tournaments = tournamentRepository.findAll();
        }
        
        return ResponseEntity.ok(
                ApiResponse.success("获取所有锦标赛成功", tournaments)
        );
    }

    // 内部类：用于接收比分更新请求
    public static class MatchScoreRequest {
        private Integer homeScore;
        private Integer awayScore;

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
    }
    
    // 内部类：用于接收拒绝审核请求
    public static class RejectAuditRequest {
        private String rejectReason;

        public String getRejectReason() {
            return rejectReason;
        }

        public void setRejectReason(String rejectReason) {
            this.rejectReason = rejectReason;
        }
    }
    
    /**
     * 获取所有待审核的淘汰赛比分修改记录
     */
    @GetMapping("/audit/pending")
    public ResponseEntity<ApiResponse> getPendingTournamentAudits() {
        List<TournamentMatchScoreAudit> audits = tournamentMatchScoreAuditRepository.findByAuditStatus("PENDING");
        return ResponseEntity.ok(
                ApiResponse.success("获取待审核记录成功", audits)
        );
    }
    
    /**
     * 批准淘汰赛比分修改
     */
    @PutMapping("/audit/{id}/approve")
    public ResponseEntity<ApiResponse> approveTournamentAudit(@PathVariable Integer id, Authentication authentication) {
        Optional<TournamentMatchScoreAudit> auditOptional = tournamentMatchScoreAuditRepository.findById(id);
        if (auditOptional.isPresent()) {
            TournamentMatchScoreAudit audit = auditOptional.get();
            TournamentMatch match = audit.getMatch();
            
            // 更新比赛比分
            match.setHomeScore(audit.getHomeScore());
            match.setAwayScore(audit.getAwayScore());
            match.setUpdatedAt(LocalDateTime.now());
            
            // 确定获胜者
            if (audit.getHomeScore() > audit.getAwayScore()) {
                match.setWinner(match.getHomeTeam());
                match.setMatchResult((short) 0);
            } else {
                match.setWinner(match.getAwayTeam());
                match.setMatchResult((short) 1);
            }
            
            // 标记比赛为已结束
            match.setStatus((short) 2);
            tournamentMatchRepository.save(match);
            
            // 检查是否需要生成下一轮比赛或更新晋级情况
            checkAndProcessNextRound(match);
            
            // 更新审核记录状态
            String username = authentication.getName();
            FcUser currentUser = fcUserRepository.findByUsername(username);
            audit.setAuditStatus("APPROVED");
            audit.setApprovedBy(currentUser);
            audit.setApprovedAt(LocalDateTime.now());
            tournamentMatchScoreAuditRepository.save(audit);
            
            return ResponseEntity.ok(
                    ApiResponse.success("批准比分修改成功", null)
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.error("审核记录不存在")
            );
        }
    }
    
    /**
     * 拒绝淘汰赛比分修改
     */
    @PutMapping("/audit/{id}/reject")
    public ResponseEntity<ApiResponse> rejectTournamentAudit(@PathVariable Integer id, @RequestBody RejectAuditRequest request, Authentication authentication) {
        Optional<TournamentMatchScoreAudit> auditOptional = tournamentMatchScoreAuditRepository.findById(id);
        if (auditOptional.isPresent()) {
            TournamentMatchScoreAudit audit = auditOptional.get();
            
            // 更新审核记录状态
            String username = authentication.getName();
            FcUser currentUser = fcUserRepository.findByUsername(username);
            audit.setAuditStatus("REJECTED");
            audit.setRejectReason(request.getRejectReason());
            audit.setApprovedBy(currentUser);
            audit.setApprovedAt(LocalDateTime.now());
            tournamentMatchScoreAuditRepository.save(audit);
            
            return ResponseEntity.ok(
                    ApiResponse.success("拒绝比分修改成功", null)
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.error("审核记录不存在")
            );
        }
    }
}
