package com.wenji.server.repository;

import com.wenji.server.model.UserOperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface UserOperationLogRepository extends JpaRepository<UserOperationLog, Long> {
    
    // 分页查询用户操作日志
    Page<UserOperationLog> findByUserId(Long userId, Pageable pageable);
    
    // 按用户ID查询操作日志并按时间倒序排序
    Page<UserOperationLog> findByUserIdOrderByOperationTimeDesc(Long userId, Pageable pageable);
    
    // 根据操作类型和描述模糊查询
    Page<UserOperationLog> findByOperationContainingOrDetailsContaining(String operationKeyword, String detailsKeyword, Pageable pageable);
    
    // 根据用户ID和操作类型查询
    Page<UserOperationLog> findByUserIdAndOperationContaining(Long userId, String operationKeyword, Pageable pageable);
    
    // 清理过期日志（超过90天）
    @Modifying
    @Transactional
    @Query("DELETE FROM UserOperationLog u WHERE u.operationTime < :expiryDate")
    int deleteExpiredLogs(@Param("expiryDate") LocalDateTime expiryDate);
    
    // 按时间范围删除日志
    @Modifying
    @Transactional
    int deleteByOperationTimeBefore(LocalDateTime dateTime);
    
    // 查询主账号及其子账号的日志
    @Query("SELECT u FROM UserOperationLog u WHERE u.userId = :userId OR u.userId IN (SELECT a.id FROM UserAccount a WHERE a.parentId = :parentId)")
    Page<UserOperationLog> findByUserIdOrUserParentIdOrderByOperationTimeDesc(@Param("userId") Long userId, @Param("parentId") Long parentId, Pageable pageable);
    
    // 按用户ID、操作类型和时间范围查询
    Page<UserOperationLog> findByUserIdAndOperationContainingAndOperationTimeBetweenOrderByOperationTimeDesc(Long userId, String operation, LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    // 按用户ID和操作类型查询（带排序）
    Page<UserOperationLog> findByUserIdAndOperationContainingOrderByOperationTimeDesc(Long userId, String operation, Pageable pageable);
    
    // 按用户ID和时间范围查询（带排序）
    Page<UserOperationLog> findByUserIdAndOperationTimeBetweenOrderByOperationTimeDesc(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    // 按操作类型和时间范围查询（带排序）
    Page<UserOperationLog> findByOperationContainingAndOperationTimeBetweenOrderByOperationTimeDesc(String operation, LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    // 按操作类型查询（带排序）
    Page<UserOperationLog> findByOperationContainingOrderByOperationTimeDesc(String operation, Pageable pageable);
    
    // 按时间范围查询（带排序）
    Page<UserOperationLog> findByOperationTimeBetweenOrderByOperationTimeDesc(LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    // 查询所有日志（带排序）
    Page<UserOperationLog> findAllByOrderByOperationTimeDesc(Pageable pageable);
}