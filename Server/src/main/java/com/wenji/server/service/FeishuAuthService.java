package com.wenji.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wenji.server.model.FeishuUser;
import com.wenji.server.model.UserAccount;
import com.wenji.server.repository.FeishuUserRepository;
import com.wenji.server.repository.UserAccountRepository;
import com.wenji.server.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class FeishuAuthService {
    
    private static final Logger log = LoggerFactory.getLogger(FeishuAuthService.class);
    
    @Value("${feishu.app-id}")
    private String appId;
    
    @Value("${feishu.app-secret}")
    private String appSecret;
    
    @Value("${feishu.redirect-uri}")
    private String redirectUri;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private FeishuUserRepository feishuUserRepository;
    
    @Autowired
    private UserAccountRepository userAccountRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 获取飞书登录二维码URL
     */
    public Map<String, String> getQrCodeUrl() {
        String state = UUID.randomUUID().toString();
        
        String qrCodeUrl = UriComponentsBuilder.fromHttpUrl("https://open.feishu.cn/open-apis/authen/v1/index")
                .queryParam("app_id", appId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("state", state)
                .build().toUriString();
        
        Map<String, String> result = new HashMap<>();
        result.put("qrCodeUrl", qrCodeUrl);
        result.put("state", state);
        
        return result;
    }
    
    /**
     * 处理飞书回调
     */
    @Transactional
    public Map<String, Object> handleCallback(String code, String state) {
        try {
            // 1. 获取访问令牌
            Map<String, Object> tokenInfo = getAccessToken(code);
            String accessToken = (String) tokenInfo.get("access_token");
            String refreshToken = (String) tokenInfo.get("refresh_token");
            Integer expiresIn = (Integer) tokenInfo.get("expires_in");
            
            // 2. 获取用户信息
            Map<String, Object> userInfo = getUserInfo(accessToken);
            String unionId = (String) userInfo.get("union_id");
            String userId = (String) userInfo.get("user_id");
            String openId = (String) userInfo.get("open_id");
            String name = (String) userInfo.get("name");
            String email = (String) userInfo.get("email");
            
            // 3. 检查用户是否已存在
            Optional<FeishuUser> existingFeishuUser = feishuUserRepository.findByFeishuUnionId(unionId);
            
            if (existingFeishuUser.isPresent()) {
                // 用户已存在，更新令牌
                FeishuUser feishuUser = existingFeishuUser.get();
                feishuUser.setAccessToken(accessToken);
                feishuUser.setRefreshToken(refreshToken);
                feishuUser.setTokenExpiresAt(LocalDateTime.now().plusSeconds(expiresIn));
                feishuUserRepository.save(feishuUser);
                
                // 获取关联的用户账号
                UserAccount userAccount = userAccountRepository.findById(feishuUser.getUserId())
                        .orElseThrow(() -> new RuntimeException("用户账号不存在"));
                
                // 生成JWT令牌
                String token = jwtUtil.generateToken(userAccount.getId(), userAccount.getAccount(), userAccount.getRole());
                
                Map<String, Object> result = new HashMap<>();
                result.put("token", token);
                result.put("userId", userAccount.getId());
                result.put("username", userAccount.getUsername());
                result.put("email", userAccount.getEmail());
                result.put("role", userAccount.getRole());
                result.put("isNewUser", false);
                
                return result;
            } else {
                // 新用户，需要绑定邮箱
                Map<String, Object> result = new HashMap<>();
                result.put("isNewUser", true);
                result.put("tempToken", generateTempToken(unionId, userId, openId, name, email, accessToken, refreshToken, expiresIn));
                result.put("name", name);
                result.put("email", email);
                
                return result;
            }
        } catch (Exception e) {
            log.error("处理飞书回调失败", e);
            throw new RuntimeException("处理飞书回调失败: " + e.getMessage());
        }
    }
    
    /**
     * 绑定飞书账号与邮箱
     */
    @Transactional
    public Map<String, Object> bindEmail(String tempToken, String email) {
        try {
            // 1. 解析临时令牌
            Map<String, Object> tokenData = parseTempToken(tempToken);
            String unionId = (String) tokenData.get("unionId");
            String userId = (String) tokenData.get("userId");
            String openId = (String) tokenData.get("openId");
            String name = (String) tokenData.get("name");
            String accessToken = (String) tokenData.get("accessToken");
            String refreshToken = (String) tokenData.get("refreshToken");
            Integer expiresIn = (Integer) tokenData.get("expiresIn");
            
            // 2. 检查邮箱是否已存在
            Optional<UserAccount> existingUser = userAccountRepository.findByEmail(email);
            UserAccount userAccount;
            
            if (existingUser.isPresent()) {
                // 邮箱已存在，关联现有账号
                userAccount = existingUser.get();
            } else {
                // 创建新账号
                userAccount = new UserAccount();
                userAccount.setUsername(name);
                userAccount.setAccount(email.split("@")[0] + "_" + System.currentTimeMillis());
                userAccount.setEmail(email);
                userAccount.setPassword(UUID.randomUUID().toString()); // 随机密码，用户无法直接登录
                userAccount.setRole(0); // 主账号
                userAccount.setEmailVerified(1); // 已验证
                userAccount.setStatus(1); // 正常
                userAccountRepository.save(userAccount);
            }
            
            // 3. 创建飞书用户关联
            FeishuUser feishuUser = new FeishuUser();
            feishuUser.setUserId(userAccount.getId());
            feishuUser.setFeishuUserId(userId);
            feishuUser.setFeishuUnionId(unionId);
            feishuUser.setFeishuOpenId(openId);
            feishuUser.setAccessToken(accessToken);
            feishuUser.setRefreshToken(refreshToken);
            feishuUser.setTokenExpiresAt(LocalDateTime.now().plusSeconds(expiresIn));
            feishuUserRepository.save(feishuUser);
            
            // 4. 生成JWT令牌
            String token = jwtUtil.generateToken(userAccount.getId(), userAccount.getAccount(), userAccount.getRole());
            
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("userId", userAccount.getId());
            result.put("username", userAccount.getUsername());
            result.put("email", userAccount.getEmail());
            result.put("role", userAccount.getRole());
            
            return result;
        } catch (Exception e) {
            log.error("绑定飞书账号与邮箱失败", e);
            throw new RuntimeException("绑定飞书账号与邮箱失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取访问令牌
     */
    private Map<String, Object> getAccessToken(String code) {
        try {
            String url = "https://open.feishu.cn/open-apis/auth/v3/app_access_token/internal";
            
            Map<String, String> request = new HashMap<>();
            request.put("app_id", appId);
            request.put("app_secret", appSecret);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json; charset=utf-8");
            
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            
            Map<String, Object> appTokenData = response.getBody();
            String appAccessToken = (String) appTokenData.get("app_access_token");
            
            // 使用app_access_token获取用户访问令牌
            url = "https://open.feishu.cn/open-apis/authen/v1/access_token";
            
            Map<String, String> codeRequest = new HashMap<>();
            codeRequest.put("grant_type", "authorization_code");
            codeRequest.put("code", code);
            
            headers = new HttpHeaders();
            headers.set("Content-Type", "application/json; charset=utf-8");
            headers.set("Authorization", "Bearer " + appAccessToken);
            
            entity = new HttpEntity<>(codeRequest, headers);
            response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            
            Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
            return data;
        } catch (Exception e) {
            log.error("获取飞书访问令牌失败", e);
            throw new RuntimeException("获取飞书访问令牌失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户信息
     */
    private Map<String, Object> getUserInfo(String accessToken) {
        try {
            String url = "https://open.feishu.cn/open-apis/authen/v1/user_info";
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            
            Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
            return data;
        } catch (Exception e) {
            log.error("获取飞书用户信息失败", e);
            throw new RuntimeException("获取飞书用户信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成临时令牌
     */
    private String generateTempToken(String unionId, String userId, String openId, String name, String email,
                                    String accessToken, String refreshToken, Integer expiresIn) {
        try {
            Map<String, Object> tokenData = new HashMap<>();
            tokenData.put("unionId", unionId);
            tokenData.put("userId", userId);
            tokenData.put("openId", openId);
            tokenData.put("name", name);
            tokenData.put("email", email);
            tokenData.put("accessToken", accessToken);
            tokenData.put("refreshToken", refreshToken);
            tokenData.put("expiresIn", expiresIn);
            tokenData.put("timestamp", System.currentTimeMillis());
            
            return jwtUtil.generateTempToken(tokenData);
        } catch (Exception e) {
            log.error("生成临时令牌失败", e);
            throw new RuntimeException("生成临时令牌失败: " + e.getMessage());
        }
    }
    
    /**
     * 解析临时令牌
     */
    private Map<String, Object> parseTempToken(String tempToken) {
        try {
            return jwtUtil.parseTempToken(tempToken);
        } catch (Exception e) {
            log.error("解析临时令牌失败", e);
            throw new RuntimeException("解析临时令牌失败: " + e.getMessage());
        }
    }
}