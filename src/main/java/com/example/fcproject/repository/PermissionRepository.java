package com.example.fcproject.repository;

import com.example.fcproject.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    /**
     * 根据权限代码查询权限
     * @param code 权限代码
     * @return 权限对象
     */
    Optional<Permission> findByCode(String code);

    /**
     * 根据父ID查询子权限
     * @param parentId 父权限ID
     * @return 子权限列表
     */
    List<Permission> findByParentIdOrderByOrderAsc(Long parentId);

    /**
     * 查询所有启用的权限
     * @return 启用的权限列表
     */
    List<Permission> findByIsEnabledTrueOrderByOrderAsc();

    /**
     * 查询用户拥有的权限列表
     * @param userId 用户ID
     * @return 权限列表
     */
    @Query("SELECT p FROM Permission p JOIN UserPermission up ON p.id = up.permission.id WHERE up.user.id = :userId AND p.isEnabled = true ORDER BY p.order ASC")
    List<Permission> findPermissionsByUserId(@Param("userId") Integer userId);
}