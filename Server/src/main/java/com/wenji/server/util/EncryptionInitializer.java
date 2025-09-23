package com.wenji.server.util;

import com.wenji.server.model.SystemEncryptionConfig;
import com.wenji.server.repository.SystemEncryptionConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 加密配置初始化工具类
 * 用于生成RSA密钥对并初始化系统加密配置
 */
@Component
public class EncryptionInitializer {

    @Autowired
    private SystemEncryptionConfigRepository systemEncryptionConfigRepository;
    
    @Autowired
    private RsaUtil rsaUtil;

    /**
     * 初始化加密配置
     * 如果数据库中没有启用的加密配置，则生成新的密钥对并保存
     * @return 是否初始化成功
     */
    public boolean initializeEncryptionConfig() {
        try {
            // Check if there's already an enabled encryption configuration
            if (systemEncryptionConfigRepository.findByStatus(1).isPresent()) {
                System.out.println("System already has enabled encryption configuration, no initialization needed");
                return true;
            }

            // Generate new RSA key pair
            var keyPairMap = rsaUtil.generateEncodedKeyPair();
            String publicKey = keyPairMap.get("publicKey");
            String privateKey = keyPairMap.get("privateKey");

            // Create encryption configuration
            SystemEncryptionConfig config = new SystemEncryptionConfig();
            config.setRsaPublicKey(publicKey);
            config.setRsaPrivateKey(privateKey);
            config.setKeyVersion("KEY_" + UUID.randomUUID().toString().substring(0, 8));
            config.setLastRotateTime(LocalDateTime.now());
            config.setStatus(1); // Active status

            // Save to database
            systemEncryptionConfigRepository.save(config);
            System.out.println("Encryption configuration initialization successful");
            return true;
        } catch (Exception e) {
            System.err.println("Encryption configuration initialization failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}