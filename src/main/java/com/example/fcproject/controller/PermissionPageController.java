package com.example.fcproject.controller;

import com.example.fcproject.model.FcUser;
import com.example.fcproject.service.FcUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@PreAuthorize("hasRole('ROOT')")
public class PermissionPageController {

    @Autowired
    private FcUserService fcUserService;

    /**
     * 权限配置页面
     * @param model 模型
     * @return 权限配置页面
     */
    @GetMapping("/permission-config")
    public String permissionConfig(Model model) {
        // 获取所有用户列表，排除当前ROOT用户
        List<FcUser> users = fcUserService.getAllUsers()
                .stream()
                .filter(user -> !"ROOT".equals(user.getRole()))
                .collect(java.util.stream.Collectors.toList());
        
        model.addAttribute("users", users);
        return "permission-config";
    }
}