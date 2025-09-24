package com.wenji.server.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户注册统计实体
 * 用于存储不同时间维度的用户注册数据
 */
@Entity
@Table(name = "user_registration_stats")
public class UserRegistrationStats {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 统计时间维度：hour, day, week, month
     */
    @Column(name = "time_dimension", nullable = false, length = 10)
    private String timeDimension;
    
    /**
     * 统计时间点
     */
    @Column(name = "stat_time", nullable = false)
    private LocalDateTime statTime;
    
    /**
     * 注册用户数量
     */
    @Column(name = "registration_count", nullable = false)
    private Integer registrationCount;
    
    /**
     * 创建时间
     */
    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
    
    // 构造函数
    public UserRegistrationStats() {
        this.createdTime = LocalDateTime.now();
    }
    
    public UserRegistrationStats(String timeDimension, LocalDateTime statTime, Integer registrationCount) {
        this();
        this.timeDimension = timeDimension;
        this.statTime = statTime;
        this.registrationCount = registrationCount;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTimeDimension() {
        return timeDimension;
    }
    
    public void setTimeDimension(String timeDimension) {
        this.timeDimension = timeDimension;
    }
    
    public LocalDateTime getStatTime() {
        return statTime;
    }
    
    public void setStatTime(LocalDateTime statTime) {
        this.statTime = statTime;
    }
    
    public Integer getRegistrationCount() {
        return registrationCount;
    }
    
    public void setRegistrationCount(Integer registrationCount) {
        this.registrationCount = registrationCount;
    }
    
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }
    
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }
    
    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }
    
    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedTime = LocalDateTime.now();
    }
}