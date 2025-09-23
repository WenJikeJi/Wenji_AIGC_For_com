package com.wenji.server.config;

import com.wenji.server.util.EncryptionInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 加密配置启动监听器
 * 在应用启动时自动初始化加密配置
 */
@Component
public class EncryptionStartupListener implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private EncryptionInitializer encryptionInitializer;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // Initialize encryption configuration after application startup
        System.out.println("Initializing system encryption configuration...");
        boolean initialized = encryptionInitializer.initializeEncryptionConfig();
        if (initialized) {
            System.out.println("Encryption configuration initialization completed");
        } else {
            System.out.println("Encryption configuration initialization failed, please check logs");
        }
    }
}