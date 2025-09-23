package com.wenji.server.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class EmailUtil {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.properties.mail.from}")
    private String fromEmail;
    
    // 发送简单邮件
    public void sendSimpleEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        // 从配置文件中读取发件人
        message.setFrom(fromEmail);
        
        mailSender.send(message);
    }
    
    // 发送HTML邮件
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true表示发送HTML内容
            helper.setFrom(fromEmail);
            
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("发送HTML邮件失败: " + e.getMessage(), e);
        }
    }
    
    // 创建验证码邮件HTML模板
    private String createVerificationCodeEmailTemplate(String code, String operationType) {
        return String.format("""
            <!DOCTYPE html>
            <html lang="zh-CN">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>🚀 Wenji 验证码 - 未来已来</title>
                <style>
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }
                    
                    body {
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, 'PingFang SC', 'Microsoft YaHei', sans-serif;
                        line-height: 1.6;
                        color: #1a1a1a;
                        background: linear-gradient(135deg, #667eea 0%%, #764ba2 50%%, #f093fb 100%%);
                        background-attachment: fixed;
                        min-height: 100vh;
                        padding: 20px;
                        position: relative;
                        overflow-x: hidden;
                    }
                    
                    body::before {
                        content: '';
                        position: fixed;
                        top: 0;
                        left: 0;
                        width: 100%%;
                        height: 100%%;
                        background: 
                            radial-gradient(circle at 20%% 80%%, rgba(120, 119, 198, 0.3) 0%%, transparent 50%%),
                            radial-gradient(circle at 80%% 20%%, rgba(255, 119, 198, 0.3) 0%%, transparent 50%%),
                            radial-gradient(circle at 40%% 40%%, rgba(120, 219, 255, 0.2) 0%%, transparent 50%%);
                        animation: backgroundFloat 15s ease-in-out infinite;
                        z-index: -1;
                    }
                    
                    @keyframes backgroundFloat {
                        0%%, 100%% { transform: translateY(0px) rotate(0deg); }
                        50%% { transform: translateY(-20px) rotate(1deg); }
                    }
                    
                    .email-container {
                        max-width: 600px;
                        margin: 0 auto;
                        background: rgba(255, 255, 255, 0.95);
                        backdrop-filter: blur(20px);
                        border-radius: 24px;
                        overflow: hidden;
                        box-shadow: 
                            0 32px 64px rgba(0, 0, 0, 0.12),
                            0 0 0 1px rgba(255, 255, 255, 0.2),
                            inset 0 1px 0 rgba(255, 255, 255, 0.3);
                        animation: containerFloat 6s ease-in-out infinite;
                        position: relative;
                    }
                    
                    @keyframes containerFloat {
                        0%%, 100%% { transform: translateY(0px); }
                        50%% { transform: translateY(-8px); }
                    }
                    
                    .header {
                        background: linear-gradient(135deg, #667eea 0%%, #764ba2 50%%, #f093fb 100%%);
                        padding: 50px 30px;
                        text-align: center;
                        position: relative;
                        overflow: hidden;
                    }
                    
                    .header::before {
                        content: '';
                        position: absolute;
                        top: -50%%;
                        left: -50%%;
                        width: 200%%;
                        height: 200%%;
                        background: 
                            radial-gradient(circle, rgba(255,255,255,0.15) 0%%, transparent 70%%),
                            conic-gradient(from 0deg, transparent, rgba(255,255,255,0.1), transparent);
                        animation: headerRotate 25s linear infinite;
                    }
                    
                    .header::after {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: 0;
                        right: 0;
                        bottom: 0;
                        background: linear-gradient(45deg, transparent 30%%, rgba(255,255,255,0.1) 50%%, transparent 70%%);
                        animation: shimmer 3s ease-in-out infinite;
                    }
                    
                    @keyframes headerRotate {
                        0%% { transform: rotate(0deg); }
                        100%% { transform: rotate(360deg); }
                    }
                    
                    @keyframes shimmer {
                        0%%, 100%% { transform: translateX(-100%%); }
                        50%% { transform: translateX(100%%); }
                    }
                    
                    .logo {
                        display: inline-flex;
                        align-items: center;
                        justify-content: center;
                        width: 80px;
                        height: 80px;
                        background: linear-gradient(135deg, #ff6b6b 0%%, #4ecdc4 50%%, #45b7d1 100%%);
                        border-radius: 20px;
                        box-shadow: 
                            0 20px 40px rgba(0, 0, 0, 0.15),
                            inset 0 1px 0 rgba(255, 255, 255, 0.3);
                        margin-bottom: 20px;
                        position: relative;
                        z-index: 1;
                        animation: logoFloat 4s ease-in-out infinite;
                        transition: all 0.3s ease;
                    }
                    
                    @keyframes logoFloat {
                        0%%, 100%% { transform: translateY(0px) rotate(0deg); }
                        50%% { transform: translateY(-5px) rotate(2deg); }
                    }
                    
                    .logo:hover {
                        transform: scale(1.1) rotate(5deg);
                    }
                    
                    .logo svg {
                        width: 40px;
                        height: 40px;
                        fill: white;
                        filter: drop-shadow(0 2px 4px rgba(0,0,0,0.2));
                    }
                    
                    .brand-name {
                        color: white;
                        font-size: 32px;
                        font-weight: 800;
                        margin-bottom: 8px;
                        position: relative;
                        z-index: 1;
                        text-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
                        background: linear-gradient(45deg, #ffffff, #f0f9ff, #ffffff);
                        background-size: 200%% 200%%;
                        -webkit-background-clip: text;
                        -webkit-text-fill-color: transparent;
                        background-clip: text;
                        animation: textShine 3s ease-in-out infinite;
                    }
                    
                    @keyframes textShine {
                        0%%, 100%% { background-position: 0%% 50%%; }
                        50%% { background-position: 100%% 50%%; }
                    }
                    
                    .brand-subtitle {
                        color: rgba(255, 255, 255, 0.95);
                        font-size: 18px;
                        font-weight: 500;
                        position: relative;
                        z-index: 1;
                        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                        letter-spacing: 0.5px;
                    }
                    
                    .content {
                        padding: 50px 40px;
                        background: linear-gradient(180deg, rgba(255,255,255,0.9) 0%%, rgba(248,250,252,0.9) 100%%);
                        position: relative;
                    }
                    
                    .content::before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: 0;
                        right: 0;
                        height: 2px;
                        background: linear-gradient(90deg, #ff6b6b, #4ecdc4, #45b7d1, #96ceb4, #feca57);
                        background-size: 300%% 100%%;
                        animation: gradientMove 4s ease-in-out infinite;
                    }
                    
                    @keyframes gradientMove {
                        0%%, 100%% { background-position: 0%% 50%%; }
                        50%% { background-position: 100%% 50%%; }
                    }
                    
                    .greeting {
                        font-size: 24px;
                        font-weight: 700;
                        color: #1a202c;
                        margin-bottom: 20px;
                        background: linear-gradient(135deg, #667eea, #764ba2);
                        -webkit-background-clip: text;
                        -webkit-text-fill-color: transparent;
                        background-clip: text;
                    }
                    
                    .content {
                        padding: 50px 40px;
                        text-align: center;
                    }
                    
                    .greeting {
                        font-size: 24px;
                        font-weight: 600;
                        color: #1f2937;
                        margin-bottom: 20px;
                    }
                    
                    .message {
                        font-size: 16px;
                        color: #6b7280;
                        margin-bottom: 40px;
                        line-height: 1.8;
                    }
                    
                    .code-container {
                        background: linear-gradient(135deg, #667eea 0%%, #764ba2 50%%, #f093fb 100%%);
                        border: 3px solid transparent;
                        border-radius: 20px;
                        padding: 40px;
                        margin: 40px 0;
                        position: relative;
                        overflow: hidden;
                        box-shadow: 0 20px 40px rgba(102, 126, 234, 0.3);
                        animation: codeGlow 3s ease-in-out infinite alternate;
                    }
                    
                    @keyframes codeGlow {
                        0%% { box-shadow: 0 20px 40px rgba(102, 126, 234, 0.3); }
                        100%% { box-shadow: 0 25px 50px rgba(118, 75, 162, 0.5); }
                    }
                    
                    .code-container::before {
                        content: '';
                        position: absolute;
                        top: -2px;
                        left: -2px;
                        right: -2px;
                        bottom: -2px;
                        background: linear-gradient(45deg, #ff6b6b, #4ecdc4, #45b7d1, #96ceb4, #ffeaa7);
                        border-radius: 22px;
                        z-index: -1;
                        animation: borderRotate 4s linear infinite;
                    }
                    
                    @keyframes borderRotate {
                        0%% { transform: rotate(0deg); }
                        100%% { transform: rotate(360deg); }
                    }
                    
                    .code-label {
                        font-size: 16px;
                        color: #ffffff;
                        margin-bottom: 15px;
                        font-weight: 600;
                        text-transform: uppercase;
                        letter-spacing: 2px;
                        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
                    }
                    
                    .verification-code {
                        font-size: 42px;
                        font-weight: 800;
                        color: #ffffff;
                        letter-spacing: 12px;
                        font-family: 'SF Mono', 'Monaco', 'Inconsolata', 'Roboto Mono', monospace;
                        text-shadow: 0 4px 8px rgba(0, 0, 0, 0.4);
                        background: linear-gradient(45deg, #ffffff, #f0f0f0);
                        -webkit-background-clip: text;
                        -webkit-text-fill-color: transparent;
                        background-clip: text;
                        animation: codeShine 2s ease-in-out infinite alternate;
                        position: relative;
                    }
                    
                    @keyframes codeShine {
                        0%% { 
                            text-shadow: 0 4px 8px rgba(0, 0, 0, 0.4);
                            transform: scale(1);
                        }
                        100%% { 
                            text-shadow: 0 6px 12px rgba(0, 0, 0, 0.6);
                            transform: scale(1.05);
                        }
                    }
                    
                    .timer-info {
                        display: inline-flex;
                        align-items: center;
                        background: linear-gradient(135deg, #ff9a9e 0%%, #fecfef 50%%, #fecfef 100%%);
                        color: #be185d;
                        padding: 16px 24px;
                        border-radius: 30px;
                        font-size: 15px;
                        font-weight: 600;
                        margin: 30px 0;
                        border: 2px solid #f9a8d4;
                        box-shadow: 0 8px 16px rgba(244, 114, 182, 0.2);
                        animation: timerPulse 2s ease-in-out infinite;
                    }
                    
                    @keyframes timerPulse {
                        0%%, 100%% { transform: scale(1); }
                        50%% { transform: scale(1.02); }
                    }
                    
                    .timer-icon {
                        width: 18px;
                        height: 18px;
                        margin-right: 10px;
                        fill: currentColor;
                        animation: timerTick 1s ease-in-out infinite;
                    }
                    
                    @keyframes timerTick {
                        0%%, 100%% { transform: rotate(0deg); }
                        50%% { transform: rotate(10deg); }
                    }
                    
                    .security-notice {
                        background: linear-gradient(135deg, #fef7ff 0%%, #f3e8ff 100%%);
                        border: 2px solid #a855f7;
                        border-radius: 16px;
                        padding: 24px;
                        margin: 30px 0;
                        text-align: left;
                        position: relative;
                        overflow: hidden;
                        box-shadow: 0 10px 25px rgba(168, 85, 247, 0.15);
                    }
                    
                    .security-notice::before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: 0;
                        right: 0;
                        height: 4px;
                        background: linear-gradient(90deg, #a855f7, #ec4899, #f59e0b, #10b981);
                        animation: securityGlow 3s linear infinite;
                    }
                    
                    @keyframes securityGlow {
                        0%% { transform: translateX(-100%%); }
                        100%% { transform: translateX(100%%); }
                    }
                    
                    .security-title {
                        font-weight: 700;
                        color: #7c3aed;
                        margin-bottom: 12px;
                        display: flex;
                        align-items: center;
                        font-size: 16px;
                    }
                    
                    .security-icon {
                        width: 20px;
                        height: 20px;
                        margin-right: 10px;
                        fill: currentColor;
                        animation: securityBlink 2s ease-in-out infinite;
                    }
                    
                    @keyframes securityBlink {
                        0%%, 100%% { opacity: 1; }
                        50%% { opacity: 0.6; }
                    }
                    
                    .security-text {
                        color: #581c87;
                        font-size: 14px;
                        line-height: 1.7;
                        font-weight: 500;
                    }
                    
                    .footer {
                        background: linear-gradient(135deg, #1e293b 0%%, #334155 100%%);
                        padding: 40px;
                        text-align: center;
                        border-top: 3px solid #64748b;
                        position: relative;
                        overflow: hidden;
                    }
                    
                    .footer::before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: -100%%;
                        width: 100%%;
                        height: 100%%;
                        background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.1), transparent);
                        animation: footerShine 3s ease-in-out infinite;
                    }
                    
                    @keyframes footerShine {
                        0%% { left: -100%%; }
                        100%% { left: 100%%; }
                    }
                    
                    .footer-text {
                        color: #cbd5e1;
                        font-size: 15px;
                        margin-bottom: 20px;
                        font-weight: 500;
                        position: relative;
                        z-index: 1;
                    }
                    
                    .website-link {
                        color: #60a5fa;
                        text-decoration: none;
                        font-weight: 600;
                        transition: all 0.3s ease;
                        position: relative;
                        z-index: 1;
                        padding: 8px 16px;
                        border-radius: 20px;
                        background: rgba(96, 165, 250, 0.1);
                        border: 1px solid rgba(96, 165, 250, 0.3);
                    }
                    
                    .website-link:hover {
                        color: #ffffff;
                        background: rgba(96, 165, 250, 0.2);
                        transform: translateY(-2px);
                        box-shadow: 0 8px 16px rgba(96, 165, 250, 0.3);
                    }
                    
                    .divider {
                        height: 3px;
                        background: linear-gradient(90deg, #3b82f6 0%%, #8b5cf6 25%%, #ec4899 50%%, #f59e0b 75%%, #10b981 100%%);
                        margin: 25px 0;
                        border-radius: 2px;
                        animation: dividerFlow 4s linear infinite;
                    }
                    
                    @keyframes dividerFlow {
                        0%% { background-position: 0%% 50%%; }
                        100%% { background-position: 200%% 50%%; }
                    }
                    
                    @media (max-width: 600px) {
                        .email-container {
                            margin: 10px;
                            border-radius: 12px;
                        }
                        
                        .header {
                            padding: 30px 20px;
                        }
                        
                        .content {
                            padding: 30px 20px;
                        }
                        
                        .verification-code {
                            font-size: 28px;
                            letter-spacing: 4px;
                        }
                        
                        .footer {
                            padding: 20px;
                        }
                    }
                </style>
            </head>
            <body>
                <div class="email-container">
                    <div class="header">
                        <div class="logo">
                            <svg viewBox="0 0 24 24">
                                <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"/>
                            </svg>
                        </div>
                        <div class="brand-name">文际智能电商系统</div>
                        <div class="brand-subtitle">GlobalPulse 跨境智能运营平台</div>
                    </div>
                    
                    <div class="content">
                        <div class="greeting">您好！</div>
                        <div class="message">
                            您正在进行<strong>%s</strong>操作，请使用以下验证码完成验证：
                        </div>
                        
                        <div class="code-container">
                            <div class="code-label">您的验证码</div>
                            <div class="verification-code">%s</div>
                        </div>
                        
                        <div class="timer-info">
                            <svg class="timer-icon" viewBox="0 0 24 24">
                                <path d="M12,2A10,10 0 0,0 2,12A10,10 0 0,0 12,22A10,10 0 0,0 22,12A10,10 0 0,0 12,2M16.2,16.2L11,13V7H12.5V12.2L17,14.7L16.2,16.2Z"/>
                            </svg>
                            验证码有效期为 15 分钟，请及时使用
                        </div>
                        
                        <div class="security-notice">
                            <div class="security-title">
                                <svg class="security-icon" viewBox="0 0 24 24">
                                    <path d="M12,1L3,5V11C3,16.55 6.84,21.74 12,23C17.16,21.74 21,16.55 21,11V5L12,1M12,7C13.4,7 14.8,8.6 14.8,10V11.5C15.4,11.5 16,12.4 16,13V16C16,17.4 15.4,18 14.8,18H9.2C8.6,18 8,17.4 8,16V13C8,12.4 8.6,11.5 9.2,11.5V10C9.2,8.6 10.6,7 12,7M12,8.2C11.2,8.2 10.5,8.7 10.5,10V11.5H13.5V10C13.5,8.7 12.8,8.2 12,8.2Z"/>
                                </svg>
                                安全提醒
                            </div>
                            <div class="security-text">
                                • 为了您的账户安全，请勿将验证码告知他人<br>
                                • 如果这不是您本人的操作，请忽略此邮件<br>
                                • 验证码仅用于本次操作，请勿重复使用
                            </div>
                        </div>
                        
                        <div class="divider"></div>
                    </div>
                    
                    <div class="footer">
                        <div class="footer-text">
                             此邮件由系统自动发送，请勿回复
                         </div>
                         <div class="footer-text">
                             <strong>GlobalPulse 跨境智能运营平台</strong> · 
                             <a href="https://wenji.shamillaa.com" class="website-link">https://wenji.shamillaa.com</a>
                         </div>
                    </div>
                </div>
            </body>
            </html>
            """, operationType, code);
    }
    public void sendVerificationCode(String to, String code, Integer type) {
        String subject = type == 1 ? "【Wenji】注册验证码" : "【Wenji】密码重置验证码";
        
        String operationType = type == 1 ? "注册" : "重置密码";
        String htmlContent = createVerificationCodeEmailTemplate(code, operationType);
        
        sendHtmlEmail(to, subject, htmlContent);
    }
    
    // 创建注册成功邮件HTML模板
    private String createRegistrationSuccessEmailTemplate(String username) {
        return String.format("""
            <!DOCTYPE html>
            <html lang="zh-CN">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>🎉 欢迎加入 Wenji 智能平台！</title>
                <style>
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }
                    
                    body {
                        font-family: 'SF Pro Display', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
                        line-height: 1.6;
                        color: #1a202c;
                        background: linear-gradient(135deg, #667eea 0%%, #764ba2 50%%, #f093fb 100%%);
                        min-height: 100vh;
                        padding: 20px;
                        animation: backgroundShift 10s ease-in-out infinite alternate;
                    }
                    
                    @keyframes backgroundShift {
                        0%% { background: linear-gradient(135deg, #667eea 0%%, #764ba2 50%%, #f093fb 100%%); }
                        100%% { background: linear-gradient(135deg, #f093fb 0%%, #667eea 50%%, #764ba2 100%%); }
                    }
                    
                    .email-container {
                        max-width: 650px;
                        margin: 0 auto;
                        background: rgba(255, 255, 255, 0.95);
                        backdrop-filter: blur(20px);
                        border-radius: 24px;
                        overflow: hidden;
                        box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
                        border: 1px solid rgba(255, 255, 255, 0.2);
                        animation: containerFloat 6s ease-in-out infinite;
                    }
                    
                    @keyframes containerFloat {
                        0%%, 100%% { transform: translateY(0px); }
                        50%% { transform: translateY(-10px); }
                    }
                    
                    .header {
                        background: linear-gradient(135deg, #10b981 0%%, #059669 30%%, #047857 70%%, #065f46 100%%);
                        padding: 50px 40px;
                        text-align: center;
                        position: relative;
                        overflow: hidden;
                    }
                    
                    .header::before {
                        content: '';
                        position: absolute;
                        top: -50%%;
                        left: -50%%;
                        width: 200%%;
                        height: 200%%;
                        background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%%, transparent 70%%);
                        animation: headerRotate 15s linear infinite;
                    }
                    
                    @keyframes headerRotate {
                        0%% { transform: rotate(0deg); }
                        100%% { transform: rotate(360deg); }
                    }
                    
                    .success-icon {
                        width: 80px;
                        height: 80px;
                        margin: 0 auto 20px;
                        background: linear-gradient(135deg, #ffffff 0%%, #f0fdf4 100%%);
                        border-radius: 50%%;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        box-shadow: 0 10px 30px rgba(16, 185, 129, 0.3);
                        animation: successPulse 2s ease-in-out infinite;
                        position: relative;
                        z-index: 2;
                    }
                    
                    @keyframes successPulse {
                        0%%, 100%% { 
                            transform: scale(1);
                            box-shadow: 0 10px 30px rgba(16, 185, 129, 0.3);
                        }
                        50%% { 
                            transform: scale(1.1);
                            box-shadow: 0 15px 40px rgba(16, 185, 129, 0.5);
                        }
                    }
                    
                    .success-icon svg {
                        width: 40px;
                        height: 40px;
                        fill: #10b981;
                        animation: checkmarkDraw 1s ease-in-out;
                    }
                    
                    @keyframes checkmarkDraw {
                        0%% { 
                            transform: scale(0) rotate(45deg);
                            opacity: 0;
                        }
                        50%% { 
                            transform: scale(1.2) rotate(45deg);
                            opacity: 0.8;
                        }
                        100%% { 
                            transform: scale(1) rotate(0deg);
                            opacity: 1;
                        }
                    }
                        background: radial-gradient(circle, rgba(255,255,255,0.1) 0%%, transparent 70%%);
                        animation: rotate 20s linear infinite;
                    }
                    
                    @keyframes rotate {
                        0%% { transform: rotate(0deg); }
                        100%% { transform: rotate(360deg); }
                    }
                    
                    .success-icon {
                        display: inline-flex;
                        align-items: center;
                        justify-content: center;
                        width: 80px;
                        height: 80px;
                        background: linear-gradient(135deg, #34d399 0%%, #10b981 100%%);
                        border-radius: 50%%;
                        margin-bottom: 20px;
                        box-shadow: 0 8px 16px rgba(16, 185, 129, 0.3);
                        position: relative;
                        z-index: 1;
                    }
                    
                    .success-icon svg {
                        width: 40px;
                        height: 40px;
                        fill: white;
                    }
                    
                    .brand-name {
                        color: #ffffff;
                        font-size: 32px;
                        font-weight: 800;
                        margin-bottom: 12px;
                        position: relative;
                        z-index: 2;
                        text-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
                        background: linear-gradient(45deg, #ffffff, #f0fdf4);
                        -webkit-background-clip: text;
                        -webkit-text-fill-color: transparent;
                        background-clip: text;
                        animation: brandShine 3s ease-in-out infinite alternate;
                    }
                    
                    @keyframes brandShine {
                        0%% { 
                            text-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
                            transform: scale(1);
                        }
                        100%% { 
                            text-shadow: 0 6px 12px rgba(0, 0, 0, 0.3);
                            transform: scale(1.02);
                        }
                    }
                    
                    .success-message {
                        color: rgba(255, 255, 255, 0.95);
                        font-size: 20px;
                        font-weight: 600;
                        position: relative;
                        z-index: 2;
                        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                        animation: messageFloat 4s ease-in-out infinite;
                    }
                    
                    @keyframes messageFloat {
                        0%%, 100%% { transform: translateY(0px); }
                        50%% { transform: translateY(-3px); }
                    }
                    
                    .content {
                        padding: 60px 50px;
                        text-align: center;
                        background: linear-gradient(135deg, #ffffff 0%%, #f8fafc 100%%);
                    }
                    
                    .greeting {
                        font-size: 28px;
                        font-weight: 700;
                        color: #1f2937;
                        margin-bottom: 25px;
                        background: linear-gradient(135deg, #1f2937 0%%, #374151 100%%);
                        -webkit-background-clip: text;
                        -webkit-text-fill-color: transparent;
                        background-clip: text;
                    }
                    
                    .welcome-message {
                        font-size: 18px;
                        color: #4b5563;
                        margin-bottom: 50px;
                        line-height: 1.8;
                        font-weight: 500;
                    }
                    
                    .features-container {
                        background: linear-gradient(135deg, #f0f9ff 0%%, #e0f2fe 50%%, #f0f9ff 100%%);
                        border: 2px solid #3b82f6;
                        border-radius: 20px;
                        padding: 40px;
                        margin: 40px 0;
                        text-align: left;
                        position: relative;
                        overflow: hidden;
                        box-shadow: 0 15px 35px rgba(59, 130, 246, 0.15);
                    }
                    
                    .features-container::before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: -100%%;
                        width: 100%%;
                        height: 100%%;
                        background: linear-gradient(90deg, transparent, rgba(59, 130, 246, 0.1), transparent);
                        animation: featureShine 4s ease-in-out infinite;
                    }
                    
                    @keyframes featureShine {
                        0%% { left: -100%%; }
                        100%% { left: 100%%; }
                    }
                    
                    .features-title {
                        font-size: 22px;
                        font-weight: 700;
                        color: #1e40af;
                        margin-bottom: 25px;
                        text-align: center;
                        position: relative;
                        z-index: 1;
                    }
                    
                    .feature-item {
                        display: flex;
                        align-items: center;
                        margin-bottom: 18px;
                        padding: 15px;
                        background: rgba(255, 255, 255, 0.8);
                        backdrop-filter: blur(10px);
                        border-radius: 12px;
                        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.08);
                        border: 1px solid rgba(59, 130, 246, 0.2);
                        transition: all 0.3s ease;
                        position: relative;
                        z-index: 1;
                    }
                    
                    .feature-item:hover {
                        transform: translateX(5px);
                        box-shadow: 0 8px 16px rgba(59, 130, 246, 0.2);
                    }
                    
                    .feature-icon {
                        width: 28px;
                        height: 28px;
                        margin-right: 15px;
                        fill: #3b82f6;
                        flex-shrink: 0;
                        animation: iconBounce 2s ease-in-out infinite;
                    }
                    
                    @keyframes iconBounce {
                        0%%, 100%% { transform: scale(1); }
                        50%% { transform: scale(1.1); }
                    }
                    
                    .feature-text {
                        color: #1f2937;
                        font-weight: 600;
                        font-size: 16px;
                    }
                    
                    .login-button {
                        display: inline-block;
                        background: linear-gradient(135deg, #3b82f6 0%%, #1d4ed8 50%%, #1e40af 100%%);
                        color: white;
                        padding: 18px 40px;
                        border-radius: 30px;
                        text-decoration: none;
                        font-weight: 700;
                        font-size: 18px;
                        margin: 40px 0;
                        box-shadow: 0 12px 24px rgba(59, 130, 246, 0.4);
                        transition: all 0.3s ease;
                        position: relative;
                        overflow: hidden;
                        text-transform: uppercase;
                        letter-spacing: 1px;
                    }
                    
                    .login-button::before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: -100%%;
                        width: 100%%;
                        height: 100%%;
                        background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
                        transition: left 0.5s ease;
                    }
                    
                    .login-button:hover {
                        transform: translateY(-3px);
                        box-shadow: 0 16px 32px rgba(59, 130, 246, 0.5);
                    }
                    
                    .login-button:hover::before {
                        left: 100%%;
                    }
                    
                    .footer {
                        background: linear-gradient(135deg, #1e293b 0%%, #334155 100%%);
                        padding: 40px;
                        text-align: center;
                        border-top: 3px solid #10b981;
                        position: relative;
                        overflow: hidden;
                    }
                    
                    .footer::before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: -100%%;
                        width: 100%%;
                        height: 100%%;
                        background: linear-gradient(90deg, transparent, rgba(16, 185, 129, 0.2), transparent);
                        animation: footerGlow 5s ease-in-out infinite;
                    }
                    
                    @keyframes footerGlow {
                        0%% { left: -100%%; }
                        100%% { left: 100%%; }
                    }
                    
                    .footer-text {
                        color: #cbd5e1;
                        font-size: 15px;
                        margin-bottom: 20px;
                        font-weight: 500;
                        position: relative;
                        z-index: 1;
                    }
                    
                    .website-link {
                        color: #10b981;
                        text-decoration: none;
                        font-weight: 600;
                        transition: all 0.3s ease;
                        position: relative;
                        z-index: 1;
                        padding: 8px 16px;
                        border-radius: 20px;
                        background: rgba(16, 185, 129, 0.1);
                        border: 1px solid rgba(16, 185, 129, 0.3);
                    }
                    
                    .website-link:hover {
                        color: #ffffff;
                        background: rgba(16, 185, 129, 0.2);
                        transform: translateY(-2px);
                        box-shadow: 0 8px 16px rgba(16, 185, 129, 0.3);
                    }
                    
                    .divider {
                        height: 4px;
                        background: linear-gradient(90deg, #10b981 0%%, #34d399 25%%, #6ee7b7 50%%, #34d399 75%%, #10b981 100%%);
                        margin: 25px 0;
                        border-radius: 2px;
                        animation: dividerPulse 3s ease-in-out infinite;
                    }
                    
                    @keyframes dividerPulse {
                        0%%, 100%% { 
                            background-size: 200%% 100%%;
                            background-position: 0%% 50%%;
                        }
                        50%% { 
                            background-size: 200%% 100%%;
                            background-position: 100%% 50%%;
                        }
                    }
                    
                    @media (max-width: 600px) {
                        .email-container {
                            margin: 10px;
                            border-radius: 12px;
                        }
                        
                        .header {
                            padding: 30px 20px;
                        }
                        
                        .content {
                            padding: 30px 20px;
                        }
                        
                        .features-container {
                            padding: 20px;
                        }
                        
                        .footer {
                            padding: 20px;
                        }
                    }
                </style>
            </head>
            <body>
                <div class="email-container">
                    <div class="header">
                        <div class="success-icon">
                            <svg viewBox="0 0 24 24">
                                <path d="M9,20.42L2.79,14.21L5.62,11.38L9,14.77L18.88,4.88L21.71,7.71L9,20.42Z"/>
                            </svg>
                        </div>
                        <div class="brand-name">注册成功！</div>
                         <div class="success-message">欢迎您使用文际智能电商系统</div>
                    </div>
                    
                    <div class="content">
                        <div class="greeting">尊敬的 %s，</div>
                         <div class="welcome-message">
                             恭喜您成功注册文际智能电商系统账户！<br>
                             所有页面同意使用 <strong>GlobalPulse 跨境智能运营平台</strong>，您现在可以使用注册邮箱登录平台，享受我们提供的各项智能服务。
                         </div>
                        
                        <div class="features-container">
                            <div class="features-title">🎉 您可以使用的功能</div>
                             
                             <div class="feature-item">
                                 <svg class="feature-icon" viewBox="0 0 24 24">
                                     <path d="M12,2A10,10 0 0,1 22,12A10,10 0 0,1 12,22A10,10 0 0,1 2,12A10,10 0 0,1 12,2M12,4A8,8 0 0,0 4,12A8,8 0 0,0 12,20A8,8 0 0,0 20,12A8,8 0 0,0 12,4M11,16.5L6.5,12L7.91,10.59L11,13.67L16.59,8.09L18,9.5L11,16.5Z"/>
                                 </svg>
                                 <span class="feature-text">跨境电商智能运营 - 全球市场拓展</span>
                             </div>
                             
                             <div class="feature-item">
                                 <svg class="feature-icon" viewBox="0 0 24 24">
                                     <path d="M22,21H2V3H4V19H6V10H10V19H12V6H16V19H18V14H22V21Z"/>
                                 </svg>
                                 <span class="feature-text">智能数据分析 - 洞察市场趋势</span>
                             </div>
                             
                             <div class="feature-item">
                                 <svg class="feature-icon" viewBox="0 0 24 24">
                                     <path d="M14,2H6A2,2 0 0,0 4,4V20A2,2 0 0,0 6,22H18A2,2 0 0,0 20,20V8L14,2M18,20H6V4H13V9H18V20Z"/>
                                 </svg>
                                 <span class="feature-text">自动化营销工具 - 提升运营效率</span>
                             </div>
                             
                             <div class="feature-item">
                                 <svg class="feature-icon" viewBox="0 0 24 24">
                                     <path d="M12,2A10,10 0 0,1 22,12A10,10 0 0,1 12,22A10,10 0 0,1 2,12A10,10 0 0,1 12,2M12,4A8,8 0 0,0 4,12A8,8 0 0,0 12,20A8,8 0 0,0 20,12A8,8 0 0,0 12,4M11,16.5L6.5,12L7.91,10.59L11,13.67L16.59,8.09L18,9.5L11,16.5Z"/>
                                 </svg>
                                 <span class="feature-text">更多功能持续更新中...</span>
                             </div>
                        </div>
                        
                        <a href="https://wenji.shamillaa.com" class="login-button">
                            立即登录平台 →
                        </a>
                        
                        <div class="divider"></div>
                        
                        <div style="color: #6b7280; font-size: 14px; margin-top: 20px;">
                            如有任何问题，请随时联系我们的客服团队
                        </div>
                    </div>
                    
                    <div class="footer">
                        <div class="footer-text">
                             此邮件由系统自动发送，请勿回复
                         </div>
                         <div class="footer-text">
                             <strong>GlobalPulse 跨境智能运营平台</strong> · 
                             <a href="https://wenji.shamillaa.com" class="website-link">https://wenji.shamillaa.com</a>
                         </div>
                    </div>
                </div>
            </body>
            </html>
            """, username);
    }
    public void sendRegisterSuccessEmail(String to, String username) {
        String subject = "【Wenji】注册成功通知";
        String htmlContent = createRegistrationSuccessEmailTemplate(username);
        sendHtmlEmail(to, subject, htmlContent);
    }
    
    // Send password reset success email
    public void sendPasswordResetSuccessEmail(String to, String username) {
        String subject = "【Wenji】密码重置成功通知";
        String htmlContent = createPasswordResetSuccessEmailTemplate(username);
        sendHtmlEmail(to, subject, htmlContent);
    }
    
    // 创建密码重置成功邮件HTML模板
    private String createPasswordResetSuccessEmailTemplate(String username) {
        return String.format("""
            <!DOCTYPE html>
            <html lang="zh-CN">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>🔐 密码重置成功 - Wenji 智能平台</title>
                <style>
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }
                    
                    body {
                        font-family: 'SF Pro Display', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
                        line-height: 1.6;
                        color: #1a202c;
                        background: linear-gradient(135deg, #667eea 0%%, #764ba2 50%%, #f093fb 100%%);
                        min-height: 100vh;
                        padding: 20px;
                        animation: backgroundShift 10s ease-in-out infinite alternate;
                    }
                    
                    @keyframes backgroundShift {
                        0%% { background: linear-gradient(135deg, #667eea 0%%, #764ba2 50%%, #f093fb 100%%); }
                        100%% { background: linear-gradient(135deg, #f093fb 0%%, #764ba2 50%%, #667eea 100%%); }
                    }
                    
                    .email-container {
                        max-width: 600px;
                        margin: 0 auto;
                        background: rgba(255, 255, 255, 0.95);
                        border-radius: 20px;
                        overflow: hidden;
                        box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
                        backdrop-filter: blur(10px);
                        border: 1px solid rgba(255, 255, 255, 0.2);
                        animation: containerFloat 6s ease-in-out infinite;
                    }
                    
                    @keyframes containerFloat {
                        0%%, 100%% { transform: translateY(0px); }
                        50%% { transform: translateY(-10px); }
                    }
                    
                    .header {
                        background: linear-gradient(135deg, #1e293b 0%%, #334155 50%%, #475569 100%%);
                        padding: 40px;
                        text-align: center;
                        position: relative;
                        overflow: hidden;
                        animation: headerShimmer 8s ease-in-out infinite;
                    }
                    
                    @keyframes headerShimmer {
                        0%%, 100%% { background-position: 0%% 50%%; }
                        50%% { background-position: 100%% 50%%; }
                    }
                    
                    .security-icon {
                        width: 80px;
                        height: 80px;
                        margin: 0 auto 20px;
                        background: linear-gradient(135deg, #10b981 0%%, #34d399 100%%);
                        border-radius: 50%%;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        box-shadow: 0 10px 30px rgba(16, 185, 129, 0.4);
                        animation: iconPulse 3s ease-in-out infinite;
                    }
                    
                    @keyframes iconPulse {
                        0%%, 100%% { transform: scale(1); box-shadow: 0 10px 30px rgba(16, 185, 129, 0.4); }
                        50%% { transform: scale(1.1); box-shadow: 0 15px 40px rgba(16, 185, 129, 0.6); }
                    }
                    
                    .security-icon svg {
                        width: 40px;
                        height: 40px;
                        fill: white;
                    }
                    
                    .brand-name {
                        color: #ffffff;
                        font-size: 28px;
                        font-weight: 700;
                        margin-bottom: 8px;
                        background: linear-gradient(45deg, #10b981, #34d399, #6ee7b7);
                        -webkit-background-clip: text;
                        -webkit-text-fill-color: transparent;
                        background-clip: text;
                        animation: textShine 4s ease-in-out infinite;
                    }
                    
                    @keyframes textShine {
                        0%%, 100%% { background-position: 0%% 50%%; }
                        50%% { background-position: 100%% 50%%; }
                    }
                    
                    .subtitle {
                        color: #cbd5e1;
                        font-size: 16px;
                        font-weight: 500;
                    }
                    
                    .content {
                        padding: 30px;
                        position: relative;
                    }
                    
                    .content::before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: 0;
                        right: 0;
                        height: 2px;
                        background: linear-gradient(90deg, #10b981, #34d399, #6ee7b7);
                        animation: borderFlow 3s ease-in-out infinite;
                    }
                    
                    @keyframes borderFlow {
                        0%%, 100%% { background-position: 0%% 50%%; }
                        50%% { background-position: 100%% 50%%; }
                    }
                    
                    .greeting {
                        font-size: 18px;
                        font-weight: 600;
                        color: #1a202c;
                        margin-bottom: 16px;
                    }
                    
                    .message {
                        font-size: 16px;
                        color: #4a5568;
                        margin-bottom: 30px;
                        line-height: 1.8;
                    }
                    
                    .info-box {
                        background: linear-gradient(135deg, #f0fdf4 0%%, #dcfce7 100%%);
                        border: 2px solid #10b981;
                        border-radius: 15px;
                        padding: 25px;
                        margin: 25px 0;
                        position: relative;
                        overflow: hidden;
                    }
                    
                    .info-box::before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: -100%%;
                        width: 100%%;
                        height: 100%%;
                        background: linear-gradient(90deg, transparent, rgba(16, 185, 129, 0.1), transparent);
                        animation: infoGlow 4s ease-in-out infinite;
                    }
                    
                    @keyframes infoGlow {
                        0%% { left: -100%%; }
                        100%% { left: 100%%; }
                    }
                    
                    .info-title {
                        font-size: 18px;
                        font-weight: 600;
                        color: #065f46;
                        margin-bottom: 15px;
                        display: flex;
                        align-items: center;
                        gap: 10px;
                    }
                    
                    .info-content {
                        color: #047857;
                        font-size: 15px;
                        line-height: 1.6;
                    }
                    
                    .security-tips {
                        background: linear-gradient(135deg, #fef3c7 0%%, #fde68a 100%%);
                        border: 2px solid #f59e0b;
                        border-radius: 15px;
                        padding: 25px;
                        margin: 25px 0;
                    }
                    
                    .tips-title {
                        font-size: 18px;
                        font-weight: 600;
                        color: #92400e;
                        margin-bottom: 15px;
                        display: flex;
                        align-items: center;
                        gap: 10px;
                    }
                    
                    .tips-content {
                        color: #a16207;
                        font-size: 15px;
                        line-height: 1.8;
                    }
                    
                    .login-button {
                        display: inline-block;
                        background: linear-gradient(135deg, #10b981 0%%, #34d399 100%%);
                        color: white;
                        text-decoration: none;
                        padding: 15px 30px;
                        border-radius: 25px;
                        font-weight: 600;
                        font-size: 16px;
                        text-align: center;
                        transition: all 0.3s ease;
                        box-shadow: 0 8px 20px rgba(16, 185, 129, 0.3);
                        margin: 20px 0;
                    }
                    
                    .login-button:hover {
                        transform: translateY(-3px);
                        box-shadow: 0 12px 30px rgba(16, 185, 129, 0.4);
                    }
                    
                    .footer {
                        background: linear-gradient(135deg, #1e293b 0%%, #334155 100%%);
                        padding: 40px;
                        text-align: center;
                        border-top: 3px solid #10b981;
                        position: relative;
                        overflow: hidden;
                    }
                    
                    .footer::before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: -100%%;
                        width: 100%%;
                        height: 100%%;
                        background: linear-gradient(90deg, transparent, rgba(16, 185, 129, 0.2), transparent);
                        animation: footerGlow 5s ease-in-out infinite;
                    }
                    
                    @keyframes footerGlow {
                        0%% { left: -100%%; }
                        100%% { left: 100%%; }
                    }
                    
                    .footer-text {
                        color: #cbd5e1;
                        font-size: 15px;
                        margin-bottom: 20px;
                        font-weight: 500;
                        position: relative;
                        z-index: 1;
                    }
                    
                    .website-link {
                        color: #10b981;
                        text-decoration: none;
                        font-weight: 600;
                        transition: all 0.3s ease;
                        position: relative;
                        z-index: 1;
                        padding: 8px 16px;
                        border-radius: 20px;
                        background: rgba(16, 185, 129, 0.1);
                        border: 1px solid rgba(16, 185, 129, 0.3);
                    }
                    
                    .website-link:hover {
                        color: #ffffff;
                        background: rgba(16, 185, 129, 0.2);
                        transform: translateY(-2px);
                        box-shadow: 0 8px 16px rgba(16, 185, 129, 0.3);
                    }
                    
                    @media (max-width: 600px) {
                        .email-container {
                            margin: 10px;
                            border-radius: 12px;
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
                    }
                </style>
            </head>
            <body>
                <div class="email-container">
                    <div class="header">
                        <div class="security-icon">
                            <svg viewBox="0 0 24 24">
                                <path d="M12,1L3,5V11C3,16.55 6.84,21.74 12,23C17.16,21.74 21,16.55 21,11V5L12,1M10,17L6,13L7.41,11.59L10,14.17L16.59,7.58L18,9L10,17Z"/>
                            </svg>
                        </div>
                        <div class="brand-name">Wenji 智能平台</div>
                        <div class="subtitle">GlobalPulse 跨境智能运营平台</div>
                    </div>
                    
                    <div class="content">
                        <div class="greeting">尊敬的 %s：</div>
                        <div class="message">
                            您的 Wenji 智能平台账户密码已成功重置！
                        </div>
                        
                        <div class="info-box">
                            <div class="info-title">
                                <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                                    <path d="M12,2A10,10 0 0,0 2,12A10,10 0 0,0 12,22A10,10 0 0,0 22,12A10,10 0 0,0 12,2M16.2,16.2L11,13V7H12.5V12.2L17,14.7L16.2,16.2Z"/>
                                </svg>
                                重置时间
                            </div>
                            <div class="info-content">
                                %s
                            </div>
                        </div>
                        
                        <div class="security-tips">
                            <div class="tips-title">
                                <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                                    <path d="M12,1L3,5V11C3,16.55 6.84,21.74 12,23C17.16,21.74 21,16.55 21,11V5L12,1M11,7H13V9H11V7M11,11H13V17H11V11Z"/>
                                </svg>
                                安全建议
                            </div>
                            <div class="tips-content">
                                • 使用强密码（包含字母、数字和特殊字符）<br>
                                • 定期更换密码，建议每3-6个月更换一次<br>
                                • 不要在多个平台使用相同密码<br>
                                • 如果这不是您本人的操作，请立即联系客服
                            </div>
                        </div>
                        
                        <a href="https://wenji.shamillaa.com" class="login-button">
                            立即登录平台 →
                        </a>
                        
                        <div style="color: #6b7280; font-size: 14px; margin-top: 20px;">
                            如有任何问题，请随时联系我们的客服团队
                        </div>
                    </div>
                    
                    <div class="footer">
                        <div class="footer-text">
                             此邮件由系统自动发送，请勿回复
                         </div>
                         <div class="footer-text">
                             <strong>GlobalPulse 跨境智能运营平台</strong> · 
                             <a href="https://wenji.shamillaa.com" class="website-link">https://wenji.shamillaa.com</a>
                         </div>
                    </div>
                </div>
            </body>
            </html>
            """, username, java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
    
    // Send account security alert email
    public void sendSecurityAlertEmail(String to, String username, String alertType) {
        String subject = "【Wenji】账户安全警报";
        String htmlContent = createSecurityAlertEmailTemplate(username, alertType);
        sendHtmlEmail(to, subject, htmlContent);
    }
    
    // 创建安全警报邮件HTML模板
    private String createSecurityAlertEmailTemplate(String username, String alertType) {
        return String.format("""
            <!DOCTYPE html>
            <html lang="zh-CN">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>🚨 安全警报 - Wenji 智能平台</title>
                <style>
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }
                    
                    body {
                        font-family: 'SF Pro Display', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
                        line-height: 1.6;
                        color: #1a202c;
                        background: linear-gradient(135deg, #dc2626 0%%, #ef4444 50%%, #f87171 100%%);
                        min-height: 100vh;
                        padding: 20px;
                        animation: alertBackground 8s ease-in-out infinite alternate;
                    }
                    
                    @keyframes alertBackground {
                        0%% { background: linear-gradient(135deg, #dc2626 0%%, #ef4444 50%%, #f87171 100%%); }
                        100%% { background: linear-gradient(135deg, #f87171 0%%, #ef4444 50%%, #dc2626 100%%); }
                    }
                    
                    .email-container {
                        max-width: 600px;
                        margin: 0 auto;
                        background: rgba(255, 255, 255, 0.95);
                        border-radius: 20px;
                        overflow: hidden;
                        box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
                        backdrop-filter: blur(10px);
                        border: 1px solid rgba(255, 255, 255, 0.2);
                        animation: containerPulse 4s ease-in-out infinite;
                    }
                    
                    @keyframes containerPulse {
                        0%%, 100%% { transform: scale(1); box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15); }
                        50%% { transform: scale(1.02); box-shadow: 0 30px 60px rgba(220, 38, 38, 0.2); }
                    }
                    
                    .header {
                        background: linear-gradient(135deg, #7f1d1d 0%%, #991b1b 50%%, #b91c1c 100%%);
                        padding: 40px;
                        text-align: center;
                        position: relative;
                        overflow: hidden;
                    }
                    
                    .header::before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: -100%%;
                        width: 100%%;
                        height: 100%%;
                        background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.1), transparent);
                        animation: headerAlert 3s ease-in-out infinite;
                    }
                    
                    @keyframes headerAlert {
                        0%% { left: -100%%; }
                        100%% { left: 100%%; }
                    }
                    
                    .alert-icon {
                        width: 80px;
                        height: 80px;
                        margin: 0 auto 20px;
                        background: linear-gradient(135deg, #fbbf24 0%%, #f59e0b 100%%);
                        border-radius: 50%%;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        box-shadow: 0 10px 30px rgba(251, 191, 36, 0.4);
                        animation: alertPulse 2s ease-in-out infinite;
                    }
                    
                    @keyframes alertPulse {
                        0%%, 100%% { 
                            transform: scale(1); 
                            box-shadow: 0 10px 30px rgba(251, 191, 36, 0.4);
                        }
                        50%% { 
                            transform: scale(1.15); 
                            box-shadow: 0 15px 40px rgba(251, 191, 36, 0.6);
                        }
                    }
                    
                    .alert-icon svg {
                        width: 40px;
                        height: 40px;
                        fill: white;
                        animation: iconShake 1s ease-in-out infinite;
                    }
                    
                    @keyframes iconShake {
                        0%%, 100%% { transform: rotate(0deg); }
                        25%% { transform: rotate(-5deg); }
                        75%% { transform: rotate(5deg); }
                    }
                    
                    .brand-name {
                        color: #ffffff;
                        font-size: 28px;
                        font-weight: 700;
                        margin-bottom: 8px;
                        background: linear-gradient(45deg, #fbbf24, #f59e0b, #d97706);
                        -webkit-background-clip: text;
                        -webkit-text-fill-color: transparent;
                        background-clip: text;
                        animation: textFlash 3s ease-in-out infinite;
                    }
                    
                    @keyframes textFlash {
                        0%%, 100%% { opacity: 1; }
                        50%% { opacity: 0.8; }
                    }
                    
                    .subtitle {
                        color: #fecaca;
                        font-size: 16px;
                        font-weight: 500;
                    }
                    
                    .content {
                        padding: 40px;
                        position: relative;
                    }
                    
                    .content::before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: 0;
                        right: 0;
                        height: 3px;
                        background: linear-gradient(90deg, #dc2626, #ef4444, #f87171);
                        animation: borderAlert 2s ease-in-out infinite;
                    }
                    
                    @keyframes borderAlert {
                        0%%, 100%% { background-position: 0%% 50%%; }
                        50%% { background-position: 100%% 50%%; }
                    }
                    
                    .greeting {
                        font-size: 20px;
                        font-weight: 600;
                        color: #1a202c;
                        margin-bottom: 20px;
                    }
                    
                    .alert-message {
                        background: linear-gradient(135deg, #fef2f2 0%%, #fee2e2 100%%);
                        border: 2px solid #dc2626;
                        border-radius: 15px;
                        padding: 25px;
                        margin: 25px 0;
                        position: relative;
                        overflow: hidden;
                        animation: messageGlow 4s ease-in-out infinite;
                    }
                    
                    @keyframes messageGlow {
                        0%%, 100%% { border-color: #dc2626; }
                        50%% { border-color: #ef4444; }
                    }
                    
                    .alert-message::before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: -100%%;
                        width: 100%%;
                        height: 100%%;
                        background: linear-gradient(90deg, transparent, rgba(220, 38, 38, 0.1), transparent);
                        animation: messageShine 5s ease-in-out infinite;
                    }
                    
                    @keyframes messageShine {
                        0%% { left: -100%%; }
                        100%% { left: 100%%; }
                    }
                    
                    .alert-title {
                        font-size: 18px;
                        font-weight: 600;
                        color: #7f1d1d;
                        margin-bottom: 15px;
                        display: flex;
                        align-items: center;
                        gap: 10px;
                    }
                    
                    .alert-content {
                        color: #991b1b;
                        font-size: 15px;
                        line-height: 1.6;
                        position: relative;
                        z-index: 1;
                    }
                    
                    .time-info {
                        background: linear-gradient(135deg, #f3f4f6 0%%, #e5e7eb 100%%);
                        border: 2px solid #6b7280;
                        border-radius: 15px;
                        padding: 20px;
                        margin: 20px 0;
                        text-align: center;
                    }
                    
                    .time-title {
                        font-size: 16px;
                        font-weight: 600;
                        color: #374151;
                        margin-bottom: 10px;
                    }
                    
                    .time-content {
                        color: #4b5563;
                        font-size: 15px;
                        font-weight: 500;
                    }
                    
                    .action-steps {
                        background: linear-gradient(135deg, #fef3c7 0%%, #fde68a 100%%);
                        border: 2px solid #f59e0b;
                        border-radius: 15px;
                        padding: 25px;
                        margin: 25px 0;
                    }
                    
                    .steps-title {
                        font-size: 18px;
                        font-weight: 600;
                        color: #92400e;
                        margin-bottom: 15px;
                        display: flex;
                        align-items: center;
                        gap: 10px;
                    }
                    
                    .steps-content {
                        color: #a16207;
                        font-size: 15px;
                        line-height: 1.8;
                    }
                    
                    .security-button {
                        display: inline-block;
                        background: linear-gradient(135deg, #dc2626 0%%, #ef4444 100%%);
                        color: white;
                        text-decoration: none;
                        padding: 15px 30px;
                        border-radius: 25px;
                        font-weight: 600;
                        font-size: 16px;
                        text-align: center;
                        transition: all 0.3s ease;
                        box-shadow: 0 8px 20px rgba(220, 38, 38, 0.3);
                        margin: 20px 0;
                        animation: buttonPulse 3s ease-in-out infinite;
                    }
                    
                    @keyframes buttonPulse {
                        0%%, 100%% { transform: scale(1); }
                        50%% { transform: scale(1.05); }
                    }
                    
                    .security-button:hover {
                        transform: translateY(-3px) scale(1.05);
                        box-shadow: 0 12px 30px rgba(220, 38, 38, 0.4);
                    }
                    
                    .footer {
                        background: linear-gradient(135deg, #1e293b 0%%, #334155 100%%);
                        padding: 40px;
                        text-align: center;
                        border-top: 3px solid #dc2626;
                        position: relative;
                        overflow: hidden;
                    }
                    
                    .footer::before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: -100%%;
                        width: 100%%;
                        height: 100%%;
                        background: linear-gradient(90deg, transparent, rgba(220, 38, 38, 0.2), transparent);
                        animation: footerAlert 6s ease-in-out infinite;
                    }
                    
                    @keyframes footerAlert {
                        0%% { left: -100%%; }
                        100%% { left: 100%%; }
                    }
                    
                    .footer-text {
                        color: #cbd5e1;
                        font-size: 15px;
                        margin-bottom: 20px;
                        font-weight: 500;
                        position: relative;
                        z-index: 1;
                    }
                    
                    .website-link {
                        color: #dc2626;
                        text-decoration: none;
                        font-weight: 600;
                        transition: all 0.3s ease;
                        position: relative;
                        z-index: 1;
                        padding: 8px 16px;
                        border-radius: 20px;
                        background: rgba(220, 38, 38, 0.1);
                        border: 1px solid rgba(220, 38, 38, 0.3);
                    }
                    
                    .website-link:hover {
                        color: #ffffff;
                        background: rgba(220, 38, 38, 0.2);
                        transform: translateY(-2px);
                        box-shadow: 0 8px 16px rgba(220, 38, 38, 0.3);
                    }
                    
                    @media (max-width: 600px) {
                        .email-container {
                            margin: 10px;
                            border-radius: 12px;
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
                    }
                </style>
            </head>
            <body>
                <div class="email-container">
                    <div class="header">
                        <div class="alert-icon">
                            <svg viewBox="0 0 24 24">
                                <path d="M13,14H11V10H13M13,18H11V16H13M1,21H23L12,2L1,21Z"/>
                            </svg>
                        </div>
                        <div class="brand-name">Wenji 智能平台</div>
                        <div class="subtitle">安全警报系统</div>
                    </div>
                    
                    <div class="content">
                        <div class="greeting">尊敬的 %s：</div>
                        
                        <div class="alert-message">
                            <div class="alert-title">
                                <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                                    <path d="M12,2L13.09,8.26L22,9L13.09,9.74L12,16L10.91,9.74L2,9L10.91,8.26L12,2Z"/>
                                </svg>
                                检测到异常活动
                            </div>
                            <div class="alert-content">
                                我们检测到您的账户存在异常活动：<strong>%s</strong>
                            </div>
                        </div>
                        
                        <div class="time-info">
                            <div class="time-title">检测时间</div>
                            <div class="time-content">%s</div>
                        </div>
                        
                        <div class="action-steps">
                            <div class="steps-title">
                                <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                                    <path d="M12,1L3,5V11C3,16.55 6.84,21.74 12,23C17.16,21.74 21,16.55 21,11V5L12,1M11,7H13V9H11V7M11,11H13V17H11V11Z"/>
                                </svg>
                                立即采取措施
                            </div>
                            <div class="steps-content">
                                如果这不是您本人的操作，请立即采取以下措施：<br><br>
                                <strong>1. 立即修改密码</strong><br>
                                <strong>2. 检查账户登录记录</strong><br>
                                <strong>3. 联系客服团队：support@shamillaa.com</strong><br><br>
                                安全建议：<br>
                                • 使用复杂密码并定期更换<br>
                                • 启用双重验证（如可用）<br>
                                • 避免在公共网络环境下登录
                            </div>
                        </div>
                        
                        <a href="https://wenji.shamillaa.com" class="security-button">
                            立即检查账户安全 →
                        </a>
                        
                        <div style="color: #6b7280; font-size: 14px; margin-top: 20px;">
                            如有任何疑问，请立即联系我们的安全团队
                        </div>
                    </div>
                    
                    <div class="footer">
                        <div class="footer-text">
                             此邮件由安全系统自动发送，请勿回复
                         </div>
                         <div class="footer-text">
                             <strong>GlobalPulse 跨境智能运营平台</strong> · 
                             <a href="https://wenji.shamillaa.com" class="website-link">https://wenji.shamillaa.com</a>
                         </div>
                    </div>
                </div>
            </body>
            </html>
            """, username, alertType, java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
    
    // Send system notification email
    public void sendSystemNotificationEmail(String to, String username, String message) {
        String subject = "【Wenji】系统通知";
        String htmlContent = createSystemNotificationEmailTemplate(username, message);
        sendHtmlEmail(to, subject, htmlContent);
    }
    
    // 创建系统通知邮件HTML模板
    private String createSystemNotificationEmailTemplate(String username, String message) {
        return String.format("""
            <!DOCTYPE html>
            <html lang="zh-CN">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>📢 系统通知 - Wenji 智能平台</title>
                <style>
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }
                    
                    body {
                        font-family: 'SF Pro Display', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
                        line-height: 1.6;
                        color: #1a202c;
                        background: linear-gradient(135deg, #667eea 0%%, #764ba2 50%%, #f093fb 100%%);
                        min-height: 100vh;
                        padding: 20px;
                        animation: notificationBackground 10s ease-in-out infinite alternate;
                    }
                    
                    @keyframes notificationBackground {
                        0%% { background: linear-gradient(135deg, #667eea 0%%, #764ba2 50%%, #f093fb 100%%); }
                        50%% { background: linear-gradient(135deg, #f093fb 0%%, #667eea 50%%, #764ba2 100%%); }
                        100%% { background: linear-gradient(135deg, #764ba2 0%%, #f093fb 50%%, #667eea 100%%); }
                    }
                    
                    .email-container {
                        max-width: 600px;
                        margin: 0 auto;
                        background: rgba(255, 255, 255, 0.95);
                        border-radius: 20px;
                        overflow: hidden;
                        box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
                        backdrop-filter: blur(10px);
                        border: 1px solid rgba(255, 255, 255, 0.2);
                        animation: containerFloat 6s ease-in-out infinite;
                    }
                    
                    @keyframes containerFloat {
                        0%%, 100%% { transform: translateY(0px) scale(1); }
                        50%% { transform: translateY(-10px) scale(1.01); }
                    }
                    
                    .header {
                        background: linear-gradient(135deg, #4c1d95 0%%, #5b21b6 50%%, #7c3aed 100%%);
                        padding: 40px;
                        text-align: center;
                        position: relative;
                        overflow: hidden;
                    }
                    
                    .header::before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: -100%%;
                        width: 100%%;
                        height: 100%%;
                        background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.15), transparent);
                        animation: headerNotification 4s ease-in-out infinite;
                    }
                    
                    @keyframes headerNotification {
                        0%% { left: -100%%; }
                        100%% { left: 100%%; }
                    }
                    
                    .notification-icon {
                        width: 80px;
                        height: 80px;
                        margin: 0 auto 20px;
                        background: linear-gradient(135deg, #06b6d4 0%%, #0891b2 100%%);
                        border-radius: 50%%;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        box-shadow: 0 10px 30px rgba(6, 182, 212, 0.4);
                        animation: notificationPulse 3s ease-in-out infinite;
                    }
                    
                    @keyframes notificationPulse {
                        0%%, 100%% { 
                            transform: scale(1) rotate(0deg); 
                            box-shadow: 0 10px 30px rgba(6, 182, 212, 0.4);
                        }
                        50%% { 
                            transform: scale(1.1) rotate(5deg); 
                            box-shadow: 0 15px 40px rgba(6, 182, 212, 0.6);
                        }
                    }
                    
                    .notification-icon svg {
                        width: 40px;
                        height: 40px;
                        fill: white;
                        animation: iconBounce 2s ease-in-out infinite;
                    }
                    
                    @keyframes iconBounce {
                        0%%, 100%% { transform: translateY(0px); }
                        50%% { transform: translateY(-5px); }
                    }
                    
                    .brand-name {
                        color: #ffffff;
                        font-size: 28px;
                        font-weight: 700;
                        margin-bottom: 8px;
                        background: linear-gradient(45deg, #06b6d4, #0891b2, #0e7490);
                        -webkit-background-clip: text;
                        -webkit-text-fill-color: transparent;
                        background-clip: text;
                        animation: textGlow 4s ease-in-out infinite;
                    }
                    
                    @keyframes textGlow {
                        0%%, 100%% { opacity: 1; text-shadow: 0 0 10px rgba(6, 182, 212, 0.5); }
                        50%% { opacity: 0.9; text-shadow: 0 0 20px rgba(6, 182, 212, 0.8); }
                    }
                    
                    .subtitle {
                        color: #c7d2fe;
                        font-size: 16px;
                        font-weight: 500;
                    }
                    
                    .content {
                        padding: 40px;
                        position: relative;
                    }
                    
                    .content::before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: 0;
                        right: 0;
                        height: 3px;
                        background: linear-gradient(90deg, #667eea, #764ba2, #f093fb);
                        animation: borderFlow 3s ease-in-out infinite;
                    }
                    
                    @keyframes borderFlow {
                        0%%, 100%% { background-position: 0%% 50%%; }
                        50%% { background-position: 100%% 50%%; }
                    }
                    
                    .greeting {
                        font-size: 20px;
                        font-weight: 600;
                        color: #1a202c;
                        margin-bottom: 20px;
                    }
                    
                    .notification-content {
                        background: linear-gradient(135deg, #f8fafc 0%%, #f1f5f9 100%%);
                        border: 2px solid #64748b;
                        border-radius: 15px;
                        padding: 25px;
                        margin: 25px 0;
                        position: relative;
                        overflow: hidden;
                    }
                    
                    .notification-content::before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: -100%%;
                        width: 100%%;
                        height: 100%%;
                        background: linear-gradient(90deg, transparent, rgba(100, 116, 139, 0.1), transparent);
                        animation: contentShine 7s ease-in-out infinite;
                    }
                    
                    @keyframes contentShine {
                        0%% { left: -100%%; }
                        100%% { left: 100%%; }
                    }
                    
                    .content-title {
                        font-size: 18px;
                        font-weight: 600;
                        color: #334155;
                        margin-bottom: 15px;
                        display: flex;
                        align-items: center;
                        gap: 10px;
                        position: relative;
                        z-index: 1;
                    }
                    
                    .content-text {
                        color: #475569;
                        font-size: 15px;
                        line-height: 1.8;
                        position: relative;
                        z-index: 1;
                    }
                    
                    .time-info {
                        background: linear-gradient(135deg, #fef3c7 0%%, #fde68a 100%%);
                        border: 2px solid #f59e0b;
                        border-radius: 15px;
                        padding: 20px;
                        margin: 20px 0;
                        text-align: center;
                    }
                    
                    .time-title {
                        font-size: 16px;
                        font-weight: 600;
                        color: #92400e;
                        margin-bottom: 10px;
                    }
                    
                    .time-content {
                        color: #a16207;
                        font-size: 15px;
                        font-weight: 500;
                    }
                    
                    .action-button {
                        display: inline-block;
                        background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                        color: white;
                        text-decoration: none;
                        padding: 15px 30px;
                        border-radius: 25px;
                        font-weight: 600;
                        font-size: 16px;
                        text-align: center;
                        transition: all 0.3s ease;
                        box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
                        margin: 20px 0;
                        animation: buttonFloat 4s ease-in-out infinite;
                    }
                    
                    @keyframes buttonFloat {
                        0%%, 100%% { transform: translateY(0px) scale(1); }
                        50%% { transform: translateY(-3px) scale(1.02); }
                    }
                    
                    .action-button:hover {
                        transform: translateY(-5px) scale(1.05);
                        box-shadow: 0 15px 35px rgba(102, 126, 234, 0.4);
                    }
                    
                    .footer {
                        background: linear-gradient(135deg, #1e293b 0%%, #334155 100%%);
                        padding: 40px;
                        text-align: center;
                        border-top: 3px solid #667eea;
                        position: relative;
                        overflow: hidden;
                    }
                    
                    .footer::before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: -100%%;
                        width: 100%%;
                        height: 100%%;
                        background: linear-gradient(90deg, transparent, rgba(102, 126, 234, 0.2), transparent);
                        animation: footerNotification 8s ease-in-out infinite;
                    }
                    
                    @keyframes footerNotification {
                        0%% { left: -100%%; }
                        100%% { left: 100%%; }
                    }
                    
                    .footer-text {
                        color: #cbd5e1;
                        font-size: 15px;
                        margin-bottom: 20px;
                        font-weight: 500;
                        position: relative;
                        z-index: 1;
                    }
                    
                    .website-link {
                        color: #667eea;
                        text-decoration: none;
                        font-weight: 600;
                        transition: all 0.3s ease;
                        position: relative;
                        z-index: 1;
                        padding: 8px 16px;
                        border-radius: 20px;
                        background: rgba(102, 126, 234, 0.1);
                        border: 1px solid rgba(102, 126, 234, 0.3);
                    }
                    
                    .website-link:hover {
                        color: #ffffff;
                        background: rgba(102, 126, 234, 0.2);
                        transform: translateY(-2px);
                        box-shadow: 0 8px 16px rgba(102, 126, 234, 0.3);
                    }
                    
                    @media (max-width: 600px) {
                        .email-container {
                            margin: 10px;
                            border-radius: 12px;
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
                    }
                </style>
            </head>
            <body>
                <div class="email-container">
                    <div class="header">
                        <div class="notification-icon">
                            <svg viewBox="0 0 24 24">
                                <path d="M12,2A2,2 0 0,1 14,4C14,4.74 13.6,5.39 13,5.73V7A7,7 0 0,1 20,14V16A1,1 0 0,0 21,17H22V19H2V17H3A1,1 0 0,0 4,16V14A7,7 0 0,1 11,7V5.73C10.4,5.39 10,4.74 10,4A2,2 0 0,1 12,2M6,14A5,5 0 0,0 11,9V11.5A2.5,2.5 0 0,1 8.5,14H6M16.5,14A2.5,2.5 0 0,1 14,11.5V9A5,5 0 0,0 19,14H16.5Z"/>
                            </svg>
                        </div>
                        <div class="brand-name">Wenji 智能平台</div>
                        <div class="subtitle">系统通知中心</div>
                    </div>
                    
                    <div class="content">
                        <div class="greeting">尊敬的 %s：</div>
                        
                        <div class="notification-content">
                            <div class="content-title">
                                <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                                    <path d="M13,9H11V7H13M13,17H11V11H13M12,2A10,10 0 0,0 2,12A10,10 0 0,0 12,22A10,10 0 0,0 22,12A10,10 0 0,0 12,2Z"/>
                                </svg>
                                系统通知
                            </div>
                            <div class="content-text">%s</div>
                        </div>
                        
                        <div class="time-info">
                            <div class="time-title">通知时间</div>
                            <div class="time-content">%s</div>
                        </div>
                        
                        <a href="https://wenji.shamillaa.com" class="action-button">
                            前往平台查看 →
                        </a>
                        
                        <div style="color: #6b7280; font-size: 14px; margin-top: 20px;">
                            如有任何疑问，请联系我们的客服团队：support@shamillaa.com
                        </div>
                    </div>
                    
                    <div class="footer">
                        <div class="footer-text">
                             此邮件由系统自动发送，请勿回复
                         </div>
                         <div class="footer-text">
                             <strong>GlobalPulse 跨境智能运营平台</strong> · 
                             <a href="https://wenji.shamillaa.com" class="website-link">https://wenji.shamillaa.com</a>
                         </div>
                    </div>
                </div>
            </body>
            </html>
            """, username, message, java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
    
    // 发送子账号创建通知邮件
    public void sendSubAccountCreatedEmail(String to, String username, String account, String initialPassword) {
        String subject = "【Wenji】子账号创建成功通知";
        String htmlContent = createSubAccountCreatedEmailTemplate(username, account, initialPassword);
        sendHtmlEmail(to, subject, htmlContent);
    }
    
    // 创建子账号创建成功邮件HTML模板
    private String createSubAccountCreatedEmailTemplate(String username, String account, String initialPassword) {
        return String.format("""
            <!DOCTYPE html>
            <html lang="zh-CN">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>🎉 子账号创建成功 - Wenji 智能平台</title>
                <style>
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }
                    
                    body {
                        font-family: 'SF Pro Display', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
                        line-height: 1.4;
                        color: #1a202c;
                        background: linear-gradient(135deg, #667eea 0%%, #764ba2 50%%, #f093fb 100%%);
                        min-height: 100vh;
                        padding: 15px;
                        animation: accountBackground 12s ease-in-out infinite alternate;
                        font-size: 14px;
                    }
                    
                    @keyframes accountBackground {
                        0%% { background: linear-gradient(135deg, #667eea 0%%, #764ba2 50%%, #f093fb 100%%); }
                        33%% { background: linear-gradient(135deg, #f093fb 0%%, #667eea 50%%, #764ba2 100%%); }
                        66%% { background: linear-gradient(135deg, #764ba2 0%%, #f093fb 50%%, #667eea 100%%); }
                        100%% { background: linear-gradient(135deg, #667eea 0%%, #764ba2 50%%, #f093fb 100%%); }
                    }
                    
                    .email-container {
                        max-width: 550px;
                        margin: 0 auto;
                        background: rgba(255, 255, 255, 0.95);
                        border-radius: 16px;
                        overflow: hidden;
                        box-shadow: 0 20px 40px rgba(0, 0, 0, 0.12);
                        backdrop-filter: blur(10px);
                        border: 1px solid rgba(255, 255, 255, 0.2);
                        animation: containerBounce 8s ease-in-out infinite;
                    }
                    
                    @keyframes containerBounce {
                        0%%, 100%% { transform: translateY(0px) scale(1) rotate(0deg); }
                        25%% { transform: translateY(-8px) scale(1.01) rotate(0.5deg); }
                        50%% { transform: translateY(-15px) scale(1.02) rotate(0deg); }
                        75%% { transform: translateY(-8px) scale(1.01) rotate(-0.5deg); }
                    }
                    
                    .header {
                        background: linear-gradient(135deg, #059669 0%%, #047857 50%%, #065f46 100%%);
                        padding: 30px;
                        text-align: center;
                        position: relative;
                        overflow: hidden;
                    }
                    
                    .header::before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: -100%%;
                        width: 100%%;
                        height: 100%%;
                        background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
                        animation: headerSuccess 5s ease-in-out infinite;
                    }
                    
                    @keyframes headerSuccess {
                        0%% { left: -100%%; }
                        100%% { left: 100%%; }
                    }
                    
                    .account-icon {
                        width: 60px;
                        height: 60px;
                        margin: 0 auto 15px;
                        background: linear-gradient(135deg, #10b981 0%%, #059669 100%%);
                        border-radius: 50%%;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        box-shadow: 0 8px 24px rgba(16, 185, 129, 0.4);
                        animation: accountPulse 4s ease-in-out infinite;
                    }
                    
                    @keyframes accountPulse {
                        0%%, 100%% { 
                            transform: scale(1) rotate(0deg); 
                            box-shadow: 0 10px 30px rgba(16, 185, 129, 0.4);
                        }
                        50%% { 
                            transform: scale(1.15) rotate(10deg); 
                            box-shadow: 0 15px 40px rgba(16, 185, 129, 0.6);
                        }
                    }
                    
                    .account-icon svg {
                        width: 30px;
                        height: 30px;
                        fill: white;
                        animation: iconSpin 6s linear infinite;
                    }
                    
                    @keyframes iconSpin {
                        0%% { transform: rotate(0deg) scale(1); }
                        25%% { transform: rotate(90deg) scale(1.1); }
                        50%% { transform: rotate(180deg) scale(1); }
                        75%% { transform: rotate(270deg) scale(1.1); }
                        100%% { transform: rotate(360deg) scale(1); }
                    }
                    
                    .brand-name {
                        color: #ffffff;
                        font-size: 24px;
                        font-weight: 700;
                        margin-bottom: 6px;
                        background: linear-gradient(45deg, #10b981, #059669, #047857);
                        -webkit-background-clip: text;
                        -webkit-text-fill-color: transparent;
                        background-clip: text;
                        animation: brandGlow 5s ease-in-out infinite;
                    }
                    
                    @keyframes brandGlow {
                        0%%, 100%% { opacity: 1; text-shadow: 0 0 10px rgba(16, 185, 129, 0.5); }
                        50%% { opacity: 0.9; text-shadow: 0 0 20px rgba(16, 185, 129, 0.8); }
                    }
                    
                    .subtitle {
                        color: #d1fae5;
                        font-size: 14px;
                        font-weight: 500;
                    }
                    
                    .content {
                        padding: 40px;
                        position: relative;
                    }
                    
                    .content::before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: 0;
                        right: 0;
                        height: 3px;
                        background: linear-gradient(90deg, #10b981, #059669, #047857);
                        animation: contentBorder 4s ease-in-out infinite;
                    }
                    
                    @keyframes contentBorder {
                        0%%, 100%% { background-position: 0%% 50%%; }
                        50%% { background-position: 100%% 50%%; }
                    }
                    
                    .greeting {
                        font-size: 20px;
                        font-weight: 600;
                        color: #1a202c;
                        margin-bottom: 20px;
                    }
                    
                    .success-message {
                        background: linear-gradient(135deg, #d1fae5 0%%, #a7f3d0 100%%);
                        border: 2px solid #10b981;
                        border-radius: 12px;
                        padding: 20px;
                        margin: 20px 0;
                        text-align: center;
                        position: relative;
                        overflow: hidden;
                    }
                    
                    .success-message::before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: -100%%;
                        width: 100%%;
                        height: 100%%;
                        background: linear-gradient(90deg, transparent, rgba(16, 185, 129, 0.2), transparent);
                        animation: successShine 6s ease-in-out infinite;
                    }
                    
                    @keyframes successShine {
                        0%% { left: -100%%; }
                        100%% { left: 100%%; }
                    }
                    
                    .success-title {
                        font-size: 16px;
                        font-weight: 600;
                        color: #047857;
                        margin-bottom: 8px;
                        position: relative;
                        z-index: 1;
                    }
                    
                    .success-text {
                        color: #065f46;
                        font-size: 14px;
                        position: relative;
                        z-index: 1;
                    }
                    
                    .account-info {
                        background: linear-gradient(135deg, #f8fafc 0%%, #f1f5f9 100%%);
                        border: 2px solid #64748b;
                        border-radius: 15px;
                        padding: 25px;
                        margin: 25px 0;
                    }
                    
                    .info-title {
                        font-size: 18px;
                        font-weight: 600;
                        color: #334155;
                        margin-bottom: 15px;
                        display: flex;
                        align-items: center;
                        gap: 10px;
                    }
                    
                    .info-item {
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                        padding: 12px 0;
                        border-bottom: 1px solid #e2e8f0;
                    }
                    
                    .info-item:last-child {
                        border-bottom: none;
                    }
                    
                    .info-label {
                        color: #64748b;
                        font-weight: 500;
                    }
                    
                    .info-value {
                        color: #1e293b;
                        font-weight: 600;
                        font-family: 'Monaco', 'Menlo', monospace;
                        background: #f1f5f9;
                        padding: 4px 8px;
                        border-radius: 6px;
                    }
                    
                    .security-tips {
                        background: linear-gradient(135deg, #fef3c7 0%%, #fde68a 100%%);
                        border: 2px solid #f59e0b;
                        border-radius: 15px;
                        padding: 25px;
                        margin: 25px 0;
                    }
                    
                    .tips-title {
                        font-size: 16px;
                        font-weight: 600;
                        color: #92400e;
                        margin-bottom: 15px;
                        display: flex;
                        align-items: center;
                        gap: 10px;
                    }
                    
                    .tips-list {
                        list-style: none;
                        padding: 0;
                    }
                    
                    .tips-list li {
                        color: #a16207;
                        font-size: 14px;
                        margin-bottom: 8px;
                        padding-left: 20px;
                        position: relative;
                    }
                    
                    .tips-list li::before {
                        content: '🔒';
                        position: absolute;
                        left: 0;
                        top: 0;
                    }
                    
                    .action-button {
                        display: inline-block;
                        background: linear-gradient(135deg, #10b981 0%%, #059669 100%%);
                        color: white;
                        text-decoration: none;
                        padding: 15px 30px;
                        border-radius: 25px;
                        font-weight: 600;
                        font-size: 16px;
                        text-align: center;
                        transition: all 0.3s ease;
                        box-shadow: 0 8px 20px rgba(16, 185, 129, 0.3);
                        margin: 20px 0;
                        animation: buttonGlow 5s ease-in-out infinite;
                    }
                    
                    @keyframes buttonGlow {
                        0%%, 100%% { 
                            transform: translateY(0px) scale(1); 
                            box-shadow: 0 8px 20px rgba(16, 185, 129, 0.3);
                        }
                        50%% { 
                            transform: translateY(-3px) scale(1.02); 
                            box-shadow: 0 12px 30px rgba(16, 185, 129, 0.5);
                        }
                    }
                    
                    .action-button:hover {
                        transform: translateY(-5px) scale(1.05);
                        box-shadow: 0 15px 35px rgba(16, 185, 129, 0.4);
                    }
                    
                    .footer {
                        background: linear-gradient(135deg, #1e293b 0%%, #334155 100%%);
                        padding: 40px;
                        text-align: center;
                        border-top: 3px solid #10b981;
                        position: relative;
                        overflow: hidden;
                    }
                    
                    .footer::before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: -100%%;
                        width: 100%%;
                        height: 100%%;
                        background: linear-gradient(90deg, transparent, rgba(16, 185, 129, 0.2), transparent);
                        animation: footerAccount 9s ease-in-out infinite;
                    }
                    
                    @keyframes footerAccount {
                        0%% { left: -100%%; }
                        100%% { left: 100%%; }
                    }
                    
                    .footer-text {
                        color: #cbd5e1;
                        font-size: 15px;
                        margin-bottom: 20px;
                        font-weight: 500;
                        position: relative;
                        z-index: 1;
                    }
                    
                    .website-link {
                        color: #10b981;
                        text-decoration: none;
                        font-weight: 600;
                        transition: all 0.3s ease;
                        position: relative;
                        z-index: 1;
                        padding: 8px 16px;
                        border-radius: 20px;
                        background: rgba(16, 185, 129, 0.1);
                        border: 1px solid rgba(16, 185, 129, 0.3);
                    }
                    
                    .website-link:hover {
                        color: #ffffff;
                        background: rgba(16, 185, 129, 0.2);
                        transform: translateY(-2px);
                        box-shadow: 0 8px 16px rgba(16, 185, 129, 0.3);
                    }
                    
                    @media (max-width: 600px) {
                        .email-container {
                            margin: 10px;
                            border-radius: 12px;
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
                    }
                </style>
            </head>
            <body>
                <div class="email-container">
                    <div class="header">
                        <div class="account-icon">
                            <svg viewBox="0 0 24 24">
                                <path d="M12,4A4,4 0 0,1 16,8A4,4 0 0,1 12,12A4,4 0 0,1 8,8A4,4 0 0,1 12,4M12,14C16.42,14 20,15.79 20,18V20H4V18C4,15.79 7.58,14 12,14Z"/>
                            </svg>
                        </div>
                        <div class="brand-name">Wenji 智能平台</div>
                        <div class="subtitle">子账号管理中心</div>
                    </div>
                    
                    <div class="content">
                        <div class="greeting">尊敬的 %s：</div>
                        
                        <div class="success-message">
                            <div class="success-title">🎉 子账号创建成功！</div>
                            <div class="success-text">您的 Wenji 智能平台子账号已成功创建并激活</div>
                        </div>
                        
                        <div class="account-info">
                            <div class="info-title">
                                <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                                    <path d="M12,4A4,4 0 0,1 16,8A4,4 0 0,1 12,12A4,4 0 0,1 8,8A4,4 0 0,1 12,4M12,14C16.42,14 20,15.79 20,18V20H4V18C4,15.79 7.58,14 12,14Z"/>
                                </svg>
                                账号信息
                            </div>
                            <div class="info-item">
                                <span class="info-label">登录账号：</span>
                                <span class="info-value">%s</span>
                            </div>
                            <div class="info-item">
                                <span class="info-label">登录密码：</span>
                                <span class="info-value">%s</span>
                            </div>
                            <div class="info-item">
                                <span class="info-label">创建时间：</span>
                                <span class="info-value">%s</span>
                            </div>
                        </div>
                        
                        <div class="security-tips">
                            <div class="tips-title">
                                <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                                    <path d="M12,1L3,5V11C3,16.55 6.84,21.74 12,23C17.16,21.74 21,16.55 21,11V5L12,1M12,7C13.4,7 14.8,8.6 14.8,10V11H16V16H8V11H9.2V10C9.2,8.6 10.6,7 12,7M12,8.2C11.2,8.2 10.4,8.7 10.4,10V11H13.6V10C13.6,8.7 12.8,8.2 12,8.2Z"/>
                                </svg>
                                重要安全提醒
                            </div>
                            <ul class="tips-list">
                                <li>请妥善保管您的登录信息</li>
                                <li>首次登录后请立即修改密码</li>
                                <li>建议使用强密码（包含字母、数字和特殊字符）</li>
                            </ul>
                        </div>
                        
                        <a href="https://wenji.shamillaa.com" class="action-button">
                            立即登录平台 →
                        </a>
                        
                        <div style="color: #6b7280; font-size: 14px; margin-top: 20px;">
                            如有任何问题，请联系我们的客服团队：support@shamillaa.com
                        </div>
                    </div>
                    
                    <div class="footer">
                        <div class="footer-text">
                             此邮件由系统自动发送，请勿回复
                         </div>
                         <div class="footer-text">
                             <strong>GlobalPulse 跨境智能运营平台</strong> · 
                             <a href="https://wenji.shamillaa.com" class="website-link">https://wenji.shamillaa.com</a>
                         </div>
                    </div>
                </div>
            </body>
            </html>
            """, username, account, initialPassword, java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
    
    // 发送解除子账号关联通知邮件
    public void sendSubAccountUnlinkedEmail(String to, String username) {
        String subject = "【Wenji】子账号关联解除通知";
        String htmlContent = createSubAccountUnlinkedEmailTemplate(username);
        sendHtmlEmail(to, subject, htmlContent);
    }
    
    // 创建子账号关联解除邮件HTML模板
    private String createSubAccountUnlinkedEmailTemplate(String username) {
        return String.format("""
            <!DOCTYPE html>
            <html lang="zh-CN">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>⚠️ 子账号关联解除 - Wenji 智能平台</title>
                <style>
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }
                    
                    body {
                        font-family: 'SF Pro Display', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
                        line-height: 1.6;
                        color: #1a202c;
                        background: linear-gradient(135deg, #ef4444 0%%, #dc2626 50%%, #b91c1c 100%%);
                        min-height: 100vh;
                        padding: 20px;
                        animation: warningBackground 8s ease-in-out infinite alternate;
                    }
                    
                    @keyframes warningBackground {
                        0%% { background: linear-gradient(135deg, #ef4444 0%%, #dc2626 50%%, #b91c1c 100%%); }
                        50%% { background: linear-gradient(135deg, #b91c1c 0%%, #ef4444 50%%, #dc2626 100%%); }
                        100%% { background: linear-gradient(135deg, #dc2626 0%%, #b91c1c 50%%, #ef4444 100%%); }
                    }
                    
                    .email-container {
                        max-width: 600px;
                        margin: 0 auto;
                        background: rgba(255, 255, 255, 0.95);
                        border-radius: 20px;
                        overflow: hidden;
                        box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
                        backdrop-filter: blur(10px);
                        border: 1px solid rgba(255, 255, 255, 0.2);
                        animation: containerShake 4s ease-in-out infinite;
                    }
                    
                    @keyframes containerShake {
                        0%%, 100%% { transform: translateX(0px) scale(1); }
                        25%% { transform: translateX(-2px) scale(1.005); }
                        50%% { transform: translateX(2px) scale(1.01); }
                        75%% { transform: translateX(-1px) scale(1.005); }
                    }
                    
                    .header {
                        background: linear-gradient(135deg, #dc2626 0%%, #b91c1c 50%%, #991b1b 100%%);
                        padding: 40px;
                        text-align: center;
                        position: relative;
                        overflow: hidden;
                    }
                    
                    .header::before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: -100%%;
                        width: 100%%;
                        height: 100%%;
                        background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.15), transparent);
                        animation: headerWarning 3s ease-in-out infinite;
                    }
                    
                    @keyframes headerWarning {
                        0%% { left: -100%%; }
                        100%% { left: 100%%; }
                    }
                    
                    .warning-icon {
                        width: 80px;
                        height: 80px;
                        margin: 0 auto 20px;
                        background: linear-gradient(135deg, #f59e0b 0%%, #d97706 100%%);
                        border-radius: 50%%;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        box-shadow: 0 10px 30px rgba(245, 158, 11, 0.4);
                        animation: warningPulse 2s ease-in-out infinite;
                    }
                    
                    @keyframes warningPulse {
                        0%%, 100%% { 
                            transform: scale(1); 
                            box-shadow: 0 10px 30px rgba(245, 158, 11, 0.4);
                        }
                        50%% { 
                            transform: scale(1.1); 
                            box-shadow: 0 15px 40px rgba(245, 158, 11, 0.6);
                        }
                    }
                    
                    .warning-icon svg {
                        width: 40px;
                        height: 40px;
                        fill: white;
                        animation: iconBlink 1.5s ease-in-out infinite;
                    }
                    
                    @keyframes iconBlink {
                        0%%, 100%% { opacity: 1; }
                        50%% { opacity: 0.7; }
                    }
                    
                    .brand-name {
                        color: #ffffff;
                        font-size: 28px;
                        font-weight: 700;
                        margin-bottom: 8px;
                        background: linear-gradient(45deg, #f59e0b, #d97706, #b45309);
                        -webkit-background-clip: text;
                        -webkit-text-fill-color: transparent;
                        background-clip: text;
                        animation: textAlert 3s ease-in-out infinite;
                    }
                    
                    @keyframes textAlert {
                        0%%, 100%% { opacity: 1; text-shadow: 0 0 10px rgba(245, 158, 11, 0.5); }
                        50%% { opacity: 0.9; text-shadow: 0 0 20px rgba(245, 158, 11, 0.8); }
                    }
                    
                    .subtitle {
                        color: #fed7aa;
                        font-size: 16px;
                        font-weight: 500;
                    }
                    
                    .content {
                        padding: 40px;
                        position: relative;
                    }
                    
                    .content::before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: 0;
                        right: 0;
                        height: 3px;
                        background: linear-gradient(90deg, #ef4444, #dc2626, #b91c1c);
                        animation: contentAlert 2s ease-in-out infinite;
                    }
                    
                    @keyframes contentAlert {
                        0%%, 100%% { background-position: 0%% 50%%; }
                        50%% { background-position: 100%% 50%%; }
                    }
                    
                    .greeting {
                        font-size: 20px;
                        font-weight: 600;
                        color: #1a202c;
                        margin-bottom: 20px;
                    }
                    
                    .warning-message {
                        background: linear-gradient(135deg, #fef2f2 0%%, #fecaca 100%%);
                        border: 2px solid #ef4444;
                        border-radius: 15px;
                        padding: 25px;
                        margin: 25px 0;
                        position: relative;
                        overflow: hidden;
                    }
                    
                    .warning-message::before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: -100%%;
                        width: 100%%;
                        height: 100%%;
                        background: linear-gradient(90deg, transparent, rgba(239, 68, 68, 0.1), transparent);
                        animation: warningShine 4s ease-in-out infinite;
                    }
                    
                    @keyframes warningShine {
                        0%% { left: -100%%; }
                        100%% { left: 100%%; }
                    }
                    
                    .warning-title {
                        font-size: 18px;
                        font-weight: 600;
                        color: #dc2626;
                        margin-bottom: 15px;
                        display: flex;
                        align-items: center;
                        gap: 10px;
                        position: relative;
                        z-index: 1;
                    }
                    
                    .warning-text {
                        color: #b91c1c;
                        font-size: 15px;
                        line-height: 1.8;
                        position: relative;
                        z-index: 1;
                    }
                    
                    .time-info {
                        background: linear-gradient(135deg, #f3f4f6 0%%, #e5e7eb 100%%);
                        border: 2px solid #6b7280;
                        border-radius: 15px;
                        padding: 20px;
                        margin: 20px 0;
                        text-align: center;
                    }
                    
                    .time-title {
                        font-size: 16px;
                        font-weight: 600;
                        color: #374151;
                        margin-bottom: 10px;
                    }
                    
                    .time-content {
                        color: #4b5563;
                        font-size: 15px;
                        font-weight: 500;
                    }
                    
                    .important-notes {
                        background: linear-gradient(135deg, #fef3c7 0%%, #fde68a 100%%);
                        border: 2px solid #f59e0b;
                        border-radius: 15px;
                        padding: 25px;
                        margin: 25px 0;
                    }
                    
                    .notes-title {
                        font-size: 16px;
                        font-weight: 600;
                        color: #92400e;
                        margin-bottom: 15px;
                        display: flex;
                        align-items: center;
                        gap: 10px;
                    }
                    
                    .notes-list {
                        list-style: none;
                        padding: 0;
                    }
                    
                    .notes-list li {
                        color: #a16207;
                        font-size: 14px;
                        margin-bottom: 8px;
                        padding-left: 20px;
                        position: relative;
                    }
                    
                    .notes-list li::before {
                        content: '•';
                        position: absolute;
                        left: 0;
                        top: 0;
                        color: #f59e0b;
                        font-weight: bold;
                    }
                    
                    .action-button {
                        display: inline-block;
                        background: linear-gradient(135deg, #ef4444 0%%, #dc2626 100%%);
                        color: white;
                        text-decoration: none;
                        padding: 15px 30px;
                        border-radius: 25px;
                        font-weight: 600;
                        font-size: 16px;
                        text-align: center;
                        transition: all 0.3s ease;
                        box-shadow: 0 8px 20px rgba(239, 68, 68, 0.3);
                        margin: 20px 0;
                        animation: buttonAlert 3s ease-in-out infinite;
                    }
                    
                    @keyframes buttonAlert {
                        0%%, 100%% { 
                            transform: translateY(0px) scale(1); 
                            box-shadow: 0 8px 20px rgba(239, 68, 68, 0.3);
                        }
                        50%% { 
                            transform: translateY(-3px) scale(1.02); 
                            box-shadow: 0 12px 30px rgba(239, 68, 68, 0.5);
                        }
                    }
                    
                    .action-button:hover {
                        transform: translateY(-5px) scale(1.05);
                        box-shadow: 0 15px 35px rgba(239, 68, 68, 0.4);
                    }
                    
                    .footer {
                        background: linear-gradient(135deg, #1e293b 0%%, #334155 100%%);
                        padding: 40px;
                        text-align: center;
                        border-top: 3px solid #ef4444;
                        position: relative;
                        overflow: hidden;
                    }
                    
                    .footer::before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: -100%%;
                        width: 100%%;
                        height: 100%%;
                        background: linear-gradient(90deg, transparent, rgba(239, 68, 68, 0.2), transparent);
                        animation: footerWarning 6s ease-in-out infinite;
                    }
                    
                    @keyframes footerWarning {
                        0%% { left: -100%%; }
                        100%% { left: 100%%; }
                    }
                    
                    .footer-text {
                        color: #cbd5e1;
                        font-size: 15px;
                        margin-bottom: 20px;
                        font-weight: 500;
                        position: relative;
                        z-index: 1;
                    }
                    
                    .website-link {
                        color: #ef4444;
                        text-decoration: none;
                        font-weight: 600;
                        transition: all 0.3s ease;
                        position: relative;
                        z-index: 1;
                        padding: 8px 16px;
                        border-radius: 20px;
                        background: rgba(239, 68, 68, 0.1);
                        border: 1px solid rgba(239, 68, 68, 0.3);
                    }
                    
                    .website-link:hover {
                        color: #ffffff;
                        background: rgba(239, 68, 68, 0.2);
                        transform: translateY(-2px);
                        box-shadow: 0 8px 16px rgba(239, 68, 68, 0.3);
                    }
                    
                    @media (max-width: 600px) {
                        .email-container {
                            margin: 10px;
                            border-radius: 12px;
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
                    }
                </style>
            </head>
            <body>
                <div class="email-container">
                    <div class="header">
                        <div class="warning-icon">
                            <svg viewBox="0 0 24 24">
                                <path d="M13,14H11V10H13M13,18H11V16H13M1,21H23L12,2L1,21Z"/>
                            </svg>
                        </div>
                        <div class="brand-name">Wenji 智能平台</div>
                        <div class="subtitle">账号安全中心</div>
                    </div>
                    
                    <div class="content">
                        <div class="greeting">尊敬的 %s：</div>
                        
                        <div class="warning-message">
                            <div class="warning-title">
                                <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                                    <path d="M13,14H11V10H13M13,18H11V16H13M1,21H23L12,2L1,21Z"/>
                                </svg>
                                子账号关联已解除
                            </div>
                            <div class="warning-text">您的 Wenji 智能平台子账号已与原主账号解除关联。</div>
                        </div>
                        
                        <div class="time-info">
                            <div class="time-title">解除时间</div>
                            <div class="time-content">%s</div>
                        </div>
                        
                        <div class="important-notes">
                            <div class="notes-title">
                                <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                                    <path d="M11,9H13V7H11M12,20C7.59,20 4,16.41 4,12C4,7.59 7.59,4 12,4C16.41,4 20,7.59 20,12C20,16.41 16.41,20 12,20M12,2A10,10 0 0,0 2,12A10,10 0 0,0 12,22A10,10 0 0,0 22,12A10,10 0 0,0 12,2M11,17H13V11H11V17Z"/>
                                </svg>
                                重要说明
                            </div>
                            <ul class="notes-list">
                                <li>解除关联后，您将无法使用原有的子账号权限</li>
                                <li>如需继续使用平台服务，请联系管理员重新关联</li>
                                <li>或者您可以注册新的独立账号</li>
                            </ul>
                        </div>
                        
                        <div style="background: linear-gradient(135deg, #fef2f2 0%%, #fecaca 100%%); border: 2px solid #ef4444; border-radius: 15px; padding: 20px; margin: 20px 0; text-align: center;">
                            <div style="color: #dc2626; font-weight: 600; margin-bottom: 10px;">如果这不是您本人的操作</div>
                            <div style="color: #b91c1c; font-size: 14px;">请立即联系客服团队进行安全检查</div>
                        </div>
                        
                        <a href="https://wenji.shamillaa.com" class="action-button">
                            联系客服团队 →
                        </a>
                        
                        <div style="color: #6b7280; font-size: 14px; margin-top: 20px;">
                            客服邮箱：support@shamillaa.com<br>
                            官方网站：https://wenji.shamillaa.com
                        </div>
                    </div>
                    
                    <div class="footer">
                        <div class="footer-text">
                             此邮件由系统自动发送，请勿回复
                         </div>
                         <div class="footer-text">
                             <strong>GlobalPulse 跨境智能运营平台</strong> · 
                             <a href="https://wenji.shamillaa.com" class="website-link">https://wenji.shamillaa.com</a>
                         </div>
                    </div>
                </div>
            </body>
            </html>
            """, username, java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}