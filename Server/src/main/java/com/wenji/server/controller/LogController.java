package com.wenji.server.controller;

import com.wenji.server.model.UserOperationLog;
import com.wenji.server.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
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
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/logs")
public class LogController {
    
    @Autowired
    private LogService logService;
    
    @Operation(
            summary = "获取操作日志列表",
            description = "获取用户的操作日志列表，支持分页和多种条件筛选",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"日志管理"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(example = "{\"total\": 100, \"page\": 1, \"size\": 10, \"data\": [{\"id\": 1, \"userId\": 1, \"username\": \"张三\", \"operation\": \"登录\", \"ip\": \"192.168.1.1\", \"address\": \"北京市\", \"result\": \"success\", \"createTime\": \"2023-01-10T15:30:00\"}, ...]}"))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权，需要登录")
    })
    @GetMapping
    public ResponseEntity<?> getOperationLogs(
            @Parameter(description = "页码，默认为1", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页条数，默认为10", example = "10") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "用户ID，可选，用于筛选特定用户的日志", example = "1") @RequestParam(required = false) Long userId,
            @Parameter(description = "操作类型，可选，用于筛选特定操作类型的日志", example = "登录") @RequestParam(required = false) String operation,
            @Parameter(description = "开始时间，可选，用于筛选指定时间范围内的日志", example = "2023-01-01T00:00:00") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "结束时间，可选，用于筛选指定时间范围内的日志", example = "2023-01-31T23:59:59") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            HttpServletRequest request) {
        try {
            // 获取当前登录用户信息
            Long currentUserId = Long.parseLong(request.getAttribute("userId").toString());
            Integer currentUserRole = Integer.parseInt(request.getAttribute("role").toString());
            
            // 权限控制
            Page<UserOperationLog> logPage;
            if (currentUserRole == 0) {
                // 主账号：可以查看自己和子账号的日志
                if (userId != null) {
                    // 如果指定了用户ID，则只查看该用户的日志
                    logPage = logService.getOperationLogs(page, size, userId, operation, startTime, endTime);
                } else {
                    // 否则查看自己和所有子账号的日志
                    logPage = logService.getMainAccountAndSubAccountsLogs(currentUserId, page, size);
                }
            } else {
                // 子账号：只能查看自己的日志
                logPage = logService.getOperationLogs(page, size, currentUserId, operation, startTime, endTime);
            }
            
            // 构造返回结果
            Map<String, Object> result = Map.of(
                    "total", logPage.getTotalElements(),
                    "page", page,
                    "size", size,
                    "data", logPage.getContent()
            );
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @Operation(
            summary = "根据操作类型获取日志",
            description = "根据操作类型获取用户的操作日志列表，支持分页",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"日志管理"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(example = "{\"total\": 50, \"page\": 1, \"size\": 10, \"data\": [{\"id\": 1, \"userId\": 1, \"username\": \"张三\", \"operation\": \"登录\", \"ip\": \"192.168.1.1\", \"address\": \"北京市\", \"result\": \"success\", \"createTime\": \"2023-01-10T15:30:00\"}, ...]}"))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权，需要登录")
    })
    @GetMapping("/by-operation")
    public ResponseEntity<?> getLogsByOperationType(
            @Parameter(description = "操作类型，必填", example = "登录") @RequestParam String operationType,
            @Parameter(description = "页码，默认为1", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页条数，默认为10", example = "10") @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        try {
            // 获取当前登录用户信息
            Long currentUserId = Long.parseLong(request.getAttribute("userId").toString());
            Integer currentUserRole = Integer.parseInt(request.getAttribute("role").toString());
            
            // 权限控制
            Page<UserOperationLog> logPage;
            if (currentUserRole == 0) {
                // 主账号：可以查看所有用户的指定类型日志
                logPage = logService.getOperationLogsByOperationType(operationType, page, size);
            } else {
                // 子账号：只能查看自己的指定类型日志
                logPage = logService.getOperationLogs(page, size, currentUserId, operationType, null, null);
            }
            
            // 构造返回结果
            Map<String, Object> result = Map.of(
                    "total", logPage.getTotalElements(),
                    "page", page,
                    "size", size,
                    "data", logPage.getContent()
            );
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @Operation(
            summary = "获取登录日志",
            description = "获取用户的登录日志列表，支持分页",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"日志管理"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(example = "{\"total\": 30, \"page\": 1, \"size\": 10, \"data\": [{\"id\": 1, \"userId\": 1, \"username\": \"张三\", \"operation\": \"登录\", \"ip\": \"192.168.1.1\", \"address\": \"北京市\", \"result\": \"success\", \"createTime\": \"2023-01-10T15:30:00\"}, ...]}"))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权，需要登录")
    })
    @GetMapping("/login")
    public ResponseEntity<?> getLoginLogs(
            @Parameter(description = "页码，默认为1", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页条数，默认为10", example = "10") @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        try {
            // 获取当前登录用户信息
            Long currentUserId = Long.parseLong(request.getAttribute("userId").toString());
            Integer currentUserRole = Integer.parseInt(request.getAttribute("role").toString());
            
            // 权限控制
            Page<UserOperationLog> logPage;
            if (currentUserRole == 0) {
                // 主账号：可以查看自己和子账号的登录日志
                logPage = logService.getOperationLogs(page, size, null, "登录", null, null);
            } else {
                // 子账号：只能查看自己的登录日志
                logPage = logService.getOperationLogs(page, size, currentUserId, "登录", null, null);
            }
            
            // 构造返回结果
            Map<String, Object> result = Map.of(
                    "total", logPage.getTotalElements(),
                    "page", page,
                    "size", size,
                    "data", logPage.getContent()
            );
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}