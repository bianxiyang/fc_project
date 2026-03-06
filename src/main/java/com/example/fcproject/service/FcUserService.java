package com.example.fcproject.service;

import com.example.fcproject.model.FcUser;
import java.util.List;
import java.util.Optional;

/**
 * FcUser服务接口，定义用户管理的业务方法
 */
public interface FcUserService {
    
    /**
     * 获取所有用户
     * @return 用户列表
     */
    List<FcUser> getAllUsers();
    
    /**
     * 根据ID获取用户
     * @param id 用户ID
     * @return 用户对象（Optional）
     */
    Optional<FcUser> getUserById(Integer id);
    
    /**
     * 根据名称获取用户
     * @param name 用户名称
     * @return 用户对象
     */
    FcUser getUserByName(String name);
    
    /**
     * 创建新用户
     * @param fcUser 用户对象
     * @return 创建后的用户对象
     */
    FcUser createUser(FcUser fcUser);
    
    /**
     * 更新用户信息
     * @param id 用户ID
     * @param fcUser 新的用户信息
     * @return 更新后的用户对象
     */
    FcUser updateUser(Integer id, FcUser fcUser);
    
    /**
     * 删除用户
     * @param id 用户ID
     * @return 是否删除成功
     */
    boolean deleteUser(Integer id);
    
    /**
     * 获取按积分降序排序的用户列表
     * @return 用户列表
     */
    List<FcUser> getUsersByPowerfulDesc();
    
    /**
     * 获取按ID升序排序的用户列表
     * @return 用户列表
     */
    List<FcUser> getUsersByIdAsc();
    
    /**
     * 根据用户名获取用户
     * @param username 用户名
     * @return 用户对象
     */
    FcUser getUserByUsername(String username);
    
    /**
     * 用户登录验证
     * @param username 用户名
     * @param password 密码
     * @return 验证结果
     */
    boolean login(String username, String password);
    
    /**
     * 获取用户的权限列表
     * @param userId 用户ID
     * @return 权限列表
     */
    List<com.example.fcproject.model.Permission> getPermissionsByUserId(Integer userId);
}