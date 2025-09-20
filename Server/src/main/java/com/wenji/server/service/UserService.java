package com.wenji.server.service;

import com.wenji.server.model.UserAccount;
import com.wenji.server.model.UserOperationLog;
import com.wenji.server.model.SystemEncryptionConfig;
import com.wenji.server.repository.UserAccountRepository;
import com.wenji.server.repository.UserOperationLogRepository;
import com.wenji.server.repository.SystemEncryptionConfigRepository;
import com.wenji.server.util.EmailUtil;
import com.wenji.server.util.RsaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserService {
    
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private UserAccountRepository userAccountRepository;
    
    @Autowired
    private UserOperationLogRepository userOperationLogRepository;
    
    @Autowired
    private SystemEncryptionConfigRepository systemEncryptionConfigRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailUtil emailUtil;
    
    @Autowired
    private RsaUtil rsaUtil;
    
    @Value("${data.python.url}")
    private String dataPythonUrl;
    
    // 获取用户列表（支持分页）
    public Page<UserAccount> getUserList(Integer page, Integer size, Long parentId) {
        Pageable pageable = PageRequest.of(page - 1, size);
        
        if (parentId != null) {
            // 如果有parentId，查询该主账号下的所有子账号
            return userAccountRepository.findByParentId(parentId, pageable);
        } else {
            // 否则查询所有主账号
            return userAccountRepository.findByRole(0, pageable);
        }
    }
    
    // 添加子账号
    @Transactional
    public void addSubAccount(Long parentId, String username, String account, String email, String encryptedPassword, String ip) {
        // 1. 验证主账号是否存在
        UserAccount parentUser = userAccountRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("主账号不存在"));
        
        // 2. 验证主账号角色
        if (parentUser.getRole() != 0) {
            throw new RuntimeException("只有主账号才能添加子账号");
        }
        
        // 3. 检查子账号信息
        if (userAccountRepository.existsByAccount(account)) {
            throw new RuntimeException("账号已存在");
        }
        
        // 4. 检查邮箱和角色组合是否已存在（一个邮箱只能注册一个主账号和一个子账号）
        if (userAccountRepository.existsByEmailAndRole(email, 1)) {
            throw new RuntimeException("该邮箱已被用作子账号");
        }
        
        try {
            // 5. 创建随机初始密码（如果没有提供）
            String password = "";
            if (encryptedPassword == null || encryptedPassword.isEmpty()) {
                password = generateRandomPassword();
            } else {
                // 使用RSA解密密码
                try {
                    // 获取系统私钥
                    SystemEncryptionConfig encryptionConfig = systemEncryptionConfigRepository.findByStatus(1)
                            .orElseThrow(() -> new RuntimeException("加密配置不存在"));
                    password = rsaUtil.decrypt(encryptedPassword, rsaUtil.getPrivateKey(encryptionConfig.getRsaPrivateKey()));
                } catch (Exception e) {
                    log.error("RSA解密密码失败: {}", e.getMessage());
                    // 解密失败时使用随机密码
                    password = generateRandomPassword();
                }
            }
            
            // 6. 创建子账号
            UserAccount subAccount = new UserAccount();
            subAccount.setUsername(username);
            subAccount.setAccount(account);
            subAccount.setEmail(email);
            subAccount.setPassword(passwordEncoder.encode(password));
            subAccount.setRole(1); // 子账号角色
            subAccount.setParentId(parentId); // 关联主账号
            subAccount.setEmailVerified(1); // 已验证邮箱
            subAccount.setStatus(1); // 启用状态
            
            userAccountRepository.save(subAccount);
            
            // 7. 记录操作日志
            UserOperationLog log = new UserOperationLog();
            log.setUserId(parentId);
            log.setOperation("添加子账号");
            log.setOperationIp(ip);
            log.setDetails("添加子账号成功，子账号: " + account);
            log.setEncryptionStatus(1); // 加密传输
            userOperationLogRepository.save(log);
            
            // 8. 发送子账号创建通知邮件
            emailUtil.sendSubAccountCreatedEmail(email, username, account, password);
        } catch (Exception e) {
            throw new RuntimeException("添加子账号失败: " + e.getMessage());
        }
    }
    
    // 解除子账号关联
    @Transactional
    public void unlinkSubAccount(Long parentId, Long subAccountId, String ip) {
        // 1. 验证主账号是否存在
        UserAccount parentUser = userAccountRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("主账号不存在"));
        
        // 2. 验证主账号角色
        if (parentUser.getRole() != 0) {
            throw new RuntimeException("只有主账号才能解除子账号关联");
        }
        
        // 3. 验证子账号是否存在且属于该主账号
        UserAccount subAccount = userAccountRepository.findById(subAccountId)
                .orElseThrow(() -> new RuntimeException("子账号不存在"));
        
        if (!subAccount.getParentId().equals(parentId)) {
            throw new RuntimeException("无权操作该子账号");
        }
        
        // 4. 解除关联
        userAccountRepository.updateUserParentId(subAccountId, null);
        
        // 5. 记录操作日志
        UserOperationLog log = new UserOperationLog();
        log.setUserId(parentId);
        log.setOperation("解除子账号关联");
        log.setOperationIp(ip);
        log.setDetails("解除子账号关联成功，子账号ID: " + subAccountId);
        log.setEncryptionStatus(1); // 加密传输
        userOperationLogRepository.save(log);
        
        // 6. 发送解除关联通知邮件
        emailUtil.sendSubAccountUnlinkedEmail(subAccount.getEmail(), subAccount.getUsername());
    }
    
    // 根据ID获取用户信息
    public UserAccount getUserById(Long userId) {
        return userAccountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }
    
    // 启用/禁用用户
    @Transactional
    public void updateUserStatus(Long userId, Integer status, Long operatorId) {
        // 1. 验证操作人是否有权限
        UserAccount operator = userAccountRepository.findById(operatorId)
                .orElseThrow(() -> new RuntimeException("操作人不存在"));
        
        // 2. 验证被操作人是否存在
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 3. 检查权限：
        // - 主账号可以管理自己及子账号
        // - 子账号只能被主账号管理
        if (operator.getRole() == 0) {
            // 主账号：可以管理任何用户，但不能禁用自己
            if (userId.equals(operatorId)) {
                throw new RuntimeException("无法禁用自己的账号");
            }
        } else {
            // 子账号：无权限管理用户
            throw new RuntimeException("无权限执行此操作");
        }
        
        // 4. 更新用户状态
        userAccountRepository.updateUserStatus(userId, status);
        
        // 5. 记录操作日志
        UserOperationLog log = new UserOperationLog();
        log.setUserId(operatorId);
        log.setOperation(status == 1 ? "启用用户" : "禁用用户");
        log.setDetails((status == 1 ? "启用" : "禁用") + "用户成功，用户ID: " + userId);
        log.setEncryptionStatus(1); // 加密传输
        userOperationLogRepository.save(log);
    }
    
    // 生成随机密码（包含数字、字母和特殊字符）
    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        StringBuilder password = new StringBuilder();
        Random random = new Random();
        
        // 确保密码长度为12位
        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return password.toString();
    }
}