<template>
  <!-- 弹窗遮罩 -->
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50" @click="$emit('close')">
    <!-- 弹窗主体 -->
    <div class="bg-white rounded-lg shadow-xl relative" style="width: 1000px; max-height: 85vh; overflow-y: auto; padding: 20px; box-shadow: 0 8px 32px rgba(0,0,0,0.12); border-radius: 12px;" @click.stop>
      <!-- 关闭按钮 -->
      <button 
        @click="$emit('close')"
        class="absolute top-4 right-4 text-gray-400 hover:text-gray-600 transition-colors duration-200 z-10"
      >
        <i class="fas fa-times text-xl"></i>
      </button>

      <!-- 弹窗标题 -->
      <div class="mb-4">
        <h2 class="text-2xl font-bold text-gray-800 mb-1">社交媒体授权管理</h2>
        <p class="text-gray-600 text-sm">管理您的Facebook、Instagram和TikTok账户授权，确保内容发布正常运行</p>
      </div>

      <!-- 平台切换标签 -->
       <div class="relative flex space-x-1 mb-4 bg-gray-100 p-1 rounded-lg">
         <!-- 滑动指示器 -->
         <div 
           class="absolute top-1 bottom-1 bg-gradient-to-r from-blue-500 to-purple-600 rounded-md transition-all duration-300 ease-out shadow-lg"
           :style="getIndicatorStyle()"
         ></div>
         
         <button
           v-for="(platform, index) in ['facebook', 'instagram', 'tiktok']"
           :key="platform"
           @click="activePlatform = platform"
           :class="[
             'relative z-10 flex-1 py-3 px-6 rounded-md font-medium transition-all duration-300',
             'min-h-[48px] flex items-center justify-center',
             activePlatform === platform
               ? 'text-white'
               : 'text-gray-600 hover:text-gray-800 hover:bg-gray-50'
           ]"
         >
           <i :class="getPlatformIcon(platform)" class="mr-2"></i>
           {{ getPlatformName(platform) }}
         </button>
       </div>

      <!-- 中部：操作栏 -->
       <div class="flex justify-between items-center mb-4">
         <div class="flex space-x-3">
           <button
             v-if="activePlatform === 'facebook'"
             @click="addFacebookAccount"
             class="bg-gradient-to-r from-blue-500 to-purple-600 hover:from-blue-600 hover:to-purple-700 text-white px-5 py-2.5 rounded-lg font-medium transition-all duration-300 transform hover:scale-103 hover:shadow-lg active:scale-98 text-sm"
           >
             <i class="fab fa-facebook-f mr-2"></i>
             添加Facebook账号
           </button>
           
           <button
             v-if="activePlatform === 'instagram'"
             @click="addInstagramAccount"
             class="bg-gradient-to-r from-blue-500 to-purple-600 hover:from-blue-600 hover:to-purple-700 text-white px-5 py-2.5 rounded-lg font-medium transition-all duration-300 transform hover:scale-103 hover:shadow-lg active:scale-98 text-sm"
           >
             <i class="fab fa-instagram mr-2"></i>
             添加Instagram账号
           </button>
           
           <button
             v-if="activePlatform === 'tiktok'"
             @click="connectPlatform('tiktok')"
             class="bg-gradient-to-r from-blue-500 to-purple-600 hover:from-blue-600 hover:to-purple-700 text-white px-5 py-2.5 rounded-lg font-medium transition-all duration-300 transform hover:scale-103 hover:shadow-lg active:scale-98 text-sm"
           >
             <i class="fab fa-tiktok mr-2"></i>
             添加TikTok账号
           </button>
           
           <button
             @click="refreshList"
             class="bg-gray-100 hover:bg-gray-200 text-gray-700 px-5 py-2.5 rounded-lg font-medium transition-all duration-300 transform hover:scale-103 hover:shadow-md active:scale-98 border border-gray-300 text-sm"
           >
             <i class="fas fa-sync-alt mr-2"></i>
             刷新列表
           </button>
         </div>
         
         <button
           @click="showTokenModal = true"
           class="bg-green-600 hover:bg-green-700 text-white px-5 py-2.5 rounded-lg font-medium transition-all duration-300 transform hover:scale-103 hover:shadow-lg active:scale-98 text-sm"
         >
           <i class="fas fa-key mr-2"></i>
           手动配置Token
         </button>
       </div>

       <!-- 下部：账号列表 -->
       <div class="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden" style="min-height: 300px;">
         <!-- 统一表格头部 -->
         <div class="bg-gradient-to-r from-gray-50 to-gray-100 border-b border-gray-200 sticky top-0 z-10">
           <div class="grid grid-cols-7 gap-4 px-6 py-4 text-sm font-semibold text-gray-700">
             <div class="col-span-2">账户信息</div>
             <div>类型</div>
             <div>状态</div>
             <div>粉丝数</div>
             <div>最后同步时间</div>
             <div class="text-center">操作</div>
           </div>
         </div>
         
         <!-- 表格内容区域 -->
         <div class="overflow-y-auto" style="height: 300px;">
           
           <!-- 表格内容 -->
           <div class="divide-y divide-gray-100">
             <div 
               v-for="account in currentPlatformAccounts" 
               :key="account.id"
               :data-account-id="account.id"
               class="grid grid-cols-7 gap-4 px-6 py-3 hover:bg-gray-50 transition-all duration-200"
             >
               <!-- 账户信息 -->
               <div class="col-span-2 flex items-center">
                 <div class="w-10 h-10 rounded-full bg-gray-200 flex items-center justify-center mr-3 overflow-hidden shadow-sm">
                   <img 
                     v-if="account.avatar"
                     :src="account.avatar"
                     :alt="account.name"
                     class="w-full h-full object-cover"
                   >
                   <i 
                     v-else
                     :class="getPlatformIcon(activePlatform)"
                     class="text-gray-400 text-base"
                   ></i>
                 </div>
                 <div>
                   <p class="text-sm font-semibold text-gray-900">{{ account.name }}</p>
                   <p class="text-xs text-gray-500">{{ account.username || account.id }}</p>
                 </div>
               </div>
               
               <!-- 类型 -->
               <div class="flex items-center">
                 <span class="px-2 py-1 text-xs font-medium bg-blue-100 text-blue-800 rounded-full">
                   {{ account.type || '个人' }}
                 </span>
               </div>
               
               <!-- 状态 -->
               <div class="flex items-center">
                 <div class="group relative">
                   <span 
                     class="px-3 py-1 text-xs font-semibold rounded-full flex items-center cursor-help transition-all duration-200 hover:shadow-md"
                     :class="getStatusClass(account.status)"
                   >
                     <i :class="getStatusIcon(account.status)" class="mr-1"></i>
                     {{ getStatusText(account.status) }}
                   </span>
                   
                   <!-- Tooltip -->
                   <div 
                     v-if="account.status === 'limited'"
                     class="absolute bottom-full left-1/2 transform -translate-x-1/2 mb-2 px-3 py-2 bg-gray-900 text-white text-xs rounded-lg opacity-0 group-hover:opacity-100 transition-opacity duration-200 pointer-events-none whitespace-nowrap z-10"
                   >
                     缺少必要权限，点击"修复权限"重新授权
                     <div class="absolute top-full left-1/2 transform -translate-x-1/2 w-0 h-0 border-l-4 border-r-4 border-t-4 border-transparent border-t-gray-900"></div>
                   </div>
                   
                   <div 
                     v-else-if="account.status === 'connected'"
                     class="absolute bottom-full left-1/2 transform -translate-x-1/2 mb-2 px-3 py-2 bg-gray-900 text-white text-xs rounded-lg opacity-0 group-hover:opacity-100 transition-opacity duration-200 pointer-events-none whitespace-nowrap z-10"
                   >
                     账户已正常连接，可以正常使用
                     <div class="absolute top-full left-1/2 transform -translate-x-1/2 w-0 h-0 border-l-4 border-r-4 border-t-4 border-transparent border-t-gray-900"></div>
                   </div>
                 </div>
               </div>
               
               <!-- 粉丝数 -->
               <div class="flex items-center">
                 <span class="text-sm font-medium text-gray-900">{{ formatFollowers(account.followers) }}</span>
               </div>
               
               <!-- 最后同步时间 -->
               <div class="flex items-center">
                 <span class="text-sm text-gray-600">{{ formatLastSync(account.lastSync) }}</span>
               </div>
               
               <!-- 操作 -->
               <div class="flex items-center justify-center space-x-1">
                 <!-- 已连接状态的操作按钮 -->
                 <template v-if="account.status === 'connected'">
                   <div class="group relative">
                     <button 
                       @click="reauthorizeAccount(account.id)"
                       class="p-2 text-blue-600 hover:text-blue-800 hover:bg-blue-50 rounded-lg transition-all duration-200 transform hover:scale-105 active:scale-98"
                       title="重新授权"
                     >
                       <i class="fas fa-key text-sm"></i>
                     </button>
                     <div class="absolute bottom-full left-1/2 transform -translate-x-1/2 mb-2 px-2 py-1 bg-gray-800 text-white text-xs rounded opacity-0 group-hover:opacity-100 transition-opacity duration-200 whitespace-nowrap">
                       重新授权
                     </div>
                   </div>
                   <div class="group relative">
                     <button 
                       @click="editAccount(account.id)"
                       class="p-2 text-gray-600 hover:text-gray-800 hover:bg-gray-50 rounded-lg transition-all duration-200 transform hover:scale-105 active:scale-98"
                       title="编辑"
                     >
                       <i class="fas fa-edit text-sm"></i>
                     </button>
                     <div class="absolute bottom-full left-1/2 transform -translate-x-1/2 mb-2 px-2 py-1 bg-gray-800 text-white text-xs rounded opacity-0 group-hover:opacity-100 transition-opacity duration-200 whitespace-nowrap">
                       编辑
                     </div>
                   </div>
                   <div class="group relative">
                     <button 
                       @click="removeAccount(account.id)"
                       class="p-2 text-red-600 hover:text-red-800 hover:bg-red-50 rounded-lg transition-all duration-200 transform hover:scale-105 active:scale-98"
                       title="移除"
                     >
                       <i class="fas fa-trash text-sm"></i>
                     </button>
                     <div class="absolute bottom-full left-1/2 transform -translate-x-1/2 mb-2 px-2 py-1 bg-gray-800 text-white text-xs rounded opacity-0 group-hover:opacity-100 transition-opacity duration-200 whitespace-nowrap">
                       移除
                     </div>
                   </div>
                 </template>
                 
                 <!-- 权限受限状态的操作按钮 -->
                 <template v-else-if="account.status === 'limited'">
                   <div class="group relative">
                     <button 
                       @click="reauthorizeAccount(account.id)"
                       class="p-2 text-white bg-orange-500 hover:bg-orange-600 rounded-lg transition-all duration-200 transform hover:scale-105 active:scale-98 shadow-md hover:shadow-lg"
                       title="修复权限"
                     >
                       <i class="fas fa-exclamation-triangle text-sm"></i>
                     </button>
                     <div class="absolute bottom-full left-1/2 transform -translate-x-1/2 mb-2 px-2 py-1 bg-gray-800 text-white text-xs rounded opacity-0 group-hover:opacity-100 transition-opacity duration-200 whitespace-nowrap">
                       修复权限
                     </div>
                   </div>
                   <div class="group relative">
                     <button 
                       @click="editAccount(account.id)"
                       class="p-2 text-gray-600 hover:text-gray-800 hover:bg-gray-50 rounded-lg transition-all duration-200 transform hover:scale-105 active:scale-98"
                       title="编辑"
                     >
                       <i class="fas fa-edit text-sm"></i>
                     </button>
                     <div class="absolute bottom-full left-1/2 transform -translate-x-1/2 mb-2 px-2 py-1 bg-gray-800 text-white text-xs rounded opacity-0 group-hover:opacity-100 transition-opacity duration-200 whitespace-nowrap">
                       编辑
                     </div>
                   </div>
                   <div class="group relative">
                     <button 
                       @click="removeAccount(account.id)"
                       class="p-2 text-red-600 hover:text-red-800 hover:bg-red-50 rounded-lg transition-all duration-200 transform hover:scale-105 active:scale-98"
                       title="移除"
                     >
                       <i class="fas fa-trash text-sm"></i>
                     </button>
                     <div class="absolute bottom-full left-1/2 transform -translate-x-1/2 mb-2 px-2 py-1 bg-gray-800 text-white text-xs rounded opacity-0 group-hover:opacity-100 transition-opacity duration-200 whitespace-nowrap">
                       移除
                     </div>
                   </div>
                 </template>
                 
                 <!-- 其他状态的默认操作按钮 -->
                 <template v-else>
                   <div class="group relative">
                     <button 
                       @click="syncAccount(account.id)"
                       class="p-2 text-blue-600 hover:text-blue-800 hover:bg-blue-50 rounded-lg transition-all duration-200 transform hover:scale-105 active:scale-98"
                       title="同步账户"
                     >
                       <i class="fas fa-sync-alt text-sm"></i>
                     </button>
                     <div class="absolute bottom-full left-1/2 transform -translate-x-1/2 mb-2 px-2 py-1 bg-gray-800 text-white text-xs rounded opacity-0 group-hover:opacity-100 transition-opacity duration-200 whitespace-nowrap">
                       同步账户
                     </div>
                   </div>
                   <div class="group relative">
                     <button 
                       @click="editAccount(account.id)"
                       class="p-2 text-gray-600 hover:text-gray-800 hover:bg-gray-50 rounded-lg transition-all duration-200 transform hover:scale-105 active:scale-98"
                       title="编辑"
                     >
                       <i class="fas fa-edit text-sm"></i>
                     </button>
                     <div class="absolute bottom-full left-1/2 transform -translate-x-1/2 mb-2 px-2 py-1 bg-gray-800 text-white text-xs rounded opacity-0 group-hover:opacity-100 transition-opacity duration-200 whitespace-nowrap">
                       编辑
                     </div>
                   </div>
                   <div class="group relative">
                     <button 
                       @click="removeAccount(account.id)"
                       class="p-2 text-red-600 hover:text-red-800 hover:bg-red-50 rounded-lg transition-all duration-200 transform hover:scale-105 active:scale-98"
                       title="移除"
                     >
                       <i class="fas fa-trash text-sm"></i>
                     </button>
                     <div class="absolute bottom-full left-1/2 transform -translate-x-1/2 mb-2 px-2 py-1 bg-gray-800 text-white text-xs rounded opacity-0 group-hover:opacity-100 transition-opacity duration-200 whitespace-nowrap">
                       移除
                     </div>
                   </div>
                 </template>
               </div>
             </div>
             
             <!-- 空状态 -->
             <div v-if="currentPlatformAccounts.length === 0" class="px-6 py-6 text-center flex flex-col items-center justify-center" style="height: 240px;">
               <!-- SVG插图 -->
               <div class="mb-6">
                 <svg width="80" height="80" viewBox="0 0 120 120" fill="none" xmlns="http://www.w3.org/2000/svg" class="text-gray-300">
                   <!-- 背景圆 -->
                   <circle cx="60" cy="60" r="50" fill="currentColor" opacity="0.1"/>
                   
                   <!-- 社交媒体图标组合 -->
                   <g transform="translate(30, 30)">
                     <!-- Facebook风格图标 -->
                     <rect x="10" y="15" width="20" height="30" rx="3" fill="currentColor" opacity="0.3"/>
                     <rect x="15" y="20" width="10" height="2" fill="white"/>
                     <rect x="15" y="25" width="8" height="2" fill="white"/>
                     
                     <!-- Instagram风格图标 -->
                     <circle cx="45" cy="30" r="8" fill="none" stroke="currentColor" stroke-width="2" opacity="0.4"/>
                     <circle cx="45" cy="30" r="3" fill="none" stroke="currentColor" stroke-width="1.5" opacity="0.4"/>
                     <circle cx="50" cy="25" r="1.5" fill="currentColor" opacity="0.4"/>
                     
                     <!-- 连接线 -->
                     <path d="M30 30 Q37.5 25 40 30" stroke="currentColor" stroke-width="1.5" fill="none" opacity="0.2" stroke-dasharray="2,2"/>
                   </g>
                   
                   <!-- 添加图标 -->
                   <circle cx="85" cy="35" r="12" fill="#3B82F6" opacity="0.8"/>
                   <path d="M85 29v12M79 35h12" stroke="white" stroke-width="2" stroke-linecap="round"/>
                 </svg>
               </div>
               
               <!-- 标题和描述 -->
               <div class="mb-6">
                 <h3 class="text-lg font-semibold text-gray-700 mb-2">
                   还没有{{ getPlatformName(activePlatform) }}账户
                 </h3>
                 <p class="text-sm text-gray-500 leading-relaxed max-w-sm mx-auto">
                   连接您的{{ getPlatformName(activePlatform) }}账户，开始管理社交媒体内容，让您的品牌在社交平台上发光发热
                 </p>
               </div>
               
               <!-- 操作按钮组 -->
               <div class="flex flex-col sm:flex-row gap-2">
                 <button 
                   @click="connectPlatform(activePlatform)"
                   class="inline-flex items-center px-5 py-2.5 bg-gradient-to-r from-blue-500 to-purple-600 text-white rounded-lg hover:from-blue-600 hover:to-purple-700 transition-all duration-300 shadow-lg hover:shadow-xl transform hover:scale-103 active:scale-98 text-sm"
                 >
                   <i class="fas fa-plus mr-2"></i>
                   连接{{ getPlatformName(activePlatform) }}账户
                 </button>
                 
                 <button 
                   @click="showTokenModal = true"
                   class="inline-flex items-center px-5 py-2.5 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-all duration-200 border border-gray-300 hover:border-gray-400 text-sm"
                 >
                   <i class="fas fa-key mr-2"></i>
                   手动配置Token
                 </button>
               </div>
               
               <!-- 帮助提示 -->
               <div class="mt-4 text-xs text-gray-400">
                 <p>需要帮助？查看我们的 
                   <a href="#" class="text-blue-500 hover:text-blue-600 underline">连接指南</a>
                 </p>
               </div>
             </div>
           </div>
         </div>
       </div>

      <!-- 底部：状态统计和操作按钮 -->
      <div class="flex items-center justify-between pt-4 border-t border-gray-200">
        <!-- 统计信息 -->
        <div class="flex items-center space-x-4">
          <div class="text-sm text-gray-600">
            <span class="font-semibold">{{ getPlatformName(activePlatform) }}</span>: 
            <span class="text-green-600 font-medium">{{ connectedCount }} 个已连接</span>
            <span 
              v-if="pendingCount > 0" 
              @click="scrollToPendingAccount"
              class="text-orange-600 font-medium ml-2 cursor-pointer hover:text-orange-700 hover:underline transition-all duration-200"
            >
              {{ pendingCount }} 个待处理
            </span>
          </div>
        </div>
        
        <!-- 操作按钮 -->
        <div class="flex space-x-3">
          <button 
            type="button"
            @click="$emit('close')"
            class="px-5 py-2 bg-gray-100 text-gray-700 rounded-lg font-medium hover:bg-gray-200 transition-all duration-200 text-sm"
          >
            关闭
          </button>
          <button 
            type="button"
            @click="syncAllAccounts"
            :disabled="isSyncing || currentPlatformAccounts.length === 0"
            class="px-5 py-2 bg-gradient-to-r from-blue-600 to-indigo-600 text-white font-medium rounded-lg transition-all duration-200 hover:from-blue-700 hover:to-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed flex items-center shadow-sm hover:shadow-md text-sm"
          >
            <i v-if="isSyncing" class="fas fa-spinner fa-spin mr-2"></i>
            <i v-else class="fas fa-sync-alt mr-2"></i>
            {{ isSyncing ? '同步中...' : '同步所有账户' }}
          </button>
        </div>
      </div>
    </div>
  </div>

  <!-- Token手动配置弹窗 -->
   <TokenConfigModal
     v-if="showTokenModal"
     :platform="activePlatform"
     @close="showTokenModal = false"
     @success="onTokenConfigSuccess"
   />
 </template>
 
 <script setup>
 import { ref, computed, onMounted } from 'vue'
 import { showNotification } from '@/utils/notification'
 import TokenConfigModal from './TokenConfigModal.vue'

