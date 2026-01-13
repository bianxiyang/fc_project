package com.example.fcproject.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

/**
 * 全局错误处理器，提供友好的错误页面
 */
@Controller
public class GlobalErrorController implements ErrorController {

    /**
     * 处理所有错误请求
     */
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        // 获取错误状态码
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            
            // 根据不同的错误状态码显示不同的错误信息
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("errorCode", 404);
                model.addAttribute("errorMessage", "页面不存在或已被移除");
                model.addAttribute("errorDescription", "您访问的页面可能已被移动或删除，或者URL输入有误");
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute("errorCode", 500);
                model.addAttribute("errorMessage", "服务器内部错误");
                model.addAttribute("errorDescription", "服务器在处理您的请求时发生了错误，请稍后再试");
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                model.addAttribute("errorCode", 403);
                model.addAttribute("errorMessage", "访问被禁止");
                model.addAttribute("errorDescription", "您没有权限访问该资源");
            } else if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
                model.addAttribute("errorCode", 401);
                model.addAttribute("errorMessage", "未授权访问");
                model.addAttribute("errorDescription", "您需要先登录才能访问该资源");
            } else {
                model.addAttribute("errorCode", statusCode);
                model.addAttribute("errorMessage", "发生错误");
                model.addAttribute("errorDescription", "处理您的请求时发生了错误");
            }
        }
        
        // 获取异常信息（如果有）
        Exception exception = (Exception) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        if (exception != null) {
            model.addAttribute("exceptionMessage", exception.getMessage());
        }
        
        return "error"; // 返回错误页面模板
    }
    
    /**
     * 获取错误路径
     */
    public String getErrorPath() {
        return "/error"; // 返回错误路径
    }
}