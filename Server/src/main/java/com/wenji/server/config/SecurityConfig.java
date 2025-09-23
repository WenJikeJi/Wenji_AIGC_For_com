package com.wenji.server.config;

import com.wenji.server.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    // 配置密码编码器
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
    
    // 配置认证管理器
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    
    // 配置RestTemplate
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    // 配置CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许的源（使用pattern，支持通配符，兼容allowCredentials=true）
        config.addAllowedOriginPattern("http://localhost:5173"); // 前端域名 IPv4
        config.addAllowedOriginPattern("http://[::1]:5173"); // 前端域名 IPv6
        config.addAllowedOriginPattern("http://127.0.0.1:5173"); // 本地回环地址
        
        // 允许的请求头
        config.addAllowedHeader("*");
        
        // 允许的请求方法
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("OPTIONS"); // 预检请求
        
        // 允许携带凭证（Cookie/Token）
        config.setAllowCredentials(true);
        
        // 预检请求缓存时间（减少OPTIONS请求）
        config.setMaxAge(3600L);
        
        // 应用到所有接口
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
    
    // 配置安全过滤器链
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 关键：启用CORS，并指定使用上面定义的corsConfigurationSource
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // 禁用CSRF保护（因为使用JWT）
            .csrf(csrf -> csrf.disable())
            // 设置会话管理为无状态
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 配置请求授权规则
            .authorizeHttpRequests(authorize -> authorize
                // 允许所有用户访问的端点
                .requestMatchers("/api/auth/login", "/api/auth/register/**", "/api/auth/send-verification-code", "/api/verify-code/**", "/api/encryption/public-key", "/api/auth/forgot-password", "/api/auth/reset-password", "/api/auth/feishu/**", "/api/auth/captcha", "/api/auth/captcha/verify", "/api/social/facebook/auth-url").permitAll()
                // 允许Swagger文档访问
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // 其他所有请求都需要认证
                .anyRequest().authenticated()
            )
            // 添加JWT过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}