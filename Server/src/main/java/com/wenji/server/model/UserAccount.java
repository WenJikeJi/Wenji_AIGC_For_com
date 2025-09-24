package com.wenji.server.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_account", 
       indexes = {
           @Index(name = "idx_parent_id", columnList = "parent_id"),
           @Index(name = "idx_last_login_time", columnList = "last_login_time")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_email_role", columnNames = {"email", "role"}),
           @UniqueConstraint(name = "uk_account", columnNames = "account")
       }
)
public class UserAccount {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 50)
    private String username;
    
    @Column(nullable = false, length = 50, unique = true)
    private String account;
    
    @Column(nullable = false, length = 100)
    private String email;
    
    @Column(nullable = false, length = 100)
    private String password;
    
    @Column(nullable = false)
    private Integer role; // 0 = 主账号，1 = 子账号
    
    @Column(name = "parent_id")
    private Long parentId;
    
    @Column(name = "email_verified", nullable = false)
    private Integer emailVerified; // 0 = 未验证，1 = 已验证
    
    @CreationTimestamp
    @Column(name = "created_time", nullable = false, updatable = false)
    private LocalDateTime createdTime;
    
    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;
    
    @Column(name = "last_login_ip", length = 50)
    private String lastLoginIp;
    
    @Column(name = "last_login_address", length = 100)
    private String lastLoginAddress;
    
    @Column(nullable = false)
    private Integer status; // 1 = 正常，0 = 禁用
    
    // 新增字段支持子账户状态管理
    @Column(name = "account_status", length = 20)
    private String accountStatus; // NORMAL, STOPPED, INVITING, INVALID, INVITE_FAILED
    
    @Column(name = "invite_email", length = 100)
    private String inviteEmail; // 邀请的邮箱地址
    
    @Column(name = "invite_create_time")
    private LocalDateTime inviteCreateTime; // 邀请创建时间
    
    @Column(name = "temp_password", length = 50)
    private String tempPassword; // 临时密码（首次登录用）
    
    @Column(name = "updated_time")
    private LocalDateTime updatedTime; // 更新时间
    
    @Column(name = "rsa_public_key", columnDefinition = "TEXT")
    private String rsaPublicKey;
    
    // 新增phone和avatar字段
    @Column(name = "phone", length = 20)
    private String phone; // 手机号码
    
    @Column(name = "avatar", length = 500)
    private String avatar; // 用户头像URL
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getAccount() {
        return account;
    }
    
    public void setAccount(String account) {
        this.account = account;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public Integer getRole() {
        return role;
    }
    
    public void setRole(Integer role) {
        this.role = role;
    }
    
    public Long getParentId() {
        return parentId;
    }
    
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
    
    public Integer getEmailVerified() {
        return emailVerified;
    }
    
    public void setEmailVerified(Integer emailVerified) {
        this.emailVerified = emailVerified;
    }
    
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }
    
    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }
    
    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
    
    public String getLastLoginIp() {
        return lastLoginIp;
    }
    
    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }
    
    public String getLastLoginAddress() {
        return lastLoginAddress;
    }
    
    public void setLastLoginAddress(String lastLoginAddress) {
        this.lastLoginAddress = lastLoginAddress;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public String getRsaPublicKey() {
        return rsaPublicKey;
    }
    
    public void setRsaPublicKey(String rsaPublicKey) {
        this.rsaPublicKey = rsaPublicKey;
    }
    
    // 新增字段的getter和setter方法
    public String getAccountStatus() {
        return accountStatus;
    }
    
    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }
    
    public String getInviteEmail() {
        return inviteEmail;
    }
    
    public void setInviteEmail(String inviteEmail) {
        this.inviteEmail = inviteEmail;
    }
    
    public LocalDateTime getInviteCreateTime() {
        return inviteCreateTime;
    }
    
    public void setInviteCreateTime(LocalDateTime inviteCreateTime) {
        this.inviteCreateTime = inviteCreateTime;
    }
    
    public String getTempPassword() {
        return tempPassword;
    }
    
    public void setTempPassword(String tempPassword) {
        this.tempPassword = tempPassword;
    }
    
    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }
    
    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
    
    // phone和avatar字段的getter和setter方法
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getAvatar() {
        return avatar;
    }
    
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}