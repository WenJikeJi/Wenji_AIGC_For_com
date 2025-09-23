<template>
  <div class="p-6 bg-slate-900 min-h-screen">
    <div class="max-w-4xl mx-auto">
      <h1 class="text-2xl font-bold text-white mb-8">弹窗组件使用示例</h1>
      
      <!-- 示例按钮网格 -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mb-8">
        <!-- 基础确认弹窗 -->
        <button 
          @click="showModal('confirm')"
          class="p-4 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors"
        >
          <i class="fas fa-question-circle mr-2"></i>
          确认对话框
        </button>
        
        <!-- 信息弹窗 -->
        <button 
          @click="showModal('info')"
          class="p-4 bg-green-600 hover:bg-green-700 text-white rounded-lg transition-colors"
        >
          <i class="fas fa-info-circle mr-2"></i>
          信息对话框
        </button>
        
        <!-- 警告弹窗 -->
        <button 
          @click="showModal('warning')"
          class="p-4 bg-yellow-600 hover:bg-yellow-700 text-white rounded-lg transition-colors"
        >
          <i class="fas fa-exclamation-triangle mr-2"></i>
          警告对话框
        </button>
        
        <!-- 错误弹窗 -->
        <button 
          @click="showModal('error')"
          class="p-4 bg-red-600 hover:bg-red-700 text-white rounded-lg transition-colors"
        >
          <i class="fas fa-times-circle mr-2"></i>
          错误对话框
        </button>
        
        <!-- 表单弹窗 -->
        <button 
          @click="showModal('form')"
          class="p-4 bg-purple-600 hover:bg-purple-700 text-white rounded-lg transition-colors"
        >
          <i class="fas fa-edit mr-2"></i>
          表单对话框
        </button>
        
        <!-- 详情弹窗 -->
        <button 
          @click="showModal('detail')"
          class="p-4 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg transition-colors"
        >
          <i class="fas fa-eye mr-2"></i>
          详情对话框
        </button>
        
        <!-- 加载状态弹窗 -->
        <button 
          @click="showModal('loading')"
          class="p-4 bg-gray-600 hover:bg-gray-700 text-white rounded-lg transition-colors"
        >
          <i class="fas fa-spinner mr-2"></i>
          加载状态
        </button>
        
        <!-- 自定义弹窗 -->
        <button 
          @click="showModal('custom')"
          class="p-4 bg-pink-600 hover:bg-pink-700 text-white rounded-lg transition-colors"
        >
          <i class="fas fa-cog mr-2"></i>
          自定义弹窗
        </button>
        
        <!-- 全屏弹窗 -->
        <button 
          @click="showModal('fullscreen')"
          class="p-4 bg-teal-600 hover:bg-teal-700 text-white rounded-lg transition-colors"
        >
          <i class="fas fa-expand mr-2"></i>
          全屏弹窗
        </button>
      </div>
      
      <!-- 当前屏幕信息 -->
      <div class="bg-slate-800 p-4 rounded-lg mb-8">
        <h3 class="text-lg font-semibold text-white mb-2">响应式信息</h3>
        <div class="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
          <div class="text-slate-300">
            <span class="block text-slate-400">屏幕尺寸:</span>
            <span class="text-white font-medium">{{ screenSize }}</span>
          </div>
          <div class="text-slate-300">
            <span class="block text-slate-400">设备类型:</span>
            <span class="text-white font-medium">
              {{ isMobile ? '移动端' : isTablet ? '平板' : '桌面' }}
            </span>
          </div>
          <div class="text-slate-300">
            <span class="block text-slate-400">弹窗尺寸:</span>
            <span class="text-white font-medium">{{ responsiveModalSize }}</span>
          </div>
          <div class="text-slate-300">
            <span class="block text-slate-400">窗口宽度:</span>
            <span class="text-white font-medium">{{ windowWidth }}px</span>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 确认对话框 -->
    <BaseModal
      :show="isModalVisible('confirm')"
      title="确认删除"
      size="sm"
      icon="fas fa-question-circle"
      icon-type="warning"
      :show-footer="true"
      :show-cancel-button="true"
      :show-confirm-button="true"
      confirm-text="删除"
      confirm-type="error"
      @close="hideModal('confirm')"
      @cancel="hideModal('confirm')"
      @confirm="handleConfirm('confirm')"
    >
      <p class="text-slate-300">
        您确定要删除这个项目吗？此操作无法撤销。
      </p>
    </BaseModal>
    
    <!-- 信息对话框 -->
    <BaseModal
      :show="isModalVisible('info')"
      title="操作成功"
      size="md"
      icon="fas fa-check-circle"
      icon-type="success"
      status="已完成"
      status-type="success"
      :show-footer="true"
      :show-confirm-button="true"
      confirm-text="知道了"
      confirm-type="success"
      @close="hideModal('info')"
      @confirm="hideModal('info')"
    >
      <div class="text-slate-300">
        <p class="mb-4">您的操作已成功完成！</p>
        <div class="bg-green-500/10 border border-green-500/20 rounded-lg p-3">
          <div class="flex items-center">
            <i class="fas fa-check text-green-400 mr-2"></i>
            <span class="text-green-300 text-sm">数据已保存到服务器</span>
          </div>
        </div>
      </div>
    </BaseModal>
    
    <!-- 警告对话框 -->
    <BaseModal
      :show="isModalVisible('warning')"
      title="注意事项"
      size="md"
      icon="fas fa-exclamation-triangle"
      icon-type="warning"
      status="需要注意"
      status-type="warning"
      :show-footer="true"
      :show-cancel-button="true"
      :show-confirm-button="true"
      confirm-text="继续"
      confirm-type="warning"
      @close="hideModal('warning')"
      @cancel="hideModal('warning')"
      @confirm="handleConfirm('warning')"
    >
      <div class="text-slate-300">
        <p class="mb-4">执行此操作可能会影响系统性能，请确认是否继续？</p>
        <ul class="list-disc list-inside space-y-1 text-sm text-slate-400">
          <li>可能需要等待较长时间</li>
          <li>期间请勿关闭浏览器</li>
          <li>建议在非高峰期执行</li>
        </ul>
      </div>
    </BaseModal>
    
    <!-- 错误对话框 -->
    <BaseModal
      :show="isModalVisible('error')"
      title="操作失败"
      size="md"
      icon="fas fa-times-circle"
      icon-type="error"
      status="错误"
      status-type="error"
      :show-footer="true"
      :show-confirm-button="true"
      confirm-text="重试"
      confirm-type="error"
      @close="hideModal('error')"
      @confirm="handleConfirm('error')"
    >
      <div class="text-slate-300">
        <p class="mb-4">操作执行失败，请检查以下问题：</p>
        <div class="bg-red-500/10 border border-red-500/20 rounded-lg p-3 mb-4">
          <div class="flex items-start">
            <i class="fas fa-exclamation-circle text-red-400 mr-2 mt-0.5"></i>
            <div class="text-red-300 text-sm">
              <p class="font-medium mb-1">网络连接错误</p>
              <p class="text-red-200/80">请检查您的网络连接是否正常</p>
            </div>
          </div>
        </div>
        <p class="text-sm text-slate-400">
          如果问题持续存在，请联系技术支持。
        </p>
      </div>
    </BaseModal>
    
    <!-- 表单对话框 -->
    <BaseModal
      :show="isModalVisible('form')"
      title="编辑用户信息"
      size="lg"
      icon="fas fa-user-edit"
      icon-type="primary"
      :show-footer="true"
      :show-cancel-button="true"
      :show-confirm-button="true"
      confirm-text="保存"
      confirm-type="primary"
      :confirm-disabled="!formValid"
      :loading="isModalLoading('form')"
      @close="hideModal('form')"
      @cancel="hideModal('form')"
      @confirm="handleFormSubmit"
    >
      <form @submit.prevent="handleFormSubmit" class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-slate-300 mb-2">
            用户名
          </label>
          <input
            v-model="formData.username"
            type="text"
            class="w-full px-3 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            placeholder="请输入用户名"
          />
        </div>
        
        <div>
          <label class="block text-sm font-medium text-slate-300 mb-2">
            邮箱地址
          </label>
          <input
            v-model="formData.email"
            type="email"
            class="w-full px-3 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            placeholder="请输入邮箱地址"
          />
        </div>
        
        <div>
          <label class="block text-sm font-medium text-slate-300 mb-2">
            角色
          </label>
          <select
            v-model="formData.role"
            class="w-full px-3 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          >
            <option value="">请选择角色</option>
            <option value="admin">管理员</option>
            <option value="user">普通用户</option>
            <option value="guest">访客</option>
          </select>
        </div>
        
        <div>
          <label class="block text-sm font-medium text-slate-300 mb-2">
            描述
          </label>
          <textarea
            v-model="formData.description"
            rows="3"
            class="w-full px-3 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
            placeholder="请输入用户描述"
          ></textarea>
        </div>
      </form>
    </BaseModal>
    
    <!-- 详情对话框 -->
    <BaseModal
      :show="isModalVisible('detail')"
      title="用户详细信息"
      size="xl"
      icon="fas fa-user"
      icon-type="info"
      :show-footer="true"
      :show-confirm-button="true"
      confirm-text="关闭"
      @close="hideModal('detail')"
      @confirm="hideModal('detail')"
    >
      <div class="space-y-6">
        <!-- 基本信息 -->
        <div>
          <h4 class="text-lg font-semibold text-white mb-3">基本信息</h4>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="bg-slate-700/50 p-3 rounded-lg">
              <span class="block text-sm text-slate-400 mb-1">用户名</span>
              <span class="text-white font-medium">john_doe</span>
            </div>
            <div class="bg-slate-700/50 p-3 rounded-lg">
              <span class="block text-sm text-slate-400 mb-1">邮箱</span>
              <span class="text-white font-medium">john@example.com</span>
            </div>
            <div class="bg-slate-700/50 p-3 rounded-lg">
              <span class="block text-sm text-slate-400 mb-1">角色</span>
              <span class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-blue-500/20 text-blue-300 border border-blue-500/30">
                管理员
              </span>
            </div>
            <div class="bg-slate-700/50 p-3 rounded-lg">
              <span class="block text-sm text-slate-400 mb-1">状态</span>
              <span class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-green-500/20 text-green-300 border border-green-500/30">
                <i class="fas fa-circle text-xs mr-1"></i>
                在线
              </span>
            </div>
          </div>
        </div>
        
        <!-- 统计信息 -->
        <div>
          <h4 class="text-lg font-semibold text-white mb-3">统计信息</h4>
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div class="text-center p-4 bg-blue-500/10 border border-blue-500/20 rounded-lg">
              <div class="text-2xl font-bold text-blue-400 mb-1">156</div>
              <div class="text-sm text-blue-300">登录次数</div>
            </div>
            <div class="text-center p-4 bg-green-500/10 border border-green-500/20 rounded-lg">
              <div class="text-2xl font-bold text-green-400 mb-1">89</div>
              <div class="text-sm text-green-300">创建项目</div>
            </div>
            <div class="text-center p-4 bg-purple-500/10 border border-purple-500/20 rounded-lg">
              <div class="text-2xl font-bold text-purple-400 mb-1">23</div>
              <div class="text-sm text-purple-300">团队成员</div>
            </div>
          </div>
        </div>
      </div>
    </BaseModal>
    
    <!-- 加载状态弹窗 -->
    <BaseModal
      :show="isModalVisible('loading')"
      title="处理中"
      size="sm"
      icon="fas fa-spinner"
      icon-type="info"
      :loading="true"
      :show-footer="false"
      :close-on-backdrop="false"
      :close-on-escape="false"
      :show-close-button="false"
    >
      <div class="text-center py-8">
        <div class="w-16 h-16 bg-blue-500/20 rounded-full flex items-center justify-center mx-auto mb-4">
          <i class="fas fa-spinner fa-spin text-2xl text-blue-400"></i>
        </div>
        <p class="text-slate-300 mb-2">正在处理您的请求...</p>
        <p class="text-sm text-slate-400">请稍候，预计需要 30 秒</p>
        
        <!-- 进度条 -->
        <div class="mt-6 bg-slate-700 rounded-full h-2 overflow-hidden">
          <div class="bg-blue-500 h-full rounded-full animate-pulse" style="width: 60%"></div>
        </div>
      </div>
    </BaseModal>
    
    <!-- 自定义弹窗 -->
    <BaseModal
      :show="isModalVisible('custom')"
      title="自定义弹窗"
      size="lg"
      custom-class="border-2 border-pink-500/30"
      :show-header="true"
      :show-footer="false"
      @close="hideModal('custom')"
    >
      <template #header-actions>
        <button class="px-3 py-1 bg-pink-600 text-white text-xs rounded-lg hover:bg-pink-700 transition-colors">
          <i class="fas fa-star mr-1"></i>
          收藏
        </button>
      </template>
      
      <div class="space-y-4">
        <div class="bg-gradient-to-r from-pink-500/10 to-purple-500/10 border border-pink-500/20 rounded-lg p-4">
          <h4 class="text-lg font-semibold text-white mb-2">
            <i class="fas fa-magic mr-2 text-pink-400"></i>
            自定义内容区域
          </h4>
          <p class="text-slate-300 mb-4">
            这是一个完全自定义的弹窗示例，展示了如何使用插槽来自定义头部操作和内容区域。
          </p>
          
          <!-- 自定义内容 -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="space-y-3">
              <div class="flex items-center p-3 bg-slate-700/50 rounded-lg">
                <i class="fas fa-palette text-pink-400 mr-3"></i>
                <div>
                  <div class="text-white font-medium">主题颜色</div>
                  <div class="text-sm text-slate-400">粉色渐变</div>
                </div>
              </div>
              
              <div class="flex items-center p-3 bg-slate-700/50 rounded-lg">
                <i class="fas fa-layer-group text-purple-400 mr-3"></i>
                <div>
                  <div class="text-white font-medium">布局方式</div>
                  <div class="text-sm text-slate-400">网格布局</div>
                </div>
              </div>
            </div>
            
            <div class="space-y-3">
              <div class="flex items-center p-3 bg-slate-700/50 rounded-lg">
                <i class="fas fa-cog text-blue-400 mr-3"></i>
                <div>
                  <div class="text-white font-medium">动画效果</div>
                  <div class="text-sm text-slate-400">淡入淡出</div>
                </div>
              </div>
              
              <div class="flex items-center p-3 bg-slate-700/50 rounded-lg">
                <i class="fas fa-mobile-alt text-green-400 mr-3"></i>
                <div>
                  <div class="text-white font-medium">响应式</div>
                  <div class="text-sm text-slate-400">自适应</div>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 自定义底部 -->
        <div class="flex justify-between items-center pt-4 border-t border-slate-600">
          <div class="text-sm text-slate-400">
            <i class="fas fa-info-circle mr-1"></i>
            这是自定义的底部区域
          </div>
          <div class="flex space-x-2">
            <button 
              @click="hideModal('custom')"
              class="px-4 py-2 bg-slate-600 text-white rounded-lg hover:bg-slate-500 transition-colors"
            >
              取消
            </button>
            <button 
              @click="handleConfirm('custom')"
              class="px-4 py-2 bg-pink-600 text-white rounded-lg hover:bg-pink-700 transition-colors"
            >
              <i class="fas fa-check mr-2"></i>
              应用设置
            </button>
          </div>
        </div>
      </div>
    </BaseModal>
    
    <!-- 全屏弹窗 -->
    <BaseModal
      :show="isModalVisible('fullscreen')"
      title="全屏弹窗"
      size="full"
      icon="fas fa-expand"
      icon-type="primary"
      custom-class="h-screen rounded-none"
      @close="hideModal('fullscreen')"
    >
      <div class="h-full flex flex-col">
        <div class="flex-1 p-6 overflow-y-auto">
          <div class="max-w-4xl mx-auto space-y-8">
            <div class="text-center">
              <h2 class="text-3xl font-bold text-white mb-4">全屏体验</h2>
              <p class="text-xl text-slate-300 mb-8">
                这是一个全屏弹窗示例，适合展示大量内容或复杂的界面。
              </p>
            </div>
            
            <!-- 模拟内容 -->
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              <div v-for="i in 9" :key="i" class="bg-slate-700/50 p-6 rounded-lg">
                <div class="w-12 h-12 bg-blue-500/20 rounded-lg flex items-center justify-center mb-4">
                  <i class="fas fa-cube text-blue-400 text-xl"></i>
                </div>
                <h3 class="text-lg font-semibold text-white mb-2">功能模块 {{ i }}</h3>
                <p class="text-slate-300 text-sm mb-4">
                  这是功能模块 {{ i }} 的详细描述，展示了该模块的主要特性和用途。
                </p>
                <button class="w-full py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
                  了解更多
                </button>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 全屏弹窗底部 -->
        <div class="bg-slate-800/50 border-t border-slate-600 p-6">
          <div class="max-w-4xl mx-auto flex justify-between items-center">
            <div class="text-slate-400">
              <i class="fas fa-info-circle mr-2"></i>
              全屏模式下的底部操作栏
            </div>
            <button 
              @click="hideModal('fullscreen')"
              class="px-6 py-2 bg-slate-600 text-white rounded-lg hover:bg-slate-500 transition-colors"
            >
              <i class="fas fa-times mr-2"></i>
              关闭全屏
            </button>
          </div>
        </div>
      </div>
    </BaseModal>
  </div>
