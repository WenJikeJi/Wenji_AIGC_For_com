package com.wenji.server.repository;

import com.wenji.server.model.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    
    // 根据账号查询用户
    Optional<UserAccount> findByAccount(String account);
    
    // 根据邮箱查询用户
    Optional<UserAccount> findByEmail(String email);
    
    // 检查邮箱和角色是否唯一（用于子账号创建）
    boolean existsByEmailAndRole(String email, Integer role);
    
    // 检查账号是否已存在
    boolean existsByAccount(String account);
    
    // 检查邮箱是否已存在
    boolean existsByEmail(String email);
    
    // 更新最后登录信息
    @Modifying
    @Transactional
    @Query("UPDATE UserAccount u SET u.lastLoginTime = :lastLoginTime, u.lastLoginIp = :lastLoginIp, u.lastLoginAddress = :lastLoginAddress WHERE u.id = :id")
    int updateLastLoginInfo(@Param("id") Long id, 
                           @Param("lastLoginTime") LocalDateTime lastLoginTime, 
                           @Param("lastLoginIp") String lastLoginIp, 
                           @Param("lastLoginAddress") String lastLoginAddress);
    
    // 更新用户状态
    @Modifying
    @Transactional
    @Query("UPDATE UserAccount u SET u.status = :status WHERE u.id = :id")
    int updateUserStatus(@Param("id") Long id, @Param("status") Integer status);
    
    // 更新用户角色
    @Modifying
    @Transactional
    @Query("UPDATE UserAccount u SET u.role = :role WHERE u.id = :id")
    int updateUserRole(@Param("id") Long id, @Param("role") Integer role);
    
    // 更新用户密码
    @Modifying
    @Transactional
    @Query("UPDATE UserAccount u SET u.password = :password WHERE u.id = :id")
    int updateUserPassword(@Param("id") Long id, @Param("password") String password);
    
    // 更新用户父账号ID
    @Modifying
    @Transactional
    @Query("UPDATE UserAccount u SET u.parentId = :parentId WHERE u.id = :id")
    int updateUserParentId(@Param("id") Long id, @Param("parentId") Long parentId);
    
    // 根据父账号ID查询子账号列表
    List<UserAccount> findByParentId(Long parentId);
    
    // 根据父账号ID分页查询子账号列表
    Page<UserAccount> findByParentId(Long parentId, Pageable pageable);
    
    // 根据角色查询用户列表
    List<UserAccount> findByRole(Integer role);
    
    // 根据角色分页查询用户列表
    Page<UserAccount> findByRole(Integer role, Pageable pageable);
}