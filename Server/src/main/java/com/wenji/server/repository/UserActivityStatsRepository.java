package com.wenji.server.repository;

import com.wenji.server.model.UserActivityStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserActivityStatsRepository extends JpaRepository<UserActivityStats, Long> {
    
    /**
     * 根据时间维度和统计时间查找记录
     */
    Optional<UserActivityStats> findByTimeDimensionAndStatTime(String timeDimension, LocalDateTime statTime);
    
    /**
     * 根据时间维度查询指定时间范围内的统计数据
     */
    @Query("SELECT u FROM UserActivityStats u WHERE u.timeDimension = :timeDimension " +
           "AND u.statTime >= :startTime AND u.statTime <= :endTime ORDER BY u.statTime ASC")
    List<UserActivityStats> findByTimeDimensionAndStatTimeBetween(
            @Param("timeDimension") String timeDimension,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    /**
     * 根据时间维度查询最近N条记录
     */
    @Query("SELECT u FROM UserActivityStats u WHERE u.timeDimension = :timeDimension " +
           "ORDER BY u.statTime DESC LIMIT :limit")
    List<UserActivityStats> findTopByTimeDimensionOrderByStatTimeDesc(
            @Param("timeDimension") String timeDimension,
            @Param("limit") int limit);
    
    /**
     * 根据时间维度查询最新的统计记录
     */
    Optional<UserActivityStats> findTopByTimeDimensionOrderByStatTimeDesc(String timeDimension);
    
    /**
     * 删除指定时间之前的旧数据（用于数据清理）
     */
    @Query("DELETE FROM UserActivityStats u WHERE u.statTime < :cutoffTime")
    void deleteByStatTimeBefore(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    /**
     * 获取指定时间维度的总活跃用户数
     */
    @Query("SELECT SUM(u.activeCount) FROM UserActivityStats u WHERE u.timeDimension = :timeDimension " +
           "AND u.statTime >= :startTime AND u.statTime <= :endTime")
    Long getTotalActiveCountByTimeDimensionAndTimeRange(
            @Param("timeDimension") String timeDimension,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}