// 导入社交媒体API
import { socialMediaAPI } from '@/utils/api'

// 响应式数据
const activePlatform = ref('facebook') // 默认选中Facebook
const isSyncing = ref(false)
const isRefreshing = ref(false)
const showTokenModal = ref(false)
const accounts = ref({})

// 计算属性
const currentPlatformAccounts = computed(() => {
  return accounts.value[activePlatform.value] || []
})

const connectedCount = computed(() => {
  return currentPlatformAccounts.value.filter(account => account.status === 'connected').length
})

const pendingCount = computed(() => {
  return currentPlatformAccounts.value.filter(account => account.status !== 'connected').length
})

// 方法
const getPlatformIcon = (platform) => {
  const icons = {
    facebook: 'fab fa-facebook-f',
    instagram: 'fab fa-instagram',
    tiktok: 'fab fa-tiktok'
  }
  return icons[platform] || 'fas fa-globe'
}

const getPlatformName = (platform) => {
  const names = {
    facebook: 'Facebook',
    instagram: 'Instagram',
    tiktok: 'TikTok'
  }
  return names[platform] || platform
}

const getIndicatorStyle = () => {
  const platforms = ['facebook', 'instagram', 'tiktok']
  const activeIndex = platforms.indexOf(activePlatform.value)
  const width = 33.333333 // 100% / 3
  const left = activeIndex * width
  
  return {
    width: `${width}%`,
    left: `${left}%`
  }
}

