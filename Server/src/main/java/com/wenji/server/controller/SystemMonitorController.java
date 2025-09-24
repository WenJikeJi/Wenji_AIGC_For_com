package com.wenji.server.controller;

import com.wenji.server.service.SystemMonitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/monitor")
@Tag(name = "系统监控", description = "系统状态监控接口")
public class SystemMonitorController {
    
    private static final Logger logger = LoggerFactory.getLogger(SystemMonitorController.class);
    
    @Autowired
    private SystemMonitorService systemMonitorService;
    
    /**
     * 特殊权限邮箱，只有此邮箱的用户可以访问监控页面
     */
    private static final String ADMIN_EMAIL = "ken@shamillaa.com";
    
    @Operation(
            summary = "获取系统监控数据",
            description = "获取系统状态、接口调用统计、数据库统计等监控信息，仅限特定管理员访问",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"系统监控"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(example = "{\"systemStatus\": {\"uptime\": \"2天3小时\", \"memoryUsage\": \"65%\", \"cpuUsage\": \"45%\"}, \"apiStats\": {\"totalCalls\": 1250, \"last10SecondsCalls\": 15, \"errorRate\": \"2.3%\"}, \"databaseStats\": {\"mainAccountCount\": 125, \"subAccountCount\": 456, \"totalUsers\": 581, \"activeConnections\": 8}}"))),
            @ApiResponse(responseCode = "401", description = "未授权，需要登录"),
            @ApiResponse(responseCode = "403", description = "权限不足，仅限特定管理员访问")
    })
    @GetMapping("/dashboard")
    public ResponseEntity<?> getMonitorDashboard(HttpServletRequest request) {
        try {
            // 权限验证：只有特定邮箱的用户可以访问
            String userEmail = (String) request.getAttribute("email");
            if (!ADMIN_EMAIL.equals(userEmail)) {
                logger.warn("未授权的监控页面访问尝试，用户邮箱: {}", userEmail);
                return ResponseEntity.status(403).body(Map.of("error", "权限不足，仅限特定管理员访问"));
            }
            
            // 获取监控数据
            Map<String, Object> monitorData = systemMonitorService.getMonitorDashboard();
            
            logger.info("管理员 {} 访问了系统监控页面", userEmail);
            return ResponseEntity.ok(monitorData);
            
        } catch (Exception e) {
            logger.error("获取系统监控数据失败", e);
            return ResponseEntity.badRequest().body(Map.of("error", "获取监控数据失败: " + e.getMessage()));
        }
    }
    
    @Operation(
            summary = "获取实时API调用统计",
            description = "获取最近10秒的API调用统计信息",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"系统监控"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(example = "{\"last10Seconds\": 15, \"currentMinute\": 89, \"errorCount\": 2, \"successRate\": \"97.8%\"}"))),
            @ApiResponse(responseCode = "401", description = "未授权，需要登录"),
            @ApiResponse(responseCode = "403", description = "权限不足，仅限特定管理员访问")
    })
    @GetMapping("/api-stats")
    public ResponseEntity<?> getApiStats(HttpServletRequest request) {
        try {
            // 权限验证
            String userEmail = (String) request.getAttribute("email");
            if (!ADMIN_EMAIL.equals(userEmail)) {
                return ResponseEntity.status(403).body(Map.of("error", "权限不足，仅限特定管理员访问"));
            }
            
            Map<String, Object> apiStats = systemMonitorService.getApiStats();
            return ResponseEntity.ok(apiStats);
            
        } catch (Exception e) {
            logger.error("获取API统计数据失败", e);
            return ResponseEntity.badRequest().body(Map.of("error", "获取API统计失败: " + e.getMessage()));
        }
    }
    
    @Operation(
            summary = "获取数据库统计信息",
            description = "获取主账号数量、子账号数量等数据库统计信息",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"系统监控"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(example = "{\"mainAccountCount\": 125, \"subAccountCount\": 456, \"totalUsers\": 581, \"activeUsers\": 234, \"todayRegistrations\": 12}"))),
            @ApiResponse(responseCode = "401", description = "未授权，需要登录"),
            @ApiResponse(responseCode = "403", description = "权限不足，仅限特定管理员访问")
    })
    @GetMapping("/database-stats")
    public ResponseEntity<?> getDatabaseStats(HttpServletRequest request) {
        try {
            // 权限验证：优先从请求头获取用户邮箱，然后从请求属性获取
            String userEmail = request.getHeader("x-user-email");
            if (userEmail == null || userEmail.isEmpty()) {
                userEmail = (String) request.getAttribute("email");
            }
            
            logger.info("系统监控数据库统计API请求，用户邮箱: {}", userEmail);
            
            if (userEmail == null || !ADMIN_EMAIL.equals(userEmail)) {
                logger.warn("未授权的数据库统计API访问尝试，用户邮箱: {}", userEmail);
                return ResponseEntity.status(403).body(Map.of("error", "权限不足，仅限特定管理员访问"));
            }
            
            Map<String, Object> dbStats = systemMonitorService.getDatabaseStats();
            return ResponseEntity.ok(dbStats);
            
        } catch (Exception e) {
            logger.error("获取数据库统计数据失败", e);
            return ResponseEntity.badRequest().body(Map.of("error", "获取数据库统计失败: " + e.getMessage()));
        }
    }
    
    @Operation(
            summary = "获取系统健康状态",
            description = "获取系统运行状态、内存使用、CPU使用等健康指标",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"系统监控"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(example = "{\"status\": \"healthy\", \"uptime\": \"2天3小时45分钟\", \"memoryUsage\": {\"used\": \"512MB\", \"total\": \"1024MB\", \"percentage\": \"50%\"}, \"cpuUsage\": \"35%\", \"diskUsage\": \"78%\"}"))),
            @ApiResponse(responseCode = "401", description = "未授权，需要登录"),
            @ApiResponse(responseCode = "403", description = "权限不足，仅限特定管理员访问")
    })
    @GetMapping("/health")
    public ResponseEntity<?> getSystemHealth(HttpServletRequest request) {
        try {
            // 权限验证
            String userEmail = (String) request.getAttribute("email");
            if (!ADMIN_EMAIL.equals(userEmail)) {
                return ResponseEntity.status(403).body(Map.of("error", "权限不足，仅限特定管理员访问"));
            }
            
            Map<String, Object> healthData = systemMonitorService.getSystemHealth();
            return ResponseEntity.ok(healthData);
            
        } catch (Exception e) {
            logger.error("获取系统健康状态失败", e);
            return ResponseEntity.badRequest().body(Map.of("error", "获取系统健康状态失败: " + e.getMessage()));
        }
    }
}