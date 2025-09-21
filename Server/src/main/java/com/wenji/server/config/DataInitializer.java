package com.wenji.server.config;

import com.wenji.server.model.UserAccount;
import com.wenji.server.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 数据初始化器
 * 在应用启动时初始化测试数据
 */
@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private UserAccountRepository userAccountRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        // 检查是否已存在admin账号
        if (!userAccountRepository.existsByAccount("admin")) {
            // 创建测试用的admin账号
            UserAccount admin = new UserAccount();
            admin.setUsername("管理员");
            admin.setAccount("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123")); // 密码为admin123
            admin.setRole(0); // 主账号角色
            admin.setEmailVerified(1); // 已验证邮箱
            admin.setStatus(1); // 启用状态
            
            userAccountRepository.save(admin);
            System.out.println("测试用户admin已初始化，密码: admin123");
        }
    }
}