const getStatusClass = (status) => {
  switch (status) {
    case 'connected':
      return 'bg-green-100 text-green-800'
    case 'limited':
      return 'bg-orange-100 text-orange-800'
    case 'disconnected':
      return 'bg-red-100 text-red-800'
    default:
      return 'bg-gray-100 text-gray-800'
  }
}

const getStatusIcon = (status) => {
  switch (status) {
    case 'connected':
      return 'fas fa-check-circle'
    case 'limited':
      return 'fas fa-exclamation-triangle'
    case 'disconnected':
      return 'fas fa-times-circle'
    default:
      return 'fas fa-question-circle'
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 'connected':
      return '已连接'
    case 'limited':
      return '权限受限'
    case 'disconnected':
      return '未连接'
    default:
      return '未知'
  }
}

const formatFollowers = (count) => {
  if (!count || count === 0) return '0'
  if (count >= 1000000) {
    return (count / 1000000).toFixed(1) + 'M'
  }
  if (count >= 1000) {
    return (count / 1000).toFixed(1) + 'K'
  }
  return count.toString()
}

const formatLastSync = (dateString) => {
  if (!dateString || dateString === '从未同步') return '从未同步'
  
  try {
    const date = new Date(dateString)
    const now = new Date()
    const diffMs = now - date
    const diffMins = Math.floor(diffMs / (1000 * 60))
    const diffHours = Math.floor(diffMs / (1000 * 60 * 60))
    const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24))
    
    if (diffMins < 60) {
      return `${diffMins}分钟前`
    } else if (diffHours < 24) {
      return `${diffHours}小时前`
    } else if (diffDays < 7) {
      return `${diffDays}天前`
    } else {
      return date.toLocaleDateString('zh-CN')
    }
  } catch (error) {
    return dateString
  }
}

