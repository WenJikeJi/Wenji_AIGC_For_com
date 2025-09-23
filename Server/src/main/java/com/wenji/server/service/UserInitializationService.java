package com.wenji.server.service;

import com.wenji.server.model.UserAccount;
import com.wenji.server.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户数据初始化服务
 * 确保所有主账号的parentId都设置为自身ID
 */
@Component
public class UserInitializationService implements CommandLineRunner {

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 查找所有角色为0（主账号）且parentId为null的用户
        List<UserAccount> mainAccountsWithoutParentId = userAccountRepository.findByRoleAndParentIdIsNull(0);
        
        if (!mainAccountsWithoutParentId.isEmpty()) {
            System.out.println("发现 " + mainAccountsWithoutParentId.size() + " 个主账号需要初始化parentId");
            
            for (UserAccount mainAccount : mainAccountsWithoutParentId) {
                // 将主账号的parentId设置为自身ID
                mainAccount.setParentId(mainAccount.getId());
                userAccountRepository.save(mainAccount);
                System.out.println("已初始化主账号 " + mainAccount.getEmail() + " 的parentId为: " + mainAccount.getId());
            }
            
            System.out.println("主账号parentId初始化完成");
        }
    }
}