package com.wenji.server.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wenji.server.dto.SocialPostRequest;
import com.wenji.server.model.SocialHomepage;
import com.wenji.server.model.SocialPost;
import com.wenji.server.model.SocialPostTask;
import com.wenji.server.repository.SocialHomepageRepository;
import com.wenji.server.repository.SocialPostRepository;
import com.wenji.server.repository.SocialPostTaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SocialPostSchedulerService {
    
    private static final Logger logger = LoggerFactory.getLogger(SocialPostSchedulerService.class);
    
    @Autowired
    private SocialPostTaskRepository taskRepository;
    
    @Autowired
    private SocialHomepageRepository homepageRepository;
    
    @Autowired
    private SocialPostRepository postRepository;
    
    @Autowired
    private FacebookApiService facebookApiService;
    
    @Autowired
    private InstagramApiService instagramApiService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 定时扫描并执行待执行的任务
     * 每分钟执行一次
     */
    @Scheduled(fixedRate = 60000) // 60秒 = 60000毫秒
    public void executePendingTasks() {
        try {
            LocalDateTime currentTime = LocalDateTime.now();
            List<SocialPostTask> pendingTasks = taskRepository.findPendingTasks(currentTime);
            
            if (!pendingTasks.isEmpty()) {
                logger.info("发现 {} 个待执行的定时任务", pendingTasks.size());
                
                for (SocialPostTask task : pendingTasks) {
                    executeTask(task);
                }
            }
            
        } catch (Exception e) {
            logger.error("定时任务扫描异常: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 定时重试失败的任务
     * 每5分钟执行一次
     */
    @Scheduled(fixedRate = 300000) // 5分钟 = 300000毫秒
    public void retryFailedTasks() {
        try {
            List<SocialPostTask> retryableTasks = taskRepository.findRetryableTasks();
            
            if (!retryableTasks.isEmpty()) {
                logger.info("发现 {} 个可重试的失败任务", retryableTasks.size());
                
                for (SocialPostTask task : retryableTasks) {
                    // 检查是否到了重试时间（失败后5分钟）
                    if (task.getUpdatedAt().plusMinutes(5).isBefore(LocalDateTime.now())) {
                        retryTask(task);
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("重试任务扫描异常: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 执行单个任务
     */
    @Transactional
    public void executeTask(SocialPostTask task) {
        logger.info("开始执行定时任务，任务ID: {}, 任务编号: {}", task.getId(), task.getTaskNo());
        
        try {
            // 解析主页ID列表
            List<String> homepageIds = Arrays.asList(task.getHomepageIds().split(","));
            
            // 解析平台类型列表
            List<Integer> platformTypes = Arrays.stream(task.getPlatformTypes().split(","))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            
            // 获取主页信息
            List<SocialHomepage> homepages = homepageRepository.findByHomepageIdsAndStatusEnabled(homepageIds);
            
            if (homepages.isEmpty()) {
                task.setTaskStatus(2); // 执行失败
                task.setExecuteResult("未找到有效的主页");
                taskRepository.save(task);
                logger.error("任务执行失败，未找到有效主页，任务ID: {}", task.getId());
                return;
            }
            
            // 构建发帖请求
            SocialPostRequest request = buildPostRequest(task);
            
            int successCount = 0;
            int failCount = 0;
            StringBuilder resultBuilder = new StringBuilder();
            
            // 遍历主页和平台进行发帖
            for (SocialHomepage homepage : homepages) {
                for (Integer platformType : platformTypes) {
                    // 检查主页是否支持该平台
                    if (!homepage.getPlatformType().equals(platformType)) {
                        continue;
                    }
                    
                    try {
                        String postId = publishToSocialMedia(homepage, request, platformType);
                        
                        if (postId != null) {
                            // 保存帖子记录
                            saveSocialPost(postId, homepage.getHomepageId(), platformType, 
                                         request, task.getId(), 1, task.getOperatorId());
                            
                            successCount++;
                            resultBuilder.append(String.format("平台%d主页%s发帖成功，帖子ID: %s; ", 
                                                             platformType, homepage.getHomepageId(), postId));
                            
                            logger.info("定时任务发帖成功 - 任务ID: {}, 平台: {}, 主页: {}, 帖子ID: {}", 
                                      task.getId(), platformType, homepage.getHomepageId(), postId);
                        } else {
                            failCount++;
                            resultBuilder.append(String.format("平台%d主页%s发帖失败; ", 
                                                             platformType, homepage.getHomepageId()));
                        }
                    } catch (Exception e) {
                        failCount++;
                        resultBuilder.append(String.format("平台%d主页%s发帖异常: %s; ", 
                                                         platformType, homepage.getHomepageId(), e.getMessage()));
                        logger.error("定时任务发帖异常 - 任务ID: {}, 平台: {}, 主页: {}, 错误: {}", 
                                   task.getId(), platformType, homepage.getHomepageId(), e.getMessage(), e);
                    }
                }
            }
            
            // 更新任务状态
            if (successCount > 0) {
                task.setTaskStatus(1); // 已执行（至少有一个成功）
            } else {
                task.setTaskStatus(2); // 执行失败
            }
            
            task.setExecuteResult(resultBuilder.toString());
            taskRepository.save(task);
            
            logger.info("定时任务执行完成，任务ID: {}, 成功: {}, 失败: {}", task.getId(), successCount, failCount);
            
        } catch (Exception e) {
            // 任务执行异常
            task.setTaskStatus(2); // 执行失败
            task.setExecuteResult("任务执行异常: " + e.getMessage());
            taskRepository.save(task);
            
            logger.error("定时任务执行异常，任务ID: {}, 错误: {}", task.getId(), e.getMessage(), e);
        }
    }
    
    /**
     * 重试失败的任务
     */
    @Transactional
    public void retryTask(SocialPostTask task) {
        logger.info("开始重试失败任务，任务ID: {}, 当前重试次数: {}", task.getId(), task.getRetryCount());
        
        // 增加重试次数
        task.setRetryCount(task.getRetryCount() + 1);
        
        // 重新执行任务
        executeTask(task);
        
        // 如果仍然失败且达到最大重试次数，发送告警
        if (task.getTaskStatus().equals(2) && task.getRetryCount() >= task.getMaxRetryCount()) {
            sendFailureAlert(task);
        }
    }
    
    /**
     * 发送失败告警
     */
    private void sendFailureAlert(SocialPostTask task) {
        logger.error("任务重试失败达到最大次数，任务ID: {}, 任务编号: {}, 重试次数: {}", 
                   task.getId(), task.getTaskNo(), task.getRetryCount());
        
        // TODO: 实现邮件或其他方式的告警通知
        // 可以发送邮件给管理员，或者推送到监控系统
    }
    
    /**
     * 构建发帖请求对象
     */
    private SocialPostRequest buildPostRequest(SocialPostTask task) {
        SocialPostRequest request = new SocialPostRequest();
        
        // 解析主页ID列表
        List<String> homepageIds = Arrays.asList(task.getHomepageIds().split(","));
        request.setHomepageIds(homepageIds);
        
        // 解析平台类型列表
        List<Integer> platformTypes = Arrays.stream(task.getPlatformTypes().split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        request.setPlatformTypes(platformTypes);
        
        request.setPostTitle(task.getPostTitle());
        request.setPostContent(task.getPostContent());
        
        // 解析媒体URL列表
        if (task.getMediaUrls() != null && !task.getMediaUrls().trim().isEmpty()) {
            try {
                List<String> mediaUrls = objectMapper.readValue(task.getMediaUrls(), new TypeReference<List<String>>() {});
                request.setMediaUrls(mediaUrls);
            } catch (Exception e) {
                logger.error("解析媒体URL失败，任务ID: {}, 错误: {}", task.getId(), e.getMessage());
            }
        }
        
        return request;
    }
    
    /**
     * 发布到社交媒体平台
     */
    private String publishToSocialMedia(SocialHomepage homepage, SocialPostRequest request, Integer platformType) {
        try {
            if (platformType == 1) { // Facebook
                return facebookApiService.publishPost(homepage, request);
            } else if (platformType == 2) { // Instagram
                return instagramApiService.publishPost(homepage, request);
            }
        } catch (Exception e) {
            logger.error("发布到平台失败，平台类型: {}, 错误: {}", platformType, e.getMessage(), e);
            throw e;
        }
        return null;
    }
    
    /**
     * 保存帖子记录
     */
    private void saveSocialPost(String postId, String homepageId, Integer platformType,
                               SocialPostRequest request, Long taskId, Integer publishSource,
                               Long operatorId) {
        SocialPost post = new SocialPost();
        post.setPostId(postId);
        post.setHomepageId(homepageId);
        post.setPlatformType(platformType);
        post.setPostTitle(request.getPostTitle());
        post.setPostContent(request.getPostContent());
        
        // 转换媒体URL为JSON字符串
        if (request.getMediaUrls() != null && !request.getMediaUrls().isEmpty()) {
            try {
                post.setMediaUrls(objectMapper.writeValueAsString(request.getMediaUrls()));
            } catch (Exception e) {
                logger.error("转换媒体URL为JSON失败", e);
            }
        }
        
        post.setPublishTime(LocalDateTime.now());
        post.setTaskId(taskId);
        post.setPublishSource(publishSource);
        post.setOperatorId(operatorId);
        
        postRepository.save(post);
    }
}