package com.wenji.server.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "social_homepage")
public class SocialHomepage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "homepage_id", nullable = false, length = 100)
    private String homepageId;
    
    @Column(name = "homepage_name", nullable = false, length = 200)
    private String homepageName;
    
    @Column(name = "platform_type", nullable = false)
    private Integer platformType; // 1-Facebook, 2-Instagram
    
    @Column(name = "access_token", columnDefinition = "TEXT")
    private String accessToken;
    
    @Column(name = "refresh_token", columnDefinition = "TEXT")
    private String refreshToken;
    
    @Column(name = "app_id", length = 100)
    private String appId;
    
    @Column(name = "app_secret", columnDefinition = "TEXT")
    private String appSecret;
    
    @Column(name = "token_expires_at")
    private LocalDateTime tokenExpiresAt;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "status")
    private Integer status = 1; // 0-禁用, 1-启用
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 构造函数
    public SocialHomepage() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getHomepageId() {
        return homepageId;
    }
    
    public void setHomepageId(String homepageId) {
        this.homepageId = homepageId;
    }
    
    public String getHomepageName() {
        return homepageName;
    }
    
    public void setHomepageName(String homepageName) {
        this.homepageName = homepageName;
    }
    
    public Integer getPlatformType() {
        return platformType;
    }
    
    public void setPlatformType(Integer platformType) {
        this.platformType = platformType;
    }
    
    public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
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
    
    public LocalDateTime getTokenExpiresAt() {
        return tokenExpiresAt;
    }
    
    public void setTokenExpiresAt(LocalDateTime tokenExpiresAt) {
        this.tokenExpiresAt = tokenExpiresAt;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}