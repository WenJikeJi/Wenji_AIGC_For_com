package com.wenji.server.security;

import com.wenji.server.model.UserAccount;
import com.wenji.server.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserAccountRepository userAccountRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 首先尝试通过账号查找用户
        UserAccount userAccount = userAccountRepository.findByAccount(username)
                // 如果找不到，尝试通过邮箱查找
                .orElseGet(() -> userAccountRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username)));
        
        // 检查用户状态
        if (userAccount.getStatus() != 1) {
            throw new UsernameNotFoundException("用户账号已被禁用");
        }
        
        // 创建UserDetails对象
        return new User(
                userAccount.getAccount(), // 使用账号作为用户名
                userAccount.getPassword(),
                true, // 账号是否启用
                true, // 账号是否未过期
                true, // 凭证是否未过期
                true, // 账号是否未锁定
                Collections.emptyList() // 用户权限列表（这里暂时为空，实际项目中应该根据角色设置）
        );
    }
}