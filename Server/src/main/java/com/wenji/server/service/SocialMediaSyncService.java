package com.wenji.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wenji.server.model.SocialComment;
import com.wenji.server.model.SocialPost;
import com.wenji.server.model.SocialHomepage;
import com.wenji.server.repository.SocialCommentRepository;
import com.wenji.server.repository.SocialHomepageRepository;
import com.wenji.server.repository.SocialPostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 社交媒体数据同步服务
 * 负责定时同步和即时同步帖子及评论数据
 */
@Service
public class SocialMediaSyncService {

    private static final Logger logger = LoggerFactory.getLogger(SocialMediaSyncService.class);
    private static final int SYNC_INTERVAL_MINUTES = 60; // 同步间隔，单位：分钟
    private static final int THREAD_POOL_SIZE = 5; // 线程池大小

    @Autowired
    private SocialHomepageRepository socialHomepageRepository;

    @Autowired
    private SocialPostRepository socialPostRepository;

    @Autowired
    private SocialCommentRepository socialCommentRepository;

    @Autowired
    private FacebookApiService facebookApiService;

    @Autowired
    private InstagramApiService instagramApiService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    /**
     * 定时同步所有帖子和评论数据
     * 每60分钟执行一次
     */
    @Scheduled(fixedRate = SYNC_INTERVAL_MINUTES * 60 * 1000) // 60分钟 = 60*60*1000毫秒
    public void syncAllSocialMediaData() {
        logger.info("开始定时同步所有社交媒体数据，当前时间: {}", LocalDateTime.now());
        
        try {
            // 获取所有有效的主页
            List<SocialHomepage> homepages = socialHomepageRepository.findByStatus(1); // 1表示启用状态
            
            if (homepages.isEmpty()) {
                logger.info("没有有效的社交媒体主页需要同步");
                return;
            }
            
            logger.info("发现 {} 个有效的社交媒体主页需要同步数据", homepages.size());
            
            // 并行同步每个主页的数据
            List<CompletableFuture<Void>> futures = homepages.stream()
                    .map(homepage -> CompletableFuture.runAsync(() -> {
                        try {
                            syncHomepageData(homepage);
                        } catch (Exception e) {
                            logger.error("同步主页数据异常，主页ID: {}, 平台: {}, 错误: {}",
                                    homepage.getHomepageId(), homepage.getPlatformType(), e.getMessage(), e);
                        }
                    }, executorService))
                    .toList();
            
            // 等待所有同步任务完成
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            
            logger.info("定时同步所有社交媒体数据完成，当前时间: {}", LocalDateTime.now());
            
        } catch (Exception e) {
            logger.error("定时同步所有社交媒体数据异常: {}", e.getMessage(), e);
        }
    }

    /**
     * 同步指定主页的帖子和评论数据
     */
    @Transactional
    public void syncHomepageData(SocialHomepage homepage) {
        try {
            logger.info("开始同步主页数据，主页名称: {}, 平台类型: {}",
                    homepage.getHomepageName(), homepage.getPlatformType());
            
            // 获取主页的帖子列表
            List<Map<String, Object>> posts;
            if (homepage.getPlatformType() == 1) { // Facebook
                posts = facebookApiService.getPosts(homepage.getHomepageId(), homepage.getAccessToken());
            } else if (homepage.getPlatformType() == 2) { // Instagram
                posts = instagramApiService.getPosts(homepage.getHomepageId(), homepage.getAccessToken());
            } else {
                logger.warn("不支持的平台类型: {}", homepage.getPlatformType());
                return;
            }
            
            if (posts == null || posts.isEmpty()) {
                logger.info("未获取到主页 {} 的帖子数据", homepage.getHomepageName());
                return;
            }
            
            // 同步每个帖子及其评论
            for (Map<String, Object> postData : posts) {
                syncPostAndComments(homepage, postData);
            }
            
            logger.info("主页 {} 数据同步完成，共同步 {} 个帖子",
                    homepage.getHomepageName(), posts.size());
            
        } catch (Exception e) {
            logger.error("同步主页 {} 数据异常: {}",
                    homepage.getHomepageName(), e.getMessage(), e);
        }
    }

