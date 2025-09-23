package com.wenji.server.repository;

import com.wenji.server.model.SocialPostTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SocialPostTaskRepository extends JpaRepository<SocialPostTask, Long> {
    
    /**
     * 查找待执行的任务（定时时间已到）
     */
    @Query("SELECT t FROM SocialPostTask t WHERE t.taskStatus = 0 AND t.scheduleTime <= :currentTime")
    List<SocialPostTask> findPendingTasks(@Param("currentTime") LocalDateTime currentTime);
    
    /**
     * 查找需要重试的失败任务
     */
    @Query("SELECT t FROM SocialPostTask t WHERE t.taskStatus = 2 AND t.retryCount < t.maxRetryCount")
    List<SocialPostTask> findRetryableTasks();
    
    /**
     * 根据任务状态分页查询
     */
    Page<SocialPostTask> findByTaskStatusOrderByCreatedAtDesc(Integer taskStatus, Pageable pageable);
    
    /**
     * 查询所有任务（按创建时间倒序）
     */
    Page<SocialPostTask> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * 根据操作人ID查询任务
     */
    Page<SocialPostTask> findByOperatorIdOrderByCreatedAtDesc(Long operatorId, Pageable pageable);
    
    /**
     * 根据任务编号查找任务
     */
    SocialPostTask findByTaskNo(String taskNo);
}