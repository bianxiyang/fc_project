package com.example.fcproject.service;

import com.example.fcproject.model.UserPermission;

import java.util.List;

public interface UserPermissionService {

    /**
     * 根据用户ID获取用户权限列表
     * @param userId 用户ID
     * @return 用户权限列表
     */
    List<UserPermission> getUserPermissionsByUserId(Integer userId);

    /**
     * 为用户分配权限
     * @param userId 用户ID
     * @param permissionIds 权限ID列表
     * @param createdBy 创建者
     */
    void assignPermissionsToUser(Integer userId, List<Long> permissionIds, String createdBy);

    /**
     * 撤销用户的所有权限
     * @param userId 用户ID
     */
    void revokeAllPermissionsFromUser(Integer userId);

    /**
     * 撤销用户的特定权限
     * @param userId 用户ID
     * @param permissionId 权限ID
     */
    void revokePermissionFromUser(Integer userId, Long permissionId);

    /**
     * 查询用户是否拥有特定权限
     * @param userId 用户ID
     * @param permissionId 权限ID
     * @return 是否拥有权限
     */
    boolean hasPermission(Integer userId, Long permissionId);

    /**
     * 查询用户是否拥有特定权限代码
     * @param userId 用户ID
     * @param permissionCode 权限代码
     * @return 是否拥有权限
     */
    boolean hasPermissionCode(Integer userId, String permissionCode);
}