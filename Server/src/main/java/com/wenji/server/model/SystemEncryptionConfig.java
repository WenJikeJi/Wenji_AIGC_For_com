package com.wenji.server.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "system_encryption_config", 
       indexes = {
           @Index(name = "idx_status", columnList = "status")
       }
)
public class SystemEncryptionConfig {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "rsa_private_key", columnDefinition = "TEXT", nullable = false)
    private String rsaPrivateKey;
    
    @Column(name = "rsa_public_key", columnDefinition = "TEXT", nullable = false)
    private String rsaPublicKey;
    
    @Column(name = "key_version", nullable = false, length = 20)
    private String keyVersion;
    
    @Column(name = "last_rotate_time", nullable = false)
    private LocalDateTime lastRotateTime;
    
    @Column(nullable = false)
    private Integer status; // 1 = 启用，0 = 禁用
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getRsaPrivateKey() {
        return rsaPrivateKey;
    }
    
    public void setRsaPrivateKey(String rsaPrivateKey) {
        this.rsaPrivateKey = rsaPrivateKey;
    }
    
    public String getRsaPublicKey() {
        return rsaPublicKey;
    }
    
    public void setRsaPublicKey(String rsaPublicKey) {
        this.rsaPublicKey = rsaPublicKey;
    }
    
    public String getKeyVersion() {
        return keyVersion;
    }
    
    public void setKeyVersion(String keyVersion) {
        this.keyVersion = keyVersion;
    }
    
    public LocalDateTime getLastRotateTime() {
        return lastRotateTime;
    }
    
    public void setLastRotateTime(LocalDateTime lastRotateTime) {
        this.lastRotateTime = lastRotateTime;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
}