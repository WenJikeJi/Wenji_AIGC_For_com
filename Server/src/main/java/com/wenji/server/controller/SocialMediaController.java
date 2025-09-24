package com.wenji.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wenji.server.dto.FacebookTokenRequest;
import com.wenji.server.dto.SocialPostRequest;
import com.wenji.server.dto.SocialPostResponse;
import com.wenji.server.dto.TaskRequest;
import com.wenji.server.dto.TaskResponse;
import com.wenji.server.model.SocialHomepage;
import com.wenji.server.model.SocialPost;
import com.wenji.server.model.SocialPostTask;
import com.wenji.server.service.FacebookTokenService;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/social-media")
@Tag(name = "社交媒体管理", description = "社交媒体管理相关接口")
public class SocialMediaController {

    private static final Logger logger = LoggerFactory.getLogger(SocialMediaController.class);

    @Autowired
    private SocialMediaService socialMediaService;

    @Autowired
    private FacebookTokenService facebookTokenService;

    /**
     * 获取用户ID - 从JWT认证信息中获取
     */
    private Long getUserIdFromToken(HttpServletRequest request) {
        // 从request属性中获取用户ID
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            logger.warn("未从JWT中获取到用户ID");
            // 实际环境中应该抛出异常或返回错误，这里为了演示暂时返回固定值
            return 1L;
        }
        return userId;
    }

    /**
     * 发布即时帖子
     */
    @Operation(
            summary = "发布即时帖子",
            description = "在指定社交媒体平台上发布即时帖子",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"社交媒体管理"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "发布成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "500", description = "发布失败")
    })
    @PostMapping("/publish")
    public ResponseEntity<SocialPostResponse> publishPost(
            @RequestBody SocialPostRequest request,
            HttpServletRequest servletRequest) {
        try {
            // 获取用户ID
            Long userId = getUserIdFromToken(servletRequest);
            logger.info("用户 {} 发布即时帖子", userId);
            
            // 调用服务层发布帖子
            SocialPostResponse response = socialMediaService.publishPost(request, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("发布即时帖子失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 创建定时发帖任务
     */
    @Operation(
            summary = "创建定时发帖任务",
            description = "创建一个定时发布到社交媒体平台的任务",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"社交媒体管理"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "任务创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "500", description = "任务创建失败")
    })
    @PostMapping("/schedule")
    public ResponseEntity<?> schedulePost(
            @RequestBody SocialPostRequest request,
            HttpServletRequest servletRequest) {
        try {
            // 获取用户ID
            Long userId = getUserIdFromToken(servletRequest);
            logger.info("用户 {} 创建定时发帖任务", userId);
            
            // 调用服务层创建定时任务
            SocialPostResponse response = socialMediaService.schedulePost(request, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("创建定时发帖任务失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 获取定时任务列表
     */
    @Operation(
            summary = "获取定时任务列表",
            description = "获取用户的所有定时发帖任务列表",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"社交媒体管理"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "500", description = "获取失败")
    })
    @GetMapping("/tasks")
    public ResponseEntity<?> getTaskList(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页条数", example = "10") @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest servletRequest) {
        try {
            // 获取用户ID
            Long userId = getUserIdFromToken(servletRequest);
            logger.info("用户 {} 获取定时任务列表", userId);
            
            // 调用服务层获取任务列表
            Pageable pageable = PageRequest.of(page - 1, pageSize);
            Page<SocialPostTask> taskPage = socialMediaService.getTaskList(null, pageable);
            return ResponseEntity.ok(taskPage);
        } catch (Exception e) {
            logger.error("获取定时任务列表失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 取消定时任务
     */
    @Operation(
            summary = "取消定时任务",
            description = "取消指定的定时发帖任务",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"社交媒体管理"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "取消成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "500", description = "取消失败")
    })
    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<?> cancelTask(
            @PathVariable Long taskId,
            HttpServletRequest servletRequest) {
        try {
            // 获取用户ID
            Long userId = getUserIdFromToken(servletRequest);
            logger.info("用户 {} 取消定时任务 {} 失败", userId, taskId);
            
            // 调用服务层取消任务
            socialMediaService.cancelTask(taskId, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("取消定时任务失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 获取用户主页列表
     */
    @Operation(
            summary = "获取用户主页列表",
            description = "获取用户绑定的所有社交媒体主页列表",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"社交媒体管理"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "500", description = "获取失败")
    })
    @GetMapping("/homepages")
    public ResponseEntity<?> fetchHomepages(
            @Parameter(description = "平台类型", example = "1") @RequestParam(required = false) Integer platformType,
            HttpServletRequest servletRequest) {
        try {
            // 获取用户ID
            Long userId = getUserIdFromToken(servletRequest);
            logger.info("用户 {} 获取主页列表", userId);
            
            // 调用服务层获取主页列表
            List<SocialHomepage> homepages = socialMediaService.getUserHomepages(userId);
            // 如果指定了平台类型，进行过滤
            if (platformType != null) {
                homepages = homepages.stream()
                    .filter(homepage -> homepage.getPlatformType().equals(platformType))
                    .collect(java.util.stream.Collectors.toList());
            }
            return ResponseEntity.ok(homepages);
        } catch (Exception e) {
            logger.error("获取主页列表失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 获取帖子列表
     */
    @Operation(
            summary = "获取帖子列表",
            description = "获取用户发布的所有帖子列表",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"社交媒体管理"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "500", description = "获取失败")
    })
    @GetMapping("/posts")
    public ResponseEntity<?> getPosts(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页条数", example = "10") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "平台类型", example = "1") @RequestParam(required = false) Integer platformType,
            @Parameter(description = "主页ID", example = "123") @RequestParam(required = false) String homepageId,
            HttpServletRequest servletRequest) {
        try {
            // 获取用户ID
            Long userId = getUserIdFromToken(servletRequest);
            logger.info("用户 {} 获取帖子列表", userId);
            
            // 调用服务层获取帖子列表
            Pageable pageable = PageRequest.of(page - 1, pageSize);
            Page<SocialPost> postPage = socialMediaService.getPostList(pageable);
            return ResponseEntity.ok(postPage);
        } catch (Exception e) {
            logger.error("获取帖子列表失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 重试失败的任务
     */
    @Operation(
            summary = "重试失败的任务",
            description = "重试指定的失败任务",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"社交媒体管理"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "重试成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "500", description = "重试失败")
    })
    @PostMapping("/tasks/{taskId}/retry")
    public ResponseEntity<?> retryTask(
            @PathVariable Long taskId,
            HttpServletRequest servletRequest) {
        try {
            // 获取用户ID
            Long userId = getUserIdFromToken(servletRequest);
            logger.info("用户 {} 重试任务 {}", userId, taskId);
            
            // 调用服务层重试任务
            socialMediaService.retryTask(taskId, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("重试任务失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== Facebook 专用接口 ====================

    /**
     * 获取Facebook授权URL
     */
    @Operation(
            summary = "获取Facebook授权URL",
            description = "获取用于引导用户授权Facebook账号的URL",
            tags = {"社交媒体管理"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "500", description = "获取失败")
    })
    @GetMapping("/facebook/auth-url")
    public ResponseEntity<String> getFacebookAuthUrl(
            @Parameter(description = "授权成功后的重定向URL", example = "http://localhost:8080/callback") 
            @RequestParam(required = false) String redirectUrl) {
        try {
            logger.info("获取Facebook授权URL");
            
            // 调用服务层生成授权URL
            String authUrl = socialMediaService.getFacebookAuthUrl(redirectUrl);
            return ResponseEntity.ok(authUrl);
        } catch (Exception e) {
            logger.error("获取Facebook授权URL失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * 验证Facebook令牌
     */
    @Operation(
            summary = "验证Facebook令牌",
            description = "验证Facebook访问令牌的有效性",
            tags = {"社交媒体管理"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "验证成功"),
            @ApiResponse(responseCode = "400", description = "令牌无效"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "500", description = "验证失败")
    })
    @PostMapping("/facebook/verify-token")
    public ResponseEntity<?> verifyFacebookToken(
            @Parameter(description = "Facebook访问令牌、APP ID和APP密钥") @RequestBody FacebookTokenRequest request,
            HttpServletRequest servletRequest) {
        try {
            // 获取用户ID（可选）
            Long userId = getUserIdFromToken(servletRequest);
            if (userId != null) {
                logger.info("用户 {} 请求验证Facebook访问令牌", userId);
            } else {
                logger.info("匿名用户请求验证Facebook访问令牌");
            }
            
            // 添加调试日志：打印接收到的请求参数
            logger.info("接收到的请求参数 - appId: {}, token前5后5位: {}", 
                request.getAppId(), 
                request.getToken() != null && request.getToken().length() > 10 ? 
                    request.getToken().substring(0, 5) + "*****" + request.getToken().substring(request.getToken().length() - 5) : 
                    "******");
            
            // 支持两种参数格式：token 或 accessToken
            String accessToken = request.getToken();
            if (accessToken == null) {
                accessToken = request.getAccessToken();
            }
            
            String appId = request.getAppId();
            String appSecret = request.getAppSecret();
            
            // 参数校验
            if (accessToken == null || accessToken.trim().isEmpty()) {
                String userIdLog = userId != null ? "[用户ID: " + userId + "]" : "";
                logger.warn("Facebook Token验证失败：缺少访问令牌参数 {}", userIdLog);
                return ResponseEntity.badRequest().body(
                        Map.of("valid", false, "error", "访问令牌不能为空")
                );
            }
            
            // APP ID和APP密钥为可选参数，但如果提供了一个，最好两个都提供
            if ((appId != null && appSecret == null) || (appId == null && appSecret != null)) {
                String userIdLog = userId != null ? "[用户ID: " + userId + "]" : "";
                logger.warn("Facebook Token验证：APP ID和APP密钥应同时提供 {}", userIdLog);
            }
            
            // 记录详细日志，但注意不要记录完整的access token
            String maskedToken = accessToken.length() > 10 ? accessToken.substring(0, 5) + "*****" + accessToken.substring(accessToken.length() - 5) : "******";
            String userIdLog = userId != null ? ", 用户ID: " + userId : "";
            logger.info("验证Facebook访问令牌，APP ID: {}{}, 令牌前5后5位: {}", appId, userIdLog, maskedToken);
            
            // 调用服务层验证令牌
            FacebookTokenService.FacebookTokenValidationResult validationResult = facebookTokenService.verifyToken(accessToken);
            
            if (validationResult.isValid()) {
                // 返回详细的验证成功信息
                return ResponseEntity.ok(Map.of(
                        "valid", true,
                        "details", Map.of(
                                "userId", validationResult.getUserId(),
                                "userName", validationResult.getUserName(),
                                "userEmail", validationResult.getUserEmail(),
                                "tokenStatus", "有效"
                        )
                ));
            } else {
                // 返回详细的验证失败信息
                logger.warn("Facebook Token验证失败：{}{}", validationResult.getErrorMessage(), userIdLog);
                return ResponseEntity.badRequest().body(
                        Map.of("valid", false, "error", validationResult.getErrorMessage())
                );
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Facebook Token验证参数异常: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    Map.of("valid", false, "error", e.getMessage())
            );
        } catch (Exception e) {
            logger.error("验证Facebook访问令牌失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("valid", false, "error", "验证失败，请稍后重试")
            );
        }
    }

    /**
     * Facebook回调接口
     * 处理Facebook授权成功后的回调，用于保存访问令牌
     */
    @GetMapping("/facebook/callback")
    @Operation(summary = "Facebook回调接口", description = "处理Facebook授权成功后的回调")
    public ResponseEntity<Void> handleFacebookCallback(
            @RequestParam("code") String code,
            @RequestParam("state") String state,
            jakarta.servlet.http.HttpServletRequest request) {
        try {
            // 从request属性中获取用户ID
            Long userId = (Long) request.getAttribute("userId");
            
            if (userId == null) {
                // 如果没有用户ID，说明用户未登录，重定向到登录页面
                return ResponseEntity.status(HttpStatus.FOUND)
                        .header(HttpHeaders.LOCATION, "/login")
                        .build();
            }
            
            // 解码state参数，获取原始的重定向URL
            byte[] decodedState = Base64.getDecoder().decode(state);
            String redirectUrl = new String(decodedState, StandardCharsets.UTF_8);
            
            logger.info("收到Facebook回调，用户ID: {}, code: {}, 重定向URL: {}", userId, code, redirectUrl);
            
            // 处理Facebook回调
            boolean success = socialMediaService.handleFacebookCallback(code, redirectUrl, userId);
            
            if (success) {
                // 回调处理成功，重定向到成功页面
                return ResponseEntity.status(HttpStatus.FOUND)
                        .header(HttpHeaders.LOCATION, "/social/facebook/success")
                        .build();
            } else {
                // 回调处理失败，重定向到失败页面
                return ResponseEntity.status(HttpStatus.FOUND)
                        .header(HttpHeaders.LOCATION, "/social/facebook/error")
                        .build();
            }
        } catch (Exception e) {
            logger.error("处理Facebook回调失败", e);
            // 发生异常，重定向到错误页面
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, "/social/facebook/error")
                    .build();
        }
    }

    /**
     * 保存Facebook访问令牌
     */
    @Operation(
            summary = "保存Facebook访问令牌",
            description = "保存用户的Facebook访问令牌、APP ID和APP密钥",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"社交媒体管理"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "保存成功"),
            @ApiResponse(responseCode = "400", description = "参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/facebook/save-token")
    public ResponseEntity<?> saveFacebookToken(
            @Parameter(description = "Facebook访问令牌、APPID和APP密钥") @RequestBody Map<String, String> requestBody,
            HttpServletRequest servletRequest) {
        try {
            // 获取用户ID
            Long userId = getUserIdFromToken(servletRequest);
            if (userId == null) {
                logger.warn("Facebook Token保存失败：用户未登录或令牌无效");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户未登录或令牌无效");
            }
            
            // 支持两种参数格式：token 或 accessToken
            String accessToken = requestBody.get("token");
            if (accessToken == null) {
                accessToken = requestBody.get("accessToken");
            }
            
            String appId = requestBody.get("appId");
            String appSecret = requestBody.get("appSecret");
            
            // 参数校验
            if (accessToken == null || accessToken.trim().isEmpty()) {
                logger.warn("Facebook Token保存失败：缺少访问令牌参数 [用户ID: {}]", userId);
                return ResponseEntity.badRequest().body("访问令牌不能为空");
            }
            
            // APP ID和APP密钥为可选参数，但如果提供了一个，最好两个都提供
            if ((appId != null && appSecret == null) || (appId == null && appSecret != null)) {
                logger.warn("Facebook Token保存：APP ID和APP密钥应同时提供 [用户ID: {}]", userId);
            }
            
            // 记录详细日志，但注意不要记录完整的access token
            String maskedToken = accessToken.length() > 10 ? accessToken.substring(0, 5) + "*****" + accessToken.substring(accessToken.length() - 5) : "******";
            logger.info("用户 {} 保存Facebook访问令牌，APP ID: {}，令牌前5后5位: {}", userId, appId, maskedToken);
            
            // 调用服务层保存令牌
            socialMediaService.saveFacebookToken(userId, accessToken, appId, appSecret);
            return ResponseEntity.ok().body("Facebook访问令牌保存成功");
        } catch (IllegalArgumentException e) {
            logger.warn("Facebook Token保存参数异常: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("保存Facebook访问令牌失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("保存Facebook访问令牌失败，请稍后重试");
        }
    }

    /**
     * 解绑Facebook账号
     */
    @Operation(
            summary = "解绑Facebook账号",
            description = "解绑用户的Facebook账号绑定",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"社交媒体管理"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "解绑成功"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "500", description = "解绑失败")
    })
    @DeleteMapping("/facebook/unbind")
    public ResponseEntity<?> unbindFacebook(
            HttpServletRequest servletRequest) {
        try {
            // 获取用户ID
            Long userId = getUserIdFromToken(servletRequest);
            logger.info("用户 {} 解绑Facebook账号", userId);
            
            // 调用服务层解绑账号
            socialMediaService.unbindFacebook(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("解绑Facebook账号失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 获取Facebook帖子列表
     */
    @Operation(
            summary = "获取Facebook帖子列表",
            description = "获取用户Facebook主页上的帖子列表",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"社交媒体管理"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "500", description = "获取失败")
    })
    @GetMapping("/facebook/posts")
    public ResponseEntity<?> getFacebookPosts(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页条数", example = "10") @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest servletRequest) {
        try {
            // 获取用户ID
            Long userId = getUserIdFromToken(servletRequest);
            logger.info("用户 {} 获取Facebook帖子列表", userId);
            
            // 调用服务层获取Facebook帖子列表
            return ResponseEntity.ok(socialMediaService.getFacebookPosts(userId, page, pageSize));
        } catch (Exception e) {
            logger.error("获取Facebook帖子列表失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== Instagram 专用接口 ====================

    /**
     * 获取Instagram Pages
     */
    @Operation(
            summary = "获取Instagram Pages",
            description = "获取用户绑定的Instagram Business Pages",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"社交媒体管理"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "500", description = "获取失败")
    })
    @GetMapping("/instagram/pages")
    public ResponseEntity<?> getInstagramPages(
            HttpServletRequest servletRequest) {
        try {
            // 获取用户ID
            Long userId = getUserIdFromToken(servletRequest);
            logger.info("用户 {} 获取Instagram Pages", userId);
            
            // 调用服务层获取Instagram Pages
            return ResponseEntity.ok(socialMediaService.getInstagramPages(userId));
        } catch (Exception e) {
            logger.error("获取Instagram Pages失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 选择Instagram Page
     */
    @Operation(
            summary = "选择Instagram Page",
            description = "选择要使用的Instagram Page",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"社交媒体管理"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "选择成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "500", description = "选择失败")
    })
    @PostMapping("/instagram/select-page")
    public ResponseEntity<?> selectInstagramPage(
            @Parameter(description = "Instagram Page ID") @RequestBody Map<String, String> requestBody,
            HttpServletRequest servletRequest) {
        try {
            // 获取用户ID
            Long userId = getUserIdFromToken(servletRequest);
            String pageId = requestBody.get("pageId");
            
            if (pageId == null) {
                return ResponseEntity.badRequest().build();
            }
            
            logger.info("用户 {} 选择Instagram Page: {}", userId, pageId);
            
            // 调用服务层选择Instagram Page
            socialMediaService.selectInstagramPage(userId, pageId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("选择Instagram Page失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 解绑Instagram账号
     */
    @Operation(
            summary = "解绑Instagram账号",
            description = "解绑用户的Instagram账号绑定",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"社交媒体管理"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "解绑成功"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "500", description = "解绑失败")
    })
    @DeleteMapping("/instagram/unbind")
    public ResponseEntity<?> unbindInstagram(
            HttpServletRequest servletRequest) {
        try {
            // 获取用户ID
            Long userId = getUserIdFromToken(servletRequest);
            logger.info("用户 {} 解绑Instagram账号", userId);
            
            // 调用服务层解绑账号
            socialMediaService.unbindInstagram(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("解绑Instagram账号失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 获取Instagram帖子列表
     */
    @Operation(
            summary = "获取Instagram帖子列表",
            description = "获取用户Instagram主页上的帖子列表",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"社交媒体管理"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "未授权"),
            @ApiResponse(responseCode = "500", description = "获取失败")
    })
    @GetMapping("/instagram/posts")
    public ResponseEntity<?> getInstagramPosts(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页条数", example = "10") @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest servletRequest) {
        try {
            // 获取用户ID
            Long userId = getUserIdFromToken(servletRequest);
            logger.info("用户 {} 获取Instagram帖子列表", userId);
            
            // 调用服务层获取Instagram帖子列表
            return ResponseEntity.ok(socialMediaService.getInstagramPosts(userId, page, pageSize));
        } catch (Exception e) {
            logger.error("获取Instagram帖子列表失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}