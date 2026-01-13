package com.example.fcproject.dto;

/**
 * 登录响应DTO类，用于返回登录结果
 */
public class LoginResponse {
    private boolean success;
    private String message;
    private String username;
    private String role;

    // Constructors
    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public LoginResponse(boolean success, String message, String username, String role) {
        this.success = success;
        this.message = message;
        this.username = username;
        this.role = role;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}