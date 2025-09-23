package com.wenji.server.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "social_post_task")
public class SocialPostTask {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "task_no", nullable = false, length = 50, unique = true)
    private String taskNo;
    
    @Column(name = "homepage_ids", nullable = false, length = 200)
    private String homepageIds; // 多个用逗号分隔
    
    @Column(name = "platform_types", nullable = false, length = 50)
    private String platformTypes; // 1-FB/2-INS，多个用逗号分隔
    
    @Column(name = "post_title", length = 200)
    private String postTitle;
    
    @Column(name = "post_content", nullable = false, columnDefinition = "TEXT")
    private String postContent;
    
    @Column(name = "media_urls", columnDefinition = "TEXT")
    private String mediaUrls; // JSON数组格式
    
    @Column(name = "schedule_time", nullable = false)
    private LocalDateTime scheduleTime;
    
    @Column(name = "task_status")
    private Integer taskStatus = 0; // 0-待执行, 1-已执行, 2-执行失败, 3-已取消
    
    @Column(name = "execute_result", columnDefinition = "TEXT")
    private String executeResult;
    
    @Column(name = "retry_count")
    private Integer retryCount = 0;
    
    @Column(name = "max_retry_count")
    private Integer maxRetryCount = 3;
    
    @Column(name = "operator_id", nullable = false)
    private Long operatorId;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 构造函数
    public SocialPostTask() {
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
    
    public String getTaskNo() {
        return taskNo;
    }
    
    public void setTaskNo(String taskNo) {
        this.taskNo = taskNo;
    }
    
    public String getHomepageIds() {
        return homepageIds;
    }
    
    public void setHomepageIds(String homepageIds) {
        this.homepageIds = homepageIds;
    }
    
    public String getPlatformTypes() {
        return platformTypes;
    }
    
    public void setPlatformTypes(String platformTypes) {
        this.platformTypes = platformTypes;
    }
    
    public String getPostTitle() {
        return postTitle;
    }
    
    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }
    
    public String getPostContent() {
        return postContent;
    }
    
    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }
    
    public String getMediaUrls() {
        return mediaUrls;
    }
    
    public void setMediaUrls(String mediaUrls) {
        this.mediaUrls = mediaUrls;
    }
    
    public LocalDateTime getScheduleTime() {
        return scheduleTime;
    }
    
    public void setScheduleTime(LocalDateTime scheduleTime) {
        this.scheduleTime = scheduleTime;
    }
    
    public Integer getTaskStatus() {
        return taskStatus;
    }
    
    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }
    
    public String getExecuteResult() {
        return executeResult;
    }
    
    public void setExecuteResult(String executeResult) {
        this.executeResult = executeResult;
    }
    
    public Integer getRetryCount() {
        return retryCount;
    }
    
    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }
    
    public Integer getMaxRetryCount() {
        return maxRetryCount;
    }
    
    public void setMaxRetryCount(Integer maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }
    
    public Long getOperatorId() {
        return operatorId;
    }
    
    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
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