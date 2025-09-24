package com.wenji.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Facebook Token验证请求")
public class FacebookTokenRequest {
    
    @Schema(description = "Facebook访问令牌", example = "EAALT5h8CpmABPWP8bbwFy2TMG2UvJdep5pMZBGTZAvErRprX2...")
    @JsonProperty("token")
    private String token;
    
    @Schema(description = "Facebook访问令牌（备用字段名）", example = "EAALT5h8CpmABPWP8bbwFy2TMG2UvJdep5pMZBGTZAvErRprX2...")
    @JsonProperty("accessToken")
    private String accessToken;
    
    @Schema(description = "Facebook应用ID", example = "795935269627488")
    @JsonProperty("appId")
    private String appId;
    
    @Schema(description = "Facebook应用密钥", example = "83a1221186bc8f01db0b7d82a9424a96")
    @JsonProperty("appSecret")
    private String appSecret;
    
    public FacebookTokenRequest() {}
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getAppId() {
        return appId;
    }
    
    public void setAppId(String appId) {
        this.appId = appId;
    }
    
    public String getAppSecret() {
        return appSecret;
    }
    
    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }
    
    @Override
    public String toString() {
        return "FacebookTokenRequest{" +
                "token='" + (token != null && token.length() > 10 ? 
                    token.substring(0, 5) + "*****" + token.substring(token.length() - 5) : "******") + '\'' +
                ", accessToken='" + (accessToken != null && accessToken.length() > 10 ? 
                    accessToken.substring(0, 5) + "*****" + accessToken.substring(accessToken.length() - 5) : "******") + '\'' +
                ", appId='" + appId + '\'' +
                ", appSecret='" + (appSecret != null ? "******" : null) + '\'' +
                '}';
    }
}