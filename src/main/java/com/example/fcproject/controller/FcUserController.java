package com.example.fcproject.controller;

import com.example.fcproject.model.FcUser;
import com.example.fcproject.service.FcUserService;
import com.example.fcproject.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/fc-users")
public class FcUserController {

    @Autowired
    private FcUserService fcUserService;

    // 获取所有用户
    @GetMapping
    public ResponseEntity<ApiResponse> getAllFcUsers() {
        List<FcUser> users = fcUserService.getAllUsers();
        return ResponseEntity.ok(
                ApiResponse.success("获取所有用户成功", users)
        );
    }

    // 根据ID获取用户
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getFcUserById(@PathVariable Integer id) {
        Optional<FcUser> userOptional = fcUserService.getUserById(id);
        if (userOptional.isPresent()) {
            return ResponseEntity.ok(
                    ApiResponse.success("获取用户成功", userOptional.get())
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.error("用户不存在")
            );
        }
    }

    // 创建新用户
    @PostMapping
    public ResponseEntity<ApiResponse> createFcUser(@RequestBody FcUser fcUser) {
        FcUser savedUser = fcUserService.createUser(fcUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("创建用户成功", savedUser)
        );
    }

    // 更新用户
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateFcUser(@PathVariable Integer id, @RequestBody FcUser fcUser) {
        FcUser updatedUser = fcUserService.updateUser(id, fcUser);
        if (updatedUser != null) {
            return ResponseEntity.ok(
                    ApiResponse.success("更新用户成功", updatedUser)
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.error("用户不存在")
            );
        }
    }

    // 删除用户
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteFcUser(@PathVariable Integer id) {
        boolean deleted = fcUserService.deleteUser(id);
        if (deleted) {
            return ResponseEntity.ok(
                    ApiResponse.success("删除用户成功")
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.error("用户不存在")
            );
        }
    }

    // 根据名称获取用户
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse> getFcUserByName(@PathVariable String name) {
        FcUser user = fcUserService.getUserByName(name);
        if (user != null) {
            return ResponseEntity.ok(
                    ApiResponse.success("获取用户成功", user)
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.error("用户不存在")
            );
        }
    }
    
    // 获取按积分降序排序的用户列表
    @GetMapping("/rank")
    public ResponseEntity<ApiResponse> getUsersByRank() {
        List<FcUser> users = fcUserService.getUsersByPowerfulDesc();
        return ResponseEntity.ok(
                ApiResponse.success("获取用户排名成功", users)
        );
    }
    
    // 获取按ID升序排序的用户列表
    @GetMapping("/sorted-by-id")
    public ResponseEntity<ApiResponse> getUsersSortedById() {
        List<FcUser> users = fcUserService.getUsersByIdAsc();
        return ResponseEntity.ok(
                ApiResponse.success("获取按ID排序的用户列表成功", users)
        );
    }
}