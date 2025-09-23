package com.wenji.server.repository;

import com.wenji.server.model.SocialHomepage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SocialHomepageRepository extends JpaRepository<SocialHomepage, Long> {
    
    /**
     * 根据用户ID查找所有启用的主页
     */
    List<SocialHomepage> findByUserIdAndStatus(Long userId, Integer status);
    
    /**
     * 根据主页ID查找主页
     */
    SocialHomepage findByHomepageId(String homepageId);
    
    /**
     * 查找所有启用状态的主页
     */
    List<SocialHomepage> findByStatus(Integer status);
    
    /**
     * 根据主页ID和平台类型查找主页
     */
    Optional<SocialHomepage> findByHomepageIdAndPlatformType(String homepageId, Integer platformType);
    
    /**
     * 根据主页ID列表查找主页
     */
    @Query("SELECT h FROM SocialHomepage h WHERE h.homepageId IN :homepageIds AND h.status = 1")
    List<SocialHomepage> findByHomepageIdsAndStatusEnabled(@Param("homepageIds") List<String> homepageIds);
    
    /**
     * 根据用户ID和平台类型查找主页
     */
    List<SocialHomepage> findByUserIdAndPlatformTypeAndStatus(Long userId, Integer platformType, Integer status);
}