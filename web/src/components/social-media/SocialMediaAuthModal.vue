<template>
  <!-- 弹窗遮罩 -->
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50" @click="$emit('close')">
    <!-- 弹窗主体 -->
    <div class="bg-white rounded-xl shadow-2xl relative flex flex-col" style="width: 1000px; max-height: 90vh; box-shadow: 0 20px 60px rgba(0,0,0,0.15); border-radius: 16px;" @click.stop>
      <!-- 关闭按钮 -->
      <button 
        @click="$emit('close')"
        class="absolute top-6 right-6 text-gray-400 hover:text-gray-600 transition-colors duration-200 z-20"
      >
        <i class="fas fa-times text-xl"></i>
      </button>

      <!-- ========== 顶部操作区 ========== -->
      <div class="px-8 pt-8 pb-6 border-b border-gray-100">
        <!-- 弹窗标题 -->
        <div class="mb-6">
          <h2 class="text-2xl font-bold text-gray-800 mb-2">社交媒体授权管理</h2>
          <p class="text-gray-600 text-sm">管理您的Facebook、Instagram和TikTok账户授权，确保内容发布正常运行</p>
        </div>

        <!-- 平台标签栏与操作按钮 -->
        <div class="flex justify-between items-center">
          <!-- 左侧：平台切换标签 -->
          <div class="relative flex space-x-1 bg-gray-100 p-1 rounded-lg">
            <!-- 滑动指示器 -->
            <div 
              class="absolute top-1 bottom-1 bg-gradient-to-r from-blue-500 to-purple-600 rounded-md transition-all duration-300 ease-out shadow-lg"
              :style="getIndicatorStyle()"
            >
              <!-- 选中态下划线 -->
              <div class="absolute bottom-0 left-2 right-2 h-0.5 bg-white rounded-full"></div>
            </div>
            
            <button
              v-for="(platform, index) in ['facebook', 'instagram', 'tiktok']"
              :key="platform"
              @click="activePlatform = platform"
              :class="[
                'relative z-10 py-3 px-6 rounded-md font-semibold transition-all duration-300',
                'min-h-[48px] flex items-center justify-center min-w-[120px]',
                activePlatform === platform
                  ? 'text-white'
                  : 'text-gray-600 hover:text-gray-800 hover:bg-gray-50'
              ]"
            >
              <i :class="getPlatformIcon(platform)" class="mr-2"></i>
              {{ getPlatformName(platform) }}
            </button>
          </div>

          <!-- 右侧：操作按钮组 -->
          <div class="flex items-center space-x-3">
            <!-- 主要操作：添加账号 -->
            <button
              v-if="activePlatform === 'facebook'"
              @click="addFacebookAccount"
              class="bg-gradient-to-r from-blue-500 to-purple-600 hover:from-blue-600 hover:to-purple-700 text-white px-6 py-3 rounded-lg font-semibold transition-all duration-300 transform hover:scale-105 hover:shadow-lg active:scale-98 shadow-md"
            >
              <i class="fab fa-facebook-f mr-2"></i>
              添加{{ getPlatformName(activePlatform) }}账号
            </button>
            
            <button
              v-else-if="activePlatform === 'instagram'"
              @click="addInstagramAccount"
              class="bg-gradient-to-r from-blue-500 to-purple-600 hover:from-blue-600 hover:to-purple-700 text-white px-6 py-3 rounded-lg font-semibold transition-all duration-300 transform hover:scale-105 hover:shadow-lg active:scale-98 shadow-md"
            >
              <i class="fab fa-instagram mr-2"></i>
              添加{{ getPlatformName(activePlatform) }}账号
            </button>
            
            <button
              v-else-if="activePlatform === 'tiktok'"
              @click="connectPlatform('tiktok')"
              class="bg-gradient-to-r from-blue-500 to-purple-600 hover:from-blue-600 hover:to-purple-700 text-white px-6 py-3 rounded-lg font-semibold transition-all duration-300 transform hover:scale-105 hover:shadow-lg active:scale-98 shadow-md"
            >
              <i class="fab fa-tiktok mr-2"></i>
              添加{{ getPlatformName(activePlatform) }}账号
            </button>
            
            <!-- 辅助操作：手动配置Token -->
            <button
              @click="showTokenModal = true"
              class="bg-green-500 hover:bg-green-600 text-white px-5 py-3 rounded-lg font-medium transition-all duration-300 transform hover:scale-105 hover:shadow-md active:scale-98"
            >
              <i class="fas fa-key mr-2"></i>
              手动配置Token
            </button>
            
            <!-- 次要操作：刷新列表 -->
            <button
              @click="refreshAccounts"
              :disabled="isRefreshing"
              class="bg-gray-100 hover:bg-gray-200 text-gray-700 px-4 py-3 rounded-lg font-medium transition-all duration-300 transform hover:scale-105 hover:shadow-sm active:scale-98 border border-gray-300 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <i 
                :class="isRefreshing ? 'fas fa-spinner fa-spin' : 'fas fa-sync-alt'" 
                class="mr-1"
              ></i>
              {{ isRefreshing ? '刷新中' : '刷新' }}
            </button>
          </div>
        </div>
      </div>

      <!-- ========== 中间引导/列表区 ========== -->
      <div class="flex-1 px-8 py-6 overflow-hidden">
        <div class="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden h-full flex flex-col" style="min-height: 400px;">
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
           <div class="flex-1 overflow-y-auto custom-scrollbar">
             
             <!-- 空状态显示 -->
             <div v-if="currentPlatformAccounts.length === 0" class="h-full flex items-center justify-center p-8 fade-in-up">
               <div class="text-center max-w-md mx-auto">
                 <!-- 动态插图 -->
                 <div class="mb-8 relative">
                   <div class="w-24 h-24 mx-auto mb-4 relative">
                     <!-- 平台图标背景 -->
                     <div class="w-full h-full rounded-full bg-gradient-to-br from-blue-50 to-purple-50 flex items-center justify-center shadow-lg breathe-animation">
                       <i :class="getPlatformIcon(activePlatform)" :style="{ color: getPlatformColor(activePlatform) }" class="text-4xl"></i>
                     </div>
                     <!-- 连接动效元素 -->
                     <div class="absolute -top-2 -right-2 w-8 h-8 bg-green-500 rounded-full flex items-center justify-center shadow-md animate-bounce">
                       <i class="fas fa-link text-white text-sm"></i>
                     </div>
                   </div>
                 </div>
                 
                 <!-- 文字内容 -->
                 <div class="mb-8">
                   <h3 class="text-subtitle mb-3">
                     还没有 {{ getPlatformName(activePlatform) }} 账户
                   </h3>
                   <p class="text-body mb-6 leading-relaxed">
                     连接您的 {{ getPlatformName(activePlatform) }} 账户，开始管理社交媒体内容，<br>
                     自动发布、数据分析、粉丝互动一站式解决
                   </p>
                   
                   <!-- 连接方式说明 -->
                   <div class="bg-gradient-to-r from-blue-50 to-purple-50 rounded-xl p-6 mb-6 text-left">
                     <h4 class="text-subtitle mb-4 flex items-center">
                       <i class="fas fa-info-circle text-blue-500 mr-2"></i>
                       两种连接方式
                     </h4>
                     <div class="space-y-3">
                       <div class="flex items-start">
                         <div class="w-6 h-6 bg-blue-500 text-white rounded-full flex items-center justify-center text-xs font-bold mr-3 mt-0.5 flex-shrink-0">1</div>
                         <div>
                           <div class="flex items-center mb-1">
                             <i class="fas fa-shield-alt text-blue-500 mr-2 text-sm"></i>
                             <span class="text-body font-medium">OAuth 自动授权</span>
                             <span class="ml-2 px-2 py-0.5 bg-green-100 text-green-700 text-xs rounded-full">推荐</span>
                           </div>
                           <p class="text-caption">安全便捷，一键完成授权连接</p>
                         </div>
                       </div>
                       <div class="flex items-start">
                         <div class="w-6 h-6 bg-green-500 text-white rounded-full flex items-center justify-center text-xs font-bold mr-3 mt-0.5 flex-shrink-0">2</div>
                         <div>
                           <div class="flex items-center mb-1">
                             <i class="fas fa-key text-green-500 mr-2 text-sm"></i>
                             <span class="text-body font-medium">手动配置 Token</span>
                           </div>
                           <p class="text-caption">适用于企业账户或特殊权限需求</p>
                         </div>
                       </div>
                     </div>
                   </div>
                 </div>
                 
                 <!-- 操作按钮组 -->
                 <div class="flex flex-col sm:flex-row gap-3 justify-center">
                   <button 
                     @click="connectAccount"
                     class="btn-primary px-6 py-3 text-sm font-medium hover:scale-105 transition-all duration-300 shadow-lg hover:shadow-xl flex items-center justify-center"
                   >
                     <i class="fas fa-plug mr-2"></i>
                     自动连接 {{ getPlatformName(activePlatform) }}
                   </button>
                   <button 
                     @click="showTokenConfig = true"
                     class="btn-secondary px-6 py-3 text-sm font-medium hover:scale-105 transition-all duration-300 flex items-center justify-center"
                   >
                     <i class="fas fa-key mr-2"></i>
                     手动配置 Token
                   </button>
                 </div>
                 
                 <!-- 帮助提示 -->
                 <div class="mt-8 pt-6 border-t border-gray-200">
                   <div class="flex items-center justify-center space-x-6 text-caption">
                     <a href="#" class="hover:text-blue-500 transition-colors duration-200 flex items-center hover:underline">
                       <i class="fas fa-question-circle mr-1"></i>
                       连接帮助
                     </a>
                     <a href="#" class="hover:text-blue-500 transition-colors duration-200 flex items-center hover:underline">
                       <i class="fas fa-book mr-1"></i>
                       使用教程
                     </a>
                     <a href="#" class="hover:text-blue-500 transition-colors duration-200 flex items-center hover:underline">
                       <i class="fas fa-headset mr-1"></i>
                       联系客服
                     </a>
                   </div>
                 </div>
               </div>
             </div>
             
             <!-- 表格内容 -->
             <div v-else class="divide-y divide-gray-100 fade-in-up">
              <div 
                v-for="account in currentPlatformAccounts" 
                :key="account.id"
                :data-account-id="account.id"
                class="grid grid-cols-7 gap-4 px-6 py-4 hover:bg-gray-50 transition-all duration-200 hover:shadow-sm group"
              >
                <!-- 账户信息 -->
                <div class="col-span-2 flex items-center">
                  <div class="w-12 h-12 rounded-full bg-gradient-to-br from-gray-100 to-gray-200 flex items-center justify-center mr-4 overflow-hidden shadow-md hover:shadow-lg transition-all duration-300 ring-2 ring-white">
                    <img 
                      v-if="account.avatar"
                      :src="account.avatar"
                      :alt="account.name"
                      class="w-full h-full object-cover hover:scale-110 transition-transform duration-300"
                    >
                    <i 
                      v-else
                      :class="getPlatformIcon(activePlatform)"
                      :style="{ color: getPlatformColor(activePlatform) }"
                      class="text-lg opacity-80"
                    ></i>
                  </div>
                  <div class="min-w-0 flex-1">
                    <p class="text-body font-semibold truncate">{{ account.name }}</p>
                    <p class="text-caption truncate">{{ account.username || account.id }}</p>
                  </div>
                </div>
                
                <!-- 类型 -->
                <div class="flex items-center">
                  <span class="px-3 py-1.5 text-xs font-medium bg-gradient-to-r from-blue-50 to-blue-100 text-blue-700 rounded-full border border-blue-200 shadow-sm hover:shadow-md transition-all duration-200">
                    {{ account.type || '个人' }}
                  </span>
                </div>
                
                <!-- 状态 -->
                <div class="flex items-center">
                  <div class="group/status relative">
                    <span 
                      class="px-3 py-1.5 text-xs font-semibold rounded-full flex items-center cursor-help transition-all duration-200 hover:shadow-md hover:scale-105"
                      :class="getStatusClass(account.status)"
                    >
                      <i :class="getStatusIcon(account.status)" class="mr-1.5 text-xs"></i>
                      {{ getStatusText(account.status) }}
                    </span>
                    
                    <!-- Tooltip -->
                    <div 
                      v-if="account.status === 'limited'"
                      class="absolute bottom-full left-1/2 transform -translate-x-1/2 mb-3 px-3 py-2 bg-gray-900 text-white text-xs rounded-lg opacity-0 group-hover/status:opacity-100 transition-all duration-300 pointer-events-none whitespace-nowrap z-30 shadow-xl"
                    >
                      缺少必要权限，点击"修复权限"重新授权
                      <div class="absolute top-full left-1/2 transform -translate-x-1/2 w-0 h-0 border-l-4 border-r-4 border-t-4 border-transparent border-t-gray-900"></div>
                    </div>
                    
                    <div 
                      v-else-if="account.status === 'connected'"
                      class="absolute bottom-full left-1/2 transform -translate-x-1/2 mb-3 px-3 py-2 bg-gray-900 text-white text-xs rounded-lg opacity-0 group-hover/status:opacity-100 transition-all duration-300 pointer-events-none whitespace-nowrap z-30 shadow-xl"
                    >
                      账户已正常连接，可以正常使用
                      <div class="absolute top-full left-1/2 transform -translate-x-1/2 w-0 h-0 border-l-4 border-r-4 border-t-4 border-transparent border-t-gray-900"></div>
                    </div>
                  </div>
                </div>
                
                <!-- 粉丝数 -->
                <div class="flex items-center">
                  <div class="flex items-center space-x-1">
                    <i class="fas fa-users text-gray-400 text-xs"></i>
                    <span class="text-body font-medium">{{ formatFollowers(account.followers) }}</span>
                  </div>
                </div>
                
                <!-- 最后同步时间 -->
                <div class="flex items-center">
                  <div class="flex items-center space-x-1">
                    <i class="fas fa-clock text-gray-400 text-xs"></i>
                    <span class="text-caption">{{ formatLastSync(account.lastSync) }}</span>
                  </div>
                </div>
                
                <!-- 操作 -->
                <div class="flex items-center justify-center space-x-1">
                  <!-- 已连接状态的操作按钮 -->
                  <template v-if="account.status === 'connected'">
                    <div class="group/btn relative opacity-60 group-hover:opacity-100 transition-opacity duration-300">
                      <button 
                        @click="reauthorizeAccount(account.id)"
                        class="p-2 text-blue-600 hover:text-blue-800 hover:bg-blue-50 rounded-lg transition-all duration-200 transform hover:scale-110 active:scale-95 hover:shadow-md"
                        title="重新授权"
                      >
                        <i class="fas fa-key text-sm"></i>
                      </button>
                      <div class="absolute bottom-full left-1/2 transform -translate-x-1/2 mb-2 px-2 py-1 bg-gray-800 text-white text-xs rounded opacity-0 group-hover/btn:opacity-100 transition-opacity duration-200 whitespace-nowrap z-20">
                        重新授权
                      </div>
                    </div>
                    <div class="group/btn relative opacity-60 group-hover:opacity-100 transition-opacity duration-300">
                      <button 
                        @click="editAccount(account.id)"
                        class="p-2 text-gray-600 hover:text-gray-800 hover:bg-gray-50 rounded-lg transition-all duration-200 transform hover:scale-110 active:scale-95 hover:shadow-md"
                        title="编辑"
                      >
                        <i class="fas fa-edit text-sm"></i>
                      </button>
                      <div class="absolute bottom-full left-1/2 transform -translate-x-1/2 mb-2 px-2 py-1 bg-gray-800 text-white text-xs rounded opacity-0 group-hover/btn:opacity-100 transition-opacity duration-200 whitespace-nowrap z-20">
                        编辑
                      </div>
                    </div>
                    <div class="group/btn relative opacity-60 group-hover:opacity-100 transition-opacity duration-300">
                      <button 
                        @click="removeAccount(account.id)"
                        class="p-2 text-red-600 hover:text-red-800 hover:bg-red-50 rounded-lg transition-all duration-200 transform hover:scale-110 active:scale-95 hover:shadow-md"
                        title="移除"
                      >
                        <i class="fas fa-trash text-sm"></i>
                      </button>
                      <div class="absolute bottom-full left-1/2 transform -translate-x-1/2 mb-2 px-2 py-1 bg-gray-800 text-white text-xs rounded opacity-0 group-hover/btn:opacity-100 transition-opacity duration-200 whitespace-nowrap z-20">
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
            </div>
             
             <!-- 空状态 -->
             <div v-if="currentPlatformAccounts.length === 0" class="px-6 py-16 text-center flex flex-col items-center justify-center" style="height: 400px;">
               <!-- SVG插图 -->
               <div class="mb-8">
                 <svg width="120" height="120" viewBox="0 0 120 120" fill="none" xmlns="http://www.w3.org/2000/svg" class="text-gray-300">
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
               <div class="mb-8">
                 <h3 class="text-xl font-semibold text-gray-700 mb-3">
                   还没有{{ getPlatformName(activePlatform) }}账户
                 </h3>
                 <p class="text-gray-500 leading-relaxed max-w-md mx-auto mb-4">
                   连接您的{{ getPlatformName(activePlatform) }}账户，开始管理社交媒体内容，
                   <br>让您的品牌在社交平台上发光发热
                 </p>
                 
                 <!-- 连接方式说明 -->
                 <div class="bg-gray-50 rounded-lg p-4 max-w-lg mx-auto mb-6">
                   <h4 class="text-sm font-medium text-gray-800 mb-3 flex items-center">
                     <i class="fas fa-lightbulb text-yellow-500 mr-2"></i>
                     两种连接方式
                   </h4>
                   <div class="space-y-3 text-sm text-gray-600">
                     <div class="flex items-start">
                       <div class="flex-shrink-0 w-6 h-6 bg-blue-100 rounded-full flex items-center justify-center mr-3 mt-0.5">
                         <span class="text-blue-600 font-semibold text-xs">1</span>
                       </div>
                       <div>
                         <p class="font-medium text-gray-700">自动授权（推荐）</p>
                         <p class="text-xs">点击"连接账户"按钮，系统将引导您完成OAuth授权流程</p>
                       </div>
                     </div>
                     <div class="flex items-start">
                       <div class="flex-shrink-0 w-6 h-6 bg-gray-100 rounded-full flex items-center justify-center mr-3 mt-0.5">
                         <span class="text-gray-600 font-semibold text-xs">2</span>
                       </div>
                       <div>
                         <p class="font-medium text-gray-700">手动配置Token</p>
                         <p class="text-xs">如果自动授权失败，可以手动输入访问令牌</p>
                       </div>
                     </div>
                   </div>
                 </div>
               </div>
               
               <!-- 操作按钮组 -->
               <div class="flex flex-col sm:flex-row gap-3">
                 <button 
                   @click="connectPlatform(activePlatform)"
                   class="inline-flex items-center px-6 py-3 bg-gradient-to-r from-blue-500 to-purple-600 text-white rounded-lg hover:from-blue-600 hover:to-purple-700 transition-all duration-300 shadow-lg hover:shadow-xl transform hover:scale-103 active:scale-98"
                 >
                   <i class="fas fa-link mr-2"></i>
                   自动连接{{ getPlatformName(activePlatform) }}
                 </button>
                 
                 <button 
                   @click="showTokenModal = true"
                   class="inline-flex items-center px-6 py-3 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-all duration-200 border border-gray-300 hover:border-gray-400"
                 >
                   <i class="fas fa-key mr-2"></i>
                   手动配置Token
                 </button>
               </div>
               
               <!-- 帮助提示 -->
               <div class="mt-8 space-y-2">
                 <div class="text-xs text-gray-400">
                   <p>连接遇到问题？</p>
                 </div>
                 <div class="flex flex-wrap justify-center gap-4 text-xs">
                   <a href="#" class="text-blue-500 hover:text-blue-600 underline flex items-center">
                     <i class="fas fa-book mr-1"></i>
                     查看连接指南
                   </a>
                   <a href="#" class="text-blue-500 hover:text-blue-600 underline flex items-center">
                     <i class="fas fa-question-circle mr-1"></i>
                     常见问题
                   </a>
                   <a href="#" class="text-blue-500 hover:text-blue-600 underline flex items-center">
                     <i class="fas fa-headset mr-1"></i>
                     联系客服
                   </a>
                 </div>
               </div>
             </div>
           </div>
         </div>
       </div>

      <!-- ========== 底部状态区 ========== -->
      <div class="px-8 py-6 bg-gradient-to-r from-gray-50 to-gray-100 border-t border-gray-200">
        <div class="flex items-center justify-between">
          <!-- 左侧：平台连接状态 -->
          <div class="flex items-center space-x-6">
            <div class="flex items-center">
              <i :class="getPlatformIcon(activePlatform)" class="text-lg mr-2" :style="{ color: getPlatformColor(activePlatform) }"></i>
              <span class="text-sm font-medium text-gray-700">{{ getPlatformName(activePlatform) }}:</span>
              <span 
                class="ml-2 text-sm font-semibold transition-colors duration-200"
                :class="connectedCount > 0 ? getPlatformColor(activePlatform) : 'text-gray-400'"
              >
                {{ connectedCount }} 个已连接
              </span>
            </div>
            
            <!-- 总体状态指示 -->
            <div class="flex items-center text-xs text-gray-500">
              <div class="w-2 h-2 rounded-full mr-2" :class="connectedCount > 0 ? 'bg-green-400' : 'bg-gray-300'"></div>
              {{ connectedCount > 0 ? '运行正常' : '等待连接' }}
            </div>
          </div>
          
          <!-- 右侧：操作按钮组 -->
          <div class="flex items-center space-x-3">
            <button 
              @click="closeModal"
              class="px-5 py-2.5 text-gray-600 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 hover:border-gray-400 transition-all duration-200 font-medium text-sm"
            >
              关闭
            </button>
            <button 
              @click="syncAllAccounts"
              :disabled="isSyncing || connectedCount === 0"
              class="px-6 py-2.5 bg-gradient-to-r from-blue-500 to-purple-600 text-white rounded-lg hover:from-blue-600 hover:to-purple-700 disabled:from-gray-300 disabled:to-gray-400 disabled:cursor-not-allowed transition-all duration-200 font-medium text-sm flex items-center min-w-[140px] justify-center"
            >
              <i 
                :class="isSyncing ? 'fas fa-spinner fa-spin' : 'fas fa-sync-alt'" 
                class="mr-2 text-sm"
              ></i>
              {{ isSyncing ? '同步中...' : '同步所有账户' }}
            </button>
          </div>
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

