package com.example.fcproject.repository;

import com.example.fcproject.model.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {

    /**
     * 根据用户ID查询用户权限关联列表
     * @param userId 用户ID
     * @return 用户权限关联列表
     */
    List<UserPermission> findByUserId(Integer userId);

    /**
     * 根据权限ID查询用户权限关联列表
     * @param permissionId 权限ID
     * @return 用户权限关联列表
     */
    List<UserPermission> findByPermissionId(Long permissionId);

    /**
     * 根据用户ID和权限ID查询用户权限关联
     * @param userId 用户ID
     * @param permissionId 权限ID
     * @return 用户权限关联对象
     */
    UserPermission findByUserIdAndPermissionId(Integer userId, Long permissionId);

    /**
     * 删除用户的所有权限关联
     * @param userId 用户ID
     */
    void deleteByUserId(Integer userId);

    /**
     * 查询用户拥有的权限ID列表
     * @param userId 用户ID
     * @return 权限ID列表
     */
    @Query("SELECT up.permission.id FROM UserPermission up WHERE up.user.id = :userId")
    List<Long> findPermissionIdsByUserId(@Param("userId") Integer userId);
}