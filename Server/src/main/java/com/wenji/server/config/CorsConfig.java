package com.wenji.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 所有接口都允许跨域
                .allowedOrigins("*") // 允许所有来源（测试用）
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 必须包含OPTIONS（预检请求）
                .allowedHeaders("*") // 允许所有请求头
                .allowCredentials(false) // 当allowedOrigins为*时，必须设为false
                .maxAge(3600); // 预检请求缓存1小时
    }
}