package com.wenji.server.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务响应DTO
 */
public class TaskResponse {
    
    private Long taskId;
    private String taskNumber;
    private String postTitle;
    private String postContent;
    private List<String> mediaUrls;
    private List<Integer> platformTypes;
    private List<String> homepageIds;
    private LocalDateTime scheduleTime;
    private Integer taskStatus;
    private LocalDateTime createdAt;
    private String message;
    private boolean success;
    
    public TaskResponse() {}
    
    public TaskResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public TaskResponse(Long taskId, String taskNumber, String postTitle, String postContent,
                      List<String> mediaUrls, List<Integer> platformTypes, List<String> homepageIds,
                      LocalDateTime scheduleTime, Integer taskStatus, LocalDateTime createdAt) {
        this.taskId = taskId;
        this.taskNumber = taskNumber;
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.mediaUrls = mediaUrls;
        this.platformTypes = platformTypes;
        this.homepageIds = homepageIds;
        this.scheduleTime = scheduleTime;
        this.taskStatus = taskStatus;
        this.createdAt = createdAt;
        this.success = true;
    }
    
    // Getters and Setters
    public Long getTaskId() {
        return taskId;
    }
    
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
    
    public String getTaskNumber() {
        return taskNumber;
    }
    
    public void setTaskNumber(String taskNumber) {
        this.taskNumber = taskNumber;
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
    
    public List<Integer> getPlatformTypes() {
        return platformTypes;
    }
    
    public void setPlatformTypes(List<Integer> platformTypes) {
        this.platformTypes = platformTypes;
    }
    
    public List<String> getHomepageIds() {
        return homepageIds;
    }
    
    public void setHomepageIds(List<String> homepageIds) {
        this.homepageIds = homepageIds;
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    @Override
    public String toString() {
        return "TaskResponse{" +
                "taskId=" + taskId +
                ", taskNumber='" + taskNumber + '\'' +
                ", postTitle='" + postTitle + '\'' +
                ", postContent='" + postContent + '\'' +
                ", mediaUrls=" + mediaUrls +
                ", platformTypes=" + platformTypes +
                ", homepageIds=" + homepageIds +
                ", scheduleTime=" + scheduleTime +
                ", taskStatus=" + taskStatus +
                ", createdAt=" + createdAt +
                ", message='" + message + '\'' +
                ", success=" + success +
                '}';
    }
}