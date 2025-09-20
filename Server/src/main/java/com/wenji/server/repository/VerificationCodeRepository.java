package com.wenji.server.repository;

import com.wenji.server.model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    
    // 查找未使用且未过期的验证码
    Optional<VerificationCode> findByEmailAndCodeAndTypeAndUsedAndExpiredTimeAfter(
            String email, String code, Integer type, Integer used, LocalDateTime now);
    
    // 标记验证码为已使用
    @Modifying
    @Transactional
    @Query("UPDATE VerificationCode v SET v.used = 1 WHERE v.id = :id")
    int markCodeAsUsed(@Param("id") Long id);
    
    // 统计指定IP在指定时间内的请求次数
    @Query("SELECT COUNT(v) FROM VerificationCode v WHERE v.requestIp = :ip AND v.createdTime >= :startTime")
    int countByRequestIpAndCreatedTimeAfter(@Param("ip") String ip, @Param("startTime") LocalDateTime startTime);
    
    // 清理过期验证码
    @Modifying
    @Transactional
    @Query("DELETE FROM VerificationCode v WHERE v.expiredTime < :now")
    int deleteExpiredCodes(@Param("now") LocalDateTime now);
}