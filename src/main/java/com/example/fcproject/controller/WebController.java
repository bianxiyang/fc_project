package com.example.fcproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Web页面控制器，处理HTML页面访问请求
 */
@Controller
public class WebController {
    
    // 访问赛程生成器页面
    @GetMapping("/schedule-generator")
    public String scheduleGenerator() {
        return "schedule-generator";
    }
    
    // 访问简易赛程生成器页面
    @GetMapping("/simple-schedule")
    public String simpleSchedule() {
        return "simple-schedule";
    }
    
    // 访问能力值计算器页面
    @GetMapping("/ability-calculator")
    public String abilityCalculator() {
        return "ability-calculator";
    }
}