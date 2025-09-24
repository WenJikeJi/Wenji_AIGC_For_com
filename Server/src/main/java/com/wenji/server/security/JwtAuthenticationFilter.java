package com.wenji.server.security;

import com.wenji.server.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    // 定义不需要JWT验证的路径 - 使用简单路径，避免startsWith匹配问题
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
        "/api/auth/login",
        "/api/auth/register", 
        "/api/auth/send-verification-code",
        "/api/auth/refresh-token",
        "/api/verify-code",
        "/api/verify-code/generate",
        "/api/verify-code/validate",
        "/api/encryption/public-key",
        "/api/auth/forgot-password",
        "/api/auth/reset-password",
        "/api/auth/feishu",
        "/api/auth/captcha",
        "/api/auth/captcha/verify",
        "/api/social/facebook/callback",
        "/api/social/facebook/auth-url",
        "/api/social/facebook/save-token",
        "/api/social/facebook/verify-token",
        "/api/social/facebook/validate-token",
        "/api/social-media/facebook/verify-token",
        "/api/social-platforms/status",
        "/swagger-ui",
        "/v3/api-docs"
    );
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestPath = request.getRequestURI();
        logger.info("JWT过滤器处理请求路径: {}", requestPath);
        
        // 检查是否是排除的路径
        boolean isExcluded = EXCLUDED_PATHS.stream().anyMatch(path -> {
            boolean matches = requestPath.equals(path);
            logger.info("检查路径 {} 是否匹配排除路径 {}: {}", requestPath, path, matches);
            return matches;
        });
        
        if (isExcluded) {
            logger.info("路径 {} 被排除，跳过JWT验证", requestPath);
            filterChain.doFilter(request, response);
            return;
        }
        
        // 特殊处理系统监控API路径 - 检查是否带有管理员邮箱头
        if ((requestPath.startsWith("/api/monitor/") || requestPath.startsWith("api/monitor/")) && request.getHeader("x-user-email") != null) {
            String adminEmail = request.getHeader("x-user-email");
            logger.info("系统监控API请求，从请求头获取管理员邮箱: {}", adminEmail);
            // 设置email属性，供SystemMonitorController使用
            request.setAttribute("email", adminEmail);
            filterChain.doFilter(request, response);
            return;
        }
        
        logger.info("路径 {} 需要JWT验证", requestPath);
        
        final String authorizationHeader = request.getHeader("Authorization");
        
        String username = null;
        String jwt = null;
        
        // 提取JWT令牌
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.getUsernameFromToken(jwt);
            } catch (Exception e) {
                logger.error("JWT令牌解析失败: {}", e.getMessage());
            }
        } else {
            logger.warn("Authorization头缺失或格式错误: {}", authorizationHeader);
        }
        
        // 验证令牌并设置安全上下文
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                
                if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                    // 从JWT中提取用户信息并设置到request属性中
                    try {
                        Long userId = jwtUtil.getUserIdFromToken(jwt);
                        Integer role = jwtUtil.getUserRoleFromToken(jwt);
                        
                        // 设置request属性，供Controller使用
                        request.setAttribute("userId", userId);
                        request.setAttribute("role", role);
                        
                        logger.debug("JWT验证成功，用户ID: {}, 角色: {}", userId, role);
                    } catch (Exception e) {
                        logger.error("从JWT中提取用户信息失败: {}", e.getMessage());
                    }
                    
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                } else {
                    logger.warn("JWT令牌验证失败");
                }
            } catch (Exception e) {
                logger.error("用户验证过程中发生错误: {}", e.getMessage());
            }
        } else {
            logger.warn("JWT令牌缺失或已过期，请求路径: {}", requestPath);
        }
        
        filterChain.doFilter(request, response);
    }
}