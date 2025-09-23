package com.wenji.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wenji.server.dto.SocialPostRequest;
import com.wenji.server.dto.SocialPostResponse;
import com.wenji.server.model.SocialHomepage;
import com.wenji.server.model.SocialPost;
import com.wenji.server.model.SocialPostTask;
import com.wenji.server.repository.SocialHomepageRepository;
import com.wenji.server.repository.SocialPostRepository;
import com.wenji.server.repository.SocialPostTaskRepository;
import com.wenji.server.repository.SocialCommentRepository;
import com.wenji.server.model.SocialComment;
import com.wenji.server.service.SocialMediaSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

@Service
public class SocialMediaService {
    
    private static final Logger logger = LoggerFactory.getLogger(SocialMediaService.class);
    
    @Autowired
    private SocialHomepageRepository homepageRepository;
    
    @Autowired
    private SocialPostRepository postRepository;
    
    @Autowired
    private SocialPostTaskRepository taskRepository;
    
    @Autowired
    private SocialCommentRepository socialCommentRepository;
    
    @Autowired
    private FacebookApiService facebookApiService;
    
    @Autowired
    private InstagramApiService instagramApiService;
    
    @Autowired
    private SocialMediaSyncService socialMediaSyncService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 即时发帖
     */
    @Transactional
    public SocialPostResponse publishPost(SocialPostRequest request, Long operatorId) {
        logger.info("开始即时发帖，操作人ID: {}", operatorId);
        
        // 验证主页权限
        List<SocialHomepage> homepages = validateHomepages(request.getHomepageIds(), operatorId);
        
        Map<String, String> postIds = new HashMap<>();
        int successCount = 0;
        int failCount = 0;
        
        // 转换媒体URL为JSON字符串
        String mediaUrlsJson = convertMediaUrlsToJson(request.getMediaUrls());
        
        // 遍历主页和平台进行发帖
        for (SocialHomepage homepage : homepages) {
            for (Integer platformType : request.getPlatformTypes()) {
                // 检查主页是否支持该平台
                if (!homepage.getPlatformType().equals(platformType)) {
                    continue;
                }
                
                try {
                    String postId = publishToSocialMedia(homepage, request, platformType);
                    
                    if (postId != null) {
                        // 保存帖子记录
                        saveSocialPost(postId, homepage.getHomepageId(), platformType, 
                                     request, null, 0, operatorId, mediaUrlsJson);
                        
                        // 发帖成功后立即同步帖子数据
                        socialMediaSyncService.syncSinglePostImmediately(homepage, postId);
                        
                        String key = platformType + "_" + homepage.getHomepageId();
                        postIds.put(key, postId);
                        successCount++;
                        
                        logger.info("发帖成功 - 平台: {}, 主页: {}, 帖子ID: {}", 
                                  platformType, homepage.getHomepageId(), postId);
                    } else {
                        failCount++;
                        logger.error("发帖失败 - 平台: {}, 主页: {}", platformType, homepage.getHomepageId());
                    }
                } catch (Exception e) {
                    failCount++;
                    logger.error("发帖异常 - 平台: {}, 主页: {}, 错误: {}", 
                               platformType, homepage.getHomepageId(), e.getMessage(), e);
                }
            }
        }
        
        return new SocialPostResponse(successCount, failCount, postIds);
    }
    
