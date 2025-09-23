package com.wenji.server.controller;

import com.wenji.server.service.AuthService;
import com.wenji.server.service.CaptchaService;
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
import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private CaptchaService captchaService;
    
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
    // 登录接口
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "登录请求参数",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{\"account\": \"admin\", \"encryptedPassword\": \"encrypted_password\", \"role\": 0, \"verificationCode\": \"1234\", \"captchaId\": \"uuid\"}" // 0: 主账号, 1: 子账号
                            )
                    )
            )
            @RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
        try {
            log.info("收到登录请求: {}", requestBody);
            String account = (String) requestBody.get("account");
            String encryptedPassword = (String) requestBody.get("encryptedPassword");
            Integer role = (Integer) requestBody.get("role");
            String verificationCode = (String) requestBody.get("verificationCode");
            String captchaId = (String) requestBody.get("captchaId");
            
            // 参数验证
            if (account == null || account.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "账号不能为空", "code", 400));
            }
            if (encryptedPassword == null || encryptedPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "密码不能为空", "code", 400));
            }
            if (role == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "角色参数不能为空", "code", 400));
            }
            
            // 验证图形验证码（如果提供了）
            if (verificationCode != null && captchaId != null) {
                boolean captchaValid = captchaService.verifyCaptcha(captchaId, verificationCode);
                if (!captchaValid) {
                    log.warn("图形验证码验证失败 - 账号: {}, 验证码: {}, ID: {}", account, verificationCode, captchaId);
                    return ResponseEntity.badRequest().body(Map.of("error", "验证码错误或已过期", "code", 400));
                }
                log.info("图形验证码验证成功 - 账号: {}", account);
            }
            
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
                                    example = "{\"username\": \"张三\", \"account\": \"zhangsan\", \"email\": \"zhangsan@example.com\", \"encryptedPassword\": \"encrypted_password\", \"encryptedConfirmPassword\": \"encrypted_confirm_password\", \"agreeTerms\": true, \"code\": \"123456\"}"
                            )
                    )
            )
            @RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
        try {
            String username = (String) requestBody.get("username");
            String account = (String) requestBody.get("account");
            String email = (String) requestBody.get("email");
            String encryptedPassword = (String) requestBody.get("encryptedPassword");
            String encryptedConfirmPassword = (String) requestBody.get("encryptedConfirmPassword");
            Boolean agreeTerms = (Boolean) requestBody.get("agreeTerms");
            String code = (String) requestBody.get("code");
            
            // 验证必要参数
            if (agreeTerms == null || !agreeTerms) {
                return ResponseEntity.badRequest().body(Map.of("error", "必须同意服务条款"));
            }
            
            // 获取客户端IP
            String clientIp = getClientIp(request);
            
            authService.register(username, account, email, encryptedPassword, encryptedConfirmPassword, agreeTerms, code, clientIp);
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
            Object typeObj = requestBody.get("type");
            Integer type = null;
            
            // 处理type参数的类型转换
            if (typeObj != null) {
                if (typeObj instanceof String) {
                    type = Integer.parseInt((String) typeObj);
                } else if (typeObj instanceof Integer) {
                    type = (Integer) typeObj;
                } else if (typeObj instanceof Number) {
                    type = ((Number) typeObj).intValue();
                }
            }
            
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
            
            // 调用服务发送验证码（注意：即使邮件发送失败，服务层也会返回成功，验证码会保存到数据库）
            authService.sendVerificationCode(email, type, clientIp);
            
            // 构建返回结果，包含提示信息
            Map<String, Object> result = new HashMap<>();
            result.put("message", "验证码已生成，我们会尽力发送到您的邮箱");
            result.put("status", "success");
            result.put("type", type);
            result.put("email", email);
            
            // 添加备用方案提示
            result.put("alternativeMethod", "如果未收到邮件，您可以通过系统管理工具从数据库获取验证码");
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // 记录异常信息
            log.error("发送验证码失败: {}", e.getMessage(), e);
            
            // 构建错误响应
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("status", "failed");
            if (requestBody != null) {
                String email = (String) requestBody.get("email");
                errorResponse.put("email", email);
            }
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            // 添加技术支持信息
            errorResponse.put("supportInfo", "请检查您的邮箱是否正确，或联系系统管理员获取帮助");
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @Operation(
            summary = "注册发送验证码",
            description = "为用户注册发送验证码到邮箱",
            tags = {"认证"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "验证码已发送", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\": \"验证码已生成，我们会尽力发送到您的邮箱\", \"status\": \"success\", \"type\": 1, \"email\": \"user@example.com\"}"))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"错误信息\", \"status\": \"failed\", \"email\": \"user@example.com\", \"timestamp\": 1684567890000}")))
    })
    // 注册发送验证码接口
    @PostMapping("/register/send-code")
    public ResponseEntity<?> registerSendCode(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "注册发送验证码请求参数",
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
            Integer type = 1; // 注册验证码类型
            
            // 获取客户端IP
            String clientIp = getClientIp(request);
            
            log.info("收到注册发送验证码请求 - 邮箱: {}, IP: {}", email, clientIp);
            
            // 调用服务发送验证码
            authService.sendVerificationCode(email, type, clientIp);
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("message", "验证码已生成，我们会尽力发送到您的邮箱");
            result.put("status", "success");
            result.put("type", type);
            result.put("email", email);
            result.put("alternativeMethod", "如果未收到邮件，您可以通过系统管理工具从数据库获取验证码");
            
            log.info("注册验证码发送成功 - 邮箱: {}", email);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // 记录异常信息
            log.error("注册发送验证码失败: {}", e.getMessage(), e);
            
            // 构建错误响应
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("status", "failed");
            if (requestBody != null) {
                String email = (String) requestBody.get("email");
                errorResponse.put("email", email);
            }
            errorResponse.put("timestamp", System.currentTimeMillis());
            errorResponse.put("supportInfo", "请检查您的邮箱是否正确，或联系系统管理员获取帮助");
            
            return ResponseEntity.status(500).body(errorResponse);
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
                                    example = "{\"email\": \"user@example.com\", \"code\": \"123456\", \"encryptedPassword\": \"encrypted_new_password\", \"newPassword\": \"plain_new_password\"}"
                            )
                    )
            )
            @RequestBody Map<String, Object> requestBody) {
        try {
            String email = (String) requestBody.get("email");
            String code = (String) requestBody.get("code");
            String encryptedPassword = (String) requestBody.get("encryptedPassword");
            String newPassword = (String) requestBody.get("newPassword");
            
            authService.resetPassword(email, code, encryptedPassword, newPassword);
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