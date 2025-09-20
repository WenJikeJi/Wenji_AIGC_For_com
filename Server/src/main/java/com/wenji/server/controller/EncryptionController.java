package com.wenji.server.controller;

import com.wenji.server.model.SystemEncryptionConfig;
import com.wenji.server.repository.SystemEncryptionConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/encryption")
public class EncryptionController {
    
    @Autowired
    private SystemEncryptionConfigRepository systemEncryptionConfigRepository;
    
    // 获取RSA公钥接口
    @GetMapping("/public-key")
    public ResponseEntity<?> getPublicKey() {
        try {
            // 查询启用的加密配置
            SystemEncryptionConfig config = systemEncryptionConfigRepository.findByStatus(1)
                    .orElseThrow(() -> new RuntimeException("未找到可用的加密配置"));
            
            // 返回公钥
            return ResponseEntity.ok(Map.of(
                    "publicKey", config.getRsaPublicKey(),
                    "keyVersion", config.getKeyVersion()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}