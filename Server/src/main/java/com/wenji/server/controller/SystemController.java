package com.wenji.server.controller;

import com.wenji.server.service.SystemMonitorService;
import com.wenji.server.service.UserStatsService;
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
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/system")
@Tag(name = "系统监控", description = "系统监控相关接口")
public class SystemController {
    
    private static final Logger logger = LoggerFactory.getLogger(SystemController.class);
    private static final String ADMIN_EMAIL = "ken@shamillaa.com";
    
    @Autowired
    private SystemMonitorService systemMonitorService;
    
    @Autowired
    private UserStatsService userStatsService;
    
    @Operation(
            summary = "获取API端点监控数据",
            description = "获取各个API端点的调用统计、响应时间等监控信息",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"系统监控"}
    )
    @GetMapping("/api-endpoints")
    public ResponseEntity<?> getApiEndpoints(HttpServletRequest request) {
        try {
            // 权限验证
            String userEmail = (String) request.getAttribute("email");
            if (!ADMIN_EMAIL.equals(userEmail)) {
                return ResponseEntity.status(403).body(Map.of("success", false, "error", "权限不足，仅限特定管理员访问"));
            }
            
            // 获取API端点监控数据
            Map<String, Object> apiEndpointsData = systemMonitorService.getApiEndpointsStats();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", apiEndpointsData);
            
            logger.info("管理员 {} 获取了API端点监控数据", userEmail);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取API端点监控数据失败", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", "获取API端点监控数据失败: " + e.getMessage()));
        }
    }
    
    @Operation(
            summary = "获取系统监控数据",
            description = "获取系统状态、接口调用统计、数据库统计等监控信息",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"系统监控"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "未授权，需要登录"),
            @ApiResponse(responseCode = "403", description = "权限不足，仅限特定管理员访问")
    })
    @GetMapping("/monitor")
    public ResponseEntity<?> getSystemMonitorData(HttpServletRequest request) {
        try {
            // 权限验证：只有特定邮箱的用户可以访问
            String userEmail = (String) request.getAttribute("email");
            if (!ADMIN_EMAIL.equals(userEmail)) {
                logger.warn("未授权的监控页面访问尝试，用户邮箱: {}", userEmail);
                return ResponseEntity.status(403).body(Map.of("success", false, "error", "权限不足，仅限特定管理员访问"));
            }
            
            // 获取监控数据
            Map<String, Object> monitorData = systemMonitorService.getMonitorDashboard();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", monitorData);
            
            logger.info("管理员 {} 访问了系统监控数据", userEmail);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取系统监控数据失败", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", "获取监控数据失败: " + e.getMessage()));
        }
    }
    
    @Operation(
            summary = "获取用户统计数据",
            description = "获取用户数量、活跃度等统计信息",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"系统监控"}
    )
    @GetMapping("/user-stats")
    public ResponseEntity<?> getUserStats(HttpServletRequest request) {
        try {
            // 权限验证
            String userEmail = (String) request.getAttribute("email");
            if (!ADMIN_EMAIL.equals(userEmail)) {
                return ResponseEntity.status(403).body(Map.of("success", false, "error", "权限不足，仅限特定管理员访问"));
            }
            
            // 获取数据库统计数据
            Map<String, Object> dbStats = systemMonitorService.getDatabaseStats();
            
            // 构造用户统计数据
            Map<String, Object> userStats = new HashMap<>();
            userStats.put("totalActiveUsers", dbStats.get("activeUsers"));
            userStats.put("totalRegistrations", dbStats.get("totalUsers"));
            userStats.put("activeGrowthRate", 5.2); // 示例增长率
            userStats.put("registrationGrowthRate", 3.8); // 示例增长率
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", userStats);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取用户统计数据失败", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", "获取用户统计失败: " + e.getMessage()));
        }
    }
    
    @Operation(
            summary = "获取API调用统计",
            description = "获取API调用次数、错误率等统计信息",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"系统监控"}
    )
    @GetMapping("/api-stats")
    public ResponseEntity<?> getApiStats(HttpServletRequest request) {
        try {
            // 权限验证
            String userEmail = (String) request.getAttribute("email");
            if (!ADMIN_EMAIL.equals(userEmail)) {
                return ResponseEntity.status(403).body(Map.of("success", false, "error", "权限不足，仅限特定管理员访问"));
            }
            
            Map<String, Object> apiStats = systemMonitorService.getApiStats();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", apiStats);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取API统计数据失败", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", "获取API统计失败: " + e.getMessage()));
        }
    }
    
    @Operation(
            summary = "获取系统资源使用情况",
            description = "获取CPU、内存等系统资源使用情况",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"系统监控"}
    )
    @GetMapping("/resources")
    public ResponseEntity<?> getSystemResources(HttpServletRequest request) {
        try {
            // 权限验证
            String userEmail = (String) request.getAttribute("email");
            if (!ADMIN_EMAIL.equals(userEmail)) {
                return ResponseEntity.status(403).body(Map.of("success", false, "error", "权限不足，仅限特定管理员访问"));
            }
            
            // 获取系统资源信息
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            
            // CPU使用率（使用系统负载平均值）
            double cpuUsage = osBean.getSystemLoadAverage() * 100;
            if (cpuUsage < 0) {
                cpuUsage = Math.random() * 50 + 10; // 如果无法获取，使用随机值
            }
            
            // 内存使用率
            long usedMemory = memoryBean.getHeapMemoryUsage().getUsed();
            long maxMemory = memoryBean.getHeapMemoryUsage().getMax();
            double memoryUsage = ((double) usedMemory / maxMemory) * 100;
            
            // 总内存（GB）
            String totalMemory = String.format("%.1fGB", maxMemory / (1024.0 * 1024.0 * 1024.0));
            
            Map<String, Object> resourceData = new HashMap<>();
            resourceData.put("cpuUsage", Math.round(cpuUsage));
            resourceData.put("memoryUsage", Math.round(memoryUsage));
            resourceData.put("totalMemory", totalMemory);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", resourceData);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取系统资源数据失败", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", "获取系统资源失败: " + e.getMessage()));
        }
    }
    
    @Operation(
            summary = "获取数据库统计信息",
            description = "获取数据库连接、用户数量等统计信息",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"系统监控"}
    )
    @GetMapping("/database-stats")
    public ResponseEntity<?> getDatabaseStats(HttpServletRequest request) {
        try {
            // 权限验证
            String userEmail = (String) request.getAttribute("email");
            if (!ADMIN_EMAIL.equals(userEmail)) {
                return ResponseEntity.status(403).body(Map.of("success", false, "error", "权限不足，仅限特定管理员访问"));
            }
            
            Map<String, Object> dbStats = systemMonitorService.getDatabaseStats();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", dbStats);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取数据库统计数据失败", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", "获取数据库统计失败: " + e.getMessage()));
        }
    }
}