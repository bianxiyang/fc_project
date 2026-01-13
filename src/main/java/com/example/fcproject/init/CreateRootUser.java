package com.example.fcproject.init;

import com.example.fcproject.model.FcUser;
import com.example.fcproject.repository.FcUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 用于创建ROOT用户的临时组件
 * 可以在应用启动时检查并创建ROOT用户
 */
@Component
public class CreateRootUser implements CommandLineRunner {

    @Autowired
    private FcUserRepository fcUserRepository;

    @Override
    public void run(String... args) throws Exception {
        // 检查是否已有ROOT用户
        boolean hasRootUser = fcUserRepository.findAll().stream()
                .anyMatch(user -> "ROOT".equals(user.getRole()));

        if (!hasRootUser) {
            // 创建超级管理员用户
            FcUser root = new FcUser();
            root.setId(9);
            root.setName("超级管理员");
            root.setUsername("root");
            root.setPassword("root123");
            root.setRole("ROOT");
            root.setWin((short) 0);
            root.setTie((short) 0);
            root.setLose((short) 0);
            root.setPowerful(1000);
            fcUserRepository.save(root);
            System.out.println("已创建ROOT用户: username=root, password=root123");
        } else {
            System.out.println("ROOT用户已存在");
        }
    }
}