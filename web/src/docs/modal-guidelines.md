# 弹窗组件设计规范

## 概述

本文档定义了项目中弹窗组件的设计原则、使用规范和最佳实践，确保所有弹窗具有统一的视觉效果和交互体验。

## 设计原则

### 1. 视觉统一
- **一致的视觉语言**：所有弹窗使用统一的颜色、字体、间距和圆角
- **品牌一致性**：保持与整体设计系统的一致性
- **层次清晰**：通过颜色、大小、位置建立清晰的信息层次

### 2. 交互反馈
- **即时反馈**：用户操作后立即提供视觉反馈
- **状态指示**：清晰显示加载、成功、错误等状态
- **动画过渡**：使用流畅的动画增强用户体验

### 3. 响应式适配
- **多设备支持**：在桌面、平板、手机上都有良好体验
- **自适应布局**：根据屏幕尺寸自动调整弹窗大小和布局
- **触摸友好**：在移动设备上提供合适的触摸目标

## 组件架构

### BaseModal 基础组件
```vue
<BaseModal
  :show="visible"
  title="弹窗标题"
  size="md"
  icon="fas fa-info-circle"
  icon-type="info"
  @close="handleClose"
>
  <!-- 弹窗内容 -->
</BaseModal>
```

### modalMixin 混入
提供统一的弹窗管理逻辑：
- 弹窗状态管理
- 响应式检测
- 加载状态控制
- 全局管理器

## 尺寸规范

| 尺寸 | 宽度 | 适用场景 |
|------|------|----------|
| `xs` | 320px | 简单确认、提示 |
| `sm` | 400px | 基础对话框 |
| `md` | 500px | 标准表单、信息展示 |
| `lg` | 600px | 复杂表单、详细信息 |
| `xl` | 800px | 大量内容、数据表格 |
| `2xl` | 1000px | 复杂界面、多列布局 |
| `full` | 100% | 全屏体验 |

### 响应式断点
- **移动端** (< 768px)：自动使用全宽布局
- **平板端** (768px - 1024px)：限制最大宽度为 90%
- **桌面端** (> 1024px)：使用指定尺寸

## 颜色系统

### 图标类型
```scss
// 主要操作
.icon-primary { color: #3b82f6; }

// 信息提示
.icon-info { color: #06b6d4; }

// 成功状态
.icon-success { color: #10b981; }

// 警告提示
.icon-warning { color: #f59e0b; }

// 错误状态
.icon-error { color: #ef4444; }
```

### 状态标签
```scss
// 成功状态
.status-success {
  background: rgba(16, 185, 129, 0.1);
  border: 1px solid rgba(16, 185, 129, 0.2);
  color: #10b981;
}

// 警告状态
.status-warning {
  background: rgba(245, 158, 11, 0.1);
  border: 1px solid rgba(245, 158, 11, 0.2);
  color: #f59e0b;
}

// 错误状态
.status-error {
  background: rgba(239, 68, 68, 0.1);
  border: 1px solid rgba(239, 68, 68, 0.2);
  color: #ef4444;
}
```

## 动画规范

### 进入动画
```scss
@keyframes modalEnter {
  from {
    opacity: 0;
    transform: scale(0.95) translateY(-10px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}
```

### 退出动画
```scss
@keyframes modalExit {
  from {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
  to {
    opacity: 0;
    transform: scale(0.95) translateY(-10px);
  }
}
```

### 动画时长
- **进入动画**：200ms ease-out
- **退出动画**：150ms ease-in
- **背景动画**：300ms ease-in-out

## 使用场景

### 1. 确认对话框
```vue
<BaseModal
  :show="showConfirm"
  title="确认删除"
  size="sm"
  icon="fas fa-question-circle"
  icon-type="warning"
  :show-cancel-button="true"
  :show-confirm-button="true"
  confirm-text="删除"
  confirm-type="error"
  @confirm="handleDelete"
  @cancel="showConfirm = false"
>
  <p>您确定要删除这个项目吗？此操作无法撤销。</p>
</BaseModal>
```

