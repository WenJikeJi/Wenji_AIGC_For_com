package com.wenji.server.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailUtil {
    
    @Autowired
    private JavaMailSender mailSender;
    
    // 发送简单邮件
    public void sendSimpleEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        // 设置发件人（可以从配置中读取）
        message.setFrom("service@shamillaa.com");
        
        mailSender.send(message);
    }
    
    // 发送验证码邮件
    public void sendVerificationCode(String to, String code, Integer type) {
        String subject = type == 1 ? "【Wenji】注册验证码" : "【Wenji】密码重置验证码";
        String content = "您的验证码是：" + code + "，有效期15分钟，请尽快使用，请勿泄露给他人。\n\n" +
                         "—— Wenji平台";
        
        sendSimpleEmail(to, subject, content);
    }
    
    // 发送注册成功邮件
    public void sendRegisterSuccessEmail(String to, String username) {
        String subject = "【Wenji】注册成功通知";
        String content = "尊敬的" + username + "：\n\n" +
                         "恭喜您成功注册Wenji平台账号！\n" +
                         "您可以使用注册邮箱登录平台，享受我们提供的各项服务。\n\n" +
                         "—— Wenji平台";
        
        sendSimpleEmail(to, subject, content);
    }
    
    // 发送密码重置成功邮件
    public void sendPasswordResetSuccessEmail(String to, String username) {
        String subject = "【Wenji】密码重置成功通知";
        String content = "尊敬的" + username + "：\n\n" +
                         "您的账号密码已成功重置！\n" +
                         "如果不是您本人操作，请立即联系客服。\n\n" +
                         "—— Wenji平台";
        
        sendSimpleEmail(to, subject, content);
    }
    
    // 发送子账号创建通知邮件
    public void sendSubAccountCreatedEmail(String to, String username, String account, String initialPassword) {
        String subject = "【Wenji】子账号创建通知";
        String content = "尊敬的" + username + "：\n\n" +
                         "您的Wenji平台子账号已创建成功！\n" +
                         "账号：" + account + "\n" +
                         "初始密码：" + initialPassword + "\n" +
                         "请使用上述信息登录平台，并尽快修改密码。\n\n" +
                         "—— Wenji平台";
        
        sendSimpleEmail(to, subject, content);
    }
    
    // 发送解除子账号关联通知邮件
    public void sendSubAccountUnlinkedEmail(String to, String username) {
        String subject = "【Wenji】子账号关联已解除";
        String content = "尊敬的" + username + "：\n\n" +
                         "您的Wenji平台子账号已与原主账号解除关联！\n" +
                         "如需继续使用平台服务，请联系管理员重新关联或注册新账号。\n\n" +
                         "—— Wenji平台";
        
        sendSimpleEmail(to, subject, content);
    }
}