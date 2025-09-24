package com.wenji.server.controller;

import com.wenji.server.model.UserActivityStats;
import com.wenji.server.model.UserRegistrationStats;
import com.wenji.server.service.UserStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user-stats")
@Tag(name = "用户统计", description = "用户活跃度和注册数据统计接口")
public class UserStatsController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserStatsController.class);
    private static final String ADMIN_EMAIL = "ken@shamillaa.com";
    
    @Autowired
    private UserStatsService userStatsService;
    
    @Operation(
            summary = "获取用户活跃度统计数据",
            description = "获取指定时间维度和时间范围的用户活跃度统计数据，仅限特定管理员访问",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"用户统计"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "未授权，需要登录"),
            @ApiResponse(responseCode = "403", description = "权限不足，仅限特定管理员访问"),
            @ApiResponse(responseCode = "400", description = "参数错误")
    })
    @GetMapping("/activity")
    public ResponseEntity<?> getActivityStats(
            @Parameter(description = "时间维度：hour, day, week, month", example = "month")
            @RequestParam(defaultValue = "month") String timeDimension,
            @Parameter(description = "开始时间，格式：yyyy-MM-dd HH:mm:ss", example = "2024-01-01 00:00:00")
            @RequestParam(required = false) String startTime,
            @Parameter(description = "结束时间，格式：yyyy-MM-dd HH:mm:ss", example = "2024-12-31 23:59:59")
            @RequestParam(required = false) String endTime,
            @Parameter(description = "限制返回条数，默认30条", example = "30")
            @RequestParam(defaultValue = "30") int limit,
            HttpServletRequest request) {
        
        try {
            // 权限验证：只有特定邮箱的用户可以访问
            String userEmail = (String) request.getAttribute("email");
            if (!ADMIN_EMAIL.equals(userEmail)) {
                logger.warn("未授权的用户统计访问尝试，用户邮箱: {}", userEmail);
                return ResponseEntity.status(403).body(Map.of("error", "权限不足，仅限特定管理员访问"));
            }
            
            // 验证时间维度参数
            if (!isValidTimeDimension(timeDimension)) {
                return ResponseEntity.badRequest().body(Map.of("error", "无效的时间维度，支持：hour, day, week, month"));
            }
            
            List<UserActivityStats> stats;
            
            // 如果提供了时间范围，使用时间范围查询
            if (startTime != null && endTime != null) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime start = LocalDateTime.parse(startTime, formatter);
                    LocalDateTime end = LocalDateTime.parse(endTime, formatter);
                    stats = userStatsService.getActivityStats(timeDimension, start, end);
                } catch (Exception e) {
                    return ResponseEntity.badRequest().body(Map.of("error", "时间格式错误，请使用：yyyy-MM-dd HH:mm:ss"));
                }
            } else {
                // 否则获取最近N条记录
                stats = userStatsService.getRecentActivityStats(timeDimension, limit);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("timeDimension", timeDimension);
            result.put("data", stats);
            result.put("count", stats.size());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("获取用户活跃度统计数据失败", e);
            return ResponseEntity.status(500).body(Map.of("error", "服务器内部错误"));
        }
    }
    
    @Operation(
            summary = "获取用户注册统计数据",
            description = "获取指定时间维度和时间范围的用户注册统计数据，仅限特定管理员访问",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"用户统计"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "未授权，需要登录"),
            @ApiResponse(responseCode = "403", description = "权限不足，仅限特定管理员访问"),
            @ApiResponse(responseCode = "400", description = "参数错误")
    })
    @GetMapping("/registration")
    public ResponseEntity<?> getRegistrationStats(
            @Parameter(description = "时间维度：hour, day, week, month", example = "month")
            @RequestParam(defaultValue = "month") String timeDimension,
            @Parameter(description = "开始时间，格式：yyyy-MM-dd HH:mm:ss", example = "2024-01-01 00:00:00")
            @RequestParam(required = false) String startTime,
            @Parameter(description = "结束时间，格式：yyyy-MM-dd HH:mm:ss", example = "2024-12-31 23:59:59")
            @RequestParam(required = false) String endTime,
            @Parameter(description = "限制返回条数，默认30条", example = "30")
            @RequestParam(defaultValue = "30") int limit,
            HttpServletRequest request) {
        
        try {
            // 权限验证：只有特定邮箱的用户可以访问
            String userEmail = (String) request.getAttribute("email");
            if (!ADMIN_EMAIL.equals(userEmail)) {
                logger.warn("未授权的用户统计访问尝试，用户邮箱: {}", userEmail);
                return ResponseEntity.status(403).body(Map.of("error", "权限不足，仅限特定管理员访问"));
            }
            
            // 验证时间维度参数
            if (!isValidTimeDimension(timeDimension)) {
                return ResponseEntity.badRequest().body(Map.of("error", "无效的时间维度，支持：hour, day, week, month"));
            }
            
            List<UserRegistrationStats> stats;
            
            // 如果提供了时间范围，使用时间范围查询
            if (startTime != null && endTime != null) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime start = LocalDateTime.parse(startTime, formatter);
                    LocalDateTime end = LocalDateTime.parse(endTime, formatter);
                    stats = userStatsService.getRegistrationStats(timeDimension, start, end);
                } catch (Exception e) {
                    return ResponseEntity.badRequest().body(Map.of("error", "时间格式错误，请使用：yyyy-MM-dd HH:mm:ss"));
                }
            } else {
                // 否则获取最近N条记录
                stats = userStatsService.getRecentRegistrationStats(timeDimension, limit);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("timeDimension", timeDimension);
            result.put("data", stats);
            result.put("count", stats.size());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("获取用户注册统计数据失败", e);
            return ResponseEntity.status(500).body(Map.of("error", "服务器内部错误"));
        }
    }
    
    @Operation(
            summary = "获取用户统计概览",
            description = "获取用户活跃度和注册数据的综合统计概览，仅限特定管理员访问",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"用户统计"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "未授权，需要登录"),
            @ApiResponse(responseCode = "403", description = "权限不足，仅限特定管理员访问")
    })
    @GetMapping("/overview")
    public ResponseEntity<?> getStatsOverview(
            @Parameter(description = "时间维度：hour, day, week, month", example = "month")
            @RequestParam(defaultValue = "month") String timeDimension,
            @Parameter(description = "限制返回条数，默认12条", example = "12")
            @RequestParam(defaultValue = "12") int limit,
            HttpServletRequest request) {
        
        try {
            // 权限验证：只有特定邮箱的用户可以访问
            String userEmail = (String) request.getAttribute("email");
            if (!ADMIN_EMAIL.equals(userEmail)) {
                logger.warn("未授权的用户统计访问尝试，用户邮箱: {}", userEmail);
                return ResponseEntity.status(403).body(Map.of("error", "权限不足，仅限特定管理员访问"));
            }
            
            // 验证时间维度参数
            if (!isValidTimeDimension(timeDimension)) {
                return ResponseEntity.badRequest().body(Map.of("error", "无效的时间维度，支持：hour, day, week, month"));
            }
            
            // 获取活跃度和注册统计数据
            List<UserActivityStats> activityStats = userStatsService.getRecentActivityStats(timeDimension, limit);
            List<UserRegistrationStats> registrationStats = userStatsService.getRecentRegistrationStats(timeDimension, limit);
            
            Map<String, Object> result = new HashMap<>();
            result.put("timeDimension", timeDimension);
            result.put("activityStats", activityStats);
            result.put("registrationStats", registrationStats);
            result.put("activityCount", activityStats.size());
            result.put("registrationCount", registrationStats.size());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("获取用户统计概览失败", e);
            return ResponseEntity.status(500).body(Map.of("error", "服务器内部错误"));
        }
    }
    
    /**
     * 验证时间维度参数是否有效
     */
    private boolean isValidTimeDimension(String timeDimension) {
        return "hour".equals(timeDimension) || 
               "day".equals(timeDimension) || 
               "week".equals(timeDimension) || 
               "month".equals(timeDimension);
    }
}