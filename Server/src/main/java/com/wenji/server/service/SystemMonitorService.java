package com.wenji.server.service;

import com.wenji.server.model.UserAccount;
import com.wenji.server.repository.UserAccountRepository;
import com.wenji.server.repository.UserOperationLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class SystemMonitorService {
    
    private static final Logger logger = LoggerFactory.getLogger(SystemMonitorService.class);
    
    @Autowired
    private UserAccountRepository userAccountRepository;
    
    @Autowired
    private UserOperationLogRepository userOperationLogRepository;
    
    // API调用统计
    private final AtomicLong totalApiCalls = new AtomicLong(0);
    private final AtomicLong totalApiErrors = new AtomicLong(0);
    private final ConcurrentHashMap<Long, AtomicLong> apiCallsPerSecond = new ConcurrentHashMap<>();
    
    // 按API路径统计调用情况
    private final ConcurrentHashMap<String, ApiEndpointStats> apiEndpointStatsMap = new ConcurrentHashMap<>();
    
    // 系统启动时间
    private final long startTime = System.currentTimeMillis();
    
    // API端点统计内部类
    private static class ApiEndpointStats {
        private final AtomicLong totalCalls = new AtomicLong(0);
        private final AtomicLong errorCalls = new AtomicLong(0);
        private final ConcurrentHashMap<Long, AtomicLong> recentCalls = new ConcurrentHashMap<>();
        private final AtomicLong totalResponseTime = new AtomicLong(0);
        private final AtomicLong maxResponseTime = new AtomicLong(0);
        private String lastStatus = "UP";
        
        // Getters and setters
        public long getTotalCalls() { return totalCalls.get(); }
        public long getErrorCalls() { return errorCalls.get(); }
        public long getSuccessCalls() { return totalCalls.get() - errorCalls.get(); }
        public double getErrorRate() { 
            long total = totalCalls.get(); 
            return total > 0 ? (errorCalls.get() * 100.0 / total) : 0; 
        }
        public long getTotalResponseTime() { return totalResponseTime.get(); }
        public double getAvgResponseTime() { 
            long total = totalCalls.get(); 
            return total > 0 ? (totalResponseTime.get() * 1.0 / total) : 0; 
        }
        public long getMaxResponseTime() { return maxResponseTime.get(); }
        public String getLastStatus() { return lastStatus; }
        public void setLastStatus(String status) { this.lastStatus = status; }
        
        // 记录调用
        public void recordCall(long responseTime) {
            totalCalls.incrementAndGet();
            totalResponseTime.addAndGet(responseTime);
            
            // 更新最大响应时间
            long currentMax;
            do {
                currentMax = maxResponseTime.get();
                if (responseTime <= currentMax) break;
            } while (!maxResponseTime.compareAndSet(currentMax, responseTime));
            
            // 记录最近的调用（用于计算最近时间段的调用频率）
            long currentSecond = System.currentTimeMillis() / 1000;
            recentCalls.computeIfAbsent(currentSecond, k -> new AtomicLong(0)).incrementAndGet();
            
            // 清理超过60秒的数据
            long cutoffTime = currentSecond - 60;
            recentCalls.entrySet().removeIf(entry -> entry.getKey() < cutoffTime);
        }
        
        // 记录错误调用
        public void recordError() {
            errorCalls.incrementAndGet();
            lastStatus = "DOWN"; // 遇到错误时标记为DOWN状态
        }
        
        // 获取最近n秒的调用次数
        public long getRecentCalls(int seconds) {
            long currentSecond = System.currentTimeMillis() / 1000;
            long total = 0;
            
            for (int i = 0; i < seconds; i++) {
                AtomicLong calls = recentCalls.get(currentSecond - i);
                if (calls != null) {
                    total += calls.get();
                }
            }
            
            return total;
        }
    }
    
    /**
     * 获取API端点统计信息
     */
    public Map<String, Object> getApiEndpointsStats() {
        Map<String, Object> endpointsStats = new HashMap<>();
        
        try {
            // 从实际记录中构建端点数据
            List<Map<String, Object>> endpoints = new ArrayList<>();
            
            // 获取并处理所有API端点的统计数据
            for (Map.Entry<String, ApiEndpointStats> entry : apiEndpointStatsMap.entrySet()) {
                String key = entry.getKey();
                ApiEndpointStats stats = entry.getValue();
                
                // 解析key，获取HTTP方法和路径
                String[] parts = key.split(" ", 2);
                String method = parts[0];
                String path = parts.length > 1 ? parts[1] : "unknown";
                
                // 构建端点统计数据
                Map<String, Object> endpoint = new HashMap<>();
                endpoint.put("path", path);
                endpoint.put("method", method);
                endpoint.put("status", stats.getLastStatus());
                endpoint.put("totalCalls", stats.getTotalCalls());
                endpoint.put("successCount", stats.getSuccessCalls());
                endpoint.put("errorCount", stats.getErrorCalls());
                endpoint.put("errorRate", String.format("%.1f%%", stats.getErrorRate()));
                endpoint.put("avgResponseTime", Math.round(stats.getAvgResponseTime()));
                endpoint.put("maxResponseTime", stats.getMaxResponseTime());
                endpoint.put("recentCalls", stats.getRecentCalls(60)); // 最近1分钟的调用次数
                
                endpoints.add(endpoint);
            }
            
            // 计算总体统计数据
            long totalCalls = totalApiCalls.get();
            long totalErrors = totalApiErrors.get();
            int healthyEndpoints = (int) endpoints.stream()
                    .filter(e -> "UP".equals(e.get("status")))
                    .count();
            
            endpointsStats.put("endpoints", endpoints);
            endpointsStats.put("totalEndpoints", endpoints.size());
            endpointsStats.put("healthyEndpoints", healthyEndpoints);
            endpointsStats.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            endpointsStats.put("totalCalls", totalCalls);
            endpointsStats.put("totalErrors", totalErrors);
            
        } catch (Exception e) {
            logger.error("获取API端点统计信息失败", e);
            endpointsStats.put("error", "获取API端点统计信息失败: " + e.getMessage());
        }
        
        return endpointsStats;
    }
    
    /**
     * 记录API调用（全局计数）
     */
    public void recordApiCall() {
        totalApiCalls.incrementAndGet();
        long currentSecond = System.currentTimeMillis() / 1000;
        apiCallsPerSecond.computeIfAbsent(currentSecond, k -> new AtomicLong(0)).incrementAndGet();
        
        // 清理超过60秒的数据
        long cutoffTime = currentSecond - 60;
        apiCallsPerSecond.entrySet().removeIf(entry -> entry.getKey() < cutoffTime);
    }
    
    /**
     * 记录API调用（按路径统计）
     * @param path API路径
     * @param method HTTP方法
     * @param responseTime 响应时间(ms)
     */
    public void recordApiCall(String path, String method, long responseTime) {
        // 记录全局调用
        recordApiCall();
        
        // 按路径记录详细信息
        String key = method + " " + path;
        ApiEndpointStats stats = apiEndpointStatsMap.computeIfAbsent(key, k -> new ApiEndpointStats());
        stats.recordCall(responseTime);
    }
    
    /**
     * 记录API错误（全局计数）
     */
    public void recordApiError() {
        totalApiErrors.incrementAndGet();
    }
    
    /**
     * 记录API错误（按路径统计）
     * @param path API路径
     * @param method HTTP方法
     */
    public void recordApiError(String path, String method) {
        // 记录全局错误
        recordApiError();
        
        // 按路径记录错误
        String key = method + " " + path;
        ApiEndpointStats stats = apiEndpointStatsMap.computeIfAbsent(key, k -> new ApiEndpointStats());
        stats.recordError();
    }
    
    /**
     * 获取监控仪表板数据
     */
    public Map<String, Object> getMonitorDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        
        // 系统状态
        dashboard.put("systemStatus", getSystemHealth());
        
        // API统计
        dashboard.put("apiStats", getApiStats());
        
        // 数据库统计
        dashboard.put("databaseStats", getDatabaseStats());
        
        // 当前时间
        dashboard.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        return dashboard;
    }
    
    /**
     * 获取API统计信息
     */
    public Map<String, Object> getApiStats() {
        Map<String, Object> stats = new HashMap<>();
        
        long currentSecond = System.currentTimeMillis() / 1000;
        
        // 最近10秒的调用次数
        long last10SecondsCalls = 0;
        for (int i = 0; i < 10; i++) {
            AtomicLong calls = apiCallsPerSecond.get(currentSecond - i);
            if (calls != null) {
                last10SecondsCalls += calls.get();
            }
        }
        
        // 最近1分钟的调用次数
        long lastMinuteCalls = 0;
        for (int i = 0; i < 60; i++) {
            AtomicLong calls = apiCallsPerSecond.get(currentSecond - i);
            if (calls != null) {
                lastMinuteCalls += calls.get();
            }
        }
        
        long totalCalls = totalApiCalls.get();
        long totalErrors = totalApiErrors.get();
        
        stats.put("totalCalls", totalCalls);
        stats.put("totalErrors", totalErrors);
        stats.put("last10SecondsCalls", last10SecondsCalls);
        stats.put("lastMinuteCalls", lastMinuteCalls);
        
        // 计算成功率
        if (totalCalls > 0) {
            double successRate = ((double) (totalCalls - totalErrors) / totalCalls) * 100;
            stats.put("successRate", String.format("%.1f%%", successRate));
            stats.put("errorRate", String.format("%.1f%%", ((double) totalErrors / totalCalls) * 100));
        } else {
            stats.put("successRate", "100.0%");
            stats.put("errorRate", "0.0%");
        }
        
        return stats;
    }
    
    /**
     * 获取数据库统计信息
     */
    public Map<String, Object> getDatabaseStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // 主账号数量 (role = 0)
            List<UserAccount> mainAccounts = userAccountRepository.findByRole(0);
            long mainAccountCount = mainAccounts.size();
            
            // 子账号数量 (role != 0 且 parentId != null)
            long subAccountCount = 0;
            List<UserAccount> allUsers = userAccountRepository.findAll();
            for (UserAccount user : allUsers) {
                if (user.getRole() != 0 && user.getParentId() != null) {
                    subAccountCount++;
                }
            }
            
            // 总用户数
            long totalUsers = userAccountRepository.count();
            
            // 活跃用户数 (最近30天有登录)
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            long activeUsers = 0;
            for (UserAccount user : allUsers) {
                if (user.getLastLoginTime() != null && user.getLastLoginTime().isAfter(thirtyDaysAgo)) {
                    activeUsers++;
                }
            }
            
            // 今日注册用户数
            LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
            long todayRegistrations = 0;
            for (UserAccount user : allUsers) {
                if (user.getCreatedTime() != null && user.getCreatedTime().isAfter(todayStart)) {
                    todayRegistrations++;
                }
            }
            
            // 近期操作日志数量 (最近24小时)
            LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
            long recentOperations = userOperationLogRepository.count(); // 简化实现，返回总数
            
            stats.put("mainAccountCount", mainAccountCount);
            stats.put("subAccountCount", subAccountCount);
            stats.put("totalUsers", totalUsers);
            stats.put("activeUsers", activeUsers);
            stats.put("inactiveUsers", totalUsers - activeUsers);
            stats.put("todayRegistrations", todayRegistrations);
            stats.put("recentOperations", recentOperations);
            
            // 计算用户活跃率
            if (totalUsers > 0) {
                double activeRate = ((double) activeUsers / totalUsers) * 100;
                stats.put("activeRate", String.format("%.1f%%", activeRate));
            } else {
                stats.put("activeRate", "0.0%");
            }
            
        } catch (Exception e) {
            logger.error("获取数据库统计信息失败", e);
            stats.put("error", "获取数据库统计信息失败: " + e.getMessage());
        }
        
        return stats;
    }
    
    /**
     * 获取系统健康状态
     */
    public Map<String, Object> getSystemHealth() {
        Map<String, Object> health = new HashMap<>();
        
        try {
            // 系统运行时间
            long uptime = System.currentTimeMillis() - startTime;
            health.put("uptime", formatUptime(uptime));
            health.put("startTime", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .format(LocalDateTime.now().minusSeconds(uptime / 1000)));
            
            // 内存使用情况
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            long usedMemory = memoryBean.getHeapMemoryUsage().getUsed();
            long maxMemory = memoryBean.getHeapMemoryUsage().getMax();
            long totalMemory = memoryBean.getHeapMemoryUsage().getCommitted();
            
            Map<String, Object> memoryInfo = new HashMap<>();
            memoryInfo.put("used", formatBytes(usedMemory));
            memoryInfo.put("total", formatBytes(totalMemory));
            memoryInfo.put("max", formatBytes(maxMemory));
            memoryInfo.put("percentage", String.format("%.1f%%", ((double) usedMemory / maxMemory) * 100));
            health.put("memoryUsage", memoryInfo);
            
            // CPU使用情况
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
                com.sun.management.OperatingSystemMXBean sunOsBean = (com.sun.management.OperatingSystemMXBean) osBean;
                double cpuUsage = sunOsBean.getProcessCpuLoad() * 100;
                health.put("cpuUsage", String.format("%.1f%%", cpuUsage));
            } else {
                health.put("cpuUsage", "N/A");
            }
            
            // 系统信息
            health.put("osName", System.getProperty("os.name"));
            health.put("osVersion", System.getProperty("os.version"));
            health.put("javaVersion", System.getProperty("java.version"));
            health.put("availableProcessors", Runtime.getRuntime().availableProcessors());
            
            // 健康状态评估
            double memoryUsagePercent = ((double) usedMemory / maxMemory) * 100;
            String status;
            if (memoryUsagePercent > 90) {
                status = "critical";
            } else if (memoryUsagePercent > 75) {
                status = "warning";
            } else {
                status = "healthy";
            }
            health.put("status", status);
            
        } catch (Exception e) {
            logger.error("获取系统健康状态失败", e);
            health.put("status", "error");
            health.put("error", "获取系统健康状态失败: " + e.getMessage());
        }
        
        return health;
    }
    
    /**
     * 格式化运行时间
     */
    private String formatUptime(long uptimeMs) {
        long seconds = uptimeMs / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) {
            return String.format("%d天%d小时%d分钟", days, hours % 24, minutes % 60);
        } else if (hours > 0) {
            return String.format("%d小时%d分钟", hours, minutes % 60);
        } else if (minutes > 0) {
            return String.format("%d分钟%d秒", minutes, seconds % 60);
        } else {
            return String.format("%d秒", seconds);
        }
    }
    
    /**
     * 格式化字节数
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", (double) bytes / 1024);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", (double) bytes / (1024 * 1024));
        } else {
            return String.format("%.1f GB", (double) bytes / (1024 * 1024 * 1024));
        }
    }
}