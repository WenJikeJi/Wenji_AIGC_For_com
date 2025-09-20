package com.wenji.server.service;

import com.wenji.server.model.UserOperationLog;
import com.wenji.server.repository.UserOperationLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LogService {
    
    @Autowired
    private UserOperationLogRepository userOperationLogRepository;
    
    // 获取操作日志列表（分页）
    public Page<UserOperationLog> getOperationLogs(Integer page, Integer size, Long userId, String operation, LocalDateTime startTime, LocalDateTime endTime) {
        Pageable pageable = PageRequest.of(page - 1, size);
        
        // 根据传入的参数组合查询条件
        if (userId != null && operation != null && startTime != null && endTime != null) {
            return userOperationLogRepository.findByUserIdAndOperationContainingAndOperationTimeBetweenOrderByOperationTimeDesc(
                    userId, operation, startTime, endTime, pageable);
        } else if (userId != null && operation != null) {
            return userOperationLogRepository.findByUserIdAndOperationContainingOrderByOperationTimeDesc(userId, operation, pageable);
        } else if (userId != null && startTime != null && endTime != null) {
            return userOperationLogRepository.findByUserIdAndOperationTimeBetweenOrderByOperationTimeDesc(userId, startTime, endTime, pageable);
        } else if (operation != null && startTime != null && endTime != null) {
            return userOperationLogRepository.findByOperationContainingAndOperationTimeBetweenOrderByOperationTimeDesc(
                    operation, startTime, endTime, pageable);
        } else if (userId != null) {
            return userOperationLogRepository.findByUserIdOrderByOperationTimeDesc(userId, pageable);
        } else if (operation != null) {
            return userOperationLogRepository.findByOperationContainingOrderByOperationTimeDesc(operation, pageable);
        } else if (startTime != null && endTime != null) {
            return userOperationLogRepository.findByOperationTimeBetweenOrderByOperationTimeDesc(startTime, endTime, pageable);
        } else {
            return userOperationLogRepository.findAllByOrderByOperationTimeDesc(pageable);
        }
    }
    
    // 根据用户ID获取操作日志（分页）
    public Page<UserOperationLog> getOperationLogsByUserId(Long userId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return userOperationLogRepository.findByUserIdOrderByOperationTimeDesc(userId, pageable);
    }
    
    // 根据操作类型获取操作日志（分页）
    public Page<UserOperationLog> getOperationLogsByOperationType(String operationType, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return userOperationLogRepository.findByOperationContainingOrderByOperationTimeDesc(operationType, pageable);
    }
    
    // 清理过期日志（90天前的日志）
    public void cleanExpiredLogs() {
        LocalDateTime ninetyDaysAgo = LocalDateTime.now().minusDays(90);
        int deletedCount = userOperationLogRepository.deleteByOperationTimeBefore(ninetyDaysAgo);
        
        // 记录清理日志操作（这里简化处理，实际应该记录到系统日志中）
        System.out.println("清理了" + deletedCount + "条过期日志（90天前的日志）");
    }
    
    // 记录操作日志
    public void recordOperationLog(Long userId, String operation, String ip, String address, String details, Integer encryptionStatus) {
        UserOperationLog log = new UserOperationLog();
        log.setUserId(userId);
        log.setOperation(operation);
        log.setOperationIp(ip);
        log.setOperationAddress(address);
        log.setDetails(details);
        log.setEncryptionStatus(encryptionStatus);
        
        userOperationLogRepository.save(log);
    }
    
    // 获取主账号及其子账号的所有操作日志（主账号权限）
    public Page<UserOperationLog> getMainAccountAndSubAccountsLogs(Long mainAccountId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return userOperationLogRepository.findByUserIdOrUserParentIdOrderByOperationTimeDesc(mainAccountId, mainAccountId, pageable);
    }
}