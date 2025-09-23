package com.wenji.server.controller;

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

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class CaptchaController {
    
    private static final Logger log = LoggerFactory.getLogger(CaptchaController.class);
    
    @Autowired
    private CaptchaService captchaService;
    
    @Operation(
            summary = "生成图形验证码",
            description = "生成图形验证码，返回验证码图片的Base64编码和验证码ID",
            tags = {"验证码"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "验证码生成成功", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(example = "{\"captchaId\": \"uuid-string\", \"captchaImage\": \"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...\"}"))),
            @ApiResponse(responseCode = "500", description = "验证码生成失败")
    })
    @GetMapping("/captcha")
    public ResponseEntity<?> generateCaptcha() {
        try {
            log.info("收到生成验证码请求");
            Map<String, String> result = captchaService.generateCaptcha();
            log.info("验证码生成成功，ID: {}", result.get("captchaId"));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("生成验证码失败: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", "验证码生成失败", "message", e.getMessage()));
        }
    }
    
    @Operation(
            summary = "验证图形验证码",
            description = "验证用户输入的图形验证码是否正确",
            tags = {"验证码"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "验证成功", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(example = "{\"valid\": true, \"message\": \"验证码正确\"}"))),
            @ApiResponse(responseCode = "400", description = "验证失败", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(example = "{\"valid\": false, \"message\": \"验证码错误或已过期\"}")))
    })
    @PostMapping("/captcha/verify")
    public ResponseEntity<?> verifyCaptcha(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "验证码验证请求参数",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{\"captchaId\": \"uuid-string\", \"captchaCode\": \"ABCD\"}"
                            )
                    )
            )
            @RequestBody Map<String, String> requestBody) {
        try {
            String captchaId = requestBody.get("captchaId");
            String captchaCode = requestBody.get("captchaCode");
            
            if (captchaId == null || captchaCode == null) {
                return ResponseEntity.badRequest().body(Map.of("valid", false, "message", "验证码ID和验证码不能为空"));
            }
            
            log.info("收到验证码验证请求，ID: {}, 验证码: {}", captchaId, captchaCode);
            
            boolean isValid = captchaService.verifyCaptcha(captchaId, captchaCode);
            
            if (isValid) {
                log.info("验证码验证成功，ID: {}", captchaId);
                return ResponseEntity.ok(Map.of("valid", true, "message", "验证码正确"));
            } else {
                log.warn("验证码验证失败，ID: {}, 验证码: {}", captchaId, captchaCode);
                return ResponseEntity.badRequest().body(Map.of("valid", false, "message", "验证码错误或已过期"));
            }
        } catch (Exception e) {
            log.error("验证码验证异常: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("valid", false, "message", "验证码验证失败", "error", e.getMessage()));
        }
    }
}