### 2. 信息提示
```vue
<BaseModal
  :show="showInfo"
  title="操作成功"
  size="md"
  icon="fas fa-check-circle"
  icon-type="success"
  status="已完成"
  status-type="success"
  :show-confirm-button="true"
  confirm-text="知道了"
  @confirm="showInfo = false"
>
  <p>您的操作已成功完成！</p>
</BaseModal>
```

### 3. 表单弹窗
```vue
<BaseModal
  :show="showForm"
  title="编辑信息"
  size="lg"
  icon="fas fa-edit"
  icon-type="primary"
  :show-cancel-button="true"
  :show-confirm-button="true"
  confirm-text="保存"
  :confirm-disabled="!formValid"
  :loading="submitting"
  @confirm="handleSubmit"
  @cancel="showForm = false"
>
  <form @submit.prevent="handleSubmit">
    <!-- 表单内容 -->
  </form>
</BaseModal>
```

### 4. 加载状态
```vue
<BaseModal
  :show="showLoading"
  title="处理中"
  size="sm"
  :loading="true"
  :show-footer="false"
  :close-on-backdrop="false"
  :close-on-escape="false"
  :show-close-button="false"
>
  <div class="text-center py-8">
    <div class="loading-spinner"></div>
    <p>正在处理您的请求...</p>
  </div>
</BaseModal>
```

## 最佳实践

### 1. 内容组织
- **标题简洁明确**：使用动词+名词的形式，如"删除项目"
- **内容层次清晰**：重要信息突出显示，次要信息适当弱化
- **操作明确**：按钮文字明确表达操作结果

### 2. 交互设计
- **主要操作突出**：使用主色调按钮
- **危险操作警示**：使用红色系按钮
- **取消操作明显**：提供明确的取消方式

### 3. 响应式考虑
- **移动端优化**：按钮足够大，便于触摸
- **内容适配**：长内容提供滚动
- **键盘导航**：支持Tab键和回车键操作

### 4. 性能优化
- **懒加载**：大型弹窗内容按需加载
- **动画优化**：使用transform和opacity进行动画
- **内存管理**：及时清理事件监听器

## 无障碍支持

### 1. 键盘导航
- **Tab键**：在可交互元素间切换
- **Enter键**：确认操作
- **Escape键**：关闭弹窗
- **方向键**：在选项间导航

### 2. 屏幕阅读器
- **aria-label**：为按钮提供描述
- **aria-describedby**：关联帮助文本
- **role="dialog"**：标识弹窗角色
- **aria-modal="true"**：标识模态状态

### 3. 焦点管理
- **自动聚焦**：弹窗打开时聚焦到第一个可交互元素
- **焦点锁定**：防止焦点跳出弹窗
- **焦点恢复**：关闭时恢复到触发元素

## 测试清单

### 功能测试
- [ ] 弹窗正常打开和关闭
- [ ] 按钮点击响应正确
- [ ] 表单验证工作正常
- [ ] 加载状态显示正确

### 响应式测试
- [ ] 在不同屏幕尺寸下显示正常
- [ ] 移动端触摸操作流畅
- [ ] 横竖屏切换适配良好

### 无障碍测试
- [ ] 键盘导航完整
- [ ] 屏幕阅读器兼容
- [ ] 焦点管理正确
- [ ] 颜色对比度符合标准

### 性能测试
- [ ] 动画流畅无卡顿
- [ ] 大量内容滚动顺畅
- [ ] 内存使用合理

## 常见问题

### Q: 如何自定义弹窗样式？
A: 使用 `custom-class` 属性添加自定义CSS类，或通过插槽自定义内容。

### Q: 如何处理长内容？
A: 内容区域会自动显示滚动条，也可以使用更大的尺寸或全屏模式。

### Q: 如何实现嵌套弹窗？
A: 使用不同的z-index值，确保后打开的弹窗在上层。

### Q: 如何优化移动端体验？
A: 在小屏幕上自动使用全宽布局，增大按钮尺寸，优化触摸体验。

## 更新日志

### v1.0.0 (2024-01-15)
- 初始版本发布
- 基础弹窗组件
- 响应式适配
- 无障碍支持

---

*本文档会根据项目需求和用户反馈持续更新。如有问题或建议，请联系设计团队。*