    /**
     * 创建定时发帖任务
     */
    @Transactional
    public SocialPostResponse schedulePost(SocialPostRequest request, Long operatorId) {
        logger.info("创建定时发帖任务，操作人ID: {}, 定时时间: {}", operatorId, request.getScheduleTime());
        
        // 验证定时时间
        if (request.getScheduleTime() == null || request.getScheduleTime().isBefore(LocalDateTime.now().plusMinutes(1))) {
            throw new IllegalArgumentException("定时时间不能为空且必须是未来时间（至少1分钟后）");
        }
        
        // 验证主页权限
        validateHomepages(request.getHomepageIds(), operatorId);
        
        // 创建任务
        SocialPostTask task = new SocialPostTask();
        task.setTaskNo(generateTaskNo());
        task.setHomepageIds(String.join(",", request.getHomepageIds()));
        task.setPlatformTypes(request.getPlatformTypes().stream()
                .map(String::valueOf)
                .reduce((a, b) -> a + "," + b)
                .orElse(""));
        task.setPostTitle(request.getPostTitle());
        task.setPostContent(request.getPostContent());
        task.setMediaUrls(convertMediaUrlsToJson(request.getMediaUrls()));
        task.setScheduleTime(request.getScheduleTime());
        task.setOperatorId(operatorId);
        
        task = taskRepository.save(task);
        
        SocialPostResponse response = new SocialPostResponse();
        response.setTaskId(task.getId());
        response.setTaskNo(task.getTaskNo());
        response.setScheduleTime(task.getScheduleTime());
        response.setTaskStatus(task.getTaskStatus());
        
        logger.info("定时任务创建成功，任务ID: {}, 任务编号: {}", task.getId(), task.getTaskNo());
        
        return response;
    }
    
    /**
     * 获取定时任务列表
     */
    public Page<SocialPostTask> getTaskList(Integer taskStatus, Pageable pageable) {
        if (taskStatus != null) {
            return taskRepository.findByTaskStatusOrderByCreatedAtDesc(taskStatus, pageable);
        } else {
            return taskRepository.findAllByOrderByCreatedAtDesc(pageable);
        }
    }
    
    /**
     * 取消定时任务
     */
    @Transactional
    public void cancelTask(Long taskId, Long operatorId) {
        SocialPostTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("任务不存在"));
        
        if (!task.getOperatorId().equals(operatorId)) {
            throw new IllegalArgumentException("无权限操作此任务");
        }
        
        if (!task.getTaskStatus().equals(0)) {
            throw new IllegalArgumentException("只能取消待执行的任务");
        }
        
        task.setTaskStatus(3); // 已取消
        taskRepository.save(task);
        
