package com.wenji.server.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 社交媒体任务请求DTO
 */
public class TaskRequest {
    
    private String postTitle;
    private String postContent;
    private List<String> mediaUrls;
    private List<String> platformTypes;
    private List<Long> homepageIds;
    private LocalDateTime scheduleTime;
    private Integer maxRetryCount;
    
    // 默认构造函数
    public TaskRequest() {
    }
    
    // 带参数构造函数
    public TaskRequest(String postTitle, String postContent, List<String> mediaUrls, 
                      List<String> platformTypes, List<Long> homepageIds, 
                      LocalDateTime scheduleTime, Integer maxRetryCount) {
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.mediaUrls = mediaUrls;
        this.platformTypes = platformTypes;
        this.homepageIds = homepageIds;
        this.scheduleTime = scheduleTime;
        this.maxRetryCount = maxRetryCount;
    }
    
    // Getter 和 Setter 方法
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
    
    public List<String> getPlatformTypes() {
        return platformTypes;
    }
    
    public void setPlatformTypes(List<String> platformTypes) {
        this.platformTypes = platformTypes;
    }
    
    public List<Long> getHomepageIds() {
        return homepageIds;
    }
    
    public void setHomepageIds(List<Long> homepageIds) {
        this.homepageIds = homepageIds;
    }
    
    public LocalDateTime getScheduleTime() {
        return scheduleTime;
    }
    
    public void setScheduleTime(LocalDateTime scheduleTime) {
        this.scheduleTime = scheduleTime;
    }
    
    public Integer getMaxRetryCount() {
        return maxRetryCount;
    }
    
    public void setMaxRetryCount(Integer maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }
    
    @Override
    public String toString() {
        return "TaskRequest{" +
                "postTitle='" + postTitle + '\'' +
                ", postContent='" + postContent + '\'' +
                ", mediaUrls=" + mediaUrls +
                ", platformTypes=" + platformTypes +
                ", homepageIds=" + homepageIds +
                ", scheduleTime=" + scheduleTime +
                ", maxRetryCount=" + maxRetryCount +
                '}';
    }
}