    /**
     * 同步单个帖子及其评论
     */
    @Transactional
    public void syncPostAndComments(SocialHomepage homepage, Map<String, Object> postData) {
        try {
            String postId = (String) postData.get("post_id");
            
            // 保存或更新帖子
            updateSocialPost(homepage, postData);
            
            // 同步评论
            syncPostComments(homepage, postId);
            
        } catch (Exception e) {
            logger.error("同步帖子及其评论异常: {}", e.getMessage(), e);
        }
    }

    /**
     * 更新帖子数据
     */
    @Transactional
    public void updateSocialPost(SocialHomepage homepage, Map<String, Object> postData) {
        try {
            String postId = (String) postData.get("post_id");
            
            // 查找现有帖子
            SocialPost post = socialPostRepository.findByPostId(postId);
            boolean isNewPost = false;
            
            if (post == null) {
                post = new SocialPost();
                post.setPostId(postId);
                post.setHomepageId(homepage.getHomepageId());
                post.setPlatformType(homepage.getPlatformType());
                post.setOperatorId(homepage.getUserId());
                isNewPost = true;
            }
            
            // 更新帖子数据
            post.setPostTitle((String) postData.getOrDefault("post_title", ""));
            post.setPostContent((String) postData.get("post_content"));
            
            // 处理媒体URL
            if (postData.containsKey("media_urls")) {
                try {
                    List<String> mediaUrls = (List<String>) postData.get("media_urls");
                    if (mediaUrls != null && !mediaUrls.isEmpty()) {
                        post.setMediaUrls(objectMapper.writeValueAsString(mediaUrls));
                    }
                } catch (Exception e) {
                    logger.error("转换媒体URL为JSON失败", e);
                }
            }
            
            // 更新其他字段
            post.setPublishTime((LocalDateTime) postData.get("publish_time"));
            
            // 保存更新
            socialPostRepository.save(post);
            
            logger.info("{}{} 帖子数据同步成功，帖子ID: {}",
                    isNewPost ? "新增" : "更新", 
                    homepage.getPlatformType() == 1 ? "Facebook" : "Instagram",
                    postId);
            
        } catch (Exception e) {
            logger.error("更新帖子数据异常: {}", e.getMessage(), e);
        }
    }

    /**
     * 同步指定帖子的评论
     */
    @Transactional
    public void syncPostComments(SocialHomepage homepage, String postId) {
        try {
            // 获取评论列表
            List<Map<String, Object>> comments;
            if (homepage.getPlatformType() == 1) { // Facebook
                comments = facebookApiService.getPostComments(postId, homepage.getAccessToken());
            } else if (homepage.getPlatformType() == 2) { // Instagram
                comments = instagramApiService.getPostComments(postId, homepage.getAccessToken());
            } else {
                logger.warn("不支持的平台类型: {}", homepage.getPlatformType());
                return;
            }
            
            if (comments == null || comments.isEmpty()) {
                logger.info("帖子 {} 没有评论数据需要同步", postId);
                return;
            }
            
            // 同步每个评论
            for (Map<String, Object> commentData : comments) {
                updateSocialComment(homepage, postId, commentData);
            }
            
            logger.info("帖子 {} 的评论数据同步完成，共同步 {} 条评论", postId, comments.size());
            
        } catch (Exception e) {
            logger.error("同步帖子 {} 的评论异常: {}", postId, e.getMessage(), e);
        }
    }

