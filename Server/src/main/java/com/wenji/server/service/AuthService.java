package com.wenji.server.service;

import com.wenji.server.model.UserAccount;
import com.wenji.server.model.VerificationCode;
import com.wenji.server.model.SystemEncryptionConfig;
import com.wenji.server.repository.UserAccountRepository;
import com.wenji.server.repository.VerificationCodeRepository;
import com.wenji.server.repository.SystemEncryptionConfigRepository;
import com.wenji.server.util.JwtUtil;
import com.wenji.server.util.RsaUtil;
import com.wenji.server.util.EmailUtil;
import com.wenji.server.util.LogClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@Service
public class AuthService {
    
    @Autowired
    private UserAccountRepository userAccountRepository;
    
    @Autowired
    private VerificationCodeRepository verificationCodeRepository;
    
    @Autowired
    private SystemEncryptionConfigRepository systemEncryptionConfigRepository;
    
    @Autowired
    private LogClient logClient;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private RsaUtil rsaUtil;
    
    @Autowired
    private EmailUtil emailUtil;
    
    @Value("${data.python.url}")
    private String dataPythonUrl;
    
    // 登录功能
    @Transactional
    public Map<String, Object> login(String account, String encryptedPassword, Integer role, String ip) {
        // 1. 查找用户
        UserAccount user = userAccountRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("账号不存在"));
        
        // 2. 检查用户状态
        if (user.getStatus() != 1) {
            throw new RuntimeException("账号已被禁用");
        }
        
        // 3. 检查用户角色
        if (user.getRole() != role) {
            throw new RuntimeException("角色不匹配");
        }
        
        try {
            // 4. 获取系统私钥并解密密码
            String password = null;
            try {
                SystemEncryptionConfig encryptionConfig = systemEncryptionConfigRepository.findByStatus(1)
                        .orElseThrow(() -> new RuntimeException("加密配置不存在"));
                
                // 尝试RSA解密
                password = rsaUtil.decrypt(encryptedPassword, rsaUtil.getPrivateKey(encryptionConfig.getRsaPrivateKey()));
            } catch (Exception e) {
                // 如果RSA解密失败，尝试处理Base64编码的密码（前端临时方案）
                try {
                    password = new String(java.util.Base64.getDecoder().decode(encryptedPassword), java.nio.charset.StandardCharsets.UTF_8);
                } catch (Exception ex) {
                    // 如果Base64解码也失败，将原始密码用于验证（开发测试用）
                    password = encryptedPassword;
                }
            }
            
            // 5. 验证密码
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new RuntimeException("密码错误");
            }
            
            // 6. 解析IP地址（这里简化处理，实际应该调用Data_python服务）
            String address = "未知地址";
            
            // 7. 更新最后登录信息
            userAccountRepository.updateLastLoginInfo(user.getId(), LocalDateTime.now(), ip, address);
            
            // 8. 生成JWT令牌
            String token = jwtUtil.generateToken(user.getId(), user.getAccount(), user.getRole());
            
            // 9. 记录操作日志到Python服务
            logClient.recordOperationLog(
                user.getId(),
                "用户登录",
                ip,
                address,
                "登录成功，账号: " + account,
                1 // 加密传输
            );
            
