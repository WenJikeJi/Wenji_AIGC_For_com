package com.wenji.server.controller;

import com.wenji.server.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthService authService;
    
    @Operation(
            summary = "用户登录",
            description = "用户通过账号和密码进行登录认证，返回JWT令牌用于后续接口访问",
            tags = {"认证"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "登录成功", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(example = "{\"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\", \"userId\": 1, \"username\": \"张三\", \"role\": 0}"))),
            @ApiResponse(responseCode = "400", description = "账号或密码错误")
    })
    // 登录接口 - 添加@CrossOrigin注解强制启用跨域配置
    @PostMapping("/login")
    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    public ResponseEntity<?> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "登录请求参数",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{\"account\": \"admin\", \"encryptedPassword\": \"encrypted_password\", \"role\": 0}" // 0: 主账号, 1: 子账号
                            )
                    )
            )
            @RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
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
    
    @Operation(
            summary = "用户注册",
            description = "用户注册新账号，需要先获取验证码进行验证",
            tags = {"认证"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "注册成功", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\": \"注册成功\"}"))),
            @ApiResponse(responseCode = "400", description = "注册失败，可能是账号已存在、邮箱已存在或验证码错误")
    })
    // 注册接口
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "注册请求参数",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{\"username\": \"张三\", \"account\": \"zhangsan\", \"email\": \"zhangsan@example.com\", \"encryptedPassword\": \"encrypted_password\", \"code\": \"123456\"}"
                            )
                    )
            )
            @RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
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
    
    @Operation(
            summary = "忘记密码",
            description = "用户忘记密码时，通过注册邮箱获取密码重置验证码",
            tags = {"认证"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "验证码已发送"),
            @ApiResponse(responseCode = "400", description = "该邮箱未注册或其他错误")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "忘记密码请求参数",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{\"email\": \"user@example.com\"}"
                            )
                    )
            )
            @RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
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
    
    @Operation(
            summary = "发送验证码",
            description = "发送验证码到用户邮箱，支持注册验证和密码重置两种类型",
            tags = {"验证码"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "验证码已发送", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\": \"验证码已发送到您的邮箱\"}"))),
            @ApiResponse(responseCode = "400", description = "验证码发送失败或邮箱格式错误")
    })
    // 发送验证码接口
    @PostMapping("/send-verification-code")
    public ResponseEntity<?> sendVerificationCode(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "发送验证码请求参数",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{\"email\": \"user@example.com\", \"type\": 1}" // 1: 注册验证, 2: 密码重置
                            )
                    )
            )
            @RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
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

    @Operation(
            summary = "重置密码",
            description = "使用验证码重置用户密码",
            tags = {"认证"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "密码重置成功"),
            @ApiResponse(responseCode = "400", description = "验证码错误或其他参数错误")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "重置密码请求参数",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{\"email\": \"user@example.com\", \"code\": \"123456\", \"encryptedPassword\": \"encrypted_new_password\"}"
                            )
                    )
            )
            @RequestBody Map<String, Object> requestBody) {
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