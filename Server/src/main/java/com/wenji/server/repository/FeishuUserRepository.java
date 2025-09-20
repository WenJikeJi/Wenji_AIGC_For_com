package com.wenji.server.repository;

import com.wenji.server.model.FeishuUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeishuUserRepository extends JpaRepository<FeishuUser, Long> {
    Optional<FeishuUser> findByFeishuUnionId(String feishuUnionId);
    Optional<FeishuUser> findByUserId(Long userId);
}