package com.wenji.server.repository;

import com.wenji.server.model.SocialPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SocialPostRepository extends JpaRepository<SocialPost, Long> {
    
    /**
     * 根据任务ID查找帖子
     */
    List<SocialPost> findByTaskId(Long taskId);
    
    /**
     * 根据帖子ID查找帖子
     */
    SocialPost findByPostId(String postId);
    
    /**
     * 根据主页ID查找帖子
     */
    Page<SocialPost> findByHomepageIdOrderByCreatedAtDesc(String homepageId, Pageable pageable);
    
    /**
     * 根据平台类型查找帖子
     */
    Page<SocialPost> findByPlatformTypeOrderByCreatedAtDesc(Integer platformType, Pageable pageable);
    
    /**
     * 根据操作人ID查找帖子
     */
    Page<SocialPost> findByOperatorIdOrderByCreatedAtDesc(Long operatorId, Pageable pageable);
    
    /**
     * 查找所有帖子（按创建时间倒序）
     */
    Page<SocialPost> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * 根据发布来源查找帖子
     */
    Page<SocialPost> findByPublishSourceOrderByCreatedAtDesc(Integer publishSource, Pageable pageable);
    
    /**
     * 统计帖子数量
     */
    @Query("SELECT COUNT(p) FROM SocialPost p WHERE p.operatorId = :operatorId")
    Long countByOperatorId(@Param("operatorId") Long operatorId);
}