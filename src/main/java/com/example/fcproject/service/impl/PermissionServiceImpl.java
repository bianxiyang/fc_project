package com.example.fcproject.service.impl;

import com.example.fcproject.model.Permission;
import com.example.fcproject.repository.PermissionRepository;
import com.example.fcproject.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    @Override
    public List<Permission> getEnabledPermissions() {
        return permissionRepository.findByIsEnabledTrueOrderByOrderAsc();
    }

    @Override
    public Optional<Permission> getPermissionById(Long id) {
        return permissionRepository.findById(id);
    }

    @Override
    public Optional<Permission> getPermissionByCode(String code) {
        return permissionRepository.findByCode(code);
    }

    @Override
    public List<Permission> getChildPermissions(Long parentId) {
        return permissionRepository.findByParentIdOrderByOrderAsc(parentId);
    }

    @Override
    public Permission savePermission(Permission permission) {
        return permissionRepository.save(permission);
    }

    @Override
    public void deletePermission(Long id) {
        permissionRepository.deleteById(id);
    }

    @Override
    public List<Permission> getPermissionsByUserId(Integer userId) {
        return permissionRepository.findPermissionsByUserId(userId);
    }

    @Override
    public List<Permission> buildPermissionMenuTree(Integer userId) {
        // 获取用户所有权限
        List<Permission> userPermissions = getPermissionsByUserId(userId);
        if (userPermissions.isEmpty()) {
            return new ArrayList<>();
        }

        // 提取所有权限ID，用于快速查找
        List<Long> permissionIds = userPermissions.stream()
                .map(Permission::getId)
                .collect(Collectors.toList());

        // 构建菜单树
        List<Permission> menuTree = new ArrayList<>();
        
        // 首先处理顶级菜单（parentId为null或0）
        for (Permission permission : userPermissions) {
            if (permission.getParentId() == null || permission.getParentId() == 0) {
                // 为顶级菜单添加子菜单
                buildChildMenu(permission, userPermissions, permissionIds);
                menuTree.add(permission);
            }
        }

        return menuTree;
    }

    /**
     * 递归构建子菜单
     * @param parentPermission 父菜单
     * @param allPermissions 所有权限
     * @param permissionIds 权限ID列表
     */
    private void buildChildMenu(Permission parentPermission, List<Permission> allPermissions, List<Long> permissionIds) {
        List<Permission> children = new ArrayList<>();
        
        for (Permission permission : allPermissions) {
            if (permission.getParentId() != null && permission.getParentId().equals(parentPermission.getId())) {
                // 递归处理子菜单的子菜单
                buildChildMenu(permission, allPermissions, permissionIds);
                children.add(permission);
            }
        }
        
        // 这里我们不需要实际存储子菜单引用，因为前端会根据parentId构建菜单树
        // 我们只需要确保返回的权限列表包含所有相关权限即可
    }
}