const getPlatformColor = (platform) => {
  const colors = {
    facebook: 'text-blue-600',
    instagram: 'text-pink-600',
    tiktok: 'text-gray-800'
  }
  return colors[platform] || 'text-gray-600'
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

const closeModal = () => {
  emit('close')
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
/* ========== 色彩系统 ========== */
:root {
  /* 品牌主色 - 蓝紫渐变 */
  --primary-gradient: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
  --primary-blue: #3b82f6;
  --primary-purple: #8b5cf6;
  --primary-hover: linear-gradient(135deg, #2563eb 0%, #7c3aed 100%);
  
  /* 辅助色 - 绿色 */
  --secondary-green: #10b981;
  --secondary-green-hover: #059669;
  
  /* 中性色系 */
  --neutral-gray-50: #f9fafb;
  --neutral-gray-100: #f3f4f6;
  --neutral-gray-200: #e5e7eb;
  --neutral-gray-300: #d1d5db;
  --neutral-gray-400: #9ca3af;
  --neutral-gray-500: #6b7280;
  --neutral-gray-600: #4b5563;
  --neutral-gray-700: #374151;
  --neutral-gray-800: #1f2937;
  
  /* 状态色 */
  --success-green: #10b981;
  --warning-yellow: #f59e0b;
  --error-red: #ef4444;
  
  /* 阴影系统 */
  --shadow-sm: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  --shadow-md: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  --shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
  --shadow-xl: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

/* ========== 表格样式 ========== */
.table-container {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: var(--shadow-sm);
}

.table-container table {
  width: 100%;
  border-collapse: collapse;
}

.table-container th {
  background: var(--neutral-gray-50);
  color: var(--neutral-gray-700);
  font-weight: 600;
  font-size: 14px;
  padding: 16px;
  text-align: left;
  border-bottom: 1px solid var(--neutral-gray-200);
}

.table-container td {
  padding: 16px;
  border-bottom: 1px solid var(--neutral-gray-100);
  vertical-align: middle;
}

.table-container tr:hover {
  background-color: var(--neutral-gray-50);
  transition: background-color 0.2s ease;
}

.table-container tr:last-child td {
  border-bottom: none;
}

/* ========== 状态标签样式 ========== */
.status-badge {
  display: inline-flex;
  align-items: center;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.status-badge.connected {
  background: linear-gradient(135deg, #dcfdf4 0%, #a7f3d0 100%);
  color: var(--success-green);
  border: 1px solid #a7f3d0;
}

.status-badge.limited {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  color: var(--warning-yellow);
  border: 1px solid #fde68a;
}

.status-badge.error {
  background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
  color: var(--error-red);
  border: 1px solid #fecaca;
}

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

/* ========== 高亮动画 ========== */
@keyframes highlight {
  0% {
    background-color: #fff7ed;
    transform: scale(1);
  }
  50% {
    background-color: #fed7aa;
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

/* ========== 滚动条优化 ========== */
.custom-scrollbar {
  scrollbar-width: thin;
  scrollbar-color: var(--neutral-gray-300) transparent;
}

.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background: var(--neutral-gray-300);
  border-radius: 3px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.custom-scrollbar:hover::-webkit-scrollbar-thumb {
  opacity: 1;
}

.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: var(--neutral-gray-400);
}

/* ========== 按钮交互优化 ========== */
.btn-primary {
  background: var(--primary-gradient);
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.btn-primary:hover {
  background: var(--primary-hover);
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

.btn-primary:active {
  transform: translateY(0);
  box-shadow: var(--shadow-md);
}

.btn-secondary {
  background: var(--secondary-green);
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.btn-secondary:hover {
  background: var(--secondary-green-hover);
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.btn-neutral {
  background: var(--neutral-gray-100);
  color: var(--neutral-gray-600);
  border: 1px solid var(--neutral-gray-200);
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.btn-neutral:hover {
  background: var(--neutral-gray-200);
  color: var(--neutral-gray-800);
  transform: translateY(-1px);
  box-shadow: var(--shadow-sm);
}

/* ========== 微动画效果 ========== */
@keyframes breathe {
  0%, 100% {
    transform: scale(1);
    opacity: 0.8;
  }
  50% {
    transform: scale(1.05);
    opacity: 1;
  }
}

.breathe-animation {
  animation: breathe 3s ease-in-out infinite;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.fade-in-up {
  animation: fadeInUp 0.6s ease-out;
}

/* ========== 字体层级系统 ========== */
.text-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--neutral-gray-800);
  line-height: 1.4;
}

.text-subtitle {
  font-size: 16px;
  font-weight: 600;
  color: var(--neutral-gray-700);
  line-height: 1.5;
}

.text-body {
  font-size: 14px;
  font-weight: 400;
  color: var(--neutral-gray-600);
  line-height: 1.6;
}

.text-caption {
  font-size: 12px;
  font-weight: 400;
  color: var(--neutral-gray-500);
  line-height: 1.5;
}

/* ========== 间距系统 ========== */
.spacing-xs { margin: 4px; }
.spacing-sm { margin: 8px; }
.spacing-md { margin: 16px; }
.spacing-lg { margin: 24px; }
.spacing-xl { margin: 32px; }

.padding-xs { padding: 4px; }
.padding-sm { padding: 8px; }
.padding-md { padding: 16px; }
.padding-lg { padding: 24px; }
.padding-xl { padding: 32px; }

/* ========== 自定义滚动条 ========== */
.custom-scrollbar {
  scrollbar-width: thin;
  scrollbar-color: transparent transparent;
}

.custom-scrollbar:hover {
  scrollbar-color: #cbd5e1 transparent;
}

.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background-color: transparent;
  border-radius: 3px;
  transition: background-color 0.2s ease;
}

.custom-scrollbar:hover::-webkit-scrollbar-thumb {
  background-color: #cbd5e1;
}

.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background-color: #94a3b8;
}

/* ========== 呼吸动画 ========== */
.breathe-animation {
  animation: breathe 3s ease-in-out infinite;
}

@keyframes breathe {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}
</style>