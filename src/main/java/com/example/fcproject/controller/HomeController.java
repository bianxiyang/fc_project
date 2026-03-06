package com.example.fcproject.controller;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 首页控制器，处理根路径请求
 */
@Controller
@RequestMapping
public class HomeController {
    
    // 不再处理根路径请求，由ViewController统一管理
}