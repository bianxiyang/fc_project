package com.example.fcproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 视图控制器，用于渲染Thymeleaf模板页面
 */
@Controller
public class ViewController {

    /**
     * 跳转到比赛比分管理页面
     */
    @GetMapping("/match-score")
    public String matchScorePage() {
        return "match-score";
    }
    
    /**
     * 首页重定向到比赛比分管理页面
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/match-score";
    }
    
    /**
     * 跳转到淘汰赛管理页面
     */
    @GetMapping("/tournament-management")
    public String tournamentManagementPage() {
        return "tournament-management";
    }
    
    /**
     * 跳转到淘汰赛赛程图页面
     */
    @GetMapping("/tournament-bracket")
    public String tournamentBracketPage() {
        return "tournament-bracket";
    }
    
    /**
     * 跳转到淘汰赛比分录入页面
     */
    @GetMapping("/tournament-score")
    public String tournamentScorePage() {
        return "tournament-score";
    }
    
    /**
     * 跳转到所有淘汰赛赛程图页面
     */
    @GetMapping("/all-tournament-brackets")
    public String allTournamentBracketsPage() {
        return "all-tournament-brackets";
    }
    
    /**
     * 跳转到淘汰赛树状图页面
     */
    @GetMapping("/tournament-tree")
    public String tournamentTreePage() {
        return "tournament-tree";
    }
}