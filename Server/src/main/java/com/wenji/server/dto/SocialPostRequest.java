package com.wenji.server.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public class SocialPostRequest {
    
    @NotEmpty(message = "主页ID不能为空")
    private List<String> homepageIds;
    
    @NotEmpty(message = "平台类型不能为空")
    private List<Integer> platformTypes;
    
    @Size(max = 200, message = "帖子标题不能超过200个字符")
    private String postTitle;
    
    @NotEmpty(message = "帖子内容不能为空")
    @Size(max = 500, message = "帖子内容不能超过500个字符")
    private String postContent;
    
    private List<String> mediaUrls;
    
    // 定时发帖时需要
    private LocalDateTime scheduleTime;
    
    // Getters and Setters
    public List<String> getHomepageIds() {
        return homepageIds;
    }
    
    public void setHomepageIds(List<String> homepageIds) {
        this.homepageIds = homepageIds;
    }
    
    public List<Integer> getPlatformTypes() {
        return platformTypes;
    }
    
    public void setPlatformTypes(List<Integer> platformTypes) {
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
    
    public List<String> getMediaUrls() {
        return mediaUrls;
    }
    
    public void setMediaUrls(List<String> mediaUrls) {
        this.mediaUrls = mediaUrls;
    }
    
    public LocalDateTime getScheduleTime() {
        return scheduleTime;
    }
    
    public void setScheduleTime(LocalDateTime scheduleTime) {
        this.scheduleTime = scheduleTime;
    }
}