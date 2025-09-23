package com.wenji.server.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "social_post")
public class SocialPost {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "post_id", nullable = false, length = 100)
    private String postId; // 平台返回的帖子ID
    
    @Column(name = "homepage_id", nullable = false, length = 100)
    private String homepageId;
    
    @Column(name = "platform_type", nullable = false)
    private Integer platformType; // 1-Facebook, 2-Instagram
    
    @Column(name = "post_title", length = 200)
    private String postTitle;
    
    @Column(name = "post_content", nullable = false, columnDefinition = "TEXT")
    private String postContent;
    
    @Column(name = "media_urls", columnDefinition = "TEXT")
    private String mediaUrls; // JSON数组格式
    
    @Column(name = "publish_time")
    private LocalDateTime publishTime;
    
    @Column(name = "task_id")
    private Long taskId; // 关联任务ID，即时发帖为空
    
    @Column(name = "publish_source")
    private Integer publishSource = 0; // 0-即时发帖, 1-定时任务
    
    @Column(name = "operator_id", nullable = false)
    private Long operatorId;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 构造函数
    public SocialPost() {
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
    
    public String getPostId() {
        return postId;
    }
    
    public void setPostId(String postId) {
        this.postId = postId;
    }
    
    public String getHomepageId() {
        return homepageId;
    }
    
    public void setHomepageId(String homepageId) {
        this.homepageId = homepageId;
    }
    
    public Integer getPlatformType() {
        return platformType;
    }
    
    public void setPlatformType(Integer platformType) {
        this.platformType = platformType;
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
    
    public LocalDateTime getPublishTime() {
        return publishTime;
    }
    
    public void setPublishTime(LocalDateTime publishTime) {
        this.publishTime = publishTime;
    }
    
    public Long getTaskId() {
        return taskId;
    }
    
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
    
    public Integer getPublishSource() {
        return publishSource;
    }
    
    public void setPublishSource(Integer publishSource) {
        this.publishSource = publishSource;
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