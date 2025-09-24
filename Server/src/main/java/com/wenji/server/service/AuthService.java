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
    
    @Autowired
    private UserStatsService userStatsService;
    
    @Value("${data.python.url}")
    private String dataPythonUrl;
    
    // 登录功能
    @Transactional
    public Map<String, Object> login(String account, String encryptedPassword, Integer role, String ip) {
        UserAccount user = null;
        try {
            // 1. 查找用户（只支持邮箱登录）
            Optional<UserAccount> userByEmail = userAccountRepository.findByEmail(account);
            if (!userByEmail.isPresent()) {
                throw new RuntimeException("邮箱不存在");
            }
            user = userByEmail.get();
            
            // 2. 检查用户状态
            if (user.getStatus() != 1) {
                throw new RuntimeException("账号已被禁用");
            }
            
            // 3. 检查用户角色
            if (user.getRole() != role) {
                throw new RuntimeException("角色不匹配");
            }
            
            // 4. 获取系统私钥并解密密码
            String password;
            try {
                // 获取系统加密配置
                SystemEncryptionConfig encryptionConfig = systemEncryptionConfigRepository.findByStatus(1)
                        .orElseThrow(() -> new RuntimeException("加密配置不存在"));
                
                // 检查是否为明文密码（简单的启发式检查）
                if (encryptedPassword.matches("^[a-zA-Z0-9]+$") && encryptedPassword.length() < 20) {
                    System.out.println("检测到明文密码，跳过RSA解密");
                    password = encryptedPassword;
                } else {
                    // 尝试RSA解密
                    password = rsaUtil.decrypt(encryptedPassword, rsaUtil.getPrivateKey(encryptionConfig.getRsaPrivateKey()));
                    System.out.println("RSA解密成功");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Base64格式错误: " + e.getMessage());
                System.out.println("使用明文密码进行验证");
                password = encryptedPassword;
            } catch (Exception e) {
                // 如果RSA解密失败，直接使用明文密码（开发环境）
                password = encryptedPassword;
                System.out.println("RSA解密失败，使用明文密码: " + password + "，错误: " + e.getMessage());
            }
            
            // 5. 验证密码
            System.out.println("解密后的密码: " + password);
            System.out.println("数据库中的密码哈希: " + user.getPassword());
            System.out.println("密码验证结果: " + passwordEncoder.matches(password, user.getPassword()));
            
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new RuntimeException("密码错误");
            }
            
            // 6. 解析IP地址（这里简化处理，实际应该调用Data_python服务）
            String address = "未知地址";
            
            // 7. 更新最后登录信息
            userAccountRepository.updateLastLoginInfo(user.getId(), LocalDateTime.now(), ip, address);
            
            // 7.1. 记录用户活跃度
            userStatsService.recordUserActivity(user.getEmail());
            
            // 8. 生成JWT令牌
            System.out.println("生成JWT令牌的参数:");
            System.out.println("用户ID: " + user.getId());
            System.out.println("用户账号: " + user.getAccount());
            System.out.println("用户角色: " + user.getRole());
            String token = jwtUtil.generateToken(user.getId(), user.getAccount(), user.getRole());
            String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getAccount());
            System.out.println("生成的JWT令牌: " + token);
            System.out.println("生成的刷新令牌: " + refreshToken);

            // 9. 记录操作日志到Python服务
            try {
                logClient.recordOperationLog(
                    user.getId(),
                    "用户登录",
                    ip,
                    address,
                    "登录成功，账号: " + account,
                    1 // 加密传输
                );
            } catch (Exception logException) {
                System.out.println("记录操作日志失败: " + logException.getMessage());
                // 不影响登录流程，继续执行
            }
            
            // 10. 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("refreshToken", refreshToken);
            result.put("user", user);
            return result;
        
        } catch (Exception e) {
            // 记录登录失败日志到Python服务
            try {
                logClient.recordOperationLog(
                    user != null ? user.getId() : null,
                    "用户登录",
                    ip,
                    null,
                    "登录失败，原因: " + e.getMessage(),
                    1 // 加密传输
                );
            } catch (Exception logException) {
                System.out.println("记录登录失败日志失败: " + logException.getMessage());
                // 不影响异常抛出
            }
            
            throw new RuntimeException("登录失败: " + e.getMessage());
        }
    }
    
    // 注册功能
    @Transactional
    public void register(String username, String account, String email, String encryptedPassword, String encryptedConfirmPassword, Boolean agreeTerms, String code, String ip) {
        // 1. 验证验证码
        VerificationCode verificationCode = verificationCodeRepository.findByEmailAndCodeAndTypeAndUsedAndExpiredTimeAfter(
                email, code, 1, 0, LocalDateTime.now())
                .orElseThrow(() -> new RuntimeException("验证码错误或已过期"));
        
        // 2. 验证服务条款同意状态
        if (agreeTerms == null || !agreeTerms) {
            throw new RuntimeException("必须同意服务条款才能注册");
        }
        
        // 3. 检查账号和邮箱是否已存在
        if (userAccountRepository.existsByAccount(account)) {
            throw new RuntimeException("账号已存在");
        }
        
        if (userAccountRepository.existsByEmail(email)) {
            throw new RuntimeException("邮箱已被注册");
        }
        
        try {
            // 4. 获取系统私钥并解密密码
            SystemEncryptionConfig encryptionConfig = systemEncryptionConfigRepository.findByStatus(1)
                    .orElseThrow(() -> new RuntimeException("加密配置不存在"));
            
            String password = null;
            String confirmPassword = null;
            
            try {
                // 检查是否为明文密码（简单的启发式检查）
                if (encryptedPassword.matches("^[a-zA-Z0-9]+$") && encryptedPassword.length() < 20) {
                    System.out.println("检测到明文密码，跳过RSA解密");
                    password = encryptedPassword;
                    confirmPassword = encryptedConfirmPassword;
                } else {
                    // 尝试RSA解密
                    password = rsaUtil.decrypt(encryptedPassword, rsaUtil.getPrivateKey(encryptionConfig.getRsaPrivateKey()));
                    confirmPassword = rsaUtil.decrypt(encryptedConfirmPassword, rsaUtil.getPrivateKey(encryptionConfig.getRsaPrivateKey()));
                    System.out.println("RSA解密成功");
                }
            } catch (Exception e) {
                System.out.println("RSA解密失败，尝试Base64解码: " + e.getMessage());
                // 如果RSA解密失败，尝试处理Base64编码的密码（前端备用方案）
                try {
                    // 检查是否为明文密码
                    if (encryptedPassword.matches("^[a-zA-Z0-9]+$") && encryptedPassword.length() < 20) {
                        System.out.println("检测到明文密码，跳过Base64解码");
                        password = encryptedPassword;
                        confirmPassword = encryptedConfirmPassword;
                    } else {
                        // 智能Base64解码 - 检查是否包含URL安全字符
                        password = decodeBase64String(encryptedPassword);
                        confirmPassword = decodeBase64String(encryptedConfirmPassword);
                    }
                    System.out.println("Base64解码成功，密码: " + password);
                } catch (Exception ex) {
                    System.out.println("Base64解码失败，使用原始密码: " + ex.getMessage());
                    // 如果所有Base64解码都失败，将原始密码用于验证（开发测试用）
                    password = encryptedPassword;
                    confirmPassword = encryptedConfirmPassword;
                }
            }
            
            // 5. 验证密码一致性
            if (!password.equals(confirmPassword)) {
                throw new RuntimeException("两次输入的密码不一致");
            }
            
            // 6. 验证密码强度（至少8位，包含数字、字母和特殊字符）
            if (!isPasswordStrong(password)) {
                throw new RuntimeException("密码强度不足，请使用至少8位，包含数字、字母和特殊字符的密码");
            }
            
            // 7. 创建用户
            UserAccount user = new UserAccount();
            user.setUsername(username);
            user.setAccount(account);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(0); // 默认为主账号
            user.setEmailVerified(1); // 已验证邮箱
            user.setStatus(1); // 启用状态
            
            userAccountRepository.save(user);
            
            // 8. 标记验证码为已使用
            verificationCodeRepository.markCodeAsUsed(verificationCode.getId());
            
            // 9. 记录操作日志到Python服务
            logClient.recordOperationLog(
                user.getId(),
                "用户注册",
                ip,
                null,
                "注册成功，账号: " + account,
                1 // 加密传输
            );
            
            // 10. 发送注册成功邮件
            emailUtil.sendRegisterSuccessEmail(email, username);
        } catch (Exception e) {
            throw new RuntimeException("注册失败: " + e.getMessage());
        }
    }
    
    // 发送验证码
    @Transactional
    public void sendVerificationCode(String email, Integer type, String ip) {
        // 1. 根据验证码类型检查邮箱状态
        if (type == 1) { // 注册验证码
            // 检查邮箱是否已经注册
            if (userAccountRepository.existsByEmail(email)) {
                throw new RuntimeException("该邮箱已经注册，请直接登录或使用忘记密码功能");
            }
        } else if (type == 2) { // 密码重置验证码
            // 检查邮箱是否存在
            if (!userAccountRepository.existsByEmail(email)) {
                throw new RuntimeException("该邮箱未注册，请先注册账号");
            }
        }
        
        // 2. 检查IP请求频率（1分钟内不超过5次）
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        int count = verificationCodeRepository.countByRequestIpAndCreatedTimeAfter(ip, oneMinuteAgo);
        if (count >= 5) {
            throw new RuntimeException("请求过于频繁，每分钟最多可以请求5次验证码");
        }
        
        // 3. 检查请求间隔（不能少于12秒）
        Optional<LocalDateTime> lastRequestTime = verificationCodeRepository.findLastRequestTimeByIp(ip);
        if (lastRequestTime.isPresent()) {
            long secondsSinceLastRequest = ChronoUnit.SECONDS.between(lastRequestTime.get(), LocalDateTime.now());
            if (secondsSinceLastRequest < 12) {
                throw new RuntimeException("请求间隔过短，请等待" + (12 - secondsSinceLastRequest) + "秒后再试");
            }
        }
        
        // 4. 作废该邮箱该类型的所有未使用验证码（实现新规则：发第二条验证码前一条作废）
        int invalidatedCount = verificationCodeRepository.markAllUnusedCodesAsUsedByEmailAndType(email, type);
        if (invalidatedCount > 0) {
            System.out.println("已作废 " + invalidatedCount + " 个未使用的验证码，邮箱: " + email + ", 类型: " + type);
        }
        
        // 5. 生成6位数字验证码
        String code = generateVerificationCode();
        
        // 6. 保存验证码
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setEmail(email);
        verificationCode.setCode(code);
        verificationCode.setType(type);
        verificationCode.setExpiredTime(LocalDateTime.now().plusMinutes(15)); // 有效期15分钟
        verificationCode.setUsed(0);
        verificationCode.setRequestIp(ip);
        
        verificationCodeRepository.save(verificationCode);
        
        // 7. Send verification code email - re-enable email sending with correct configuration
        try {
            emailUtil.sendVerificationCode(email, code, type);
            System.out.println("=== Verification Code Sent ===");
            System.out.println("Email: " + email);
            System.out.println("Code: " + code);
            System.out.println("Type: " + (type == 1 ? "Registration" : "Password Reset"));
            System.out.println("Valid for: 15 minutes");
            System.out.println("==============================");
        } catch (Exception e) {
            // Log email sending failure details
            System.err.println("Email sending failed: " + e.getMessage());
            System.err.println("Detailed error info: " + e.toString());
            
            // Get complete exception stack trace
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.PrintWriter pw = new java.io.PrintWriter(sw);
            e.printStackTrace(pw);
            System.err.println("Exception stack: " + sw.toString());
            
            // Record to log service (if available)
            try {
                logClient.recordOperationLog(
                    null,  // Unauthenticated user
                    "Email sending failed",
                    ip,
                    null,
                    "Failed to send verification code to " + email + ": " + e.getMessage(),
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
    public void resetPassword(String email, String code, String encryptedPassword, String newPassword) {
        // 1. 验证验证码
        VerificationCode verificationCode = verificationCodeRepository.findByEmailAndCodeAndTypeAndUsedAndExpiredTimeAfter(
                email, code, 2, 0, LocalDateTime.now())
                .orElseThrow(() -> new RuntimeException("验证码错误或已过期"));
        
        // 2. 查找用户
        UserAccount user = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        try {
            String password = null;
            
            // 3. 优先使用加密密码，如果没有则使用明文密码
            if (encryptedPassword != null && !encryptedPassword.trim().isEmpty()) {
                // 获取系统私钥并解密密码
                SystemEncryptionConfig encryptionConfig = systemEncryptionConfigRepository.findByStatus(1)
                        .orElseThrow(() -> new RuntimeException("加密配置不存在"));
                
                password = rsaUtil.decrypt(encryptedPassword, rsaUtil.getPrivateKey(encryptionConfig.getRsaPrivateKey()));
            } else if (newPassword != null && !newPassword.trim().isEmpty()) {
                // 使用明文密码（开发测试用）
                password = newPassword;
            } else {
                throw new RuntimeException("密码参数不能为空");
            }
            
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
    
    /**
     * 智能Base64解码方法 - 自动检测并处理URL安全字符
     */
    private String decodeBase64String(String encodedString) throws Exception {
        if (encodedString == null || encodedString.isEmpty()) {
            return encodedString;
        }
        
        try {
            // 检查是否包含URL安全字符 (- 或 _)
            if (encodedString.contains("-") || encodedString.contains("_")) {
                System.out.println("检测到URL安全Base64字符，使用URL解码器");
                return new String(java.util.Base64.getUrlDecoder().decode(encodedString), java.nio.charset.StandardCharsets.UTF_8);
            } else {
                System.out.println("使用标准Base64解码器");
                return new String(java.util.Base64.getDecoder().decode(encodedString), java.nio.charset.StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            System.out.println("Base64解码失败: " + e.getMessage());
            throw e;
        }
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
    
    // 刷新JWT令牌
    public Map<String, Object> refreshToken(String refreshToken) {
        try {
            // 验证刷新令牌格式
            if (refreshToken == null || refreshToken.trim().isEmpty()) {
                throw new RuntimeException("刷新令牌不能为空");
            }
            
            // 获取用户名和用户ID
            String username = jwtUtil.getUsernameFromToken(refreshToken);
            Long userId = jwtUtil.getUserIdFromRefreshToken(refreshToken);
            
            // 验证刷新令牌
            if (!jwtUtil.validateRefreshToken(refreshToken, username)) {
                throw new RuntimeException("刷新令牌无效或已过期");
            }
            
            // 获取用户信息
            UserAccount user = userAccountRepository.findByAccount(username)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            
            // 生成新的访问令牌和刷新令牌
            String newAccessToken = jwtUtil.generateToken(user.getId(), user.getAccount(), user.getRole());
            String newRefreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getAccount());
            
            System.out.println("刷新令牌成功:");
            System.out.println("用户: " + username);
            System.out.println("新访问令牌: " + newAccessToken);
            System.out.println("新刷新令牌: " + newRefreshToken);
            
            // 返回新令牌
            Map<String, Object> result = new HashMap<>();
            result.put("token", newAccessToken);
            result.put("refreshToken", newRefreshToken);
            result.put("userId", user.getId());
            result.put("username", user.getUsername());
            result.put("role", user.getRole());
            
            return result;
        } catch (Exception e) {
            System.err.println("刷新令牌失败: " + e.getMessage());
            throw new RuntimeException("刷新令牌失败: " + e.getMessage());
        }
    }
}