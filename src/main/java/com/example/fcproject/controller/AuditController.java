package com.example.fcproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 审核页面控制器
 */
@Controller
public class AuditController {
    
    /**
     * 显示审核页面
     */
    @GetMapping("/audit-page")
    public String showAuditPage() {
        return "audit-page";
    }
}