package com.wenji.server.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * 日志服务客户端，用于调用Python日志服务记录操作日志
 */
@Component
public class LogClient {

    private static final Logger logger = Logger.getLogger(LogClient.class.getName());

    @Value("${data.python.url}")
    private String pythonServiceUrl;

    @Value("${data.python.api.key:default_api_key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public LogClient() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * 记录用户操作日志
     * 
     * @param userId 用户ID
     * @param operation 操作类型
     * @param ip 操作IP
     * @param address 操作地址
     * @param details 操作详情
     * @param encryptionStatus 加密状态（0=未加密，1=加密）
     * @return 是否记录成功
     */
    public boolean recordOperationLog(Long userId, String operation, String ip, String address, String details, Integer encryptionStatus) {
        try {
            // 构建请求URL
            String url = pythonServiceUrl + (pythonServiceUrl.endsWith("/") ? "api/logs/record" : "/api/logs/record");
            
            // 构建请求参数
            Map<String, Object> params = new HashMap<>();
            params.put("user_id", userId);
            params.put("operation", operation);
            params.put("ip_address", ip);
            params.put("address", address);
            params.put("details", details);
            params.put("encryption_status", encryptionStatus);
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-API-KEY", apiKey); // 从配置文件中读取API Key
            
            // 构建请求实体（使用JSON格式）
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(params, headers);
            
            // 发送请求
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
            
            // 处理响应
            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody != null && "success".equals(responseBody.get("status"))) {
                    logger.info(String.format("操作日志记录成功: 用户ID=%d, 操作=%s", userId, operation));
                    return true;
                } else {
                    logger.warning(String.format("操作日志记录失败: %s", responseBody));
                    return false;
                }
            } else {
                logger.warning(String.format("操作日志服务请求失败: 状态码=%s", response.getStatusCode()));
                return false;
            }
        } catch (Exception e) {
            // 异常处理，确保主业务流程不受影响
            logger.warning(String.format("操作日志记录异常: %s", e.getMessage()));
            return false;
        }
    }
}