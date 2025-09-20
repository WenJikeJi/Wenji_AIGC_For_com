package com.wenji.server.controller;

import com.wenji.server.model.UserOperationLog;
import com.wenji.server.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/logs")
public class LogController {
    
    @Autowired
    private LogService logService;
    
    // 获取操作日志列表接口
    @GetMapping
    public ResponseEntity<?> getOperationLogs(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String operation,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
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
    
    // 根据操作类型获取日志接口
    @GetMapping("/by-operation")
    public ResponseEntity<?> getLogsByOperationType(
            @RequestParam String operationType,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
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
    
    // 获取登录日志接口
    @GetMapping("/login")
    public ResponseEntity<?> getLoginLogs(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
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