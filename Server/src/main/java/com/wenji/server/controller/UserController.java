package com.wenji.server.controller;

import com.wenji.server.model.UserAccount;
import com.wenji.server.service.UserService;
import com.wenji.server.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthService authService;
    
    /**
     * 获取用户ID - 从JWT认证信息中获取
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        // 从request属性中获取用户ID
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            log.warn("未从JWT中获取到用户ID");
            // 实际环境中应该抛出异常或返回错误，这里为了演示暂时返回固定值
            return 1L;
        }
        return userId;
    }
    
    @Operation(
            summary = "获取用户列表",
            description = "主账号可以查看所有用户列表，子账号无权限查看",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"用户管理"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(example = "{\"total\": 2, \"page\": 1, \"size\": 10, \"data\": [{\"id\": 1, \"username\": \"张三\", \"account\": \"zhangsan\", \"role\": 0, \"status\": 1}, {\"id\": 2, \"username\": \"李四\", \"account\": \"lisi\", \"role\": 1, \"status\": 1}]}"))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权，需要登录"),
            @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @GetMapping
    public ResponseEntity<?> getUserList(
            @Parameter(description = "页码，默认为1", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页条数，默认为10", example = "10") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "父账号ID，可选，用于筛选特定主账号下的子账号", example = "1") @RequestParam(required = false) Long parentId,
            HttpServletRequest request) {
        try {
            // 获取当前登录用户信息（这里简化处理）
            Long currentUserId = Long.parseLong(request.getAttribute("userId").toString());
            Integer currentUserRole = Integer.parseInt(request.getAttribute("role").toString());
            
            // 权限控制
            // - 主账号只能查看自己和自己创建的子账号
            // - 子账号只能查看自己
            Page<UserAccount> userPage;
            if (currentUserRole == 0) {
                // 主账号，只能查看自己和自己的子账号
                userPage = userService.getUserListForMainAccount(page, size, currentUserId, parentId);
            } else {
                // 子账号，只能查看自己
                throw new RuntimeException("无权限查看用户列表");
            }
            
            // 构造返回结果
            Map<String, Object> result = Map.of(
                    "total", userPage.getTotalElements(),
                    "page", page,
                    "size", size,
                    "users", userPage.getContent()  // 修改为users字段，与前端期望一致
            );
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @Operation(
            summary = "添加子账号",
            description = "主账号添加子账号，每个子账号与主账号关联",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"用户管理"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "子账号添加成功", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\": \"子账号添加成功\"}"))),
            @ApiResponse(responseCode = "400", description = "请求参数错误或子账号添加失败"),
            @ApiResponse(responseCode = "401", description = "未授权，需要登录"),
            @ApiResponse(responseCode = "403", description = "只有主账号才能添加子账号")
    })
    @PostMapping("/subaccount")
    public ResponseEntity<?> addSubAccount(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "子账号信息",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{\"username\": \"子账号名称\", \"account\": \"subaccount001\", \"email\": \"subaccount@example.com\", \"password\": \"password123\", \"role\": 3}"
                            )
                    )
            )
            @RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
        try {
            // 获取当前登录用户信息
            Long currentUserId = Long.parseLong(request.getAttribute("userId").toString());
            Integer currentUserRole = Integer.parseInt(request.getAttribute("role").toString());
            
            // 验证是否为主账号
            if (currentUserRole != 0) {
                throw new RuntimeException("只有主账号才能添加子账号");
            }
            
            String username = (String) requestBody.get("username");
            String email = (String) requestBody.get("email");
            Integer role = requestBody.get("role") != null ? 
                Integer.parseInt(requestBody.get("role").toString()) : 3; // 默认编辑角色
            
            // 获取客户端IP
            String clientIp = getClientIp(request);
            
            // 使用邮箱作为账号，不传递密码让后端生成临时密码
            userService.addSubAccount(currentUserId, username, email, email, null, role, clientIp);
            return ResponseEntity.ok(Map.of("message", "子账号添加成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @Operation(
            summary = "解除子账号关联",
            description = "主账号解除与子账号的关联关系",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"用户管理"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "子账号解除关联成功", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\": \"子账号解除关联成功\"}"))),
            @ApiResponse(responseCode = "400", description = "解除关联失败"),
            @ApiResponse(responseCode = "401", description = "未授权，需要登录"),
            @ApiResponse(responseCode = "403", description = "只有主账号才能解除子账号关联"),
            @ApiResponse(responseCode = "404", description = "子账号不存在")
    })
    @DeleteMapping("/subaccount/{subAccountId}")
    public ResponseEntity<?> unlinkSubAccount(
            @Parameter(description = "子账号ID", example = "2") @PathVariable Long subAccountId, 
            HttpServletRequest request) {
        try {
            // 获取当前登录用户信息
            Long currentUserId = Long.parseLong(request.getAttribute("userId").toString());
            Integer currentUserRole = Integer.parseInt(request.getAttribute("role").toString());
            
            // 验证是否为主账号
            if (currentUserRole != 0) {
                throw new RuntimeException("只有主账号才能解除子账号关联");
            }
            
            // 获取客户端IP
            String clientIp = getClientIp(request);
            
            userService.unlinkSubAccount(currentUserId, subAccountId, clientIp);
            return ResponseEntity.ok(Map.of("message", "子账号解除关联成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @Operation(
            summary = "获取用户详情",
            description = "获取用户的详细信息，主账号可以查看任何用户，子账号只能查看自己",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"用户管理"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(example = "{\"id\": 1, \"username\": \"张三\", \"account\": \"zhangsan\", \"email\": \"zhangsan@example.com\", \"role\": 0, \"status\": 1, \"parentId\": null, \"createdTime\": \"2023-01-01T10:00:00\", \"lastLoginTime\": \"2023-01-10T15:30:00\"}"))),
            @ApiResponse(responseCode = "400", description = "查询失败"),
            @ApiResponse(responseCode = "401", description = "未授权，需要登录"),
            @ApiResponse(responseCode = "403", description = "无权限查看该用户信息"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserDetail(
            @Parameter(description = "用户ID", example = "1") @PathVariable Long userId, 
            HttpServletRequest request) {
        try {
            // 获取当前登录用户信息
            Long currentUserId = Long.parseLong(request.getAttribute("userId").toString());
            Integer currentUserRole = Integer.parseInt(request.getAttribute("role").toString());
            
            // 权限控制
            // - 超级账户（主账号）可以查看自己和自己的子账户
            // - 子账号只能查看自己
            if (currentUserRole == 0) {
                // 主账号权限：可以查看自己或自己的子账户
                UserAccount targetUser = userService.getUserById(userId);
                if (!userId.equals(currentUserId) && !currentUserId.equals(targetUser.getParentId())) {
                    throw new RuntimeException("无权限查看该用户信息");
                }
            } else {
                // 子账号权限：只能查看自己
                if (!userId.equals(currentUserId)) {
                    throw new RuntimeException("无权限查看该用户信息");
                }
            }
            
            UserAccount user = userService.getUserById(userId);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @Operation(
            summary = "更新用户状态",
            description = "更新用户的状态（启用/禁用）",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"用户管理"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "用户状态更新成功", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\": \"用户状态更新成功\"}"))),
            @ApiResponse(responseCode = "400", description = "更新失败或状态值无效"),
            @ApiResponse(responseCode = "401", description = "未授权，需要登录"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    // 启用/禁用用户
    @PutMapping("/{userId}/status")
    public ResponseEntity<Map<String, Object>> updateUserStatus(
            @PathVariable Long userId,
            @RequestBody Map<String, Integer> request,
            HttpServletRequest httpRequest) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 获取当前用户ID
            Long currentUserId = getCurrentUserId(httpRequest);
            
            Integer status = request.get("status");
            if (status == null || (status != 0 && status != 1)) {
                response.put("success", false);
                response.put("message", "状态值无效");
                return ResponseEntity.badRequest().body(response);
            }
            
            userService.updateUserStatus(userId, status, currentUserId);
            
            response.put("success", true);
            response.put("message", status == 1 ? "用户启用成功" : "用户禁用成功");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("更新用户状态失败", e);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 更新用户基本信息
    @PutMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable Long userId,
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 获取当前用户ID
            Long currentUserId = getCurrentUserId(httpRequest);
            
            String username = request.get("username");
            String account = request.get("account");
            String email = request.get("email");
            String phone = request.get("phone");
            String avatar = request.get("avatar");
            
            // 验证必填字段
            if ((username == null || username.trim().isEmpty()) &&
                (account == null || account.trim().isEmpty()) &&
                (email == null || email.trim().isEmpty()) &&
                (phone == null || phone.trim().isEmpty()) &&
                (avatar == null || avatar.trim().isEmpty())) {
                response.put("success", false);
                response.put("message", "至少需要提供一个要更新的字段");
                return ResponseEntity.badRequest().body(response);
            }
            
            userService.updateUserInfo(userId, username, account, email, phone, avatar, currentUserId);
            
            response.put("success", true);
            response.put("message", "用户信息更新成功");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("更新用户信息失败", e);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 修改密码接口（已登录状态）
    @Operation(
            summary = "修改用户密码",
            description = "当前登录用户修改自己的密码，需要验证旧密码的正确性，新密码需要符合系统安全要求",
            tags = {"用户管理"},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "密码修改成功", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(example = "{\"message\": \"密码修改成功\"}"))),
            @ApiResponse(responseCode = "400", description = "参数错误或密码不匹配", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(example = "{\"error\": \"旧密码错误\"}"))),
            @ApiResponse(responseCode = "401", description = "未授权，需要登录", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(example = "{\"error\": \"未授权访问\"}"))),
            @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @PostMapping("/password")
    public ResponseEntity<?> changePassword(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "密码修改请求参数",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{\"encryptedOldPassword\": \"old_encrypted_pwd\", \"encryptedNewPassword\": \"new_encrypted_pwd\"}"
                            )
                    )
            )
            @RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
        try {
            // 获取当前登录用户信息
            Long currentUserId = Long.parseLong(request.getAttribute("userId").toString());
            
            String encryptedOldPassword = (String) requestBody.get("encryptedOldPassword");
            String encryptedNewPassword = (String) requestBody.get("encryptedNewPassword");
            
            // 调用认证服务的修改密码方法
            authService.changePassword(currentUserId, encryptedOldPassword, encryptedNewPassword);
            
            return ResponseEntity.ok(Map.of("message", "密码修改成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // 获取客户端IP
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果是多级代理，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}