        logger.info("任务取消成功，任务ID: {}, 操作人: {}", taskId, operatorId);
    }
    
    /**
     * 获取用户的主页列表
     */
    public List<SocialHomepage> getUserHomepages(Long userId) {
        return homepageRepository.findByUserIdAndStatus(userId, 1);
    }
    
    /**
     * 获取帖子列表
     */
    public Page<SocialPost> getPostList(Pageable pageable) {
        return postRepository.findAll(pageable);
    }
    
    // ==================== Facebook 专用方法 ====================
    
    @Value("${facebook.app.id}")
    private String facebookAppId;
    
    @Value("${facebook.app.secret}")
    private String facebookAppSecret;
    
    @Value("${facebook.redirect.uri}")
    private String facebookRedirectUri;
    
    @Value("${facebook.scope}")
    private String facebookScope;
    
    @Value("${facebook.feature.enabled:true}")
    private boolean facebookFeatureEnabled;
    
    /**
     * 获取Facebook授权URL
     */
    public String getFacebookAuthUrl(String redirectUrl) {
        // 检查Facebook功能是否启用
        if (!facebookFeatureEnabled) {
            logger.warn("Facebook功能当前不可用");
            throw new RuntimeException("Facebook功能当前不可用，我们正在更新这款应用的其他详情。请稍后重试。");
        }
        
        // 使用提供的redirectUrl或默认的redirectUri
        String finalRedirectUri = (redirectUrl != null && !redirectUrl.isEmpty()) ? redirectUrl : facebookRedirectUri;
        
        // 生成state参数，用于防止CSRF攻击并传递原始重定向URL
        String state = Base64.getEncoder().encodeToString(finalRedirectUri.getBytes(StandardCharsets.UTF_8));
        
        logger.info("生成Facebook授权URL，App ID: {}, Redirect URI: {}", facebookAppId, finalRedirectUri);
        
        // 更新为统一的Facebook API版本V23
        return String.format(
            "https://www.facebook.com/v23.0/dialog/oauth?client_id=%s&redirect_uri=%s&scope=%s&response_type=code&state=%s",
            facebookAppId, finalRedirectUri, facebookScope, state
        );
    }
    
    /**
     * 处理Facebook授权回调
     */
    @Transactional
    public boolean handleFacebookCallback(String code, String redirectUrl, Long userId) {
        try {
            // 1. 从授权码获取访问令牌
            Map<String, Object> tokenInfo = facebookApiService.getAccessToken(
                code, facebookAppId, facebookAppSecret, redirectUrl
            );
            
            if (tokenInfo == null || !tokenInfo.containsKey("access_token")) {
                logger.error("获取访问令牌失败，未返回有效的token信息");
                return false;
            }
            
            // 2. 获取访问令牌
            String accessToken = (String) tokenInfo.get("access_token");
            
            // 3. 保存Facebook访问令牌和主页信息
            saveFacebookToken(userId, accessToken);
            
            return true;
        } catch (Exception e) {
            logger.error("处理Facebook授权回调失败: {}", e.getMessage(), e);
            throw new RuntimeException("处理Facebook授权回调失败: " + e.getMessage());
        }
    }
    
    /**
     * 保存Facebook访问令牌
     */
    /**
     * 验证Facebook访问令牌的有效性
     */
    public boolean validateFacebookToken(String accessToken) {
        logger.info("验证Facebook访问令牌有效性");
        try {
            return facebookApiService.validateAccessToken(accessToken);
        } catch (Exception e) {
            logger.error("验证Facebook访问令牌异常: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 验证Facebook令牌是否有权限访问指定页面
     */
    public boolean verifyFacebookPageAccess(String accessToken, String pageId) {
        logger.info("验证Facebook令牌是否有权限访问页面: {}", pageId);
        try {
            List<Map<String, Object>> pages = facebookApiService.getUserPages(accessToken);
            if (pages != null && !pages.isEmpty()) {
                for (Map<String, Object> page : pages) {
                    if (page.containsKey("id") && pageId.equals(page.get("id"))) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            logger.error("验证Facebook页面访问权限异常: {}", e.getMessage());
            return false;
        }
    }
    
    @Transactional
    public void saveFacebookToken(Long userId, String accessToken) {
        try {
            // 1. 验证访问令牌有效性
            if (!facebookApiService.validateAccessToken(accessToken)) {
                throw new RuntimeException("Facebook访问令牌无效");
            }
            
            // 2. 获取用户的Facebook页面信息
            List<Map<String, Object>> pages = facebookApiService.getUserPages(accessToken);
            if (pages == null || pages.isEmpty()) {
                throw new RuntimeException("未找到关联的Facebook页面");
            }
            
            // 3. 保存或更新用户的Facebook绑定信息
            for (Map<String, Object> page : pages) {
                String pageId = String.valueOf(page.get("id"));
                String pageName = String.valueOf(page.get("name"));
                String pageAccessToken = String.valueOf(page.get("access_token"));
                
                // 检查是否已存在该页面的绑定
                Optional<SocialHomepage> homepageOpt = homepageRepository.findByHomepageIdAndPlatformType(pageId, 1);
                SocialHomepage homepage;
                if (!homepageOpt.isPresent()) {
                    // 创建新的主页绑定
                    homepage = new SocialHomepage();
                    homepage.setHomepageId(pageId);
                    homepage.setHomepageName(pageName);
                    homepage.setPlatformType(1); // 1表示Facebook
                    homepage.setUserId(userId);
                } else {
                    homepage = homepageOpt.get();
                }
                
                // 更新访问令牌信息
                homepage.setAccessToken(pageAccessToken);
                // Facebook令牌有效期通常为60天，这里设置为50天作为缓冲
                homepage.setTokenExpiresAt(LocalDateTime.now().plusDays(50));
                homepage.setUpdatedAt(LocalDateTime.now());
                
                homepageRepository.save(homepage);
                logger.info("已保存Facebook主页信息: {}, 页面ID: {}, 用户ID: {}", pageName, pageId, userId);
            }
            
            logger.info("保存Facebook访问令牌成功，用户ID: {}, 绑定页面数: {}", userId, pages.size());
        } catch (Exception e) {
            logger.error("保存Facebook访问令牌失败，用户ID: {}", userId, e);
            throw new RuntimeException("保存Facebook访问令牌失败: " + e.getMessage());
        }
    }
    
    /**
     * 解绑Facebook账号
     */
    @Transactional
    public void unbindFacebook(Long userId) {
        try {
            // 1. 查询用户关联的所有Facebook主页
            List<SocialHomepage> homepages = homepageRepository.findByUserIdAndPlatformTypeAndStatus(userId, 1, 1);
            if (homepages.isEmpty()) {
                logger.info("用户没有绑定的Facebook账号，用户ID: {}", userId);
                return;
            }
            
            // 2. 删除所有关联的主页信息
            for (SocialHomepage homepage : homepages) {
                logger.info("解除绑定Facebook主页: {}, 页面ID: {}", homepage.getHomepageName(), homepage.getHomepageId());
                homepageRepository.delete(homepage);
            }
            
            logger.info("解绑Facebook账号成功，用户ID: {}, 解绑主页数: {}", userId, homepages.size());
        } catch (Exception e) {
            logger.error("解绑Facebook账号失败，用户ID: {}", userId, e);
            throw new RuntimeException("解绑Facebook账号失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取Facebook帖子列表
     */
    public Page<SocialPost> getFacebookPosts(Long userId, int page, int size) {
        try {
            // TODO: 实现获取Facebook帖子的逻辑
            // 1. 获取用户绑定的Facebook页面
            // 2. 调用Facebook API获取帖子
            // 3. 转换为统一的SocialPost格式
            
            logger.info("获取Facebook帖子列表，用户ID: {}, 页码: {}, 大小: {}", userId, page, size);
            
            // 暂时返回空结果
            return Page.empty();
        } catch (Exception e) {
            logger.error("获取Facebook帖子失败，用户ID: {}", userId, e);
            throw new RuntimeException("获取Facebook帖子失败: " + e.getMessage());
        }
    }
    
    // ==================== Instagram 专用方法 ====================
    
    /**
     * 获取Instagram Business Pages
     */
    public List<Map<String, Object>> getInstagramPages(Long userId) {
        try {
            // 1. 获取用户关联的Facebook主页，用于获取Facebook访问令牌
            List<SocialHomepage> facebookHomepages = homepageRepository.findByUserIdAndPlatformTypeAndStatus(userId, 1, 1);
            if (facebookHomepages.isEmpty()) {
                throw new RuntimeException("未找到绑定的Facebook账号，请先绑定Facebook");
            }
            
            // 2. 使用第一个Facebook主页的访问令牌来获取Instagram Business账号
            SocialHomepage facebookHomepage = facebookHomepages.get(0);
            String accessToken = facebookHomepage.getAccessToken();
            String facebookPageId = facebookHomepage.getHomepageId();
            
            // 3. 通过Facebook API获取关联的Instagram Business账号
            List<Map<String, Object>> instagramPages = instagramApiService.getInstagramBusinessPages(facebookPageId, accessToken);
            
            logger.info("获取Instagram Pages成功，用户ID: {}, 页面数: {}", userId, instagramPages.size());
            
            return instagramPages;
        } catch (Exception e) {
            logger.error("获取Instagram Pages失败，用户ID: {}", userId, e);
            throw new RuntimeException("获取Instagram Pages失败: " + e.getMessage());
        }
    }
    
    /**
     * 选择Instagram Page
     */
    @Transactional
    public void selectInstagramPage(Long userId, String pageId) {
        try {
            // 1. 获取用户已绑定的Facebook主页，用于获取访问令牌
            List<SocialHomepage> facebookHomepages = homepageRepository.findByUserIdAndPlatformTypeAndStatus(userId, 1, 1);
            if (facebookHomepages.isEmpty()) {
                throw new RuntimeException("未找到绑定的Facebook账号");
            }
            
            // 2. 验证页面ID的有效性
            String accessToken = facebookHomepages.get(0).getAccessToken();
            Map<String, Object> instagramPage = instagramApiService.getInstagramPageInfo(pageId, accessToken);
            if (instagramPage == null) {
                throw new RuntimeException("Instagram页面信息无效或不存在");
            }
            
            // 3. 保存用户选择的Instagram页面
            String pageName = String.valueOf(instagramPage.get("username"));
            
            // 检查是否已存在该Instagram页面的绑定
            Optional<SocialHomepage> homepageOpt = homepageRepository.findByHomepageIdAndPlatformType(pageId, 2);
            SocialHomepage homepage;
            if (!homepageOpt.isPresent()) {
                // 创建新的主页绑定
                homepage = new SocialHomepage();
                homepage.setHomepageId(pageId);
                homepage.setHomepageName(pageName);
                homepage.setPlatformType(2); // 2表示Instagram
                homepage.setUserId(userId);
            } else {
                homepage = homepageOpt.get();
            }
            
            // 更新访问令牌信息
            homepage.setAccessToken(accessToken);
            // 设置令牌有效期（与Facebook相同）
            homepage.setTokenExpiresAt(LocalDateTime.now().plusDays(50));
            homepage.setUpdatedAt(LocalDateTime.now());
            
            homepageRepository.save(homepage);
            
            logger.info("选择Instagram Page成功，用户ID: {}, 页面ID: {}, 页面名称: {}", userId, pageId, pageName);
        } catch (Exception e) {
            logger.error("选择Instagram Page失败，用户ID: {}, 页面ID: {}", userId, pageId, e);
            throw new RuntimeException("选择Instagram Page失败: " + e.getMessage());
        }
    }
    
    /**
     * 解绑Instagram账号
     */
    @Transactional
    public void unbindInstagram(Long userId) {
        try {
            // 1. 查询用户关联的所有Instagram主页
            List<SocialHomepage> homepages = homepageRepository.findByUserIdAndPlatformTypeAndStatus(userId, 2, 1);
            if (homepages.isEmpty()) {
                logger.info("用户没有绑定的Instagram账号，用户ID: {}", userId);
                return;
            }
            
            // 2. 删除所有关联的主页信息
            for (SocialHomepage homepage : homepages) {
                logger.info("解除绑定Instagram主页: {}, 页面ID: {}", homepage.getHomepageName(), homepage.getHomepageId());
                homepageRepository.delete(homepage);
            }
            
            logger.info("解绑Instagram账号成功，用户ID: {}, 解绑主页数: {}", userId, homepages.size());
        } catch (Exception e) {
            logger.error("解绑Instagram账号失败，用户ID: {}", userId, e);
            throw new RuntimeException("解绑Instagram账号失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取Instagram帖子列表
     */
    public Page<SocialPost> getInstagramPosts(Long userId, int page, int size) {
        try {
            // TODO: 实现获取Instagram帖子的逻辑
            // 1. 获取用户绑定的Instagram页面
            // 2. 调用Instagram API获取帖子
            // 3. 转换为统一的SocialPost格式
            
            logger.info("获取Instagram帖子列表，用户ID: {}, 页码: {}, 大小: {}", userId, page, size);
            
            // 暂时返回空结果
            return Page.empty();
        } catch (Exception e) {
            logger.error("获取Instagram帖子失败，用户ID: {}", userId, e);
            throw new RuntimeException("获取Instagram帖子失败: " + e.getMessage());
        }
    }

    /**
     * 重试失败任务
     */
    @Transactional
    public void retryTask(Long taskId, Long operatorId) {
        Optional<SocialPostTask> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            throw new IllegalArgumentException("任务不存在");
        }
        
        SocialPostTask task = taskOpt.get();
        if (task.getTaskStatus() != 4) { // 4表示执行失败
            throw new IllegalArgumentException("只能重试失败的任务");
        }
        
        // 重置任务状态为待执行
        task.setTaskStatus(0);
        task.setUpdatedAt(LocalDateTime.now());
        taskRepository.save(task);
        
        logger.info("任务重试成功，任务ID: {}, 操作人ID: {}", taskId, operatorId);
    }
    
    /**
     * 验证主页权限
     */
    private List<SocialHomepage> validateHomepages(List<String> homepageIds, Long operatorId) {
        List<SocialHomepage> homepages = homepageRepository.findByHomepageIdsAndStatusEnabled(homepageIds);
        
        if (homepages.isEmpty()) {
            throw new IllegalArgumentException("未找到有效的主页");
        }
        
        // 检查权限（这里简化处理，实际应该检查用户是否有权限管理这些主页）
        for (SocialHomepage homepage : homepages) {
            if (!homepage.getUserId().equals(operatorId)) {
                throw new IllegalArgumentException("无权限操作主页: " + homepage.getHomepageName());
            }
        }
        
        return homepages;
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
        }
        return null;
    }
    
    /**
     * 保存帖子记录
     */
    private void saveSocialPost(String postId, String homepageId, Integer platformType,
                               SocialPostRequest request, Long taskId, Integer publishSource,
                               Long operatorId, String mediaUrlsJson) {
        SocialPost post = new SocialPost();
        post.setPostId(postId);
        post.setHomepageId(homepageId);
        post.setPlatformType(platformType);
        post.setPostTitle(request.getPostTitle());
        post.setPostContent(request.getPostContent());
        post.setMediaUrls(mediaUrlsJson);
        post.setPublishTime(LocalDateTime.now());
        post.setTaskId(taskId);
        post.setPublishSource(publishSource);
        post.setOperatorId(operatorId);
        
        postRepository.save(post);
    }
    
    /**
     * 生成任务编号
     */
    private String generateTaskNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomStr = String.format("%03d", new Random().nextInt(1000));
        return "TASK_" + dateStr + randomStr;
    }
    
    /**
     * 转换媒体URL列表为JSON字符串
     */
    private String convertMediaUrlsToJson(List<String> mediaUrls) {
        if (mediaUrls == null || mediaUrls.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(mediaUrls);
        } catch (JsonProcessingException e) {
            logger.error("转换媒体URL为JSON失败", e);
            return null;
        }
    }
    
    /**
     * 获取帖子的评论列表
     * @param postId 帖子ID
     * @param platformType 平台类型
     * @param accessToken 访问令牌
     * @return 评论列表
     */
    public List<SocialComment> getPostComments(String postId, String platformType, String accessToken) {
        // 从数据库获取评论
        List<SocialComment> comments = socialCommentRepository.findByPostId(postId);
        
        // 如果数据库没有评论，则从API获取并保存
        if (comments.isEmpty()) {
            List<Map<String, Object>> apiComments = null;
            
            if ("FACEBOOK".equals(platformType)) {
                apiComments = facebookApiService.getPostComments(postId, accessToken);
            } else if ("INSTAGRAM".equals(platformType)) {
                apiComments = instagramApiService.getPostComments(postId, accessToken);
            }
            
            if (apiComments != null && !apiComments.isEmpty()) {
                for (Map<String, Object> apiComment : apiComments) {
                    SocialComment comment = new SocialComment();
                    comment.setCommentId((String) apiComment.get("comment_id"));
                    comment.setPostId(postId);
                    // 将平台类型字符串转换为整数类型
                    Integer platformTypeInt = "FACEBOOK".equals(platformType) ? 1 : ("INSTAGRAM".equals(platformType) ? 2 : 0);
                    comment.setPlatformType(platformTypeInt);
                    comment.setCommentContent((String) apiComment.get("comment_content"));
                    comment.setCommenterName((String) apiComment.get("commenter_name"));
                    // 设置用户ID，如果评论者ID存在则尝试转换为Long类型
                    String commenterIdStr = (String) apiComment.get("commenter_id");
                    if (commenterIdStr != null && !commenterIdStr.isEmpty()) {
                        try {
                            comment.setUserId(Long.parseLong(commenterIdStr));
                        } catch (NumberFormatException e) {
                            // 如果无法转换为Long，记录日志但不影响主要功能
                            logger.warn("评论者ID无法转换为Long类型: {}", commenterIdStr);
                        }
                    }
                    comment.setCommentTime((LocalDateTime) apiComment.get("comment_time"));
                    comment.setLikeCount((Integer) apiComment.get("like_count"));
                    comment.setParentCommentId((String) apiComment.get("parent_comment_id"));
                    
                    socialCommentRepository.save(comment);
                    comments.add(comment);
                }
            }
        }
        
        return comments;
    }
    
    /**
     * 回复评论
     */
    @Transactional
    public String replyToComment(String postId, String commentId, String replyContent, Long operatorId) {
        logger.info("开始回复评论，帖子ID: {}, 评论ID: {}, 操作人ID: {}", postId, commentId, operatorId);
        
        // 查找帖子
        SocialPost post = postRepository.findByPostId(postId);
        if (post == null) {
            throw new IllegalArgumentException("帖子不存在");
        }
        
        // 查找主页
        SocialHomepage homepage = homepageRepository.findByHomepageId(post.getHomepageId());
        if (homepage == null) {
            throw new IllegalArgumentException("主页不存在");
        }
        
        // 验证操作人权限
        validateHomepageAccess(homepage.getHomepageId(), operatorId);
        
        // 调用平台API回复评论
        String replyId = null;
        try {
            if (homepage.getPlatformType() == 1) { // Facebook
                replyId = facebookApiService.replyToComment(commentId, replyContent, homepage.getAccessToken());
            } else if (homepage.getPlatformType() == 2) { // Instagram
                replyId = instagramApiService.replyToComment(commentId, replyContent, homepage.getAccessToken());
            } else {
                throw new IllegalArgumentException("不支持的平台类型: " + homepage.getPlatformType());
            }
            
            if (replyId != null) {
                // 回复成功后立即同步评论数据
                socialMediaSyncService.syncSingleCommentImmediately(homepage, postId, replyId);
                
                logger.info("回复评论成功，回复ID: {}", replyId);
            } else {
                throw new RuntimeException("回复评论失败");
            }
        } catch (Exception e) {
            logger.error("回复评论异常: {}", e.getMessage(), e);
            throw new RuntimeException("回复评论异常: " + e.getMessage());
        }
        
        return replyId;
    }
    
    /**
     * 验证主页访问权限
     */
    private void validateHomepageAccess(String homepageId, Long operatorId) {
        SocialHomepage homepage = homepageRepository.findByHomepageId(homepageId);
        if (homepage == null || !homepage.getUserId().equals(operatorId)) {
            throw new IllegalArgumentException("无权限操作该主页");
        }
    }
    
    /**
     * 删除评论
     * @param commentId 评论ID
     * @param platformType 平台类型
     * @param accessToken 访问令牌
     * @return 删除是否成功
     */
    @Transactional
    public boolean deleteComment(String commentId, String platformType, String accessToken) {
        boolean success = false;
        
        // 先调用平台API删除评论
        if ("FACEBOOK".equals(platformType)) {
            success = facebookApiService.deleteComment(commentId, accessToken);
        } else if ("INSTAGRAM".equals(platformType)) {
            success = instagramApiService.deleteComment(commentId, accessToken);
        }
        
        // 如果API删除成功，则从数据库中删除或标记为已删除
        if (success) {
            SocialComment comment = socialCommentRepository.findByCommentId(commentId);
            if (comment != null) {
                // 设置状态为已删除
                comment.setStatus(0);
                socialCommentRepository.save(comment);
            }
        }
        
        return success;
    }
}