</template>

<script>
import BaseModal from './BaseModal.vue'
import { modalMixin } from '../utils/modalMixin.js'

export default {
  name: 'ModalExample',
  components: {
    BaseModal
  },
  mixins: [modalMixin],
  
  data() {
    return {
      windowWidth: window.innerWidth,
      formData: {
        username: '',
        email: '',
        role: '',
        description: ''
      }
    }
  },
  
  computed: {
    formValid() {
      return this.formData.username && 
             this.formData.email && 
             this.formData.role
    }
  },
  
  mounted() {
    window.addEventListener('resize', this.updateWindowWidth)
    
    // 模拟加载弹窗自动关闭
    setTimeout(() => {
      if (this.isModalVisible('loading')) {
        this.hideModal('loading')
      }
    }, 3000)
  },
  
  beforeUnmount() {
    window.removeEventListener('resize', this.updateWindowWidth)
  },
  
  methods: {
    updateWindowWidth() {
      this.windowWidth = window.innerWidth
    },
    
    handleConfirm(modalName) {
      console.log(`确认操作: ${modalName}`)
      
      // 模拟不同的确认逻辑
      switch (modalName) {
        case 'confirm':
          alert('项目已删除')
          break
        case 'warning':
          alert('操作已继续执行')
          break
        case 'error':
          alert('正在重试...')
          break
        case 'custom':
          alert('设置已应用')
          break
      }
      
      this.hideModal(modalName)
    },
    
    async handleFormSubmit() {
      if (!this.formValid) return
      
      this.setModalLoading('form', true)
      
      try {
        // 模拟API调用
        await new Promise(resolve => setTimeout(resolve, 2000))
        
        console.log('表单数据:', this.formData)
        alert('用户信息已保存')
        
        // 重置表单
        this.formData = {
          username: '',
          email: '',
          role: '',
          description: ''
        }
        
        this.hideModal('form')
      } catch (error) {
        console.error('保存失败:', error)
        alert('保存失败，请重试')
      } finally {
        this.setModalLoading('form', false)
      }
    }
  }
}
</script>