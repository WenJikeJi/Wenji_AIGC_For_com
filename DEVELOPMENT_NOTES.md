# 开发说明文档

## 待完成功能 (TODO)

### 社交媒体控制器 (SocialMediaController)

以下功能需要进一步实现：

#### 用户认证相关
- [ ] **用户ID获取**: 当前多处使用硬编码的 `operatorId = 1L`，需要从JWT认证信息中获取真实用户ID
  - 影响方法：`publishPost()`, `schedulePost()`, `fetchHomepages()`, `saveFacebookToken()`, `unbindFacebook()`, `getFacebookPosts()`, `getInstagramPages()`, `selectInstagramPage()`, `unbindInstagram()`, `getInstagramPosts()`

#### Facebook集成
- [ ] **Facebook授权URL生成**: `getFacebookAuthUrl()` 方法需要实现真实的Facebook OAuth授权逻辑
- [ ] **Facebook Token保存**: `saveFacebookToken()` 方法需要实现保存Facebook访问令牌的逻辑
- [ ] **Facebook账号解绑**: `unbindFacebook()` 方法需要实现解绑Facebook账号的逻辑
- [ ] **Facebook帖子获取**: `getFacebookPosts()` 方法需要实现获取Facebook帖子的逻辑

#### Instagram集成
- [ ] **Instagram Pages获取**: `getInstagramPages()` 方法需要实现获取Instagram Business Pages的逻辑
- [ ] **Instagram Page选择**: `selectInstagramPage()` 方法需要实现选择Instagram Page的逻辑
- [ ] **Instagram账号解绑**: `unbindInstagram()` 方法需要实现解绑Instagram账号的逻辑
- [ ] **Instagram帖子获取**: `getInstagramPosts()` 方法需要实现获取Instagram帖子的逻辑

### 社交媒体服务 (SocialMediaService)

- [ ] **Facebook帖子获取**: `getFacebookPosts()` 方法需要实现获取Facebook帖子的逻辑
- [ ] **Instagram帖子获取**: `getInstagramPosts()` 方法需要实现获取Instagram帖子的逻辑

### 定时任务调度服务 (SocialPostSchedulerService)

- [ ] **告警通知**: `sendAlertNotification()` 方法需要实现邮件或其他方式的告警通知

## 代码质量改进建议

### 1. 用户认证统一处理
建议创建一个工具类或使用Spring Security的上下文来统一获取当前用户信息，避免在每个方法中重复处理。

### 2. 异常处理优化
当前的异常处理比较简单，建议：
- 创建自定义异常类
- 实现全局异常处理器
- 提供更详细的错误信息

### 3. 配置外部化
Facebook和Instagram的API配置应该移到配置文件中，避免硬编码。

### 4. 日志记录规范
建议统一日志记录格式，包含更多上下文信息。

## 安全注意事项

### 1. 敏感信息处理
- 所有API密钥和访问令牌应通过环境变量管理
- 避免在日志中记录敏感信息
- 实现令牌的安全存储和定期刷新

### 2. 输入验证
- 加强对用户输入的验证
- 防止SQL注入和XSS攻击
- 实现请求频率限制

### 3. 权限控制
- 确保用户只能操作自己的数据
- 实现细粒度的权限控制
- 添加操作审计日志

## 性能优化建议

### 1. 数据库查询优化
- 添加必要的数据库索引
- 优化分页查询性能
- 考虑使用缓存减少数据库访问

### 2. 异步处理
- 社交媒体API调用应该异步处理
- 实现任务队列机制
- 添加重试机制

### 3. 监控和指标
- 添加应用性能监控
- 实现健康检查端点
- 收集关键业务指标

## 测试建议

### 1. 单元测试
- 为所有业务逻辑添加单元测试
- 模拟外部API调用
- 测试异常情况处理

### 2. 集成测试
- 测试完整的API流程
- 验证数据库操作
- 测试认证和授权

### 3. 端到端测试
- 测试用户完整的使用流程
- 验证前后端集成
- 测试不同浏览器兼容性