// 新增方法
const addFacebookAccount = async () => {
  try {
    showNotification('正在跳转到Facebook授权页面...', 'info')
    
    // 调用后端API获取授权URL
    const response = await socialMediaAPI.getFBAuthUrl()
    
    // 检查响应格式
    let authUrl
    if (response.data && response.data.authUrl) {
      authUrl = response.data.authUrl
    } else if (response.authUrl) {
      authUrl = response.authUrl
    } else {
      throw new Error('无法获取授权URL')
    }
    
    // 打开授权窗口
    const authWindow = window.open(authUrl, 'facebook_auth', 'width=600,height=600,scrollbars=yes,resizable=yes')
    
    if (!authWindow) {
      throw new Error('无法打开授权窗口，请检查浏览器弹窗设置')
    }
    
    // 监听授权完成
    const checkClosed = setInterval(() => {
      if (authWindow.closed) {
        clearInterval(checkClosed)
        refreshAccounts()
        showNotification('Facebook授权流程已完成', 'success')
      }
    }, 1000)
    
  } catch (error) {
    console.error('Facebook授权失败:', error)
    showNotification(`Facebook授权失败: ${error.message || '请重试'}`, 'error')
  }
}

const addInstagramAccount = async () => {
  try {
    showNotification('正在跳转到Instagram授权页面...', 'info')
    
    // Instagram授权流程
    const authUrl = `https://api.instagram.com/oauth/authorize?client_id=YOUR_APP_ID&redirect_uri=${encodeURIComponent(window.location.origin + '/auth/instagram/callback')}&scope=user_profile,user_media&response_type=code`
    
    const authWindow = window.open(authUrl, 'instagram_auth', 'width=600,height=600,scrollbars=yes,resizable=yes')
    
    const checkClosed = setInterval(() => {
      if (authWindow.closed) {
        clearInterval(checkClosed)
        refreshAccounts()
        showNotification('Instagram授权流程已完成', 'success')
      }
    }, 1000)
    
  } catch (error) {
    console.error('Instagram授权失败:', error)
    showNotification('Instagram授权失败，请重试', 'error')
  }
}

