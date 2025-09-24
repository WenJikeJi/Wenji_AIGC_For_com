package com.wenji.server.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wenji.server.dto.SocialPostRequest;
import com.wenji.server.model.SocialHomepage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class InstagramApiService {
    
    private static final Logger logger = LoggerFactory.getLogger(InstagramApiService.class);
    
    @Value("${instagram.api.base-url:https://graph.facebook.com/v23.0}")
    private String instagramApiBaseUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 发布帖子到Instagram
     */
    public String publishPost(SocialHomepage homepage, SocialPostRequest request) {
        try {
            String accessToken = homepage.getAccessToken();
            String instagramAccountId = homepage.getHomepageId();
            
            // 检查访问令牌是否有效
            if (accessToken == null || accessToken.isEmpty()) {
                throw new RuntimeException("Instagram访问令牌为空");
            }
            
            // Instagram发帖需要两步：1. 创建媒体容器 2. 发布媒体容器
            if (request.getMediaUrls() != null && !request.getMediaUrls().isEmpty()) {
                return publishMediaPost(instagramAccountId, accessToken, request);
            } else {
                // Instagram不支持纯文本帖子，必须有媒体
                throw new RuntimeException("Instagram发帖必须包含媒体文件");
            }
            
        } catch (Exception e) {
            logger.error("Instagram发帖失败: {}", e.getMessage(), e);
            throw new RuntimeException("Instagram发帖失败: " + e.getMessage());
        }
    }
    
    /**
     * 发布媒体帖子到Instagram
     */
    private String publishMediaPost(String instagramAccountId, String accessToken, SocialPostRequest request) {
        try {
            // 第一步：创建媒体容器
            String containerId = createMediaContainer(instagramAccountId, accessToken, request);
            if (containerId == null) {
                return null;
            }
            
            // 第二步：发布媒体容器
            return publishMediaContainer(instagramAccountId, accessToken, containerId);
            
        } catch (Exception e) {
            logger.error("Instagram媒体帖子发布异常: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 创建媒体容器
     */
    private String createMediaContainer(String instagramAccountId, String accessToken, SocialPostRequest request) {
        String url = instagramApiBaseUrl + "/" + instagramAccountId + "/media";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        
        // 添加媒体URL（使用第一个媒体文件）
        String mediaUrl = request.getMediaUrls().get(0);
        if (isVideoUrl(mediaUrl)) {
            params.add("media_type", "VIDEO");
            params.add("video_url", mediaUrl);
        } else {
            params.add("image_url", mediaUrl);
        }
        
        // 添加标题（Instagram中标题是可选的）
        String caption = buildInstagramCaption(request);
        if (caption != null && !caption.trim().isEmpty()) {
            params.add("caption", caption);
        }
        
        params.add("access_token", accessToken);
        
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                String containerId = jsonNode.get("id").asText();
                logger.info("Instagram媒体容器创建成功，容器ID: {}", containerId);
                return containerId;
            } else {
                logger.error("Instagram媒体容器创建失败，状态码: {}, 响应: {}", response.getStatusCode(), response.getBody());
                return null;
            }
        } catch (Exception e) {
            logger.error("Instagram媒体容器创建异常: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 发布媒体容器
     */
    private String publishMediaContainer(String instagramAccountId, String accessToken, String containerId) {
        String url = instagramApiBaseUrl + "/" + instagramAccountId + "/media_publish";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("creation_id", containerId);
        params.add("access_token", accessToken);
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                String mediaId = jsonNode.get("id").asText();
                logger.info("Instagram帖子发布成功，媒体ID: {}", mediaId);
                return mediaId;
            } else {
                logger.error("Instagram帖子发布失败，状态码: {}, 响应: {}", response.getStatusCode(), response.getBody());
                return null;
            }
        } catch (Exception e) {
            logger.error("Instagram帖子发布异常: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 构建Instagram标题
     */
    private String buildInstagramCaption(SocialPostRequest request) {
        StringBuilder caption = new StringBuilder();
        
        // Instagram中标题是可选的，但内容通常作为标题
        if (request.getPostContent() != null && !request.getPostContent().trim().isEmpty()) {
            caption.append(request.getPostContent());
        }
        
        // 如果有标题，也可以添加
        if (request.getPostTitle() != null && !request.getPostTitle().trim().isEmpty()) {
            if (caption.length() > 0) {
                caption.append("\n\n");
            }
            caption.append(request.getPostTitle());
        }
        
        return caption.toString();
    }
    
    /**
     * 判断是否为视频URL
     */
    private boolean isVideoUrl(String url) {
        if (url == null) return false;
        String lowerUrl = url.toLowerCase();
        return lowerUrl.endsWith(".mp4") || lowerUrl.endsWith(".mov") || 
               lowerUrl.endsWith(".avi") || lowerUrl.contains("video");
    }
    
    /**
     * 刷新访问令牌
     */
    public String refreshAccessToken(String refreshToken) {
        // 实现访问令牌刷新逻辑
        // Instagram使用Facebook的令牌系统
        logger.info("刷新Instagram访问令牌");
        return null; // 暂时返回null，实际需要实现
    }
    
    /**
     * 验证访问令牌
     */
    public boolean validateAccessToken(String accessToken, String instagramAccountId) {
        // 对access_token进行URL编码，避免特殊字符导致的URISyntaxException
        String encodedAccessToken = URLEncoder.encode(accessToken, StandardCharsets.UTF_8);
        String url = instagramApiBaseUrl + "/" + instagramAccountId + "?fields=id,username&access_token=" + encodedAccessToken;
        
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            logger.error("验证Instagram访问令牌失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 获取与Facebook页面关联的Instagram Business页面
     */
    public List<Map<String, Object>> getInstagramBusinessPages(String facebookPageId, String accessToken) {
        try {
            // 对access_token进行URL编码，避免特殊字符导致的URISyntaxException
            String encodedAccessToken = URLEncoder.encode(accessToken, StandardCharsets.UTF_8);
            
            // 获取Facebook页面关联的Instagram账号
            String url = instagramApiBaseUrl + "/" + facebookPageId + "/instagram_business_account?fields=id,username,name&access_token=" + encodedAccessToken;
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                
                // 转换为List<Map<String, Object>>
                List<Map<String, Object>> instagramAccounts = new ArrayList<>();
                
                // 检查是否存在Instagram Business账号
                if (jsonNode.has("id")) {
                    Map<String, Object> accountInfo = new HashMap<>();
                    accountInfo.put("id", jsonNode.get("id").asText());
                    if (jsonNode.has("username")) {
                        accountInfo.put("username", jsonNode.get("username").asText());
                    }
                    if (jsonNode.has("name")) {
                        accountInfo.put("name", jsonNode.get("name").asText());
                    }
                    instagramAccounts.add(accountInfo);
                } else if (jsonNode.has("data")) {
                    // 处理可能返回多个账号的情况
                    JsonNode dataNode = jsonNode.get("data");
                    if (dataNode.isArray()) {
                        for (JsonNode accountNode : dataNode) {
                            Map<String, Object> accountInfo = new HashMap<>();
                            accountInfo.put("id", accountNode.get("id").asText());
                            if (accountNode.has("username")) {
                                accountInfo.put("username", accountNode.get("username").asText());
                            }
                            if (accountNode.has("name")) {
                                accountInfo.put("name", accountNode.get("name").asText());
                            }
                            instagramAccounts.add(accountInfo);
                        }
                    }
                }
                
                logger.info("成功获取Instagram Business页面列表，账号数: {}", instagramAccounts.size());
                return instagramAccounts;
            } else {
                logger.error("获取Instagram Business页面列表失败，状态码: {}, 响应: {}", response.getStatusCode(), response.getBody());
                return null;
            }
        } catch (Exception e) {
            logger.error("获取Instagram Business页面列表异常: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 获取Instagram页面详细信息
     */
    public Map<String, Object> getInstagramPageInfo(String instagramAccountId, String accessToken) {
        try {
            // 对access_token进行URL编码，避免特殊字符导致的URISyntaxException
            String encodedAccessToken = URLEncoder.encode(accessToken, StandardCharsets.UTF_8);
            String url = instagramApiBaseUrl + "/" + instagramAccountId + "?fields=id,username,name&access_token=" + encodedAccessToken;
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                
                Map<String, Object> pageInfo = new HashMap<>();
                pageInfo.put("id", jsonNode.get("id").asText());
                if (jsonNode.has("username")) {
                    pageInfo.put("username", jsonNode.get("username").asText());
                }
                if (jsonNode.has("name")) {
                    pageInfo.put("name", jsonNode.get("name").asText());
                }
                
                logger.info("成功获取Instagram页面信息，页面ID: {}", instagramAccountId);
                return pageInfo;
            } else {
                logger.error("获取Instagram页面信息失败，状态码: {}, 响应: {}", response.getStatusCode(), response.getBody());
                return null;
            }
        } catch (Exception e) {
            logger.error("获取Instagram页面信息异常: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 获取帖子的评论列表
     */
    public List<Map<String, Object>> getPostComments(String mediaId, String accessToken) {
        try {
            // Instagram评论API需要media_id参数
            String url = instagramApiBaseUrl + "/" + mediaId + "/comments?fields=id,text,from,created_time,like_count&access_token=" + accessToken;
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                JsonNode dataNode = jsonNode.get("data");
                
                // 转换为List<Map<String, Object>>
                List<Map<String, Object>> comments = new ArrayList<>();
                if (dataNode != null && dataNode.isArray()) {
                    for (JsonNode commentNode : dataNode) {
                        Map<String, Object> commentInfo = new HashMap<>();
                        commentInfo.put("comment_id", commentNode.get("id").asText());
                        commentInfo.put("comment_content", commentNode.get("text").asText());
                        
                        // 评论人信息
                        if (commentNode.has("from")) {
                            JsonNode fromNode = commentNode.get("from");
                            commentInfo.put("commenter_name", fromNode.get("username").asText());
                            commentInfo.put("commenter_id", fromNode.get("id").asText());
                        }
                        
                        // 评论时间
                        if (commentNode.has("created_time")) {
                            String timestamp = commentNode.get("created_time").asText();
                            LocalDateTime commentTime = LocalDateTime.ofEpochSecond(Long.parseLong(timestamp), 0, java.time.ZoneOffset.UTC);
                            commentInfo.put("comment_time", commentTime);
                        }
                        
                        // 点赞数量
                        if (commentNode.has("like_count")) {
                            commentInfo.put("like_count", commentNode.get("like_count").asInt());
                        }
                        
                        comments.add(commentInfo);
                    }
                }
                
                logger.info("成功获取Instagram帖子评论列表，评论数: {}", comments.size());
                return comments;
            } else {
                logger.error("获取Instagram帖子评论列表失败，状态码: {}, 响应: {}", response.getStatusCode(), response.getBody());
                return null;
            }
        } catch (Exception e) {
            logger.error("获取Instagram帖子评论列表异常: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 删除评论
     */
    public boolean deleteComment(String commentId, String accessToken) {
        try {
            String url = instagramApiBaseUrl + "/" + commentId + "?access_token=" + accessToken;
            
            restTemplate.delete(url);
            
            logger.info("Instagram评论删除成功，评论ID: {}", commentId);
            return true;
        } catch (Exception e) {
            logger.error("Instagram评论删除失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取Instagram主页的帖子列表
     */
    public List<Map<String, Object>> getPosts(String instagramAccountId, String accessToken) {
        // 对access_token进行URL编码，避免特殊字符导致的URISyntaxException
        String encodedAccessToken = URLEncoder.encode(accessToken, StandardCharsets.UTF_8);
        
        // 注意：Instagram API需要先获取media对象列表
        String url = instagramApiBaseUrl + "/" + instagramAccountId + "/media?fields=id,caption,timestamp,media_url,permalink&access_token=" + encodedAccessToken;
        
        try {
            logger.info("获取Instagram主页帖子列表，URL: {}", url);
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                JsonNode dataNode = jsonNode.get("data");
                
                List<Map<String, Object>> posts = new ArrayList<>();
                
                if (dataNode != null && dataNode.isArray()) {
                    for (JsonNode postNode : dataNode) {
                        Map<String, Object> postData = new HashMap<>();
                        
                        postData.put("post_id", postNode.get("id").asText());
                        
                        if (postNode.has("caption")) {
                            postData.put("post_content", postNode.get("caption").asText());
                        } else {
                            postData.put("post_content", "");
                        }
                        
                        // 解析创建时间
                        String createdTimeStr = postNode.get("timestamp").asText();
                        // 处理Instagram日期格式
                        LocalDateTime publishTime;
                        if (createdTimeStr.contains("+")) {
                            String formattedTimeStr = createdTimeStr.substring(0, createdTimeStr.length() - 5) + ":" + createdTimeStr.substring(createdTimeStr.length() - 5);
                            publishTime = LocalDateTime.parse(formattedTimeStr, DateTimeFormatter.ISO_DATE_TIME);
                        } else {
                            publishTime = LocalDateTime.parse(createdTimeStr, DateTimeFormatter.ISO_DATE_TIME);
                        }
                        postData.put("publish_time", publishTime);
                        
                        // 处理媒体URL
                        List<String> mediaUrls = new ArrayList<>();
                        if (postNode.has("media_url")) {
                            mediaUrls.add(postNode.get("media_url").asText());
                        }
                        
                        postData.put("media_urls", mediaUrls);
                        posts.add(postData);
                    }
                }
                
                logger.info("获取Instagram主页帖子列表成功，共 {} 个帖子", posts.size());
                return posts;
            } else {
                logger.error("获取Instagram主页帖子列表失败，状态码: {}, 响应: {}", 
                        response.getStatusCode(), response.getBody());
                return null;
            }
        } catch (Exception e) {
            logger.error("获取Instagram主页帖子列表异常: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取Instagram帖子详情
     */
    public Map<String, Object> getPostDetails(String mediaId, String accessToken) {
        // 对access_token进行URL编码，避免特殊字符导致的URISyntaxException
        String encodedAccessToken = URLEncoder.encode(accessToken, StandardCharsets.UTF_8);
        String url = instagramApiBaseUrl + "/" + mediaId + "?fields=id,caption,timestamp,media_url,permalink,like_count,comments_count&access_token=" + encodedAccessToken;
        
        try {
            logger.info("获取Instagram帖子详情，URL: {}", url);
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                
                Map<String, Object> postData = new HashMap<>();
                postData.put("post_id", jsonNode.get("id").asText());
                
                if (jsonNode.has("caption")) {
                    postData.put("post_content", jsonNode.get("caption").asText());
                } else {
                    postData.put("post_content", "");
                }
                
                // 解析创建时间
                String createdTimeStr = jsonNode.get("timestamp").asText();
                // 处理Instagram日期格式
                LocalDateTime publishTime;
                if (createdTimeStr.contains("+")) {
                    String formattedTimeStr = createdTimeStr.substring(0, createdTimeStr.length() - 5) + ":" + createdTimeStr.substring(createdTimeStr.length() - 5);
                    publishTime = LocalDateTime.parse(formattedTimeStr, DateTimeFormatter.ISO_DATE_TIME);
                } else {
                    publishTime = LocalDateTime.parse(createdTimeStr, DateTimeFormatter.ISO_DATE_TIME);
                }
                postData.put("publish_time", publishTime);
                
                // 处理媒体URL
                List<String> mediaUrls = new ArrayList<>();
                if (jsonNode.has("media_url")) {
                    mediaUrls.add(jsonNode.get("media_url").asText());
                }
                
                postData.put("media_urls", mediaUrls);
                
                // 获取点赞数和评论数
                if (jsonNode.has("like_count")) {
                    postData.put("like_count", jsonNode.get("like_count").asInt());
                }
                
                if (jsonNode.has("comments_count")) {
                    postData.put("comment_count", jsonNode.get("comments_count").asInt());
                }
                
                logger.info("获取Instagram帖子详情成功，帖子ID: {}", mediaId);
                return postData;
            } else {
                logger.error("获取Instagram帖子详情失败，状态码: {}, 响应: {}", 
                        response.getStatusCode(), response.getBody());
                return null;
            }
        } catch (Exception e) {
            logger.error("获取Instagram帖子详情异常: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 回复Instagram评论
     */
    public String replyToComment(String commentId, String replyContent, String accessToken) {
        String url = instagramApiBaseUrl + "/" + commentId + "/replies";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("message", replyContent);
        params.add("access_token", accessToken);
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                String replyId = jsonNode.get("id").asText();
                logger.info("Instagram评论回复成功，回复ID: {}", replyId);
                return replyId;
            } else {
                logger.error("Instagram评论回复失败，状态码: {}, 响应: {}", response.getStatusCode(), response.getBody());
                return null;
            }
        } catch (Exception e) {
            logger.error("Instagram评论回复异常: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取Instagram评论详情
     */
    public Map<String, Object> getCommentDetails(String commentId, String accessToken) {
        // 对access_token进行URL编码，避免特殊字符导致的URISyntaxException
        String encodedAccessToken = URLEncoder.encode(accessToken, StandardCharsets.UTF_8);
        String url = instagramApiBaseUrl + "/" + commentId + "?fields=id,text,timestamp,username&access_token=" + encodedAccessToken;
        
        try {
            logger.info("获取Instagram评论详情，URL: {}", url);
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                
                Map<String, Object> commentData = new HashMap<>();
                commentData.put("comment_id", jsonNode.get("id").asText());
                commentData.put("comment_content", jsonNode.get("text").asText());
                
                // 解析评论时间
                String createdTimeStr = jsonNode.get("timestamp").asText();
                // 处理Instagram日期格式
                LocalDateTime commentTime;
                if (createdTimeStr.contains("+")) {
                    String formattedTimeStr = createdTimeStr.substring(0, createdTimeStr.length() - 5) + ":" + createdTimeStr.substring(createdTimeStr.length() - 5);
                    commentTime = LocalDateTime.parse(formattedTimeStr, DateTimeFormatter.ISO_DATE_TIME);
                } else {
                    commentTime = LocalDateTime.parse(createdTimeStr, DateTimeFormatter.ISO_DATE_TIME);
                }
                commentData.put("comment_time", commentTime);
                
                // 处理评论人信息
                if (jsonNode.has("username")) {
                    commentData.put("commenter_name", jsonNode.get("username").asText());
                    commentData.put("commenter_id", jsonNode.get("username").asText()); // Instagram API返回的是用户名
                }
                
                // 注意：Instagram API获取评论的点赞数需要额外请求
                // 这里可以添加获取评论点赞数的代码
                
                logger.info("获取Instagram评论详情成功，评论ID: {}", commentId);
                return commentData;
            } else {
                logger.error("获取Instagram评论详情失败，状态码: {}, 响应: {}", 
                        response.getStatusCode(), response.getBody());
                return null;
            }
        } catch (Exception e) {
            logger.error("获取Instagram评论详情异常: {}", e.getMessage(), e);
            return null;
        }
    }
}