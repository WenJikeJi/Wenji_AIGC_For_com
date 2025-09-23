package com.wenji.server.controller;

import com.wenji.server.dto.SocialPostRequest;
import com.wenji.server.dto.SocialPostResponse;
import com.wenji.server.model.SocialHomepage;
import com.wenji.server.model.SocialPost;
import com.wenji.server.model.SocialPostTask;
import com.wenji.server.model.SocialComment;
import com.wenji.server.service.SocialMediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/social")
@Validated
@Tag(name = "社媒管理", description = "Facebook和Instagram发帖管理接口")
public class SocialMediaController {
    
    private static final Logger logger = LoggerFactory.getLogger(SocialMediaController.class);
    
    @Autowired
    private SocialMediaService socialMediaService;
    
    /**
     * 获取用户绑定的主页列表
     */
    @GetMapping("/homepages")
    @Operation(summary = "获取主页列表", description = "获取用户绑定的所有社交媒体主页")
    public ResponseEntity<Map<String, Object>> getUserHomepages(jakarta.servlet.http.HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 从request属性中获取用户ID，这是从JWT认证过滤器中设置的
            Long userId = (Long) request.getAttribute("userId");
            
            if (userId == null) {
                logger.error("无法获取用户ID，认证失败");
                response.put("code", 401);
                response.put("message", "认证失败，请重新登录");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            List<SocialHomepage> homepages = socialMediaService.getUserHomepages(userId);
            
            // 转换为前端友好的格式
            List<Map<String, Object>> formattedHomepages = homepages.stream().map(homepage -> {
                Map<String, Object> homePageMap = new HashMap<>();
                homePageMap.put("id", homepage.getHomepageId());
                homePageMap.put("name", homepage.getHomepageName());
                homePageMap.put("platformType", homepage.getPlatformType());
                homePageMap.put("platformName", homepage.getPlatformType() == 1 ? "Facebook" : "Instagram");
                homePageMap.put("tokenExpiresAt", homepage.getTokenExpiresAt());
                homePageMap.put("updatedAt", homepage.getUpdatedAt());
                return homePageMap;
            }).collect(Collectors.toList());
            
            response.put("code", 200);
            response.put("message", "获取成功");
            response.put("data", formattedHomepages);
            logger.info("获取用户主页列表成功，用户ID: {}, 主页数: {}", userId, homepages.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取用户主页列表失败", e);
            response.put("code", 500);
            response.put("message", "获取主页列表失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 即时发帖接口
     */
    @PostMapping("/posts/publish")
    public ResponseEntity<Map<String, Object>> publishPost(@Valid @RequestBody SocialPostRequest request) {
        try {
            // 这里应该从JWT token中获取用户ID，暂时使用固定值
            Long operatorId = 1L; // TODO: 从认证信息中获取
            
            logger.info("收到即时发帖请求，操作人ID: {}", operatorId);
            
            SocialPostResponse response = socialMediaService.publishPost(request, operatorId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("data", response);
            result.put("message", "发帖成功");
            
            return ResponseEntity.ok(result);
            
        } catch (IllegalArgumentException e) {
            logger.error("即时发帖参数错误: {}", e.getMessage());
            Map<String, Object> result = new HashMap<>();
            result.put("code", 400);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
            
        } catch (Exception e) {
            logger.error("即时发帖失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("message", "发帖失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }
    
    /**
     * 定时发帖接口
     */
    @PostMapping("/posts/schedule")
    public ResponseEntity<Map<String, Object>> schedulePost(@Valid @RequestBody SocialPostRequest request) {
        try {
            // 这里应该从JWT token中获取用户ID，暂时使用固定值
            Long operatorId = 1L; // TODO: 从认证信息中获取
            
            logger.info("收到定时发帖请求，操作人ID: {}, 定时时间: {}", operatorId, request.getScheduleTime());
            
            SocialPostResponse response = socialMediaService.schedulePost(request, operatorId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("data", response);
            result.put("message", "定时任务创建成功");
            
            return ResponseEntity.ok(result);
            
        } catch (IllegalArgumentException e) {
            logger.error("定时发帖参数错误: {}", e.getMessage());
            Map<String, Object> result = new HashMap<>();
            result.put("code", 400);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
            
        } catch (Exception e) {
            logger.error("定时发帖失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("message", "定时任务创建失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }
    
    /**
     * 查看定时任务列表
     */
    @Operation(
            summary = "获取定时任务列表",
            description = "分页查询定时发帖任务列表，支持按状态筛选",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"社媒管理"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(example = "{\"code\": 200, \"message\": \"查询成功\", \"data\": {\"content\": [{\"id\": 1, \"platform\": \"facebook\", \"content\": \"任务内容\", \"status\": 0}], \"totalElements\": 10}}"))),
            @ApiResponse(responseCode = "500", description = "查询失败", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(example = "{\"code\": 500, \"message\": \"查询失败\"}")))
    })
    @GetMapping("/posts/tasks")
    public ResponseEntity<Map<String, Object>> getTaskList(
            @Parameter(description = "页码，从1开始", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "任务状态筛选，0-待执行，1-执行中，2-已完成，3-已取消，4-执行失败") @RequestParam(required = false) Integer taskStatus) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<SocialPostTask> taskPage = socialMediaService.getTaskList(taskStatus, pageable);
            
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("data", taskPage);
            result.put("message", "查询成功");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("查询任务列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }
    
    /**
     * 取消定时任务
     */
    @Operation(
            summary = "取消定时任务",
            description = "取消指定的定时发帖任务",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"社媒管理"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "任务取消成功", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(example = "{\"code\": 200, \"message\": \"任务取消成功\"}"))),
            @ApiResponse(responseCode = "400", description = "参数错误", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(example = "{\"code\": 400, \"message\": \"任务不存在或无法取消\"}"))),
            @ApiResponse(responseCode = "500", description = "取消任务失败", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(example = "{\"code\": 500, \"message\": \"取消任务失败\"}")))
    })
    @PostMapping("/posts/tasks/{taskId}/cancel")
    public ResponseEntity<Map<String, Object>> cancelTask(
            @Parameter(description = "任务ID", example = "1") @PathVariable Long taskId) {
        try {
            // 这里应该从JWT token中获取用户ID，暂时使用固定值
            Long operatorId = 1L; // TODO: 从认证信息中获取
            
            logger.info("收到取消任务请求，任务ID: {}, 操作人ID: {}", taskId, operatorId);
            
            socialMediaService.cancelTask(taskId, operatorId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "任务取消成功");
            
            return ResponseEntity.ok(result);
            
        } catch (IllegalArgumentException e) {
            logger.error("取消任务参数错误: {}", e.getMessage());
            Map<String, Object> result = new HashMap<>();
            result.put("code", 400);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
            
        } catch (Exception e) {
            logger.error("取消任务失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("message", "取消任务失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }
    
    /**
     * 获取用户主页列表
     */
    @Operation(
            summary = "获取用户主页列表",
            description = "获取用户在各社交平台的主页信息",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"社媒管理"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(example = "{\"code\": 200, \"message\": \"获取成功\", \"data\": [{\"platform\": \"facebook\", \"pageId\": \"123456\", \"pageName\": \"我的主页\"}]}"))),
            @ApiResponse(responseCode = "500", description = "获取失败", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(example = "{\"code\": 500, \"message\": \"获取失败\"}")))
    })
    @GetMapping("/homepages/fetch")
    public ResponseEntity<Map<String, Object>> fetchHomepages() {
        try {
            // 这里应该从JWT token中获取用户ID，暂时使用固定值
            Long userId = 1L; // TODO: 从认证信息中获取
            
            List<SocialHomepage> homepages = socialMediaService.getUserHomepages(userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("data", homepages);
            result.put("message", "查询成功");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("查询用户主页失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }
    
    /**
     * 获取帖子列表
     */
    @Operation(
            summary = "获取帖子列表",
            description = "分页查询用户的发帖历史记录",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"社媒管理"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(example = "{\"code\": 200, \"message\": \"查询成功\", \"data\": {\"content\": [{\"id\": 1, \"platform\": \"facebook\", \"content\": \"帖子内容\", \"publishTime\": \"2024-01-01 12:00:00\"}], \"totalElements\": 50}}"))),
            @ApiResponse(responseCode = "500", description = "查询失败", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(example = "{\"code\": 500, \"message\": \"查询失败\"}")))
    })
    @GetMapping("/posts")
    public ResponseEntity<Map<String, Object>> getPosts(
            @Parameter(description = "页码，从1开始", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小", example = "20") @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<SocialPost> postPage = socialMediaService.getPostList(pageable);
            
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("data", postPage);
            result.put("message", "查询成功");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("查询帖子列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }
    
    /**
     * 重试失败任务
     */
    @PostMapping("/posts/tasks/{taskId}/retry")
    public ResponseEntity<Map<String, Object>> retryTask(
            @Parameter(description = "任务ID", example = "1") @PathVariable Long taskId, jakarta.servlet.http.HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 从request属性中获取用户ID，这是从JWT认证过滤器中设置的
            Long operatorId = (Long) request.getAttribute("userId");
            
            if (operatorId == null) {
                logger.error("无法获取用户ID，认证失败");
                response.put("code", 401);
                response.put("message", "认证失败，请重新登录");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            logger.info("收到重试任务请求，任务ID: {}, 操作人ID: {}", taskId, operatorId);
            
            socialMediaService.retryTask(taskId, operatorId);
            
            response.put("code", 200);
            response.put("message", "任务重试成功");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("重试任务参数错误: {}", e.getMessage());
            response.put("code", 400);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("重试任务失败", e);
            response.put("code", 500);
            response.put("message", "任务重试失败");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // ==================== Facebook 专用接口 ====================
    
    /**
     * 获取Facebook授权URL
     */
    @GetMapping("/facebook/auth-url")
    @Operation(summary = "获取Facebook授权URL", description = "获取Facebook OAuth授权链接")
    public ResponseEntity<Map<String, Object>> getFacebookAuthUrl(@RequestParam(required = false) String redirectUrl) {
        Map<String, Object> response = new HashMap<>();
        try {
            String authUrl = socialMediaService.getFacebookAuthUrl(redirectUrl);
            
            response.put("code", 200);
            response.put("message", "获取成功");
            response.put("data", Map.of("authUrl", authUrl));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取Facebook授权URL失败", e);
            response.put("code", 500);
            response.put("message", "获取授权URL失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Facebook授权回调处理
     */
    @GetMapping("/facebook/callback")
    @Operation(summary = "Facebook授权回调", description = "处理Facebook授权回调并保存访问令牌")
    public String facebookCallback(
            @RequestParam("code") String code,
            @RequestParam("state") String state,
            jakarta.servlet.http.HttpServletRequest request) {
        try {
            // 从state中解析出原始的重定向URL
            String redirectUrl = new String(java.util.Base64.getDecoder().decode(state), java.nio.charset.StandardCharsets.UTF_8);
            
            // 从request属性中获取用户ID，这是从JWT认证过滤器中设置的
            Long userId = (Long) request.getAttribute("userId");
            
            if (userId == null) {
                logger.error("无法获取用户ID，认证失败");
                return "redirect:" + redirectUrl + "?success=false&error=认证失败，请重新登录";
            }
            
            // 调用服务层处理授权码并保存令牌
            boolean success = socialMediaService.handleFacebookCallback(code, redirectUrl, userId);
            
            if (success) {
                // 授权成功，重定向到前端页面并携带成功参数
                return "redirect:" + redirectUrl + "?success=true";
            } else {
                // 授权失败，重定向到前端页面并携带失败参数
                return "redirect:" + redirectUrl + "?success=false&error=处理Facebook授权回调失败";
            }
        } catch (Exception e) {
            logger.error("处理Facebook授权回调异常: {}", e.getMessage(), e);
            // 异常情况，重定向到前端页面并携带错误信息
            try {
                String redirectUrl = new String(java.util.Base64.getDecoder().decode(state), java.nio.charset.StandardCharsets.UTF_8);
                return "redirect:" + redirectUrl + "?success=false&error=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8");
            } catch (Exception ex) {
                // 如果解析state也失败，返回默认错误页面
                return "error";
            }
        }
    }

    /**
     * 保存Facebook访问令牌
     */
    @PostMapping("/facebook/save-token")
    @Operation(summary = "保存Facebook访问令牌", description = "保存用户的Facebook访问令牌")
    public ResponseEntity<Map<String, Object>> saveFacebookToken(@RequestBody Map<String, String> request, jakarta.servlet.http.HttpServletRequest servletRequest) {
        Map<String, Object> response = new HashMap<>();
        try {
            String token = request.get("token");
            if (token == null || token.isEmpty()) {
                response.put("code", 400);
                response.put("message", "访问令牌不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            // 从request属性中获取用户ID，这是从JWT认证过滤器中设置的
            Long userId = (Long) servletRequest.getAttribute("userId");
            
            if (userId == null) {
                logger.error("无法获取用户ID，认证失败");
                response.put("code", 401);
                response.put("message", "认证失败，请重新登录");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            socialMediaService.saveFacebookToken(userId, token);
            
            response.put("code", 200);
            response.put("message", "Facebook账号绑定成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("保存Facebook令牌失败", e);
            response.put("code", 500);
            response.put("message", "绑定Facebook账号失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 解绑Facebook账号
     */
    @PostMapping("/facebook/unbind")
    @Operation(summary = "解绑Facebook账号", description = "解除Facebook账号绑定")
    public ResponseEntity<Map<String, Object>> unbindFacebook(jakarta.servlet.http.HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 从request属性中获取用户ID，这是从JWT认证过滤器中设置的
            Long userId = (Long) request.getAttribute("userId");
            
            if (userId == null) {
                logger.error("无法获取用户ID，认证失败");
                response.put("code", 401);
                response.put("message", "认证失败，请重新登录");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            socialMediaService.unbindFacebook(userId);
            
            response.put("code", 200);
            response.put("message", "Facebook账号解绑成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("解绑Facebook账号失败", e);
            response.put("code", 500);
            response.put("message", "解绑Facebook账号失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取Facebook帖子列表
     */
    @GetMapping("/facebook/posts")
    @Operation(summary = "获取Facebook帖子", description = "获取用户Facebook页面的帖子列表")
    public ResponseEntity<Map<String, Object>> getFacebookPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 这里应该从JWT token中获取用户ID，暂时使用固定值
            Long userId = 1L; // TODO: 从认证信息中获取
            
            // TODO: 实现获取Facebook帖子的逻辑
            // Page<SocialPost> posts = socialMediaService.getFacebookPosts(userId, page - 1, size);
            
            // 模拟返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("content", List.of());
            data.put("totalElements", 0);
            data.put("totalPages", 0);
            data.put("currentPage", page);
            
            response.put("code", 200);
            response.put("message", "获取成功");
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取Facebook帖子失败", e);
            response.put("code", 500);
            response.put("message", "获取Facebook帖子失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ==================== Instagram 专用接口 ====================
    
    /**
     * 获取Instagram Pages
     */
    @GetMapping("/instagram/pages")
    @Operation(summary = "获取Instagram Pages", description = "获取用户的Instagram Business Pages")
    public ResponseEntity<Map<String, Object>> getInstagramPages(jakarta.servlet.http.HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 从request属性中获取用户ID，这是从JWT认证过滤器中设置的
            Long userId = (Long) request.getAttribute("userId");
            
            if (userId == null) {
                logger.error("无法获取用户ID，认证失败");
                response.put("code", 401);
                response.put("message", "认证失败，请重新登录");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            List<Map<String, Object>> pages = socialMediaService.getInstagramPages(userId);
            
            response.put("code", 200);
            response.put("message", "获取成功");
            response.put("data", pages);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取Instagram Pages失败", e);
            response.put("code", 500);
            response.put("message", "获取Instagram Pages失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 选择Instagram Page
     */
    @PostMapping("/instagram/select-page")
    @Operation(summary = "选择Instagram Page", description = "选择要管理的Instagram Business Page")
    public ResponseEntity<Map<String, Object>> selectInstagramPage(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String pageId = request.get("pageId");
            if (pageId == null || pageId.isEmpty()) {
                response.put("code", 400);
                response.put("message", "页面ID不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            // 这里应该从JWT token中获取用户ID，暂时使用固定值
            Long userId = 1L; // TODO: 从认证信息中获取
            
            // TODO: 实现选择Instagram Page的逻辑
            // socialMediaService.selectInstagramPage(userId, pageId);
            
            response.put("code", 200);
            response.put("message", "Instagram页面选择成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("选择Instagram Page失败", e);
            response.put("code", 500);
            response.put("message", "选择Instagram页面失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 解绑Instagram账号
     */
    @PostMapping("/instagram/unbind")
    @Operation(summary = "解绑Instagram账号", description = "解除Instagram账号绑定")
    public ResponseEntity<Map<String, Object>> unbindInstagram() {
        Map<String, Object> response = new HashMap<>();
        try {
            // 这里应该从JWT token中获取用户ID，暂时使用固定值
            Long userId = 1L; // TODO: 从认证信息中获取
            
            // TODO: 实现解绑Instagram账号的逻辑
            // socialMediaService.unbindInstagram(userId);
            
            response.put("code", 200);
            response.put("message", "Instagram账号解绑成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("解绑Instagram账号失败", e);
            response.put("code", 500);
            response.put("message", "解绑Instagram账号失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取Instagram帖子列表
     */
    @GetMapping("/instagram/posts")
    @Operation(summary = "获取Instagram帖子", description = "获取用户Instagram页面的帖子列表")
    public ResponseEntity<Map<String, Object>> getInstagramPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 这里应该从JWT token中获取用户ID，暂时使用固定值
            Long userId = 1L; // TODO: 从认证信息中获取
            
            // TODO: 实现获取Instagram帖子的逻辑
            // Page<SocialPost> posts = socialMediaService.getInstagramPosts(userId, page - 1, size);
            
            // 模拟返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("content", List.of());
            data.put("totalElements", 0);
            data.put("totalPages", 0);
            data.put("currentPage", page);
            
            response.put("code", 200);
            response.put("message", "获取成功");
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取Instagram帖子失败", e);
            response.put("code", 500);
            response.put("message", "获取Instagram帖子失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 获取帖子的评论列表
     */
    @GetMapping("/posts/{postId}/comments")
    @Operation(summary = "获取帖子评论列表", description = "获取指定帖子的评论列表，包含评论人、评论内容、评论回复、点赞量等信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(example = "{\"code\": 200, \"message\": \"获取成功\", \"data\": [{\"commentId\": \"12345\", \"commenterName\": \"用户名称\", \"commentContent\": \"评论内容\", \"likeCount\": 10}]}"))),
            @ApiResponse(responseCode = "400", description = "参数错误", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(example = "{\"code\": 400, \"message\": \"参数错误\"}"))),
            @ApiResponse(responseCode = "500", description = "获取失败", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(example = "{\"code\": 500, \"message\": \"获取评论列表失败\"}")))
    })
    public ResponseEntity<Map<String, Object>> getPostComments(
            @Parameter(description = "帖子ID", example = "123456") @PathVariable String postId,
            @Parameter(description = "平台类型: FACEBOOK, INSTAGRAM", example = "FACEBOOK") @RequestParam String platformType,
            @Parameter(description = "访问令牌", example = "EAA...") @RequestParam String accessToken) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("获取帖子评论列表请求，帖子ID: {}, 平台类型: {}", postId, platformType);
            
            List<SocialComment> comments = socialMediaService.getPostComments(postId, platformType, accessToken);
            
            response.put("code", 200);
            response.put("message", "获取成功");
            response.put("data", comments);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("获取评论列表参数错误: {}", e.getMessage());
            response.put("code", 400);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("获取评论列表失败: {}", e.getMessage(), e);
            response.put("code", 500);
            response.put("message", "获取评论列表失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 删除评论
     */
    @DeleteMapping("/comments/{commentId}")
    @Operation(summary = "删除评论", description = "删除指定的评论")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "删除成功", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(example = "{\"code\": 200, \"message\": \"评论删除成功\"}"))),
            @ApiResponse(responseCode = "400", description = "参数错误", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(example = "{\"code\": 400, \"message\": \"参数错误\"}"))),
            @ApiResponse(responseCode = "500", description = "删除失败", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(example = "{\"code\": 500, \"message\": \"评论删除失败\"}")))
    })
    public ResponseEntity<Map<String, Object>> deleteComment(
            @Parameter(description = "评论ID", example = "123456") @PathVariable String commentId,
            @Parameter(description = "平台类型: FACEBOOK, INSTAGRAM", example = "FACEBOOK") @RequestParam String platformType,
            @Parameter(description = "访问令牌", example = "EAA...") @RequestParam String accessToken) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("删除评论请求，评论ID: {}, 平台类型: {}", commentId, platformType);
            
            boolean success = socialMediaService.deleteComment(commentId, platformType, accessToken);
            
            if (success) {
                response.put("code", 200);
                response.put("message", "评论删除成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("code", 500);
                response.put("message", "评论删除失败");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (IllegalArgumentException e) {
            logger.error("删除评论参数错误: {}", e.getMessage());
            response.put("code", 400);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("删除评论失败: {}", e.getMessage(), e);
            response.put("code", 500);
            response.put("message", "删除评论失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}