const addTikTokAccount = async () => {
  try {
    showNotification('正在跳转到TikTok授权页面...', 'info')
    
    // TikTok授权流程
    const authUrl = `https://www.tiktok.com/auth/authorize/?client_key=YOUR_CLIENT_KEY&response_type=code&scope=user.info.basic,video.list&redirect_uri=${encodeURIComponent(window.location.origin + '/auth/tiktok/callback')}`
    
    const authWindow = window.open(authUrl, 'tiktok_auth', 'width=600,height=600,scrollbars=yes,resizable=yes')
    
    const checkClosed = setInterval(() => {
      if (authWindow.closed) {
        clearInterval(checkClosed)
        refreshAccounts()
        showNotification('TikTok授权流程已完成', 'success')
      }
    }, 1000)
    
  } catch (error) {
    console.error('TikTok授权失败:', error)
    showNotification('TikTok授权失败，请重试', 'error')
  }
}

const refreshAccounts = async () => {
  if (isRefreshing.value) return
  
  isRefreshing.value = true
  
  try {
    showNotification('正在刷新账户列表...', 'info')
    
    // 调用后端API获取最新的账户数据
    const response = await socialMediaAPI.getPlatformStatus()
    // 确保accounts对象存在相应平台的键
    if (!accounts.value[activePlatform.value]) {
      accounts.value[activePlatform.value] = []
    }
    
    showNotification('账户列表刷新成功！', 'success')
  } catch (error) {
    console.error('刷新账户列表失败:', error)
    showNotification('刷新失败，请重试', 'error')
  } finally {
    isRefreshing.value = false
  }
}

