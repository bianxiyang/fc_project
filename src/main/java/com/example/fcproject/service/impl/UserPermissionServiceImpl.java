package com.example.fcproject.service.impl;

import com.example.fcproject.model.FcUser;
import com.example.fcproject.model.Permission;
import com.example.fcproject.model.UserPermission;
import com.example.fcproject.repository.FcUserRepository;
import com.example.fcproject.repository.PermissionRepository;
import com.example.fcproject.repository.UserPermissionRepository;
import com.example.fcproject.service.UserPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserPermissionServiceImpl implements UserPermissionService {

    @Autowired
    private UserPermissionRepository userPermissionRepository;

    @Autowired
    private FcUserRepository fcUserRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public List<UserPermission> getUserPermissionsByUserId(Integer userId) {
        return userPermissionRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public void assignPermissionsToUser(Integer userId, List<Long> permissionIds, String createdBy) {
        // 验证用户是否存在
        FcUser user = fcUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));

        // 验证权限是否存在
        List<Permission> permissions = permissionRepository.findAllById(permissionIds);
        if (permissions.size() != permissionIds.size()) {
            throw new RuntimeException("部分权限不存在");
        }

        // 先删除用户现有的所有权限
        userPermissionRepository.deleteByUserId(userId);

        // 为用户分配新权限
        List<UserPermission> userPermissions = new ArrayList<>();
        for (Permission permission : permissions) {
            UserPermission userPermission = new UserPermission();
            userPermission.setUser(user);
            userPermission.setPermission(permission);
            userPermission.setCreatedAt(new Date());
            userPermission.setCreatedBy(createdBy);
            userPermissions.add(userPermission);
        }

        userPermissionRepository.saveAll(userPermissions);
    }

    @Override
    @Transactional
    public void revokeAllPermissionsFromUser(Integer userId) {
        userPermissionRepository.deleteByUserId(userId);
    }

    @Override
    @Transactional
    public void revokePermissionFromUser(Integer userId, Long permissionId) {
        UserPermission userPermission = userPermissionRepository.findByUserIdAndPermissionId(userId, permissionId);
        if (userPermission != null) {
            userPermissionRepository.delete(userPermission);
        }
    }

    @Override
    public boolean hasPermission(Integer userId, Long permissionId) {
        UserPermission userPermission = userPermissionRepository.findByUserIdAndPermissionId(userId, permissionId);
        return userPermission != null;
    }

    @Override
    public boolean hasPermissionCode(Integer userId, String permissionCode) {
        // 查询权限是否存在
        Permission permission = permissionRepository.findByCode(permissionCode)
                .orElse(null);
        if (permission == null) {
            return false;
        }

        // 查询用户是否拥有该权限
        UserPermission userPermission = userPermissionRepository.findByUserIdAndPermissionId(userId, permission.getId());
        return userPermission != null;
    }
}