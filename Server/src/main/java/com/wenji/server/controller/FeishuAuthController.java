package com.wenji.server.controller;

import com.wenji.server.service.FeishuAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/feishu")
public class FeishuAuthController {
    
    private static final Logger log = LoggerFactory.getLogger(FeishuAuthController.class);
    
    @Autowired
    private FeishuAuthService feishuAuthService;
    
    /**
     * 获取飞书登录二维码URL
     */
    @GetMapping("/qrcode")
    public ResponseEntity<?> getQrCodeUrl() {
        try {
            Map<String, String> result = feishuAuthService.getQrCodeUrl();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("获取飞书登录二维码失败", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 飞书回调接口
     */
    @GetMapping("/callback")
    public ResponseEntity<?> callback(@RequestParam("code") String code, @RequestParam("state") String state) {
        try {
            Map<String, Object> result = feishuAuthService.handleCallback(code, state);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("处理飞书回调失败", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 绑定飞书账号与邮箱
     */
    @PostMapping("/bind-email")
    public ResponseEntity<?> bindEmail(@RequestBody Map<String, String> requestBody) {
        try {
            String tempToken = requestBody.get("tempToken");
            String email = requestBody.get("email");
            
            if (tempToken == null || email == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "临时令牌和邮箱不能为空"));
            }
            
            Map<String, Object> result = feishuAuthService.bindEmail(tempToken, email);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("绑定飞书账号与邮箱失败", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}