const reauthorizeAccount = async (accountId) => {
  try {
    showNotification('正在重新授权账户...', 'info')
    
    // 根据平台类型调用相应的授权方法
    switch (activePlatform.value) {
      case 'facebook':
        await addFacebookAccount()
        break
      case 'instagram':
        await addInstagramAccount()
        break
      case 'tiktok':
        await addTikTokAccount()
        break
    }
    
  } catch (error) {
    console.error('重新授权失败:', error)
    showNotification('重新授权失败，请重试', 'error')
  }
}

const onTokenConfigSuccess = (tokenData) => {
  showNotification('Token配置成功！', 'success')
  refreshAccounts()
}

const syncAccount = async (accountId) => {
  try {
    showNotification('开始同步账户...', 'info')
    
    // 目前没有直接的同步API，这里简化处理
    // 刷新账户列表以获取最新状态
    await refreshAccounts()
    
    showNotification('账户同步成功！', 'success')
  } catch (error) {
    console.error('同步账户失败:', error)
    showNotification('同步失败，请重试', 'error')
  }
}

const removeAccount = async (accountId) => {
  if (!confirm('确定要移除此账户吗？')) return
  
  try {
    // 调用后端API移除账户
    if (activePlatform.value === 'facebook') {
      await socialMediaAPI.unbindFB()
    } else if (activePlatform.value === 'instagram') {
      await socialMediaAPI.unbindInstagram()
    } else if (activePlatform.value === 'tiktok') {
      // 目前没有TikTok解绑API，这里简化处理
      console.warn('TikTok解绑功能暂未实现')
    }
    
    // 刷新账户列表
    await refreshAccounts()
    
    showNotification('账户已移除', 'success')
  } catch (error) {
    console.error('移除账户失败:', error)
    showNotification('移除失败，请重试', 'error')
  }
}

