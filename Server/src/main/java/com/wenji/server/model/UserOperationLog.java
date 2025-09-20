package com.wenji.server.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_operation_log", 
       indexes = {
           @Index(name = "idx_user_id", columnList = "user_id"),
           @Index(name = "idx_operation_time", columnList = "operation_time")
       }
)
public class UserOperationLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(nullable = false, length = 200)
    private String operation;
    
    @CreationTimestamp
    @Column(name = "operation_time", nullable = false, updatable = false)
    private LocalDateTime operationTime;
    
    @Column(name = "operation_ip", length = 50)
    private String operationIp;
    
    @Column(name = "operation_address", length = 100)
    private String operationAddress;
    
    @Column(columnDefinition = "TEXT")
    private String details;
    
    @Column(name = "encryption_status", nullable = false)
    private Integer encryptionStatus; // 1 = 加密传输，0 = 未加密
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public void setOperation(String operation) {
        this.operation = operation;
    }
    
    public LocalDateTime getOperationTime() {
        return operationTime;
    }
    
    public String getOperationIp() {
        return operationIp;
    }
    
    public void setOperationIp(String operationIp) {
        this.operationIp = operationIp;
    }
    
    public String getOperationAddress() {
        return operationAddress;
    }
    
    public void setOperationAddress(String operationAddress) {
        this.operationAddress = operationAddress;
    }
    
    public String getDetails() {
        return details;
    }
    
    public void setDetails(String details) {
        this.details = details;
    }
    
    public Integer getEncryptionStatus() {
        return encryptionStatus;
    }
    
    public void setEncryptionStatus(Integer encryptionStatus) {
        this.encryptionStatus = encryptionStatus;
    }
}