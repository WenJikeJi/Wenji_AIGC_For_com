package com.wenji.server.config;

import com.wenji.server.config.ApiRequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置类，用于注册拦截器和其他Web配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private ApiRequestInterceptor apiRequestInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册API请求拦截器，拦截所有请求路径
        // 拦截器会在内部过滤只处理/api/开头的请求
        registry.addInterceptor(apiRequestInterceptor)
                .addPathPatterns("/**")  // 拦截所有路径
                .excludePathPatterns("/static/**")  // 排除静态资源
                .excludePathPatterns("/favicon.ico");  // 排除favicon
    }
}