    /**
     * 更新评论数据
     */
    @Transactional
    public void updateSocialComment(SocialHomepage homepage, String postId, Map<String, Object> commentData) {
        try {
            String commentId = (String) commentData.get("comment_id");
            
            // 查找现有评论
            SocialComment comment = socialCommentRepository.findByCommentId(commentId);
            boolean isNewComment = false;
            
            if (comment == null) {
                comment = new SocialComment();
                comment.setCommentId(commentId);
                comment.setPostId(postId);
                comment.setHomepageId(homepage.getHomepageId());
                comment.setPlatformType(homepage.getPlatformType());
                isNewComment = true;
            }
            
            // 更新评论数据
            comment.setCommentContent((String) commentData.get("comment_content"));
            comment.setCommenterName((String) commentData.get("commenter_name"));
            // 设置用户ID，如果评论者ID存在则尝试转换为Long类型
            String commenterIdStr = (String) commentData.getOrDefault("commenter_id", "");
            if (commenterIdStr != null && !commenterIdStr.isEmpty()) {
                try {
                    comment.setUserId(Long.parseLong(commenterIdStr));
                } catch (NumberFormatException e) {
                    // 如果无法转换为Long，记录日志但不影响主要功能
                    logger.warn("评论者ID无法转换为Long类型: {}", commenterIdStr);
                }
            }
            comment.setCommenterAvatar((String) commentData.getOrDefault("commenter_avatar", ""));
            comment.setLikeCount((Integer) commentData.getOrDefault("like_count", 0));
            comment.setCommentTime((LocalDateTime) commentData.get("comment_time"));
            
            // 处理父评论ID
            if (commentData.containsKey("parent_comment_id")) {
                comment.setParentCommentId((String) commentData.get("parent_comment_id"));
            }
            
            // 保存更新
            socialCommentRepository.save(comment);
            
            logger.debug("{}{} 评论数据同步成功，评论ID: {}",
                    isNewComment ? "新增" : "更新",
                    homepage.getPlatformType() == 1 ? "Facebook" : "Instagram",
                    commentId);
            
        } catch (Exception e) {
            logger.error("更新评论数据异常: {}", e.getMessage(), e);
        }
    }

    /**
     * 即时同步单个帖子数据
     * 用于发帖后立即同步
     */
    @Transactional
    public void syncSinglePostImmediately(SocialHomepage homepage, String postId) {
        try {
            logger.info("开始即时同步帖子数据，平台: {}, 主页: {}, 帖子ID: {}",
                    homepage.getPlatformType() == 1 ? "Facebook" : "Instagram",
                    homepage.getHomepageId(), postId);
            
            // 获取帖子详情
            Map<String, Object> postData;
            if (homepage.getPlatformType() == 1) { // Facebook
                postData = facebookApiService.getPostDetails(postId, homepage.getAccessToken());
            } else if (homepage.getPlatformType() == 2) { // Instagram
                postData = instagramApiService.getPostDetails(postId, homepage.getAccessToken());
            } else {
                logger.warn("不支持的平台类型: {}", homepage.getPlatformType());
                return;
            }
            
            if (postData != null) {
                // 更新帖子数据
                updateSocialPost(homepage, postData);
                
                // 同步评论数据
                syncPostComments(homepage, postId);
                
                logger.info("帖子 {} 即时同步完成", postId);
            } else {
                logger.warn("未能获取到帖子 {} 的详情数据", postId);
            }
            
        } catch (Exception e) {
            logger.error("即时同步帖子 {} 异常: {}", postId, e.getMessage(), e);
        }
    }

    /**
     * 即时同步单个评论数据
     * 用于回评后立即同步
     */
    @Transactional
    public void syncSingleCommentImmediately(SocialHomepage homepage, String postId, String commentId) {
        try {
            logger.info("开始即时同步评论数据，平台: {}, 主页: {}, 帖子ID: {}, 评论ID: {}",
                    homepage.getPlatformType() == 1 ? "Facebook" : "Instagram",
                    homepage.getHomepageId(), postId, commentId);
            
            // 获取评论详情
            Map<String, Object> commentData;
            if (homepage.getPlatformType() == 1) { // Facebook
                commentData = facebookApiService.getCommentDetails(commentId, homepage.getAccessToken());
            } else if (homepage.getPlatformType() == 2) { // Instagram
                commentData = instagramApiService.getCommentDetails(commentId, homepage.getAccessToken());
            } else {
                logger.warn("不支持的平台类型: {}", homepage.getPlatformType());
                return;
            }
            
            if (commentData != null) {
                // 更新评论数据
                updateSocialComment(homepage, postId, commentData);
                
                logger.info("评论 {} 即时同步完成", commentId);
            } else {
                logger.warn("未能获取到评论 {} 的详情数据", commentId);
            }
            
        } catch (Exception e) {
            logger.error("即时同步评论 {} 异常: {}", commentId, e.getMessage(), e);
        }
    }
}