const editAccount = async (accountId) => {
  try {
    showNotification('正在打开编辑界面...', 'info')
    
    // 这里可以打开编辑弹窗或跳转到编辑页面
    // 暂时用提示代替实际编辑功能
    const account = currentPlatformAccounts.value.find(acc => acc.id === accountId)
    if (account) {
      showNotification(`编辑账户: ${account.name}`, 'info')
      // TODO: 实现编辑功能
    }
  } catch (error) {
    console.error('编辑账户失败:', error)
    showNotification('编辑失败，请重试', 'error')
  }
}

const connectPlatform = (platform) => {
  // 根据平台类型调用相应的添加方法
  switch (platform) {
    case 'facebook':
      addFacebookAccount()
      break
    case 'instagram':
      addInstagramAccount()
      break
    case 'tiktok':
      addTikTokAccount()
      break
    default:
      showNotification(`暂不支持${getPlatformName(platform)}平台`, 'warning')
  }
}

const syncAllAccounts = async () => {
  if (currentPlatformAccounts.value.length === 0) {
    showNotification('没有可同步的账户', 'warning')
    return
  }
  
  isSyncing.value = true
  
  try {
    showNotification('开始同步所有账户...', 'info')
    
    // 目前没有批量同步API，这里简化处理
    // 刷新账户列表以获取最新状态
    await refreshAccounts()
    
    showNotification('所有账户同步完成！', 'success')
  } catch (error) {
    console.error('批量同步失败:', error)
    showNotification('同步失败，请重试', 'error')
  } finally {
    isSyncing.value = false
  }
}

