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
            // 否则查询所有用户（包括主账号和子账号）
            return userAccountRepository.findAll(pageable);
        }
    }
    
    // 为主账号获取用户列表（只能看到自己和自己的子账号）
    public Page<UserAccount> getUserListForMainAccount(Integer page, Integer size, Long currentUserId, Long parentId) {
        Pageable pageable = PageRequest.of(page - 1, size);
        
        if (parentId != null) {
            // 如果指定了parentId，且parentId是当前用户，则查询当前用户的子账号
            if (parentId.equals(currentUserId)) {
                return userAccountRepository.findByParentId(parentId, pageable);
            } else {
                // 如果parentId不是当前用户，返回空结果
                return Page.empty(pageable);
            }
        } else {
            // 如果没有指定parentId，查询当前用户自己和自己的子账号
            return userAccountRepository.findByIdOrParentId(currentUserId, currentUserId, pageable);
        }
    }
    
    // 添加子账号
    @Transactional
    public void addSubAccount(Long parentId, String username, String account, String email, String password, Integer role, String ip) {
        // 1. 验证主账号是否存在
        UserAccount parentUser = userAccountRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("主账号不存在"));
        
        // 2. 验证主账号角色
        if (parentUser.getRole() != 0) {
            throw new RuntimeException("只有主账号才能添加子账号");
        }
        
        // 2.1. 确保主账号的parentId设置为自身ID（如果还未设置）
        if (parentUser.getParentId() == null) {
            parentUser.setParentId(parentId);
            userAccountRepository.save(parentUser);
        }
        
        // 3. 验证角色值是否有效（2=管理员，3=编辑，4=查看者）
        if (role == null || (role != 2 && role != 3 && role != 4)) {
            role = 3; // 默认为编辑角色
        }
        
        // 4. 检查子账号信息
        if (userAccountRepository.existsByAccount(account)) {
            throw new RuntimeException("账号已存在");
        }
        
        // 5. 检查邮箱是否已被使用
        if (userAccountRepository.existsByEmail(email)) {
            throw new RuntimeException("该邮箱已被注册");
        }
        
        try {
            // 6. 使用传入的密码，如果没有则生成临时密码
            String actualPassword = (password != null && !password.trim().isEmpty()) ? password : generateTempPassword();
            
            // 7. 创建子账号
            UserAccount subAccount = new UserAccount();
            subAccount.setUsername(username);
            subAccount.setAccount(email); // 使用邮箱作为账号
            subAccount.setEmail(email);
            subAccount.setInviteEmail(email);
            subAccount.setPassword(passwordEncoder.encode(actualPassword));
            subAccount.setTempPassword(actualPassword); // 保存密码用于邮件发送
            subAccount.setRole(role); // 使用传入的角色
            subAccount.setParentId(parentId); // 关联主账号
            subAccount.setEmailVerified(1); // 已验证邮箱
            subAccount.setStatus(1); // 启用状态
            subAccount.setAccountStatus("INVITING"); // 邀请中状态
            subAccount.setInviteCreateTime(LocalDateTime.now()); // 邀请创建时间
            subAccount.setUpdatedTime(LocalDateTime.now());
            
            userAccountRepository.save(subAccount);
            
            // 8. 记录操作日志
            UserOperationLog log = new UserOperationLog();
            log.setUserId(parentId);
            log.setOperation("创建子账号");
            log.setOperationIp(ip);
            log.setDetails("创建子账号成功，邮箱: " + email + "，角色: " + getRoleName(role));
            log.setEncryptionStatus(1); // 加密传输
            userOperationLogRepository.save(log);
            
            // 9. 发送邀请邮件
            sendInviteEmail(email, username, parentUser.getUsername(), actualPassword);
        } catch (Exception e) {
            throw new RuntimeException("创建子账号失败: " + e.getMessage());
        }
    }
    
    // 生成临时密码
    private String generateTempPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        StringBuilder password = new StringBuilder();
        java.util.Random random = new java.util.Random();
        
        // 确保包含大写字母、小写字母、数字和特殊字符
        password.append(chars.charAt(random.nextInt(26))); // 大写字母
        password.append(chars.charAt(26 + random.nextInt(26))); // 小写字母
        password.append(chars.charAt(52 + random.nextInt(10))); // 数字
        password.append(chars.charAt(62 + random.nextInt(5))); // 特殊字符
        
        // 填充剩余4位
        for (int i = 4; i < 8; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        // 打乱顺序
        char[] array = password.toString().toCharArray();
        for (int i = array.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
        
        return new String(array);
    }
    
    // 发送邀请邮件
    private void sendInviteEmail(String email, String username, String inviterName, String tempPassword) {
        try {
            String subject = "【Wenji】邀请您加入智能协作平台";
            String htmlContent = createInviteEmailTemplate(username, inviterName, email, tempPassword);
            
            // 使用EmailUtil发送HTML邮件
            emailUtil.sendHtmlEmail(email, subject, htmlContent);
            System.out.println("邀请邮件已发送到: " + email);
        } catch (Exception e) {
            // 如果邮件发送失败，更新账户状态为邀请失败
            Optional<UserAccount> userOptional = userAccountRepository.findByEmail(email);
            UserAccount user = userOptional.orElse(null);
            if (user != null) {
                user.setAccountStatus("INVITE_FAILED");
                user.setUpdatedTime(LocalDateTime.now());
                userAccountRepository.save(user);
            }
            throw new RuntimeException("邮件发送失败: " + e.getMessage());
        }
    }
    
    // 创建邀请邮件HTML模板
    private String createInviteEmailTemplate(String username, String inviterName, String email, String tempPassword) {
        return String.format("""
            <!DOCTYPE html>
            <html lang="zh-CN">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>🚀 邀请加入 Wenji 智能协作平台</title>
                <style>
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }
                    
                    body {
                        font-family: 'SF Pro Display', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, 'PingFang SC', 'Microsoft YaHei', sans-serif;
                        line-height: 1.6;
                        color: #1a202c;
                        background: linear-gradient(135deg, #1e293b 0%%, #334155 25%%, #475569 50%%, #64748b 75%%, #94a3b8 100%%);
                        min-height: 100vh;
                        padding: 20px;
                        animation: deepBackground 15s ease-in-out infinite alternate;
                    }
                    
                    @keyframes deepBackground {
                        0%% { background: linear-gradient(135deg, #1e293b 0%%, #334155 25%%, #475569 50%%, #64748b 75%%, #94a3b8 100%%); }
                        25%% { background: linear-gradient(135deg, #0f172a 0%%, #1e293b 25%%, #334155 50%%, #475569 75%%, #64748b 100%%); }
                        50%% { background: linear-gradient(135deg, #334155 0%%, #475569 25%%, #64748b 50%%, #94a3b8 75%%, #cbd5e1 100%%); }
                        75%% { background: linear-gradient(135deg, #1e293b 0%%, #0f172a 25%%, #334155 50%%, #475569 75%%, #64748b 100%%); }
                        100%% { background: linear-gradient(135deg, #475569 0%%, #64748b 25%%, #94a3b8 50%%, #cbd5e1 75%%, #e2e8f0 100%%); }
                    }
                    
                    body::before {
                        content: '';
                        position: fixed;
                        top: 0;
                        left: 0;
                        width: 100%%;
                        height: 100%%;
                        background: 
                            radial-gradient(circle at 20%% 30%%, rgba(59, 130, 246, 0.15) 0%%, transparent 50%%),
                            radial-gradient(circle at 80%% 70%%, rgba(139, 92, 246, 0.15) 0%%, transparent 50%%),
                            radial-gradient(circle at 40%% 80%%, rgba(16, 185, 129, 0.1) 0%%, transparent 50%%);
                        pointer-events: none;
                        z-index: 0;
                    }
                    
                    .email-container {
                        max-width: 600px;
                        margin: 0 auto;
                        background: rgba(15, 23, 42, 0.95);
                        backdrop-filter: blur(20px);
                        border-radius: 24px;
                        box-shadow: 
                            0 25px 50px -12px rgba(0, 0, 0, 0.5),
                            0 0 0 1px rgba(255, 255, 255, 0.1),
                            inset 0 1px 0 rgba(255, 255, 255, 0.1);
                        overflow: hidden;
                        position: relative;
                        z-index: 1;
                        border: 1px solid rgba(148, 163, 184, 0.2);
                    }
                    
                    .email-container::before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: 0;
                        right: 0;
                        height: 1px;
                        background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
                    }
                    
                    .header {
                        background: linear-gradient(135deg, #1e293b 0%%, #334155 50%%, #475569 100%%);
                        padding: 40px 30px;
                        text-align: center;
                        position: relative;
                        overflow: hidden;
                    }
                    
                    .header::before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: 0;
                        right: 0;
                        bottom: 0;
                        background: 
                            radial-gradient(circle at 30%% 20%%, rgba(59, 130, 246, 0.2) 0%%, transparent 50%%),
                            radial-gradient(circle at 70%% 80%%, rgba(139, 92, 246, 0.2) 0%%, transparent 50%%);
                        pointer-events: none;
                    }
                    
                    .invite-icon {
                        width: 80px;
                        height: 80px;
                        margin: 0 auto 20px;
                        background: linear-gradient(135deg, #3b82f6, #8b5cf6);
                        border-radius: 50%%;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        box-shadow: 0 10px 30px rgba(59, 130, 246, 0.4);
                        animation: iconPulse 3s ease-in-out infinite;
                        position: relative;
                        z-index: 1;
                    }
                    
                    @keyframes iconPulse {
                        0%%, 100%% { transform: scale(1); box-shadow: 0 10px 30px rgba(59, 130, 246, 0.4); }
                        50%% { transform: scale(1.05); box-shadow: 0 15px 40px rgba(59, 130, 246, 0.6); }
                    }
                    
                    .invite-icon svg {
                        width: 40px;
                        height: 40px;
                        fill: white;
                    }
                    
                    .brand-name {
                        font-size: 32px;
                        font-weight: 800;
                        background: linear-gradient(135deg, #ffffff, #e2e8f0);
                        -webkit-background-clip: text;
                        -webkit-text-fill-color: transparent;
                        background-clip: text;
                        margin-bottom: 8px;
                        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
                        position: relative;
                        z-index: 1;
                    }
                    
                    .tagline {
                        color: #cbd5e1;
                        font-size: 16px;
                        font-weight: 500;
                        position: relative;
                        z-index: 1;
                    }
                    
                    .content {
                        padding: 40px 30px;
                        background: rgba(15, 23, 42, 0.8);
                    }
                    
                    .greeting {
                        font-size: 20px;
                        font-weight: 600;
                        color: #f8fafc;
                        margin-bottom: 20px;
                        text-align: center;
                    }
                    
                    .invite-message {
                        color: #cbd5e1;
                        font-size: 16px;
                        line-height: 1.7;
                        margin-bottom: 30px;
                        text-align: center;
                    }
                    
                    .credentials-container {
                        background: linear-gradient(135deg, #1e293b, #334155);
                        border-radius: 16px;
                        padding: 30px;
                        margin: 30px 0;
                        border: 1px solid rgba(148, 163, 184, 0.2);
                        box-shadow: 0 10px 25px rgba(0, 0, 0, 0.3);
                    }
                    
                    .credentials-title {
                        color: #f8fafc;
                        font-size: 18px;
                        font-weight: 600;
                        margin-bottom: 20px;
                        text-align: center;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        gap: 10px;
                    }
                    
                    .credentials-title::before {
                        content: '🔐';
                        font-size: 20px;
                    }
                    
                    .credential-item {
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                        padding: 15px 20px;
                        background: rgba(15, 23, 42, 0.6);
                        border-radius: 12px;
                        margin-bottom: 15px;
                        border: 1px solid rgba(148, 163, 184, 0.1);
                        transition: all 0.3s ease;
                    }
                    
                    .credential-item:hover {
                        background: rgba(15, 23, 42, 0.8);
                        border-color: rgba(59, 130, 246, 0.3);
                        transform: translateY(-2px);
                    }
                    
                    .credential-label {
                        color: #94a3b8;
                        font-size: 14px;
                        font-weight: 500;
                    }
                    
                    .credential-value {
                        color: #f8fafc;
                        font-size: 16px;
                        font-weight: 600;
                        font-family: 'SF Mono', Monaco, 'Cascadia Code', 'Roboto Mono', Consolas, 'Courier New', monospace;
                        background: rgba(59, 130, 246, 0.1);
                        padding: 8px 12px;
                        border-radius: 8px;
                        border: 1px solid rgba(59, 130, 246, 0.2);
                    }
                    
                    .login-button {
                        display: inline-block;
                        background: linear-gradient(135deg, #3b82f6, #8b5cf6);
                        color: white;
                        text-decoration: none;
                        padding: 16px 32px;
                        border-radius: 12px;
                        font-weight: 600;
                        font-size: 16px;
                        text-align: center;
                        margin: 30px auto;
                        display: block;
                        max-width: 200px;
                        box-shadow: 0 10px 25px rgba(59, 130, 246, 0.4);
                        transition: all 0.3s ease;
                        position: relative;
                        overflow: hidden;
                    }
                    
                    .login-button::before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: -100%%;
                        width: 100%%;
                        height: 100%%;
                        background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
                        transition: left 0.5s ease;
                    }
                    
                    .login-button:hover {
                        transform: translateY(-3px);
                        box-shadow: 0 15px 35px rgba(59, 130, 246, 0.6);
                    }
                    
                    .login-button:hover::before {
                        left: 100%%;
                    }
                    
                    .warning-box {
                        background: linear-gradient(135deg, rgba(245, 158, 11, 0.1), rgba(217, 119, 6, 0.1));
                        border: 1px solid rgba(245, 158, 11, 0.3);
                        border-radius: 12px;
                        padding: 20px;
                        margin: 30px 0;
                        color: #fbbf24;
                        font-size: 14px;
                        line-height: 1.6;
                    }
                    
                    .warning-box::before {
                        content: '⚠️';
                        font-size: 18px;
                        margin-right: 10px;
                    }
                    
                    .footer {
                        background: linear-gradient(135deg, #0f172a, #1e293b);
                        padding: 30px;
                        text-align: center;
                        border-top: 1px solid rgba(148, 163, 184, 0.2);
                    }
                    
                    .footer-text {
                        color: #64748b;
                        font-size: 14px;
                        margin-bottom: 10px;
                    }
                    
                    .website-link {
                        color: #3b82f6;
                        text-decoration: none;
                        font-weight: 500;
                        transition: all 0.3s ease;
                        position: relative;
                        z-index: 1;
                        padding: 8px 16px;
                        border-radius: 20px;
                        background: rgba(59, 130, 246, 0.1);
                        border: 1px solid rgba(59, 130, 246, 0.3);
                    }
                    
                    .website-link:hover {
                        color: #ffffff;
                        background: rgba(59, 130, 246, 0.2);
                        transform: translateY(-2px);
                        box-shadow: 0 8px 16px rgba(59, 130, 246, 0.3);
                    }
                    
                    @media (max-width: 600px) {
                        .email-container {
                            margin: 10px;
                            border-radius: 16px;
                        }
                        
                        .header, .content, .footer {
                            padding: 30px 20px;
                        }
                        
                        .brand-name {
                            font-size: 24px;
                        }
                        
                        .greeting {
                            font-size: 18px;
                        }
                        
                        .credentials-container {
                            padding: 20px;
                        }
                        
                        .credential-item {
                            flex-direction: column;
                            align-items: flex-start;
                            gap: 10px;
                        }
                        
                        .credential-value {
                            align-self: stretch;
                            text-align: center;
                        }
                    }
                </style>
            </head>
            <body>
                <div class="email-container">
                    <div class="header">
                        <div class="invite-icon">
                            <svg viewBox="0 0 24 24">
                                <path d="M12,2A10,10 0 0,0 2,12A10,10 0 0,0 12,22A10,10 0 0,0 22,12A10,10 0 0,0 12,2M11,16.5L6.5,12L7.91,10.59L11,13.67L16.59,8.09L18,9.5L11,16.5Z"/>
                            </svg>
                        </div>
                        <div class="brand-name">Wenji</div>
                        <div class="tagline">智能协作平台</div>
                    </div>
                    
                    <div class="content">
                        <div class="greeting">您好，%s！</div>
                        
                        <div class="invite-message">
                            您被「<strong>%s</strong>」邀请加入 <strong>Wenji 智能协作平台</strong>，开启高效协作之旅！
                        </div>
                        
                        <div class="credentials-container">
                            <div class="credentials-title">登录凭据</div>
                            
                            <div class="credential-item">
                                <span class="credential-label">登录账号</span>
                                <span class="credential-value">%s</span>
                            </div>
                            
                            <div class="credential-item">
                                <span class="credential-label">临时密码</span>
                                <span class="credential-value">%s</span>
                            </div>
                        </div>
                        
                        <a href="http://localhost:5173/login" class="login-button">
                            立即登录平台
                        </a>
                        
                        <div class="warning-box">
                            <strong>重要提醒：</strong><br>
                            • 邀请有效期为 14 天，请及时登录<br>
                            • 首次登录后请立即修改密码<br>
                            • 如有疑问，请联系邀请人
                        </div>
                    </div>
                    
                    <div class="footer">
                        <div class="footer-text">
                            此邮件由系统自动发送，请勿回复
                        </div>
                        <div class="footer-text">
                            <strong>Wenji 智能协作平台</strong> · 
                            <a href="https://wenji.shamillaa.com" class="website-link">https://wenji.shamillaa.com</a>
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """, 
            username != null ? username : "用户",
            inviterName,
            email,
            tempPassword
        );
    }
    
    // 获取角色名称
    private String getRoleName(Integer role) {
        switch (role) {
            case 0: return "超级用户";
            case 1: return "普通用户";
            case 2: return "管理员";
            case 3: return "编辑";
            case 4: return "查看者";
            default: return "未知角色";
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