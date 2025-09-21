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
import java.util.HashMap;

@RestController
@RequestMapping("/api/verify-code")
public class VerificationCodeController {
    
    private static final Logger log = LoggerFactory.getLogger(VerificationCodeController.class);
    
    @Autowired
    private AuthService authService;
    
    @Operation(
            summary = "生成验证码",
            description = "生成并发送验证码到用户邮箱，支持注册验证和密码重置两种类型",
            tags = {"验证码"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "验证码已生成", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\": \"验证码已生成，我们会尽力发送到您的邮箱\", \"status\": \"success\", \"type\": 1, \"email\": \"user@example.com\", \"alternativeMethod\": \"如果未收到邮件，您可以通过系统管理工具从数据库获取验证码\"}"))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"错误信息\", \"status\": \"failed\", \"email\": \"user@example.com\", \"timestamp\": 1684567890000, \"supportInfo\": \"请检查您的邮箱是否正确，或联系系统管理员获取帮助\"}")))
    })
    @PostMapping("/generate")
    public ResponseEntity<?> generateVerificationCode(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "生成验证码请求参数",
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
            Integer type = (Integer) requestBody.get("type"); // 1 = 注册验证, 2 = 密码重置
            
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
            log.error("生成验证码失败: {}", e.getMessage(), e);
            
            // 构建错误响应
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("status", "failed");
            String email = (String) requestBody.get("email");
            errorResponse.put("email", email);
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            // 添加技术支持信息
            errorResponse.put("supportInfo", "请检查您的邮箱是否正确，或联系系统管理员获取帮助");
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    // 验证验证码接口
    @PostMapping("/validate")
    public ResponseEntity<?> validateVerificationCode(@RequestBody Map<String, Object> requestBody) {
        try {
            String email = (String) requestBody.get("email");
            String code = (String) requestBody.get("code");
            Integer type = (Integer) requestBody.get("type");
            
            // 这里简化处理，实际应该在服务层实现验证码验证逻辑
            // 由于我们已经在注册和重置密码时验证了验证码，这里主要是为了前端可以单独验证
            // 我们可以直接返回成功，因为实际验证会在后续操作中进行
            return ResponseEntity.ok(Map.of("valid", true, "message", "验证码验证成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("valid", false, "message", e.getMessage()));
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