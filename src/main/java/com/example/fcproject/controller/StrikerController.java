package com.example.fcproject.controller;

import com.example.fcproject.model.StrikerStats;
import com.example.fcproject.service.StrikerService;
import com.example.fcproject.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 射手榜控制器，处理射手榜相关的页面访问和API请求
 */
@Controller
public class StrikerController {

    @Autowired
    private StrikerService strikerService;

    /**
     * 显示射手榜页面
     * @param model 模型对象
     * @return 射手榜页面模板
     */
    @GetMapping("/striker-table")
    public String showStrikerTable(Model model) {
        List<StrikerStats> strikerStats = strikerService.getAllStrikerStats();
        model.addAttribute("strikerStats", strikerStats);
        return "striker-table";
    }
    
    /**
     * 获取所有球员的射手榜统计（API接口）
     * @return 射手榜统计列表
     */
    @GetMapping("/api/striker-stats")
    @ResponseBody
    public ResponseEntity<ApiResponse> getAllStrikerStats() {
        List<StrikerStats> strikerStats = strikerService.getAllStrikerStats();
        return ResponseEntity.ok(
                ApiResponse.success("获取射手榜统计成功", strikerStats)
        );
    }
    
    /**
     * 获取特定球员的射手榜统计（API接口）
     * @param userId 用户ID
     * @return 射手榜统计对象
     */
    @GetMapping("/api/striker-stats/{userId}")
    @ResponseBody
    public ResponseEntity<ApiResponse> getStrikerStatsByUserId(@PathVariable Integer userId) {
        StrikerStats strikerStats = strikerService.getStrikerStatsByUserId(userId);
        if (strikerStats != null) {
            return ResponseEntity.ok(
                    ApiResponse.success("获取球员射手榜统计成功", strikerStats)
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.error("用户不存在")
            );
        }
    }
}