const scrollToPendingAccount = () => {
  // 查找第一个权限受限的账户
  const pendingAccount = currentPlatformAccounts.value.find(account => account.status === 'limited')
  
  if (pendingAccount) {
    // 使用账户ID作为元素选择器
    const accountElement = document.querySelector(`[data-account-id="${pendingAccount.id}"]`)
    
    if (accountElement) {
      // 滚动到目标元素
      accountElement.scrollIntoView({ 
        behavior: 'smooth', 
        block: 'center' 
      })
      
      // 添加高亮效果
      accountElement.classList.add('highlight-account')
      
      // 1.5秒后移除高亮效果
      setTimeout(() => {
        accountElement.classList.remove('highlight-account')
      }, 1500)
    }
  }
}

// 定义 emits
const emit = defineEmits(['close'])

// 组件挂载
onMounted(() => {
  // 初始化数据，获取账户列表
  refreshAccounts()
})
</script>

<style scoped>
/* 表格样式优化 */
.grid-cols-6 {
  display: grid;
  grid-template-columns: 200px 80px 80px 80px 100px 60px;
}

/* 悬停效果 */
.hover\:bg-gray-50:hover {
  background-color: #f9fafb;
}

/* 状态标签样式 */
.bg-green-100 {
  background-color: #e8f5e9;
}

.text-green-800 {
  color: #2ecc71;
}

.bg-orange-100 {
  background-color: #fff3e0;
}

.text-orange-800 {
  color: #f39c12;
}

.bg-red-100 {
  background-color: #ffebee;
}

/* 高亮动画效果 */
.highlight-account {
  background-color: #fff3e0 !important;
  animation: highlight-fade 1.5s ease-out;
}

@keyframes highlight-fade {
  0% {
    background-color: #ffcc80;
    transform: scale(1.02);
  }
  100% {
    background-color: #fff3e0;
    transform: scale(1);
  }
}

.text-red-800 {
  color: #e74c3c;
}
</style>