            // 10. 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("user", user);
            return result;
        } catch (Exception e) {
            // 记录登录失败日志到Python服务
            logClient.recordOperationLog(
                user.getId(),
                "用户登录",
                ip,
                null,
                "登录失败，原因: " + e.getMessage(),
                1 // 加密传输
            );
            
            throw new RuntimeException("登录失败: " + e.getMessage());
        }
    }
    
    // 注册功能
    @Transactional
    public void register(String username, String account, String email, String encryptedPassword, String code, String ip) {
        // 1. 验证验证码
        VerificationCode verificationCode = verificationCodeRepository.findByEmailAndCodeAndTypeAndUsedAndExpiredTimeAfter(
                email, code, 1, 0, LocalDateTime.now())
                .orElseThrow(() -> new RuntimeException("验证码错误或已过期"));
        
        // 2. 检查账号和邮箱是否已存在
        if (userAccountRepository.existsByAccount(account)) {
            throw new RuntimeException("账号已存在");
        }
        
        if (userAccountRepository.existsByEmail(email)) {
            throw new RuntimeException("邮箱已被注册");
        }
        
        try {
            // 3. 获取系统私钥并解密密码
            SystemEncryptionConfig encryptionConfig = systemEncryptionConfigRepository.findByStatus(1)
                    .orElseThrow(() -> new RuntimeException("加密配置不存在"));
            
            String password = rsaUtil.decrypt(encryptedPassword, rsaUtil.getPrivateKey(encryptionConfig.getRsaPrivateKey()));
            
            // 4. 验证密码强度（至少8位，包含数字、字母和特殊字符）
            if (!isPasswordStrong(password)) {
                throw new RuntimeException("密码强度不足，请使用至少8位，包含数字、字母和特殊字符的密码");
            }
            
            // 5. 创建用户
            UserAccount user = new UserAccount();
            user.setUsername(username);
            user.setAccount(account);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(0); // 默认为主账号
            user.setEmailVerified(1); // 已验证邮箱
            user.setStatus(1); // 启用状态
            
            userAccountRepository.save(user);
            
            // 6. 标记验证码为已使用
            verificationCodeRepository.markCodeAsUsed(verificationCode.getId());
            
            // 7. 记录操作日志到Python服务
            logClient.recordOperationLog(
                user.getId(),
                "用户注册",
                ip,
                null,
                "注册成功，账号: " + account,
                1 // 加密传输
            );
            
            // 8. 发送注册成功邮件
            emailUtil.sendRegisterSuccessEmail(email, username);
        } catch (Exception e) {
            throw new RuntimeException("注册失败: " + e.getMessage());
        }
    }
    
    // 发送验证码
    public void sendVerificationCode(String email, Integer type, String ip) {
        // 1. 检查IP请求频率（1分钟内不超过5次）
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        int count = verificationCodeRepository.countByRequestIpAndCreatedTimeAfter(ip, oneMinuteAgo);
        if (count >= 5) {
            throw new RuntimeException("请求过于频繁，每分钟最多可以请求5次验证码");
        }
        
        // 2. 检查请求间隔（不能少于12秒）
        Optional<LocalDateTime> lastRequestTime = verificationCodeRepository.findLastRequestTimeByIp(ip);
        if (lastRequestTime.isPresent()) {
            long secondsSinceLastRequest = ChronoUnit.SECONDS.between(lastRequestTime.get(), LocalDateTime.now());
            if (secondsSinceLastRequest < 12) {
                throw new RuntimeException("请求间隔过短，请等待" + (12 - secondsSinceLastRequest) + "秒后再试");
            }
        }
        
        // 3. 生成6位数字验证码
        String code = generateVerificationCode();
        
        // 4. 保存验证码
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setEmail(email);
        verificationCode.setCode(code);
        verificationCode.setType(type);
        verificationCode.setExpiredTime(LocalDateTime.now().plusMinutes(15)); // 有效期15分钟
        verificationCode.setUsed(0);
        verificationCode.setRequestIp(ip);
        
        verificationCodeRepository.save(verificationCode);
        
        // 5. 发送验证码邮件 - 优化异常处理，确保即使邮件发送失败也不影响核心功能
        try {
            emailUtil.sendVerificationCode(email, code, type);
        } catch (Exception e) {
            // 记录邮件发送失败的详细日志
            System.err.println("邮件发送失败: " + e.getMessage());
            System.err.println("详细错误信息: " + e.toString());
            
            // 获取完整的异常堆栈信息
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.PrintWriter pw = new java.io.PrintWriter(sw);
            e.printStackTrace(pw);
            System.err.println("异常堆栈: " + sw.toString());
            
            // 记录到日志服务（如果可用）
            try {
                logClient.recordOperationLog(
                    null,  // 未登录用户
                    "邮件发送失败",
                    ip,
                    null,
                    "向" + email + "发送验证码失败: " + e.getMessage(),
                    1 // 加密传输
                );
            } catch (Exception logEx) {
                System.err.println("记录日志失败: " + logEx.getMessage());
            }
            
            // 注意：不抛出异常，确保验证码生成和保存成功
        }
    }
    
    // 重置密码
    @Transactional
    public void resetPassword(String email, String code, String encryptedPassword) {
        // 1. 验证验证码
        VerificationCode verificationCode = verificationCodeRepository.findByEmailAndCodeAndTypeAndUsedAndExpiredTimeAfter(
                email, code, 2, 0, LocalDateTime.now())
                .orElseThrow(() -> new RuntimeException("验证码错误或已过期"));
        
        // 2. 查找用户
        UserAccount user = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        try {
            // 3. 获取系统私钥并解密密码
            SystemEncryptionConfig encryptionConfig = systemEncryptionConfigRepository.findByStatus(1)
                    .orElseThrow(() -> new RuntimeException("加密配置不存在"));
            
            String password = rsaUtil.decrypt(encryptedPassword, rsaUtil.getPrivateKey(encryptionConfig.getRsaPrivateKey()));
            
            // 4. 验证密码强度
            if (!isPasswordStrong(password)) {
                throw new RuntimeException("密码强度不足");
            }
            
            // 5. 更新密码
            userAccountRepository.updateUserPassword(user.getId(), passwordEncoder.encode(password));
            
            // 6. 标记验证码为已使用
            verificationCodeRepository.markCodeAsUsed(verificationCode.getId());
            
            // 7. 记录操作日志到Python服务
            logClient.recordOperationLog(
                user.getId(),
                "密码重置",
                null,
                null,
                "密码重置成功",
                1 // 加密传输
            );
            
            // 8. 发送密码重置成功邮件
            emailUtil.sendPasswordResetSuccessEmail(email, user.getUsername());
        } catch (Exception e) {
            throw new RuntimeException("密码重置失败: " + e.getMessage());
        }
    }
    
    // 修改密码（已登录状态）
    @Transactional
    public void changePassword(Long userId, String encryptedOldPassword, String encryptedNewPassword) {
        // 1. 查找用户
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        try {
            // 2. 获取系统私钥并解密密码
            SystemEncryptionConfig encryptionConfig = systemEncryptionConfigRepository.findByStatus(1)
                    .orElseThrow(() -> new RuntimeException("加密配置不存在"));
            
            String oldPassword = rsaUtil.decrypt(encryptedOldPassword, rsaUtil.getPrivateKey(encryptionConfig.getRsaPrivateKey()));
            String newPassword = rsaUtil.decrypt(encryptedNewPassword, rsaUtil.getPrivateKey(encryptionConfig.getRsaPrivateKey()));
            
            // 3. 验证旧密码
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                throw new RuntimeException("旧密码错误");
            }
            
            // 4. 验证新密码强度
            if (!isPasswordStrong(newPassword)) {
                throw new RuntimeException("新密码强度不足");
            }
            
            // 5. 更新密码
            userAccountRepository.updateUserPassword(user.getId(), passwordEncoder.encode(newPassword));
            
            // 6. 记录操作日志到Python服务
            logClient.recordOperationLog(
                user.getId(),
                "修改密码",
                null,
                null,
                "密码修改成功",
                1 // 加密传输
            );
        } catch (Exception e) {
            throw new RuntimeException("修改密码失败: " + e.getMessage());
        }
    }
    
    // 生成随机验证码
    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 生成6位数字
        return String.valueOf(code);
    }
    
    // 验证密码强度
    private boolean isPasswordStrong(String password) {
        if (password.length() < 8) {
            return false;
        }
        
        // 包含数字、字母和特殊字符
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasSpecial = password.matches(".*[^a-zA-Z0-9].*");
        
        return hasDigit && hasLetter && hasSpecial;
    }
}