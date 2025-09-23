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

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class FacebookApiService {
    
    private static final Logger logger = LoggerFactory.getLogger(FacebookApiService.class);
    
    @Value("${facebook.api.base-url:https://graph.facebook.com/v23.0}")
    private String facebookApiBaseUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 发布帖子到Facebook
     */
    public String publishPost(SocialHomepage homepage, SocialPostRequest request) {
        try {
            String accessToken = homepage.getAccessToken();
            String pageId = homepage.getHomepageId();
            
            // 检查访问令牌是否有效
            if (accessToken == null || accessToken.isEmpty()) {
                throw new RuntimeException("Facebook访问令牌为空");
            }
            
            // 构建发帖内容
            String message = buildPostMessage(request);
            
            // 如果有媒体文件，先上传媒体
            if (request.getMediaUrls() != null && !request.getMediaUrls().isEmpty()) {
                return publishPostWithMedia(pageId, accessToken, message, request.getMediaUrls());
            } else {
                return publishTextPost(pageId, accessToken, message);
            }
            
        } catch (Exception e) {
            logger.error("Facebook发帖失败: {}", e.getMessage(), e);
            throw new RuntimeException("Facebook发帖失败: " + e.getMessage());
        }
    }
    
    /**
     * 发布纯文本帖子
     */
    private String publishTextPost(String pageId, String accessToken, String message) {
        String url = facebookApiBaseUrl + "/" + pageId + "/feed";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("message", message);
        params.add("access_token", accessToken);
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                String postId = jsonNode.get("id").asText();
                logger.info("Facebook文本帖子发布成功，帖子ID: {}", postId);
                return postId;
            } else {
                logger.error("Facebook发帖失败，状态码: {}, 响应: {}", response.getStatusCode(), response.getBody());
                return null;
            }
        } catch (Exception e) {
            logger.error("Facebook发帖请求异常: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 发布带媒体的帖子
     */
    private String publishPostWithMedia(String pageId, String accessToken, String message, List<String> mediaUrls) {
        // 这里简化处理，实际应该先上传媒体文件到Facebook，然后使用返回的media_id发帖
        // 由于涉及文件上传的复杂性，这里先实现基本的图片链接发帖
        
        String url = facebookApiBaseUrl + "/" + pageId + "/photos";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("message", message);
        params.add("url", mediaUrls.get(0)); // 使用第一个媒体URL
        params.add("access_token", accessToken);
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                String postId = jsonNode.get("id").asText();
                logger.info("Facebook媒体帖子发布成功，帖子ID: {}", postId);
                return postId;
            } else {
                logger.error("Facebook媒体帖子发布失败，状态码: {}, 响应: {}", response.getStatusCode(), response.getBody());
                return null;
            }
        } catch (Exception e) {
            logger.error("Facebook媒体帖子发布异常: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 构建帖子消息内容
     */
    private String buildPostMessage(SocialPostRequest request) {
        StringBuilder message = new StringBuilder();
        
        if (request.getPostTitle() != null && !request.getPostTitle().trim().isEmpty()) {
            message.append(request.getPostTitle()).append("\n\n");
        }
        
        if (request.getPostContent() != null && !request.getPostContent().trim().isEmpty()) {
            message.append(request.getPostContent());
        }
        
        return message.toString();
    }
    
    /**
     * 从授权码获取访问令牌
     */
    public Map<String, Object> getAccessToken(String code, String appId, String appSecret, String redirectUri) {
        try {
            String url = facebookApiBaseUrl + "/oauth/access_token";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("code", code);
            params.add("client_id", appId);
            params.add("client_secret", appSecret);
            params.add("redirect_uri", redirectUri);
            
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                
                Map<String, Object> tokenInfo = new HashMap<>();
                tokenInfo.put("access_token", jsonNode.get("access_token").asText());
                
                if (jsonNode.has("expires_in")) {
                    tokenInfo.put("expires_in", jsonNode.get("expires_in").asInt());
                }
                
                if (jsonNode.has("refresh_token")) {
                    tokenInfo.put("refresh_token", jsonNode.get("refresh_token").asText());
                }
                
                logger.info("获取Facebook访问令牌成功");
                return tokenInfo;
            } else {
                logger.error("获取Facebook访问令牌失败，状态码: {}, 响应: {}", response.getStatusCode(), response.getBody());
                return null;
            }
        } catch (Exception e) {
            logger.error("获取Facebook访问令牌异常: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 刷新访问令牌
     */
    public String refreshAccessToken(String refreshToken, String appId, String appSecret) {
        try {
            String url = facebookApiBaseUrl + "/oauth/access_token";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "fb_exchange_token");
            params.add("client_id", appId);
            params.add("client_secret", appSecret);
            params.add("fb_exchange_token", refreshToken);
            
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                String newAccessToken = jsonNode.get("access_token").asText();
                logger.info("刷新Facebook访问令牌成功");
                return newAccessToken;
            } else {
                logger.error("刷新Facebook访问令牌失败，状态码: {}, 响应: {}", response.getStatusCode(), response.getBody());
                return null;
            }
        } catch (Exception e) {
            logger.error("刷新Facebook访问令牌异常: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 验证访问令牌
     */
    public boolean validateAccessToken(String accessToken) {
        String url = facebookApiBaseUrl + "/me?access_token=" + accessToken;
        
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            logger.error("验证Facebook访问令牌失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 获取用户管理的Facebook页面列表
     */
    public List<Map<String, Object>> getUserPages(String accessToken) {
        try {
            // 首先获取用户信息
            String userUrl = facebookApiBaseUrl + "/me/accounts?fields=id,name,access_token&access_token=" + accessToken;
            
            ResponseEntity<String> response = restTemplate.getForEntity(userUrl, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                JsonNode dataNode = jsonNode.get("data");
                
                // 转换为List<Map<String, Object>>
                List<Map<String, Object>> pages = new ArrayList<>();
                if (dataNode != null && dataNode.isArray()) {
                    for (JsonNode pageNode : dataNode) {
                        Map<String, Object> pageInfo = new HashMap<>();
                        pageInfo.put("id", pageNode.get("id").asText());
                        pageInfo.put("name", pageNode.get("name").asText());
                        pageInfo.put("access_token", pageNode.get("access_token").asText());
                        pages.add(pageInfo);
                    }
                }
                
                logger.info("成功获取Facebook页面列表，页面数: {}", pages.size());
                return pages;
            } else {
                logger.error("获取Facebook页面列表失败，状态码: {}, 响应: {}", response.getStatusCode(), response.getBody());
                return null;
            }
        } catch (Exception e) {
            logger.error("获取Facebook页面列表异常: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 获取帖子的评论列表
     */
    public List<Map<String, Object>> getPostComments(String postId, String accessToken) {
        try {
            String url = facebookApiBaseUrl + "/" + postId + "/comments?fields=id,message,from,created_time,like_count,parent&access_token=" + accessToken;
            
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
                        commentInfo.put("comment_content", commentNode.get("message").asText());
                        
                        // 评论人信息
                        if (commentNode.has("from")) {
                            JsonNode fromNode = commentNode.get("from");
                            commentInfo.put("commenter_name", fromNode.get("name").asText());
                            commentInfo.put("commenter_id", fromNode.get("id").asText());
                        }
                        
                        // 评论时间
                        if (commentNode.has("created_time")) {
                            String dateTimeStr = commentNode.get("created_time").asText();
                            // 解析Facebook的日期时间格式 (ISO 8601)
                            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                            LocalDateTime commentTime = LocalDateTime.parse(dateTimeStr.substring(0, dateTimeStr.length() - 5) + ":" + dateTimeStr.substring(dateTimeStr.length() - 5), formatter);
                            commentInfo.put("comment_time", commentTime);
                        }
                        
                        // 点赞数量
                        if (commentNode.has("like_count")) {
                            commentInfo.put("like_count", commentNode.get("like_count").asInt());
                        }
                        
                        // 父评论ID（用于回复）
                        if (commentNode.has("parent")) {
                            JsonNode parentNode = commentNode.get("parent");
                            commentInfo.put("parent_comment_id", parentNode.get("id").asText());
                        }
                        
                        comments.add(commentInfo);
                    }
                }
                
                logger.info("成功获取Facebook帖子评论列表，评论数: {}", comments.size());
                return comments;
            } else {
                logger.error("获取Facebook帖子评论列表失败，状态码: {}, 响应: {}", response.getStatusCode(), response.getBody());
                return null;
            }
        } catch (Exception e) {
            logger.error("获取Facebook帖子评论列表异常: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 删除评论
     */
    public boolean deleteComment(String commentId, String accessToken) {
        try {
            String url = facebookApiBaseUrl + "/" + commentId + "?access_token=" + accessToken;
            
            restTemplate.delete(url);
            
            logger.info("Facebook评论删除成功，评论ID: {}", commentId);
            return true;
        } catch (Exception e) {
            logger.error("Facebook评论删除失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取Facebook主页的帖子列表
     */
    public List<Map<String, Object>> getPosts(String pageId, String accessToken) {
        String url = facebookApiBaseUrl + "/" + pageId + "/posts?fields=id,message,created_time,attachments{media{image{url}}}&access_token=" + accessToken;
        
        try {
            logger.info("获取Facebook主页帖子列表，URL: {}", url);
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                JsonNode dataNode = jsonNode.get("data");
                
                List<Map<String, Object>> posts = new ArrayList<>();
                
                if (dataNode != null && dataNode.isArray()) {
                    for (JsonNode postNode : dataNode) {
                        Map<String, Object> postData = new HashMap<>();
                        
                        postData.put("post_id", postNode.get("id").asText());
                        
                        if (postNode.has("message")) {
                            postData.put("post_content", postNode.get("message").asText());
                        } else {
                            postData.put("post_content", "");
                        }
                        
                        // 解析创建时间
                        String createdTimeStr = postNode.get("created_time").asText();
                        // 处理Facebook日期格式
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
                        if (postNode.has("attachments")) {
                            JsonNode attachmentsNode = postNode.get("attachments");
                            if (attachmentsNode.has("data")) {
                                for (JsonNode attachmentNode : attachmentsNode.get("data")) {
                                    if (attachmentNode.has("media")) {
                                        JsonNode mediaNode = attachmentNode.get("media");
                                        if (mediaNode.has("image")) {
                                            String imageUrl = mediaNode.get("image").get("url").asText();
                                            mediaUrls.add(imageUrl);
                                        }
                                    }
                                }
                            }
                        }
                        
                        postData.put("media_urls", mediaUrls);
                        posts.add(postData);
                    }
                }
                
                logger.info("获取Facebook主页帖子列表成功，共 {} 个帖子", posts.size());
                return posts;
            } else {
                logger.error("获取Facebook主页帖子列表失败，状态码: {}, 响应: {}", 
                        response.getStatusCode(), response.getBody());
                return null;
            }
        } catch (Exception e) {
            logger.error("获取Facebook主页帖子列表异常: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取Facebook帖子详情
     */
    public Map<String, Object> getPostDetails(String postId, String accessToken) {
        String url = facebookApiBaseUrl + "/" + postId + "?fields=id,message,created_time,attachments{media{image{url}}}&access_token=" + accessToken;
        
        try {
            logger.info("获取Facebook帖子详情，URL: {}", url);
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                
                Map<String, Object> postData = new HashMap<>();
                postData.put("post_id", jsonNode.get("id").asText());
                
                if (jsonNode.has("message")) {
                    postData.put("post_content", jsonNode.get("message").asText());
                } else {
                    postData.put("post_content", "");
                }
                
                // 解析创建时间
                String createdTimeStr = jsonNode.get("created_time").asText();
                // 处理Facebook日期格式
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
                if (jsonNode.has("attachments")) {
                    JsonNode attachmentsNode = jsonNode.get("attachments");
                    if (attachmentsNode.has("data")) {
                        for (JsonNode attachmentNode : attachmentsNode.get("data")) {
                            if (attachmentNode.has("media")) {
                                JsonNode mediaNode = attachmentNode.get("media");
                                if (mediaNode.has("image")) {
                                    String imageUrl = mediaNode.get("image").get("url").asText();
                                    mediaUrls.add(imageUrl);
                                }
                            }
                        }
                    }
                }
                
                postData.put("media_urls", mediaUrls);
                
                logger.info("获取Facebook帖子详情成功，帖子ID: {}", postId);
                return postData;
            } else {
                logger.error("获取Facebook帖子详情失败，状态码: {}, 响应: {}", 
                        response.getStatusCode(), response.getBody());
                return null;
            }
        } catch (Exception e) {
            logger.error("获取Facebook帖子详情异常: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 回复Facebook评论
     */
    public String replyToComment(String commentId, String replyContent, String accessToken) {
        String url = facebookApiBaseUrl + "/" + commentId + "/comments";
        
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
                logger.info("Facebook评论回复成功，回复ID: {}", replyId);
                return replyId;
            } else {
                logger.error("Facebook评论回复失败，状态码: {}, 响应: {}", response.getStatusCode(), response.getBody());
                return null;
            }
        } catch (Exception e) {
            logger.error("Facebook评论回复异常: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取Facebook评论详情
     */
    public Map<String, Object> getCommentDetails(String commentId, String accessToken) {
        String url = facebookApiBaseUrl + "/" + commentId + "?fields=id,message,created_time,from{name,id,picture{url}}&access_token=" + accessToken;
        
        try {
            logger.info("获取Facebook评论详情，URL: {}", url);
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                
                Map<String, Object> commentData = new HashMap<>();
                commentData.put("comment_id", jsonNode.get("id").asText());
                commentData.put("comment_content", jsonNode.get("message").asText());
                
                // 解析评论时间
                String createdTimeStr = jsonNode.get("created_time").asText();
                // 处理Facebook日期格式
                LocalDateTime commentTime;
                if (createdTimeStr.contains("+")) {
                    String formattedTimeStr = createdTimeStr.substring(0, createdTimeStr.length() - 5) + ":" + createdTimeStr.substring(createdTimeStr.length() - 5);
                    commentTime = LocalDateTime.parse(formattedTimeStr, DateTimeFormatter.ISO_DATE_TIME);
                } else {
                    commentTime = LocalDateTime.parse(createdTimeStr, DateTimeFormatter.ISO_DATE_TIME);
                }
                commentData.put("comment_time", commentTime);
                
                // 处理评论人信息
                if (jsonNode.has("from")) {
                    JsonNode fromNode = jsonNode.get("from");
                    commentData.put("commenter_name", fromNode.get("name").asText());
                    commentData.put("commenter_id", fromNode.get("id").asText());
                    
                    if (fromNode.has("picture")) {
                        JsonNode pictureNode = fromNode.get("picture");
                        if (pictureNode.has("url")) {
                            commentData.put("commenter_avatar", pictureNode.get("url").asText());
                        }
                    }
                }
                
                // 获取点赞数（需要额外请求）
                String likesUrl = facebookApiBaseUrl + "/" + commentId + "/likes?summary=true&access_token=" + accessToken;
                ResponseEntity<String> likesResponse = restTemplate.getForEntity(likesUrl, String.class);
                
                if (likesResponse.getStatusCode() == HttpStatus.OK) {
                    JsonNode likesJsonNode = objectMapper.readTree(likesResponse.getBody());
                    if (likesJsonNode.has("summary")) {
                        int likeCount = likesJsonNode.get("summary").get("total_count").asInt();
                        commentData.put("like_count", likeCount);
                    }
                }
                
                logger.info("获取Facebook评论详情成功，评论ID: {}", commentId);
                return commentData;
            } else {
                logger.error("获取Facebook评论详情失败，状态码: {}, 响应: {}", 
                        response.getStatusCode(), response.getBody());
                return null;
            }
        } catch (Exception e) {
            logger.error("获取Facebook评论详情异常: {}", e.getMessage(), e);
            return null;
        }
    }
}