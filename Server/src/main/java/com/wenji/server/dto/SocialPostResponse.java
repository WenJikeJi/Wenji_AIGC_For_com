package com.wenji.server.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class SocialPostResponse {
    
    private Integer successCount;
    private Integer failCount;
    private Map<String, String> postIds; // key: platformType_homepageId, value: postId
    private String message;
    
    // 定时任务相关字段
    private Long taskId;
    private String taskNo;
    private LocalDateTime scheduleTime;
    private Integer taskStatus;
    
    public SocialPostResponse() {}
    
    public SocialPostResponse(Integer successCount, Integer failCount, Map<String, String> postIds) {
        this.successCount = successCount;
        this.failCount = failCount;
        this.postIds = postIds;
    }
    
    // Getters and Setters
    public Integer getSuccessCount() {
        return successCount;
    }
    
    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }
    
    public Integer getFailCount() {
        return failCount;
    }
    
    public void setFailCount(Integer failCount) {
        this.failCount = failCount;
    }
    
    public Map<String, String> getPostIds() {
        return postIds;
    }
    
    public void setPostIds(Map<String, String> postIds) {
        this.postIds = postIds;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Long getTaskId() {
        return taskId;
    }
    
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
    
    public String getTaskNo() {
        return taskNo;
    }
    
    public void setTaskNo(String taskNo) {
        this.taskNo = taskNo;
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
}