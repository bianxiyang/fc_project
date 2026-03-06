package com.example.fcproject.service.impl;

import com.example.fcproject.model.FcUser;
import com.example.fcproject.model.Permission;
import com.example.fcproject.model.UserPermission;
import com.example.fcproject.repository.FcUserRepository;
import com.example.fcproject.repository.PermissionRepository;
import com.example.fcproject.repository.UserPermissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserPermissionServiceImplTest {

    @Mock
    private UserPermissionRepository userPermissionRepository;

    @Mock
    private FcUserRepository fcUserRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private UserPermissionServiceImpl userPermissionService;

    private FcUser testUser;
    private Permission testPermission1;
    private Permission testPermission2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // 创建测试用户
        testUser = new FcUser();
        testUser.setId(1);
        testUser.setName("测试用户");
        testUser.setUsername("testuser");
        testUser.setPassword("123456");
        testUser.setRole("USER");

        // 创建测试权限
        testPermission1 = new Permission();
        testPermission1.setId(1L);
        testPermission1.setCode("match_list");
        testPermission1.setName("比赛列表");

        testPermission2 = new Permission();
        testPermission2.setId(2L);
        testPermission2.setCode("match_edit");
        testPermission2.setName("编辑比赛");
    }

    @Test
    public void testGetUserPermissionsByUserId() {
        // 模拟用户权限数据
        List<UserPermission> userPermissions = new ArrayList<>();
        UserPermission up1 = new UserPermission();
        up1.setUser(testUser);
        up1.setPermission(testPermission1);
        userPermissions.add(up1);

        // 模拟Repository方法
        when(userPermissionRepository.findByUserId(1)).thenReturn(userPermissions);

        // 测试方法
        List<UserPermission> result = userPermissionService.getUserPermissionsByUserId(1);

        // 验证结果
        assertEquals(1, result.size());
        verify(userPermissionRepository, times(1)).findByUserId(1);
    }

    @Test
    public void testAssignPermissionsToUser() {
        // 准备测试数据
        List<Long> permissionIds = List.of(1L, 2L);
        List<Permission> permissions = List.of(testPermission1, testPermission2);

        // 模拟Repository方法
        when(fcUserRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(permissionRepository.findAllById(permissionIds)).thenReturn(permissions);
        doNothing().when(userPermissionRepository).deleteByUserId(1);

        // 测试方法
        userPermissionService.assignPermissionsToUser(1, permissionIds, "admin");

        // 验证方法调用
        verify(fcUserRepository, times(1)).findById(1);
        verify(permissionRepository, times(1)).findAllById(permissionIds);
        verify(userPermissionRepository, times(1)).deleteByUserId(1);
        verify(userPermissionRepository, times(1)).saveAll(anyList());
    }

    @Test
    public void testRevokeAllPermissionsFromUser() {
        // 模拟Repository方法
        doNothing().when(userPermissionRepository).deleteByUserId(1);

        // 测试方法
        userPermissionService.revokeAllPermissionsFromUser(1);

        // 验证方法调用
        verify(userPermissionRepository, times(1)).deleteByUserId(1);
    }

    @Test
    public void testRevokePermissionFromUser() {
        // 模拟用户权限数据
        UserPermission userPermission = new UserPermission();
        userPermission.setUser(testUser);
        userPermission.setPermission(testPermission1);

        // 模拟Repository方法
        when(userPermissionRepository.findByUserIdAndPermissionId(1, 1L)).thenReturn(userPermission);
        doNothing().when(userPermissionRepository).delete(userPermission);

        // 测试方法
        userPermissionService.revokePermissionFromUser(1, 1L);

        // 验证方法调用
        verify(userPermissionRepository, times(1)).findByUserIdAndPermissionId(1, 1L);
        verify(userPermissionRepository, times(1)).delete(userPermission);
    }

    @Test
    public void testHasPermission() {
        // 模拟用户权限数据
        UserPermission userPermission = new UserPermission();
        userPermission.setUser(testUser);
        userPermission.setPermission(testPermission1);

        // 模拟Repository方法
        when(userPermissionRepository.findByUserIdAndPermissionId(1, 1L)).thenReturn(userPermission);

        // 测试方法
        boolean result = userPermissionService.hasPermission(1, 1L);

        // 验证结果
        assertTrue(result);
        verify(userPermissionRepository, times(1)).findByUserIdAndPermissionId(1, 1L);
    }

    @Test
    public void testHasPermissionCode() {
        // 模拟权限数据
        when(permissionRepository.findByCode("match_list")).thenReturn(Optional.of(testPermission1));
        when(userPermissionRepository.findByUserIdAndPermissionId(1, 1L)).thenReturn(new UserPermission());

        // 测试方法
        boolean result = userPermissionService.hasPermissionCode(1, "match_list");

        // 验证结果
        assertTrue(result);
        verify(permissionRepository, times(1)).findByCode("match_list");
        verify(userPermissionRepository, times(1)).findByUserIdAndPermissionId(1, 1L);
    }

    @Test
    public void testHasPermissionCode_PermissionNotExist() {
        // 模拟Repository方法
        when(permissionRepository.findByCode("non_existent")).thenReturn(Optional.empty());

        // 测试方法
        boolean result = userPermissionService.hasPermissionCode(1, "non_existent");

        // 验证结果
        assertFalse(result);
        verify(permissionRepository, times(1)).findByCode("non_existent");
        verify(userPermissionRepository, never()).findByUserIdAndPermissionId(anyInt(), anyLong());
    }
}
