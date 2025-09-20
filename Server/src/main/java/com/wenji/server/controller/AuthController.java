package com.wenji.server.controller;

import com.wenji.server.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthService authService;
    
    // 登录接口 - 添加@CrossOrigin注解强制启用跨域配置
    @PostMapping("/login")
    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
        try {
            log.info("收到登录请求: {}", requestBody);
            String account = (String) requestBody.get("account");
            String encryptedPassword = (String) requestBody.get("encryptedPassword");
            Integer role = (Integer) requestBody.get("role");
            
            log.info("登录请求参数 - 账号: {}, 角色: {}", account, role);
            
            // 获取客户端IP
            String clientIp = getClientIp(request);
            log.info("客户端IP: {}", clientIp);
            
            Map<String, Object> result = authService.login(account, encryptedPassword, role, clientIp);
            log.info("登录成功，返回结果: {}", result.get("token") != null ? "已生成token" : "无token");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("登录失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage(), "code", 400));
        }
    }
    
    // 注册接口
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
        try {
            String username = (String) requestBody.get("username");
            String account = (String) requestBody.get("account");
            String email = (String) requestBody.get("email");
            String encryptedPassword = (String) requestBody.get("encryptedPassword");
            String code = (String) requestBody.get("code");
            
            // 获取客户端IP
            String clientIp = getClientIp(request);
            
            authService.register(username, account, email, encryptedPassword, code, clientIp);
            return ResponseEntity.ok(Map.of("message", "注册成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // 忘记密码接口
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
        try {
            String email = (String) requestBody.get("email");
            Integer type = 2; // 密码重置类型
            
            // 获取客户端IP
            String clientIp = getClientIp(request);
            
            authService.sendVerificationCode(email, type, clientIp);
            return ResponseEntity.ok(Map.of("message", "验证码已发送"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // 发送验证码接口
    @PostMapping("/send-verification-code")
    public ResponseEntity<?> sendVerificationCode(@RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
        try {
            String email = (String) requestBody.get("email");
            Integer type = (Integer) requestBody.get("type");
            
            // 如果没有指定type，默认为注册验证码
            if (type == null) {
                type = 1;
            }
            
            // 验证type参数
            if (type != 1 && type != 2) {
                throw new RuntimeException("验证码类型错误");
            }
            
            // 获取客户端IP
            String clientIp = getClientIp(request);
            
            authService.sendVerificationCode(email, type, clientIp);
            return ResponseEntity.ok(Map.of("message", "验证码已发送到您的邮箱"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, Object> requestBody) {
        try {
            String email = (String) requestBody.get("email");
            String code = (String) requestBody.get("code");
            String encryptedPassword = (String) requestBody.get("encryptedPassword");
            
            authService.resetPassword(email, code, encryptedPassword);
            return ResponseEntity.ok(Map.of("message", "密码重置成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // 修改密码接口（已登录状态）
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
        try {
            // 从JWT中获取用户ID
            // 这里简化处理，实际应该从SecurityContextHolder中获取认证信息
            String userIdStr = request.getAttribute("userId").toString();
            Long userId = Long.parseLong(userIdStr);
            
            String encryptedOldPassword = (String) requestBody.get("encryptedOldPassword");
            String encryptedNewPassword = (String) requestBody.get("encryptedNewPassword");
            
            authService.changePassword(userId, encryptedOldPassword, encryptedNewPassword);
            return ResponseEntity.ok(Map.of("message", "密码修改成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // 获取客户端IP
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果是多级代理，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}