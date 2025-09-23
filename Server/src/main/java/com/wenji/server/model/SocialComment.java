package com.wenji.server.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "social_comment")
public class SocialComment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "comment_id", nullable = false, length = 100)
    private String commentId; // 平台返回的评论ID
    
    @Column(name = "post_id", nullable = false, length = 100)
    private String postId; // 帖子ID
    
    @Column(name = "homepage_id", nullable = false, length = 100)
    private String homepageId; // 主页ID
    
    @Column(name = "platform_type", nullable = false)
    private Integer platformType; // 1-Facebook, 2-Instagram
    
    @Column(name = "commenter_name", nullable = false, length = 100)
    private String commenterName; // 评论人姓名
    
    @Column(name = "commenter_avatar", length = 500)
    private String commenterAvatar; // 评论人头像URL
    
    @Column(name = "comment_content", nullable = false, columnDefinition = "TEXT")
    private String commentContent; // 评论内容
    
    @Column(name = "parent_comment_id", length = 100)
    private String parentCommentId; // 父评论ID，用于回复功能
    
    @Column(name = "like_count")
    private Integer likeCount = 0; // 点赞数量
    
    @Column(name = "comment_time")
    private LocalDateTime commentTime; // 评论时间
    
    @Column(name = "user_id")
    private Long userId; // 系统用户ID（如果是系统用户发布的评论）
    
    @Column(name = "status")
    private Integer status = 1; // 0-已删除, 1-正常
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 构造函数
    public SocialComment() {
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
    
    public String getCommentId() {
        return commentId;
    }
    
    public void setCommentId(String commentId) {
        this.commentId = commentId;
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
    
    public String getCommenterName() {
        return commenterName;
    }
    
    public void setCommenterName(String commenterName) {
        this.commenterName = commenterName;
    }
    
    public String getCommenterAvatar() {
        return commenterAvatar;
    }
    
    public void setCommenterAvatar(String commenterAvatar) {
        this.commenterAvatar = commenterAvatar;
    }
    
    public String getCommentContent() {
        return commentContent;
    }
    
    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }
    
    public String getParentCommentId() {
        return parentCommentId;
    }
    
    public void setParentCommentId(String parentCommentId) {
        this.parentCommentId = parentCommentId;
    }
    
    public Integer getLikeCount() {
        return likeCount;
    }
    
    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }
    
    public LocalDateTime getCommentTime() {
        return commentTime;
    }
    
    public void setCommentTime(LocalDateTime commentTime) {
        this.commentTime = commentTime;
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