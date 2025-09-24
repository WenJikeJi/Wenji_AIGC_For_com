package com.wenji.server.config;

import com.wenji.server.service.SystemMonitorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * API请求拦截器，用于记录API调用情况，实现主动监控
 * 拦截所有API请求，记录调用次数、响应时间、错误状态等信息
 */
@Component
public class ApiRequestInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ApiRequestInterceptor.class);
    private static final String START_TIME = "startTime";
    private static final String API_PATH_PREFIX = "/api/";
    
    @Autowired
    private SystemMonitorService systemMonitorService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        
        // 添加详细的调试日志
        logger.info("ApiRequestInterceptor - 拦截到请求: {} {}", method, requestURI);
        
        // 仅拦截API路径的请求
        if (requestURI.startsWith(API_PATH_PREFIX)) {
            logger.info("ApiRequestInterceptor - 这是API请求，记录开始时间");
            // 记录请求开始时间
            request.setAttribute(START_TIME, System.currentTimeMillis());
        } else {
            logger.info("ApiRequestInterceptor - 这不是API请求，跳过处理");
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        // 在请求处理完成后，但在视图渲染前调用
        // 此处不需要处理
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 仅处理API路径的请求
        if (request.getRequestURI().startsWith(API_PATH_PREFIX)) {
            // 获取开始时间
            Long startTime = (Long) request.getAttribute(START_TIME);
            if (startTime != null) {
                // 计算响应时间
                long responseTime = System.currentTimeMillis() - startTime;
                String path = request.getRequestURI();
                String method = request.getMethod();
                int statusCode = response.getStatus();
                
                try {
                    // 记录API调用（按路径统计）
                    systemMonitorService.recordApiCall(path, method, responseTime);
                    
                    // 记录API错误（状态码>=400表示错误）
                    if (statusCode >= 400) {
                        systemMonitorService.recordApiError(path, method);
                        logger.warn("API调用出错: {} {} - 状态码: {} - 响应时间: {}ms", 
                                method, path, statusCode, responseTime);
                    } else {
                        // 记录正常调用的详细信息（可选，用于调试）
                        logger.debug("API调用成功: {} {} - 响应时间: {}ms", 
                                method, path, responseTime);
                    }
                    
                } catch (Exception e) {
                    // 确保监控本身的错误不会影响业务流程
                    logger.error("记录API调用信息失败", e);
                }
            }
        }
    }
}