package com.example.fcproject.controller;

import com.example.fcproject.model.Permission;
import com.example.fcproject.model.UserPermission;
import com.example.fcproject.service.PermissionService;
import com.example.fcproject.service.UserPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserPermissionService userPermissionService;

    /**
     * 获取所有权限列表
     * @return 权限列表
     */
    @GetMapping
    public ResponseEntity<List<Permission>> getAllPermissions() {
        List<Permission> permissions = permissionService.getAllPermissions();
        return new ResponseEntity<>(permissions, HttpStatus.OK);
    }

    /**
     * 获取所有启用的权限列表
     * @return 启用的权限列表
     */
    @GetMapping("/enabled")
    public ResponseEntity<List<Permission>> getEnabledPermissions() {
        List<Permission> permissions = permissionService.getEnabledPermissions();
        return new ResponseEntity<>(permissions, HttpStatus.OK);
    }

    /**
     * 根据用户ID获取用户拥有的权限列表
     * @param userId 用户ID
     * @return 用户权限列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Permission>> getPermissionsByUserId(@PathVariable Integer userId) {
        List<Permission> permissions = permissionService.getPermissionsByUserId(userId);
        return new ResponseEntity<>(permissions, HttpStatus.OK);
    }

    /**
     * 为用户分配权限
     * @param userId 用户ID
     * @param request 请求体，包含permissionIds字段
     * @return 成功响应
     */
    @PostMapping("/assign/{userId}")
    public ResponseEntity<String> assignPermissions(@PathVariable Integer userId, @RequestBody Map<String, List<Long>> request) {
        try {
            List<Long> permissionIds = request.get("permissionIds");
            if (permissionIds == null || permissionIds.isEmpty()) {
                return new ResponseEntity<>("权限ID列表不能为空", HttpStatus.BAD_REQUEST);
            }

            // 获取当前登录用户作为创建者
            String createdBy = getCurrentUsername();
            userPermissionService.assignPermissionsToUser(userId, permissionIds, createdBy);
            return new ResponseEntity<>("权限分配成功", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("权限分配失败: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 撤销用户的所有权限
     * @param userId 用户ID
     * @return 成功响应
     */
    @DeleteMapping("/revoke-all/{userId}")
    public ResponseEntity<String> revokeAllPermissions(@PathVariable Integer userId) {
        try {
            userPermissionService.revokeAllPermissionsFromUser(userId);
            return new ResponseEntity<>("权限撤销成功", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("权限撤销失败: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取用户权限菜单树
     * @param userId 用户ID
     * @return 菜单树结构
     */
    @GetMapping("/menu-tree/{userId}")
    public ResponseEntity<List<Permission>> getMenuTree(@PathVariable Integer userId) {
        List<Permission> menuTree = permissionService.buildPermissionMenuTree(userId);
        return new ResponseEntity<>(menuTree, HttpStatus.OK);
    }

    /**
     * 获取当前登录用户名
     * @return 当前登录用户名
     */
    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }
}