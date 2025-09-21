package com.wenji.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@SpringBootApplication
public class WenjiServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(WenjiServerApplication.class, args);
    }
    
    // 使用CorsFilter配置CORS
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.addAllowedOriginPattern("http://localhost:5173"); // 前端域名
        configuration.addAllowedMethod("*"); // 允许所有HTTP方法
        configuration.addAllowedHeader("*"); // 允许所有请求头
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return new CorsFilter(source);
    }

}