package com.example.fcproject.controller;

import com.example.fcproject.model.FcUser;
import com.example.fcproject.service.FcUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 页面控制器，处理HTML页面请求
 */
@Controller
public class PageController {

    @Autowired
    private FcUserService fcUserService;

    /**
     * 跳转到积分榜页面
     */
    @GetMapping("/rank-table")
    public String rankTable(Model model) {
        // 获取按积分降序排序的用户列表，会动态计算积分
        List<FcUser> users = fcUserService.getUsersByPowerfulDesc();
        // 将用户列表添加到模型中，供Thymeleaf模板使用
        model.addAttribute("users", users);
        // 返回积分榜页面
        return "rank-table";
    }
    
    /**
     * 跳转到用户管理页面
     */
    @GetMapping("/fc-user")
    public String fcUser() {
        return "fc-user";
    }
    
    /**
     * 跳转到登录页面
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    /**
     * 处理登录失败
     */
    @GetMapping("/login?error=true")
    public String loginError(Model model) {
        model.addAttribute("error", "用户名或密码错误");
        return "login";
    }
}