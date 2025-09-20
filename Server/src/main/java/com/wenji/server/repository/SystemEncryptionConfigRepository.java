package com.wenji.server.repository;

import com.wenji.server.model.SystemEncryptionConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemEncryptionConfigRepository extends JpaRepository<SystemEncryptionConfig, Long> {
    
    // 查找启用状态的加密配置
    Optional<SystemEncryptionConfig> findByStatus(Integer status);
    
    // 根据密钥版本查找配置
    Optional<SystemEncryptionConfig> findByKeyVersion(String keyVersion);
}