package com.wenji.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 所有接口都允许跨域
                .allowedOrigins("http://localhost:5173") // 精确匹配前端地址
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 必须包含OPTIONS（预检请求）
                .allowedHeaders("*") // 允许所有请求头
                .allowCredentials(true) // 允许携带Cookie
                .maxAge(3600); // 预检请求缓存1小时
    }
}