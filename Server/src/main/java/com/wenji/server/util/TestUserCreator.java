package com.wenji.server.util;

import com.wenji.server.model.UserAccount;
import com.wenji.server.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TestUserCreator implements CommandLineRunner {

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 检查是否已存在测试用户
        if (!userAccountRepository.existsByEmail("test@example.com")) {
            // 创建测试用户
            UserAccount testUser = new UserAccount();
            testUser.setUsername("测试用户");
            testUser.setAccount("testuser");
            testUser.setEmail("test@example.com");
            testUser.setPassword(passwordEncoder.encode("12345678"));
            testUser.setRole(0); // 主账号
            testUser.setEmailVerified(1);
            testUser.setStatus(1);
            
            userAccountRepository.save(testUser);
            System.out.println("测试用户创建成功: test@example.com / 12345678");
        }

        // 检查是否已存在管理员用户
        if (!userAccountRepository.existsByEmail("admin@example.com")) {
            // 创建管理员用户
            UserAccount adminUser = new UserAccount();
            adminUser.setUsername("管理员");
            adminUser.setAccount("admin");
            adminUser.setEmail("admin@example.com");
            adminUser.setPassword(passwordEncoder.encode("12345678"));
            adminUser.setRole(0); // 主账号
            adminUser.setEmailVerified(1);
            adminUser.setStatus(1);
            
            userAccountRepository.save(adminUser);
            System.out.println("管理员用户创建成功: admin@example.com / 12345678");
        }
    }
}