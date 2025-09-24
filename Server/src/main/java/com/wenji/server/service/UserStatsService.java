package com.wenji.server.service;

import com.wenji.server.model.UserActivityStats;
import com.wenji.server.model.UserRegistrationStats;
import com.wenji.server.repository.UserActivityStatsRepository;
import com.wenji.server.repository.UserRegistrationStatsRepository;
import com.wenji.server.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserStatsService {
    
    @Autowired
    private UserActivityStatsRepository activityStatsRepository;
    
    @Autowired
    private UserRegistrationStatsRepository registrationStatsRepository;
    
    @Autowired
    private UserAccountRepository userAccountRepository;
    
    // 用于记录活跃用户的临时存储（内存中）
    private final Set<String> hourlyActiveUsers = ConcurrentHashMap.newKeySet();
    private final Set<String> dailyActiveUsers = ConcurrentHashMap.newKeySet();
    private final Set<String> weeklyActiveUsers = ConcurrentHashMap.newKeySet();
    private final Set<String> monthlyActiveUsers = ConcurrentHashMap.newKeySet();
    
    /**
     * 记录用户活跃度（用户登录时调用）
     */
    public void recordUserActivity(String userEmail) {
        if (userEmail != null && !userEmail.isEmpty()) {
            hourlyActiveUsers.add(userEmail);
            dailyActiveUsers.add(userEmail);
            weeklyActiveUsers.add(userEmail);
            monthlyActiveUsers.add(userEmail);
        }
    }
    
    /**
     * 每小时统计活跃用户数（每小时的第1分钟执行）
     */
    @Scheduled(cron = "0 1 * * * ?")
    @Transactional
    public void updateHourlyActivityStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime statTime = now.truncatedTo(ChronoUnit.HOURS);
        
        int activeCount = hourlyActiveUsers.size();
        updateActivityStats("hour", statTime, activeCount);
        
        // 清空小时活跃用户记录
        hourlyActiveUsers.clear();
    }
    
    /**
     * 每日统计活跃用户数（每天凌晨0:05执行）
     */
    @Scheduled(cron = "0 5 0 * * ?")
    @Transactional
    public void updateDailyActivityStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime statTime = now.truncatedTo(ChronoUnit.DAYS);
        
        int activeCount = dailyActiveUsers.size();
        updateActivityStats("day", statTime, activeCount);
        
        // 清空日活跃用户记录
        dailyActiveUsers.clear();
    }
    
    /**
     * 每周统计活跃用户数（每周一凌晨0:10执行）
     */
    @Scheduled(cron = "0 10 0 * * MON")
    @Transactional
    public void updateWeeklyActivityStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime statTime = now.truncatedTo(ChronoUnit.DAYS);
        
        int activeCount = weeklyActiveUsers.size();
        updateActivityStats("week", statTime, activeCount);
        
        // 清空周活跃用户记录
        weeklyActiveUsers.clear();
    }
    
    /**
     * 每月统计活跃用户数（每月1号凌晨0:15执行）
     */
    @Scheduled(cron = "0 15 0 1 * ?")
    @Transactional
    public void updateMonthlyActivityStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime statTime = now.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
        
        int activeCount = monthlyActiveUsers.size();
        updateActivityStats("month", statTime, activeCount);
        
        // 清空月活跃用户记录
        monthlyActiveUsers.clear();
    }
    
    /**
     * 更新活跃度统计数据
     */
    private void updateActivityStats(String timeDimension, LocalDateTime statTime, int activeCount) {
        Optional<UserActivityStats> existingStats = activityStatsRepository
                .findByTimeDimensionAndStatTime(timeDimension, statTime);
        
        if (existingStats.isPresent()) {
            UserActivityStats stats = existingStats.get();
            stats.setActiveCount(activeCount);
            stats.setUpdatedTime(LocalDateTime.now());
            activityStatsRepository.save(stats);
        } else {
            UserActivityStats newStats = new UserActivityStats(timeDimension, statTime, activeCount);
            activityStatsRepository.save(newStats);
        }
    }
    
    /**
     * 每小时统计注册用户数（每小时的第2分钟执行）
     */
    @Scheduled(cron = "0 2 * * * ?")
    @Transactional
    public void updateHourlyRegistrationStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime statTime = now.truncatedTo(ChronoUnit.HOURS);
        LocalDateTime startTime = statTime;
        LocalDateTime endTime = statTime.plusHours(1);
        
        long registrationCount = userAccountRepository.countByCreatedTimeBetween(startTime, endTime);
        updateRegistrationStats("hour", statTime, (int) registrationCount);
    }
    
    /**
     * 每日统计注册用户数（每天凌晨0:06执行）
     */
    @Scheduled(cron = "0 6 0 * * ?")
    @Transactional
    public void updateDailyRegistrationStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime statTime = now.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime startTime = statTime;
        LocalDateTime endTime = statTime.plusDays(1);
        
        long registrationCount = userAccountRepository.countByCreatedTimeBetween(startTime, endTime);
        updateRegistrationStats("day", statTime, (int) registrationCount);
    }
    
    /**
     * 每周统计注册用户数（每周一凌晨0:11执行）
     */
    @Scheduled(cron = "0 11 0 * * MON")
    @Transactional
    public void updateWeeklyRegistrationStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime statTime = now.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime startTime = statTime.minusDays(6); // 本周开始
        LocalDateTime endTime = statTime.plusDays(1);
        
        long registrationCount = userAccountRepository.countByCreatedTimeBetween(startTime, endTime);
        updateRegistrationStats("week", statTime, (int) registrationCount);
    }
    
    /**
     * 每月统计注册用户数（每月1号凌晨0:16执行）
     */
    @Scheduled(cron = "0 16 0 1 * ?")
    @Transactional
    public void updateMonthlyRegistrationStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime statTime = now.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
        LocalDateTime startTime = statTime;
        LocalDateTime endTime = statTime.plusMonths(1);
        
        long registrationCount = userAccountRepository.countByCreatedTimeBetween(startTime, endTime);
        updateRegistrationStats("month", statTime, (int) registrationCount);
    }
    
    /**
     * 更新注册统计数据
     */
    private void updateRegistrationStats(String timeDimension, LocalDateTime statTime, int registrationCount) {
        Optional<UserRegistrationStats> existingStats = registrationStatsRepository
                .findByTimeDimensionAndStatTime(timeDimension, statTime);
        
        if (existingStats.isPresent()) {
            UserRegistrationStats stats = existingStats.get();
            stats.setRegistrationCount(registrationCount);
            stats.setUpdatedTime(LocalDateTime.now());
            registrationStatsRepository.save(stats);
        } else {
            UserRegistrationStats newStats = new UserRegistrationStats(timeDimension, statTime, registrationCount);
            registrationStatsRepository.save(newStats);
        }
    }
    
    /**
     * 获取活跃度统计数据
     */
    public List<UserActivityStats> getActivityStats(String timeDimension, LocalDateTime startTime, LocalDateTime endTime) {
        return activityStatsRepository.findByTimeDimensionAndStatTimeBetween(timeDimension, startTime, endTime);
    }
    
    /**
     * 获取注册统计数据
     */
    public List<UserRegistrationStats> getRegistrationStats(String timeDimension, LocalDateTime startTime, LocalDateTime endTime) {
        return registrationStatsRepository.findByTimeDimensionAndStatTimeBetween(timeDimension, startTime, endTime);
    }
    
    /**
     * 获取最近N条活跃度统计数据
     */
    public List<UserActivityStats> getRecentActivityStats(String timeDimension, int limit) {
        return activityStatsRepository.findTopByTimeDimensionOrderByStatTimeDesc(timeDimension, limit);
    }
    
    /**
     * 获取最近N条注册统计数据
     */
    public List<UserRegistrationStats> getRecentRegistrationStats(String timeDimension, int limit) {
        return registrationStatsRepository.findTopByTimeDimensionOrderByStatTimeDesc(timeDimension, limit);
    }
    
    /**
     * 数据清理任务（每天凌晨2点执行，清理90天前的小时数据）
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void cleanupOldData() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(90);
        
        // 清理90天前的小时级数据
        activityStatsRepository.deleteByStatTimeBefore(cutoffTime);
        registrationStatsRepository.deleteByStatTimeBefore(cutoffTime);
    }
}