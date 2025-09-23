package com.wenji.server.repository;

import com.wenji.server.model.SocialComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SocialCommentRepository extends JpaRepository<SocialComment, Long> {
    
    /**
     * 根据帖子ID和状态获取评论列表（按评论时间倒序）
     */
    Page<SocialComment> findByPostIdAndStatusOrderByCommentTimeDesc(String postId, Integer status, Pageable pageable);
    
    /**
     * 根据父评论ID和状态获取回复列表（按评论时间正序）
     */
    List<SocialComment> findByParentCommentIdAndStatusOrderByCommentTimeAsc(String parentCommentId, Integer status);
    
    /**
     * 根据评论ID、主页ID、平台类型和状态查找评论
     */
    SocialComment findByCommentIdAndHomepageIdAndPlatformTypeAndStatus(String commentId, String homepageId, Integer platformType, Integer status);
    
    /**
     * 根据评论ID查找评论
     */
    SocialComment findByCommentId(String commentId);
    
    /**
     * 根据帖子ID查找评论列表
     */
    List<SocialComment> findByPostId(String postId);
    
    /**
     * 软删除评论（更新状态为已删除）
     */
    @Transactional
    @Modifying
    @Query("UPDATE SocialComment c SET c.status = 0 WHERE c.commentId = :commentId AND c.homepageId = :homepageId AND c.platformType = :platformType")
    int softDeleteByCommentIdAndHomepageIdAndPlatformType(@Param("commentId") String commentId, @Param("homepageId") String homepageId, @Param("platformType") Integer platformType);
    
    /**
     * 根据主页ID获取评论数量
     */
    Long countByHomepageIdAndStatus(String homepageId, Integer status);
    
    /**
     * 根据平台类型获取评论数量
     */
    Long countByPlatformTypeAndStatus(Integer platformType, Integer status);
    
    /**
     * 更新评论点赞数量
     */
    @Transactional
    @Modifying
    @Query("UPDATE SocialComment c SET c.likeCount = :likeCount WHERE c.commentId = :commentId AND c.homepageId = :homepageId AND c.platformType = :platformType")
    int updateLikeCountByCommentIdAndHomepageIdAndPlatformType(@Param("commentId") String commentId, @Param("homepageId") String homepageId, @Param("platformType") Integer platformType, @Param("likeCount") Integer likeCount);
}