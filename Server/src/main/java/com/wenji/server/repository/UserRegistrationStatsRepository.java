package com.wenji.server.repository;

import com.wenji.server.model.UserRegistrationStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRegistrationStatsRepository extends JpaRepository<UserRegistrationStats, Long> {
    
    /**
     * 根据时间维度和统计时间查找记录
     */
    Optional<UserRegistrationStats> findByTimeDimensionAndStatTime(String timeDimension, LocalDateTime statTime);
    
    /**
     * 根据时间维度查询指定时间范围内的统计数据
     */
    @Query("SELECT u FROM UserRegistrationStats u WHERE u.timeDimension = :timeDimension " +
           "AND u.statTime >= :startTime AND u.statTime <= :endTime ORDER BY u.statTime ASC")
    List<UserRegistrationStats> findByTimeDimensionAndStatTimeBetween(
            @Param("timeDimension") String timeDimension,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    /**
     * 根据时间维度查询最近N条记录
     */
    @Query("SELECT u FROM UserRegistrationStats u WHERE u.timeDimension = :timeDimension " +
           "ORDER BY u.statTime DESC LIMIT :limit")
    List<UserRegistrationStats> findTopByTimeDimensionOrderByStatTimeDesc(
            @Param("timeDimension") String timeDimension,
            @Param("limit") int limit);
    
    /**
     * 根据时间维度查询最新的统计记录
     */
    Optional<UserRegistrationStats> findTopByTimeDimensionOrderByStatTimeDesc(String timeDimension);
    
    /**
     * 删除指定时间之前的旧数据（用于数据清理）
     */
    @Query("DELETE FROM UserRegistrationStats u WHERE u.statTime < :cutoffTime")
    void deleteByStatTimeBefore(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    /**
     * 获取指定时间维度的总注册用户数
     */
    @Query("SELECT SUM(u.registrationCount) FROM UserRegistrationStats u WHERE u.timeDimension = :timeDimension " +
           "AND u.statTime >= :startTime AND u.statTime <= :endTime")
    Long getTotalRegistrationCountByTimeDimensionAndTimeRange(
            @Param("timeDimension") String timeDimension,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}