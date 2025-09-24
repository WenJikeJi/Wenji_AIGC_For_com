package com.wenji.server.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户活跃度统计实体
 * 用于存储不同时间维度的用户活跃数据
 */
@Entity
@Table(name = "user_activity_stats")
public class UserActivityStats {
    
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
     * 活跃用户数量
     */
    @Column(name = "active_count", nullable = false)
    private Integer activeCount;
    
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
    public UserActivityStats() {
        this.createdTime = LocalDateTime.now();
    }
    
    public UserActivityStats(String timeDimension, LocalDateTime statTime, Integer activeCount) {
        this();
        this.timeDimension = timeDimension;
        this.statTime = statTime;
        this.activeCount = activeCount;
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
    
    public Integer getActiveCount() {
        return activeCount;
    }
    
    public void setActiveCount(Integer activeCount) {
        this.activeCount = activeCount;
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