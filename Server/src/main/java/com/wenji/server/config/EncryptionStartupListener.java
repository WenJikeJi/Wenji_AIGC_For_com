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
        // 应用启动后初始化加密配置
        System.out.println("正在初始化系统加密配置...");
        boolean initialized = encryptionInitializer.initializeEncryptionConfig();
        if (initialized) {
            System.out.println("加密配置初始化完成");
        } else {
            System.out.println("加密配置初始化失败，请检查日志");
        }
    }
}