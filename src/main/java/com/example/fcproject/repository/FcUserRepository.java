package com.example.fcproject.repository;

import com.example.fcproject.model.FcUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FcUserRepository extends JpaRepository<FcUser, Integer> {
    
    // 可以根据需要添加自定义查询方法
    // 例如根据名称查询用户
    FcUser findByName(String name);
    
    // 根据用户名查询用户
    FcUser findByUsername(String username);
    
    // 根据id删除用户
    void deleteById(Integer id);
    
    // 按积分降序排序查询所有用户
    List<FcUser> findAllByOrderByPowerfulDesc();
    
    // 按ID升序排序查询所有用户
    List<FcUser> findAllByOrderByIdAsc();
    
    // 按积分降序排序查询非管理员用户
    List<FcUser> findByRoleNotOrderByPowerfulDesc(String role);
    
    // 按ID升序排序查询非管理员用户
    List<FcUser> findByRoleNotOrderByIdAsc(String role);
}