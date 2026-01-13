package com.example.fcproject.service;

import com.example.fcproject.model.Permission;

import java.util.List;
import java.util.Optional;

public interface PermissionService {

    /**
     * 获取所有权限列表
     * @return 权限列表
     */
    List<Permission> getAllPermissions();

    /**
     * 获取所有启用的权限列表
     * @return 启用的权限列表
     */
    List<Permission> getEnabledPermissions();

    /**
     * 根据ID获取权限
     * @param id 权限ID
     * @return 权限对象
     */
    Optional<Permission> getPermissionById(Long id);

    /**
     * 根据代码获取权限
     * @param code 权限代码
     * @return 权限对象
     */
    Optional<Permission> getPermissionByCode(String code);

    /**
     * 根据父ID获取子权限列表
     * @param parentId 父权限ID
     * @return 子权限列表
     */
    List<Permission> getChildPermissions(Long parentId);

    /**
     * 保存权限
     * @param permission 权限对象
     * @return 保存后的权限对象
     */
    Permission savePermission(Permission permission);

    /**
     * 删除权限
     * @param id 权限ID
     */
    void deletePermission(Long id);

    /**
     * 根据用户ID获取用户拥有的权限列表
     * @param userId 用户ID
     * @return 用户权限列表
     */
    List<Permission> getPermissionsByUserId(Integer userId);

    /**
     * 构建用户权限菜单树
     * @param userId 用户ID
     * @return 菜单树结构
     */
    List<Permission> buildPermissionMenuTree(Integer userId);
}