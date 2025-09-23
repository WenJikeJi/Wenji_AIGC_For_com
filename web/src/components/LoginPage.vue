<template>
  <div class="w-screen h-screen flex flex-col overflow-hidden animate-page-enter">
    <div class="lg:hidden flex items-center justify-between p-4 bg-white shadow-sm z-10 animate-slide-down">
      <div class="flex items-center cursor-pointer animate-fade-in-up" @click="goToHome" style="animation-delay: 0.2s;">
        <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-blue-500 to-blue-600 flex items-center justify-center mr-3 shadow-md hover:shadow-lg transition-all duration-300 hover:scale-105">
          <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"></path>
          </svg>
        </div>
        <div>
          <h1 class="text-lg font-bold bg-clip-text text-transparent bg-gradient-to-r from-blue-900 to-blue-600">GlobalPulse</h1>
          <p class="text-xs text-blue-500">跨境智能运营平台</p>
        </div>
      </div>
      <div class="text-xs text-gray-500 animate-fade-in" style="animation-delay: 0.4s;">
        <span v-if="currentTab === 'email'">已有账号</span>
        <span v-else>企业登录</span>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="flex-1 flex flex-col xl:flex-row overflow-hidden">
      <!-- 左侧：视觉区域 - 大屏幕55%，平板和手机端上下布局 -->
      <div class="hidden md:flex xl:w-[55%] h-full xl:h-full md:h-1/2 relative text-white overflow-hidden"
           :class="{'md:flex xl:hidden': false, 'xl:flex': true}"
           style="background: radial-gradient(circle at 10% 20%, #1E40AF, #0F2080);">
        <!-- 增强的径向渐变背景层 -->
        <div class="absolute inset-0 bg-gradient-radial from-blue-600/20 via-blue-700/15 to-transparent"></div>
        
        <!-- 几何纹理背景 -->
        <div class="absolute inset-0 opacity-20" style="background-image: url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAiIGhlaWdodD0iNDAiIHZpZXdCb3g9IjAgMCA0MCA0MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48ZGVmcz48cGF0dGVybiBpZD0iZ3JpZCIgd2lkdGg9IjQwIiBoZWlnaHQ9IjQwIiBwYXR0ZXJuVW5pdHM9InVzZXJTcGFjZU9uVXNlIj48cGF0aCBkPSJNIDQwIDAgTCAwIDAgMCA0MCIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjNjM2NkYxIiBzdHJva2Utd2lkdGg9IjAuNSIgb3BhY2l0eT0iMC4zIi8+PC9wYXR0ZXJuPjwvZGVmcz48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSJ1cmwoI2dyaWQpIi8+PC9zdmc+'); background-blend-mode: overlay;"></div>
        
        <!-- 增强的背景装饰 -->
        <div class="absolute inset-0 overflow-hidden animate-bg-move">
          <!-- 主装饰圆形 -->
          <div class="absolute top-0 right-0 w-[600px] h-[600px] bg-blue-600/10 rounded-full blur-3xl animate-pulse-slow"></div>
          <div class="absolute bottom-0 left-0 w-[600px] h-[600px] bg-blue-500/10 rounded-full blur-3xl animate-pulse-slow" style="animation-delay: 1s;"></div>
          <div class="absolute top-1/3 left-1/4 w-[400px] h-[400px] bg-blue-700/10 rounded-full blur-3xl animate-pulse-slow" style="animation-delay: 2s;"></div>

          <!-- 右上角半透明浅蓝色圆形 -->
          <div class="absolute top-10 right-10 w-[200px] h-[200px] bg-blue-400/15 rounded-full blur-2xl animate-float"></div>
          
          <!-- 新增装饰元素 -->
          <div class="absolute top-1/4 right-1/3 w-[100px] h-[100px] bg-blue-400/20 rounded-full blur-xl animate-float"></div>
          <div class="absolute bottom-1/4 left-1/3 w-[150px] h-[150px] bg-blue-300/15 rounded-full blur-xl animate-float" style="animation-delay: 1.5s;"></div>
          <div class="absolute top-2/3 right-1/4 w-[80px] h-[80px] bg-blue-500/20 rounded-full blur-xl animate-float" style="animation-delay: 3s;"></div>
          
          <!-- 半透明圆形/波纹装饰元素 -->
          <div class="absolute top-1/5 left-1/5 w-[200px] h-[200px] bg-gradient-to-br from-blue-400/10 to-blue-600/5 rounded-full blur-2xl animate-float-slow"></div>
          <div class="absolute bottom-1/5 right-1/5 w-[300px] h-[300px] bg-gradient-to-tl from-blue-500/8 to-blue-700/4 rounded-full blur-2xl animate-float-slow" style="animation-delay: 2s;"></div>
        </div>

        <!-- 网格装饰 -->
        <div class="absolute inset-0 bg-[url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHZpZXdCb3g9IjAgMCA2MCA2MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48ZyBmaWxsPSJub25lIiBmaWxsLXJ1bGU9ImV2ZW5vZGQiPjxnIGZpbGw9IiMyMDIwMjAiIGZpbGwtb3BhY2l0eT0iMC4wNSI+PHBhdGggZD0iTTEwIDBIMHoiLz48cGF0aCBkPSJNMzAgMEg0MHYxMCIvPjxwYXRoIGQ9Ik02MCAzMEg1MHYxMCIvPjxwYXRoIGQ9Ik01MCA2MEg0MHYtMTAiLz48cGF0aCBkPSJNMzAgNUgzMHYxMCIvPjxwYXRoIGQ9Ik0xMCAzMEgwdjEwIi8+PHBhdGggZD0iTTYwIDUwSDUwdjEwIi8+PHBhdGggZD0iTTMwIDYwSDIwdjEwIi8+PC9nPjwvZz48L3N2Zz4=')] opacity-30"></div>

        <!-- 顶部Logo - 添加呼吸动画 -->
        <div class="absolute top-8 left-8 z-10 flex items-center hidden xl:flex cursor-pointer animate-breathe" @click="goToHome">
          <div class="w-14 h-14 rounded-lg bg-gradient-to-br from-blue-500 to-blue-600 flex items-center justify-center mr-4 shadow-xl">
            <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"></path>
            </svg>
          </div>
          <div class="no-select">
            <h1 class="text-2xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-white to-blue-200">GlobalPulse</h1>
            <p class="text-sm text-blue-300 mt-0.5">跨境智能运营平台</p>
          </div>
        </div>

        <!-- 主视觉内容 - 禁止复制 -->
        <div class="h-full flex flex-col justify-center items-center px-6 sm:px-8 md:px-12 lg:px-16 relative z-10 max-w-4xl mx-auto no-select">
          <!-- 企业级标题 -->
          <div class="mb-10 max-w-lg text-center">
            <div class="inline-flex items-center px-4 py-2 bg-blue-500/20 backdrop-blur-sm text-blue-200 text-sm font-medium rounded-full mb-6 border border-blue-400/30 shadow-lg">
              <i class="fas fa-rocket mr-2 text-blue-300"></i>
              AI 跨境电商平台
            </div>
            <h2 class="text-5xl md:text-6xl font-bold mb-6 leading-tight tracking-tight animate-breathe text-white">
              智能出海
              <span class="block mt-2 bg-clip-text text-transparent bg-gradient-to-r from-blue-300 to-white">
                轻松赚钱
              </span>
            </h2>
            <p class="text-blue-200 text-lg max-w-md leading-relaxed mx-auto">
              AI 驱动的跨境电商解决方案，让出海变得简单高效
            </p>
          </div>

          <!-- 核心业务数据 -->
          <div class="grid grid-cols-2 gap-4 mb-10">
            <div class="bg-white/10 backdrop-blur-sm rounded-xl p-4 border border-white/20 hover:bg-white/15 transition-all duration-300 hover:scale-105 hover:shadow-lg">
              <div class="text-2xl font-bold text-white mb-1">2.8K+</div>
              <div class="text-blue-200 text-sm">企业客户</div>
            </div>
            <div class="bg-white/10 backdrop-blur-sm rounded-xl p-4 border border-white/20 hover:bg-white/15 transition-all duration-300 hover:scale-105 hover:shadow-lg">
              <div class="text-2xl font-bold text-white mb-1">98%</div>
              <div class="text-blue-200 text-sm">成功率</div>
            </div>
          </div>

          <!-- 核心功能轮播 -->
          <div class="w-full max-w-3xl mb-8">
            <h3 class="text-xl font-semibold mb-6 text-blue-100 text-center">
              核心功能
            </h3>

            <div class="relative h-80 overflow-hidden flex justify-center items-center">
              <!-- 功能卡片容器 -->
              <div class="absolute inset-0">
                <!-- 社媒管理 -->
                <div class="absolute transition-all duration-1000 ease-in-out feature-card" 
                     :class="getFeatureCardClass(0)"
                     :style="getFeatureCardStyle(0)">
                  <div class="flex items-center bg-white/10 rounded-xl p-5 border border-white/10 backdrop-blur-sm">
                    <div class="w-12 h-12 bg-blue-500/30 rounded-xl flex items-center justify-center mr-4">
                      <i class="fas fa-share-alt text-blue-200 text-lg"></i>
                    </div>
                    <div>
                      <div class="text-white font-medium text-lg">社媒管理</div>
                      <div class="text-blue-200 text-sm">统一管理多平台账号</div>
                    </div>
                  </div>
                </div>

                <!-- 广告投放 -->
                <div class="absolute transition-all duration-1000 ease-in-out feature-card"
                     :class="getFeatureCardClass(1)"
                     :style="getFeatureCardStyle(1)">
                  <div class="flex items-center bg-white/10 rounded-xl p-5 border border-white/10 backdrop-blur-sm">
                    <div class="w-12 h-12 bg-purple-500/30 rounded-xl flex items-center justify-center mr-4">
                      <i class="fas fa-bullhorn text-purple-200 text-lg"></i>
                    </div>
                    <div>
                      <div class="text-white font-medium text-lg">广告投放</div>
                      <div class="text-purple-200 text-sm">智能投放优化策略</div>
                    </div>
                  </div>
                </div>

                <!-- 数据分析 -->
                <div class="absolute transition-all duration-1000 ease-in-out feature-card"
                     :class="getFeatureCardClass(2)"
                     :style="getFeatureCardStyle(2)">
                  <div class="flex items-center bg-white/10 rounded-xl p-5 border border-white/10 backdrop-blur-sm">
                    <div class="w-12 h-12 bg-green-500/30 rounded-xl flex items-center justify-center mr-4">
                      <i class="fas fa-chart-bar text-green-200 text-lg"></i>
                    </div>
                    <div>
                      <div class="text-white font-medium text-lg">数据分析</div>
                      <div class="text-green-200 text-sm">深度洞察用户行为</div>
                    </div>
                  </div>
                </div>

                <!-- 红人管理 -->
                <div class="absolute transition-all duration-1000 ease-in-out feature-card"
                     :class="getFeatureCardClass(3)"
                     :style="getFeatureCardStyle(3)">
                  <div class="flex items-center bg-white/10 rounded-xl p-5 border border-white/10 backdrop-blur-sm">
                    <div class="w-12 h-12 bg-orange-500/30 rounded-xl flex items-center justify-center mr-4">
                      <i class="fas fa-users text-orange-200 text-lg"></i>
                    </div>
                    <div>
                      <div class="text-white font-medium text-lg">红人管理</div>
                      <div class="text-orange-200 text-sm">精准匹配优质达人</div>
                    </div>
                  </div>
                </div>

                <!-- 商品管理 -->
                <div class="absolute transition-all duration-1000 ease-in-out feature-card"
                     :class="getFeatureCardClass(4)"
                     :style="getFeatureCardStyle(4)">
                  <div class="flex items-center bg-white/10 rounded-xl p-5 border border-white/10 backdrop-blur-sm">
                    <div class="w-12 h-12 bg-indigo-500/30 rounded-xl flex items-center justify-center mr-4">
                      <i class="fas fa-shopping-bag text-indigo-200 text-lg"></i>
                    </div>
                    <div>
                      <div class="text-white font-medium text-lg">商品管理</div>
                      <div class="text-indigo-200 text-sm">全链路商品运营</div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

        </div>

        <!-- 底部信息 -->
        <div class="absolute bottom-6 left-0 right-0 px-6 z-10 no-select">
          <!-- 企业信任标识 -->
          <div class="flex items-center justify-center space-x-6 text-sm text-blue-200 mb-4">
            <div class="flex items-center">
              <i class="fas fa-shield-alt text-blue-300 mr-2"></i>
              <span>安全保障</span>
            </div>
            <div class="flex items-center">
              <i class="fas fa-headset text-green-300 mr-2"></i>
              <span>专属服务</span>
            </div>
          </div>
          
          <!-- 版权和链接 -->
          <div class="flex flex-col md:flex-row justify-between items-center">
            <div class="text-blue-400/70 text-sm mb-3 md:mb-0">© 2023 GlobalPulse. 保留所有权利</div>
            <div class="flex space-x-6">
              <a href="#/terms" class="text-blue-400/70 hover:text-white text-sm transition-colors">服务条款</a>
              <a href="#/privacy" class="text-blue-400/70 hover:text-white text-sm transition-colors">隐私政策</a>
              <a href="#/contact" class="text-blue-400/70 hover:text-white text-sm transition-colors">联系我们</a>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：登录区 - 响应式布局优化 -->
      <div class="w-full xl:w-[45%] h-full xl:h-full md:h-1/2 flex items-center justify-center bg-gray-50 p-4 md:p-6 relative overflow-hidden">
        <!-- 增强的右侧背景装饰 -->
        <div class="absolute -top-64 -right-64 w-[500px] h-[500px] bg-blue-100 rounded-full opacity-70 animate-float-right"></div>
        <div class="absolute -bottom-64 -left-64 w-[500px] h-[500px] bg-blue-200 rounded-full opacity-50 animate-float-right" style="animation-delay: 1s;"></div>

        <!-- 新增装饰元素 -->
        <div class="absolute top-1/4 left-1/4 w-[200px] h-[200px] bg-blue-50 rounded-full opacity-60 animate-float-right" style="animation-delay: 2s;"></div>
        <div class="absolute bottom-1/3 right-1/3 w-[150px] h-[150px] bg-indigo-50 rounded-full opacity-70 animate-float-right" style="animation-delay: 3s;"></div>

        <!-- 更多装饰元素 -->
        <div class="absolute top-1/2 right-1/4 w-[80px] h-[80px] bg-purple-50 rounded-full opacity-50 animate-float-right" style="animation-delay: 1.5s;"></div>
        <div class="absolute bottom-1/4 left-1/3 w-[60px] h-[60px] bg-teal-50 rounded-full opacity-60 animate-float-right" style="animation-delay: 2.5s;"></div>

        <!-- 登录卡片 - 响应式宽度优化 -->
        <div class="w-full max-w-sm sm:max-w-md md:max-w-lg xl:max-w-xl relative z-10 animate-card-enter" style="animation-delay: 0.6s;">
          <div class="bg-white rounded-xl shadow-[0_5px_15px_rgba(0,0,0,0.05),0_15px_35px_rgba(0,0,0,0.1)] overflow-hidden border border-gray-100 transform transition-all duration-300 hover:shadow-2xl">
            <!-- 顶部装饰条 - 品牌色线性渐变 -->
            <div class="h-1 bg-gradient-to-r from-blue-500 to-blue-400"></div>

            <!-- 登录内容 -->
            <div class="p-4 sm:p-6 md:p-8">
              <h2 class="text-lg sm:text-xl md:text-2xl font-bold text-gray-900 mb-3 md:mb-5">账户登录</h2>

              <!-- 登录标签页 -->
              <div class="flex space-x-2 sm:space-x-4 md:space-x-6 mb-3 md:mb-5">
                <button
                    @click="currentTab = 'email'"
                    class="pb-2 md:pb-3 px-2 sm:px-3 md:px-4 text-sm sm:text-base font-medium transition-all relative border-b-2"
                    :class="currentTab === 'email' ? 'text-blue-700 border-blue-600' : 'text-gray-500 hover:text-gray-700 border-transparent hover:border-gray-300'"
                >
                  邮箱登录
                </button>
                <button
                    @click="currentTab = 'feishu'"
                    class="pb-2 md:pb-3 px-2 sm:px-3 md:px-4 text-sm sm:text-base font-medium transition-all relative border-b-2"
                    :class="currentTab === 'feishu' ? 'text-blue-700 border-blue-600' : 'text-gray-500 hover:text-gray-700 border-transparent hover:border-gray-300'"
                >
                  飞书登录
                </button>
              </div>

              <!-- 登录区域容器 - 固定高度确保两个标签页完全一致 -->
              <div class="h-[380px] sm:h-[420px] md:h-[430px] flex flex-col">
                <!-- 邮箱登录表单 -->
                <div v-if="currentTab === 'email'" class="space-y-2 sm:space-y-3 md:space-y-4 animate-slideIn flex-grow">
                  <div>
                    <label for="email" class="block text-xs sm:text-sm font-medium text-gray-700 mb-1">邮箱地址</label>
                    <div class="relative">
                      <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                        <svg class="w-4 h-4 sm:w-5 sm:h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"></path>
                        </svg>
                      </div>
                      <input
                          type="email"
                          v-model="email"
                          id="email"
                          placeholder="请输入您的邮箱"
                          aria-label="邮箱地址输入框"
                          aria-describedby="email-error"
                          aria-required="true"
                          autocomplete="off"
                          class="w-full pl-8 sm:pl-10 pr-3 sm:pr-4 py-2.5 sm:py-3 md:py-3.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 focus:shadow-[0_0_0_3px_rgba(59,130,246,0.1)] transition-all duration-300 text-gray-900 bg-gray-50 placeholder-gray-400 text-sm sm:text-base hover:bg-white hover:shadow-md transform hover:scale-[1.01]"
                          :class="emailError ? 'border-red-400 focus:ring-red-300 focus:border-red-300 animate-shake' : ''"
                          :aria-invalid="!!emailError"
                          @input="validateEmail"
                          @focus="onInputFocus"
                          @blur="onInputBlur"
                      />
                    </div>
                    <span v-if="emailError" id="email-error" class="text-red-500 text-xs mt-1 block animate-fade-in" role="alert" aria-live="polite">{{ emailError }}</span>
                  </div>

                  <div>
                    <label for="password" class="block text-xs sm:text-sm font-medium text-gray-700 mb-1">密码</label>
                    <div class="relative">
                      <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                        <svg class="w-4 h-4 sm:w-5 sm:h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"></path>
                        </svg>
                      </div>
                      <input
                          type="password"
                          v-model="password"
                          id="password"
                          placeholder="请输入您的密码"
                          aria-label="密码输入框"
                          aria-describedby="password-error"
                          aria-required="true"
                          autocomplete="off"
                          class="w-full pl-8 sm:pl-10 pr-3 sm:pr-4 py-2.5 sm:py-3 md:py-3.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 focus:shadow-[0_0_0_3px_rgba(59,130,246,0.1)] transition-all duration-300 text-gray-900 bg-white shadow-sm placeholder-gray-400 text-sm sm:text-base hover:shadow-md transform hover:scale-[1.01]"
                          :class="passwordError ? 'border-red-400 focus:ring-red-300 focus:border-red-300 animate-shake' : ''"
                          :aria-invalid="!!passwordError"
                          @input="validatePassword"
                          @focus="onInputFocus"
                          @blur="onInputBlur"
                      />
                    </div>
                    <span v-if="passwordError" id="password-error" class="text-red-500 text-xs mt-1 block animate-fade-in" role="alert" aria-live="polite">{{ passwordError }}</span>
                  </div>

                  <!-- 验证码区域 -->
                  <div>
                    <label for="verificationCode" class="block text-xs sm:text-sm font-medium text-gray-700 mb-1">验证码</label>
                    <div class="flex space-x-2 md:space-x-3">
                      <div class="relative flex-1 min-w-0">
                        <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                          <svg class="w-4 h-4 sm:w-5 sm:h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"></path>
                          </svg>
                        </div>
                        <input
                            type="text"
                            v-model="verificationCode"
                            id="verificationCode"
                            placeholder="请输入验证码"
                            aria-label="验证码输入框"
                            aria-describedby="verification-code-error captcha-image"
                            aria-required="true"
                            autocomplete="off"
                            class="w-full pl-8 sm:pl-10 pr-3 sm:pr-4 py-2.5 sm:py-3 md:py-3.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 focus:shadow-[0_0_0_3px_rgba(59,130,246,0.1)] transition-all duration-300 text-gray-900 bg-white shadow-sm placeholder-gray-400 text-sm sm:text-base hover:shadow-md transform hover:scale-[1.01]"
                            :class="verificationCodeError ? 'border-red-400 focus:ring-red-300 focus:border-red-300 animate-shake' : ''"
                            :aria-invalid="!!verificationCodeError"
                            @input="validateVerificationCode"
                        />
                      </div>
                      <button 
                          type="button"
                          id="captcha-image"
                          class="w-24 sm:w-28 md:w-32 h-10 sm:h-11 md:h-12 bg-gray-100 rounded-lg overflow-hidden flex items-center justify-center relative flex-shrink-0 cursor-pointer group hover:bg-gray-200 transition-colors focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2" 
                          @click="refreshCaptcha" 
                          aria-label="验证码图片，点击刷新获取新的验证码"
                          title="点击刷新验证码">
                        <img :src="captchaImage" alt="验证码图片，包含随机字符用于验证用户身份" class="w-full h-full object-cover" />
                        <!-- 刷新图标提示 -->
                        <div class="absolute inset-0 bg-black/20 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center">
                          <svg class="w-3 h-3 sm:w-4 sm:h-4 text-white animate-spin" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
                          </svg>
                        </div>
                      </button>
                    </div>
                    <span v-if="verificationCodeError" id="verification-code-error" class="text-red-500 text-xs mt-1 block animate-fade-in" role="alert" aria-live="polite">{{ verificationCodeError }}</span>
                  </div>

                  <div class="flex items-center justify-end">
                    <button
                        type="button"
                        class="text-xs sm:text-sm text-blue-600 hover:text-blue-800 hover:underline transition-all focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 rounded px-1 py-1"
                        @click="showForgotPasswordModal = true"
                        aria-label="忘记密码，点击找回密码"
                    >
                      忘记密码?
                    </button>
                  </div>

                  <!-- 用户协议和隐私条款确认 -->
                  <div class="mb-4">
                    <label class="flex items-start cursor-pointer">
                      <input 
                        type="checkbox" 
                        v-model="agreeTermsLogin" 
                        id="agree-terms-login"
                        aria-describedby="terms-login-error"
                        aria-required="true"
                        class="h-4 w-4 text-blue-600 rounded border-gray-300 focus:ring-blue-500 mt-0.5 flex-shrink-0 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2" 
                        style="appearance: checkbox; -webkit-appearance: checkbox;"
                        :aria-invalid="!agreeTermsLogin && termsLoginError"
                      />
                      <span class="ml-2 text-sm text-gray-600">
                        我已阅读并同意<a @click.prevent="handleTermsClick" href="#" class="text-blue-600 hover:underline focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-1 rounded" aria-label="查看服务条款">服务条款</a>和<a @click.prevent="handlePrivacyClick" href="#" class="text-blue-600 hover:underline focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-1 rounded" aria-label="查看隐私政策">隐私政策</a>
                      </span>
                    </label>
                    <span v-if="!agreeTermsLogin && termsLoginError" id="terms-login-error" class="text-red-500 text-xs block mt-1 animate-fade-in" role="alert" aria-live="polite">{{ termsLoginError }}</span>
                  </div>

                  <!-- 登录按钮 -->
                  <button
                      type="submit"
                      @click="handleLogin"
                      class="w-full bg-blue-600 text-white py-2.5 sm:py-3 md:py-3.5 rounded-lg font-medium hover:bg-blue-700 hover:scale-[1.02] active:scale-[0.98] transition-all flex items-center justify-center mt-3 md:mt-4 shadow-md hover:shadow-lg text-sm sm:text-base focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
                      aria-label="提交登录表单"
                  >
                    登录
                    <svg class="w-4 h-4 sm:w-5 sm:h-5 ml-2 transform hover:translate-x-1 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 5l7 7m0 0l-7 7m7-7H3"></path>
                    </svg>
                  </button>
                </div>

                <!-- 飞书登录 - 响应式二维码 -->
                <div v-if="currentTab === 'feishu'" class="flex flex-col items-center animate-slideIn h-full justify-between">
                  <div class="w-full">
                    <!-- 飞书二维码容器 -->
                    <div class="w-full bg-gray-50 rounded-xl flex items-center justify-center mb-4 sm:mb-6 border border-gray-200 shadow-sm relative py-3 sm:py-4">
                      <div class="text-center px-2 sm:px-4">
                        <p class="text-xs sm:text-sm text-gray-500 mb-2 sm:mb-3">通过飞书账号登录，享受更便捷的服务</p>
                        
                        <!-- 动态二维码 -->
                        <div v-if="isFeishuLoading" class="w-48 h-48 sm:w-56 sm:h-56 md:w-64 md:h-64 flex items-center justify-center">
                          <div class="animate-spin rounded-full h-8 w-8 sm:h-10 sm:w-10 md:h-12 md:w-12 border-b-2 border-blue-600"></div>
                        </div>
                        
                        <div v-else-if="feishuQrCodeUrl" class="w-48 h-48 sm:w-56 sm:h-56 md:w-64 md:h-64 mx-auto">
                          <iframe 
                            :src="feishuQrCodeUrl" 
                            class="w-full h-full border-0 rounded-lg"
                            title="飞书登录二维码">
                          </iframe>
                        </div>
                        
                        <div v-else class="w-48 h-48 sm:w-56 sm:h-56 md:w-64 md:h-64 flex items-center justify-center text-gray-400">
                          <div class="text-center">
                            <svg class="w-12 h-12 sm:w-14 sm:h-14 md:w-16 md:h-16 mx-auto mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.732-.833-2.5 0L4.268 18.5c-.77.833.192 2.5 1.732 2.5z"></path>
                            </svg>
                            <p class="text-xs sm:text-sm">二维码加载失败</p>
                            <button @click="initFeishuLogin" class="mt-2 text-blue-600 hover:text-blue-800 text-xs sm:text-sm">
                              重新加载
                            </button>
                          </div>
                        </div>
                        
                        <div class="absolute bottom-0 left-0 right-0 bg-blue-800 text-white text-xs text-center py-1.5 sm:py-2 rounded-b-xl">
                          扫码登录飞书账号
                        </div>
                      </div>
                    </div>

                    <button class="w-full flex items-center justify-center px-3 sm:px-4 py-2.5 sm:py-3 md:py-3.5 border border-gray-300 rounded-lg text-gray-800 hover:bg-gray-50 transition-all shadow-sm hover:shadow mb-3 sm:mb-4 text-sm sm:text-base">
                      <svg class="w-4 h-4 sm:w-5 sm:h-5 mr-2 sm:mr-2.5 text-blue-600" viewBox="0 0 24 24" fill="currentColor">
                        <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"></path>
                      </svg>
                      打开飞书客户端扫码
                    </button>
                  </div>

                  <!-- 精确匹配邮箱登录表单高度的占位区域 -->
                  <div class="w-full space-y-3 sm:space-y-4 mt-auto">
                    <div class="h-3 sm:h-4"></div> <!-- 模拟验证码下方的说明文字高度 -->
                    <div class="h-6 sm:h-8"></div> <!-- 模拟"记住我"和"忘记密码"区域高度 -->
                    <div class="h-12"></div> <!-- 模拟登录按钮高度 -->
                  </div>
                </div>
              </div>



              <!-- 注册提示 -->
              <div class="mt-4 sm:mt-6 md:mt-8 text-center">
                <span class="text-gray-600 text-sm sm:text-base">还没有账号? </span>
                <button
                    class="text-blue-600 font-medium hover:text-blue-800 hover:underline transition-all text-sm sm:text-base"
                    @click="showRegisterModal = true"
                >
                  立即注册
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 忘记密码模态框 -->
    <div v-if="showForgotPasswordModal" class="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4 animate-fadeIn">
      <div class="bg-white rounded-2xl shadow-2xl w-full max-w-md mx-auto overflow-hidden transform transition-all scale-100" @click.stop>
        <div class="h-1.5 bg-gradient-to-r from-blue-600 to-blue-400"></div>
        <div class="p-6 md:p-7">
          <div class="flex justify-between items-center mb-5">
            <h3 class="text-xl font-bold text-gray-900">找回密码</h3>
            <button @click="showForgotPasswordModal = false" class="text-gray-400 hover:text-gray-600 transition-colors">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
              </svg>
            </button>
          </div>

          <div class="space-y-4 mb-6">
            <p class="text-gray-600 text-sm">请输入您的注册邮箱，我们将发送验证码到您的邮箱，验证后即可重置密码</p>

            <div>
              <label for="forgotEmail" class="block text-sm font-medium text-gray-700 mb-1">邮箱地址</label>
              <div class="relative">
                <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"></path>
                  </svg>
                </div>
                <input
                    type="email"
                    v-model="forgotEmail"
                    id="forgotEmail"
                    placeholder="请输入您的邮箱"
                    aria-label="忘记密码邮箱地址输入框"
                    aria-describedby="forgot-email-error"
                    aria-required="true"
                    autocomplete="off"
                    class="w-full pl-10 pr-4 py-3 border md:py-3.5 border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all text-gray-900 bg-gray-50"
                    :class="forgotEmailError ? 'border-red-400 focus:ring-red-300 focus:border-red-300 animate-shake' : ''"
                    :aria-invalid="!!forgotEmailError"
                />
              </div>
              <span v-if="forgotEmailError" id="forgot-email-error" class="text-red-500 text-xs mt-1 block animate-fade-in" role="alert" aria-live="polite">{{ forgotEmailError }}</span>
            </div>

            <div>
              <label for="resetCode" class="block text-sm font-medium text-gray-700 mb-1">验证码</label>
              <div class="flex space-x-2">
                <div class="relative flex-1">
                  <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"></path>
                    </svg>
                  </div>
                  <input
                      type="text"
                      v-model="resetCode"
                      id="resetCode"
                      placeholder="请输入验证码"
                      aria-label="重置密码验证码输入框"
                      aria-describedby="reset-code-error send-reset-code-btn"
                      aria-required="true"
                      autocomplete="off"
                      class="w-full pl-10 pr-4 py-3 border md:py-3.5 border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all text-gray-900 bg-gray-50"
                      :class="resetCodeError ? 'border-red-400 focus:ring-red-300 focus:border-red-300 animate-shake' : ''"
                      :aria-invalid="!!resetCodeError"
                  />
                </div>
                <button
                    type="button"
                    id="send-reset-code-btn"
                    @click="sendResetCode"
                    class="whitespace-nowrap px-4 py-3 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-all font-medium text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
                    :disabled="isCodeButtonDisabled"
                    :aria-label="isSendingCode ? '正在发送验证码' : '发送重置密码验证码'"
                >
                  {{ isSendingCode ? '发送中...' : codeButtonText }}
                </button>
              </div>
              <span v-if="resetCodeError" id="reset-code-error" class="text-red-500 text-xs mt-1 block animate-fade-in" role="alert" aria-live="polite">{{ resetCodeError }}</span>
            </div>

            <div>
              <label for="newPassword" class="block text-sm font-medium text-gray-700 mb-1">新密码</label>
              <div class="relative">
                <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"></path>
                  </svg>
                </div>
                <input
                    type="password"
                    v-model="newPassword"
                    id="newPassword"
                    placeholder="请设置新密码"
                    aria-label="新密码输入框"
                    aria-describedby="new-password-error new-password-help"
                    aria-required="true"
                    autocomplete="off"
                    class="w-full pl-10 pr-4 py-3 border md:py-3.5 border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all text-gray-900 bg-gray-50"
                    :class="newPasswordError ? 'border-red-400 focus:ring-red-300 focus:border-red-300 animate-shake' : ''"
                    :aria-invalid="!!newPasswordError"
                />
              </div>
              <span v-if="newPasswordError" id="new-password-error" class="text-red-500 text-xs mt-1 block animate-fade-in" role="alert" aria-live="polite">{{ newPasswordError }}</span>
              <span id="new-password-help" class="text-gray-500 text-xs block mt-1">密码长度不能少于6位，建议包含字母和数字</span>
            </div>
          </div>

          <div class="flex space-x-3">
            <button
                type="button"
                @click="showForgotPasswordModal = false"
                class="flex-1 py-3 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-all font-medium focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-offset-2"
                aria-label="取消重置密码操作"
            >
              取消
            </button>
            <button
                type="submit"
                @click="resetPassword"
                class="flex-1 py-3 bg-blue-600 text-white rounded-lg font-medium hover:bg-blue-700 transition-all focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
                aria-label="确认重置密码"
            >
              重置密码
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 注册账号模态框 -->
    <div v-if="showRegisterModal" class="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4 animate-fadeIn">
      <div class="bg-white rounded-2xl shadow-2xl w-full max-w-md mx-auto overflow-hidden transform transition-all scale-100" @click.stop>
        <div class="h-1.5 bg-gradient-to-r from-blue-600 to-blue-400"></div>
        <div class="p-6 md:p-7">
          <div class="flex justify-between items-center mb-5">
            <h3 class="text-xl font-bold text-gray-900">创建账号</h3>
            <button @click="showRegisterModal = false" class="text-gray-400 hover:text-gray-600 transition-colors">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
              </svg>
            </button>
          </div>

          <div class="space-y-4 mb-6">
            <div>
              <label for="registerEmail" class="block text-sm font-medium text-gray-700 mb-1">邮箱地址</label>
              <div class="relative">
                <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"></path>
                  </svg>
                </div>
                <input
                    type="email"
                    v-model="registerEmail"
                    id="registerEmail"
                    placeholder="请输入您的邮箱"
                    aria-label="注册邮箱地址输入框"
                    aria-describedby="register-email-error"
                    aria-required="true"
                    autocomplete="off"
                    class="w-full pl-10 pr-4 py-3 border md:py-3.5 border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all text-gray-900 bg-gray-50"
                    :class="registerEmailError ? 'border-red-400 focus:ring-red-300 focus:border-red-300 animate-shake' : ''"
                    :aria-invalid="!!registerEmailError"
                />
              </div>
              <span v-if="registerEmailError" id="register-email-error" class="text-red-500 text-xs mt-1 block animate-fade-in" role="alert" aria-live="polite">{{ registerEmailError }}</span>
            </div>

            <div>
              <label for="registerCode" class="block text-sm font-medium text-gray-700 mb-1">验证码</label>
              <div class="flex space-x-2">
                <div class="relative flex-1">
                  <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"></path>
                    </svg>
                  </div>
                  <input
                      type="text"
                      v-model="registerCode"
                      id="registerCode"
                      placeholder="请输入验证码"
                      aria-label="注册验证码输入框"
                      aria-describedby="register-code-error send-register-code-btn"
                      aria-required="true"
                      autocomplete="off"
                      class="w-full pl-10 pr-4 py-3 border md:py-3.5 border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all text-gray-900 bg-gray-50"
                      :class="registerCodeError ? 'border-red-400 focus:ring-red-300 focus:border-red-300 animate-shake' : ''"
                      :aria-invalid="!!registerCodeError"
                  />
                </div>
                <button
                    type="button"
                    id="send-register-code-btn"
                    @click="sendRegisterCode"
                    class="whitespace-nowrap px-4 py-3 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-all font-medium text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
                    :disabled="isRegisterCodeButtonDisabled"
                    :aria-label="isRegisterSendingCode ? '正在发送注册验证码' : '发送注册验证码'"
                >
                  {{ isRegisterSendingCode ? '发送中...' : registerCodeButtonText }}
                </button>
              </div>
              <span v-if="registerCodeError" id="register-code-error" class="text-red-500 text-xs mt-1 block animate-fade-in" role="alert" aria-live="polite">{{ registerCodeError }}</span>
            </div>

            <div>
              <label for="registerPassword" class="block text-sm font-medium text-gray-700 mb-1">设置密码</label>
              <div class="relative">
                <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"></path>
                  </svg>
                </div>
                <input
                    type="password"
                    v-model="registerPassword"
                    id="registerPassword"
                    placeholder="请设置密码"
                    aria-label="注册密码输入框"
                    aria-describedby="register-password-error register-password-help"
                    aria-required="true"
                    autocomplete="off"
                    class="w-full pl-10 pr-4 py-3 border md:py-3.5 border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all text-gray-900 bg-gray-50"
                    :class="registerPasswordError ? 'border-red-400 focus:ring-red-300 focus:border-red-300 animate-shake' : ''"
                    :aria-invalid="!!registerPasswordError"
                />
              </div>
              <span v-if="registerPasswordError" id="register-password-error" class="text-red-500 text-xs mt-1 block animate-fade-in" role="alert" aria-live="polite">{{ registerPasswordError }}</span>
              <span id="register-password-help" class="text-gray-500 text-xs block mt-1">密码长度不能少于6位，建议包含字母和数字</span>
            </div>

            <div>
              <label for="confirmPassword" class="block text-sm font-medium text-gray-700 mb-1">确认密码</label>
              <div class="relative">
                <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"></path>
                  </svg>
                </div>
                <input
                    type="password"
                    v-model="confirmPassword"
                    id="confirmPassword"
                    placeholder="请再次输入密码"
                    aria-label="确认密码输入框"
                    aria-describedby="confirm-password-error"
                    aria-required="true"
                    autocomplete="off"
                    class="w-full pl-10 pr-4 py-3 border md:py-3.5 border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all text-gray-900 bg-gray-50"
                    :class="confirmPasswordError ? 'border-red-400 focus:ring-red-300 focus:border-red-300 animate-shake' : ''"
                    :aria-invalid="!!confirmPasswordError"
                />
              </div>
              <span v-if="confirmPasswordError" id="confirm-password-error" class="text-red-500 text-xs mt-1 block animate-fade-in" role="alert" aria-live="polite">{{ confirmPasswordError }}</span>
            </div>

            <div class="mb-4">
              <label class="flex items-start cursor-pointer">
                <input 
                  type="checkbox" 
                  v-model="agreeTerms" 
                  id="agree-terms"
                  aria-describedby="terms-error"
                  aria-required="true"
                  class="h-4 w-4 text-blue-600 rounded border-gray-300 focus:ring-blue-500 mt-0.5 flex-shrink-0 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2" 
                  style="appearance: checkbox; -webkit-appearance: checkbox;"
                  :aria-invalid="!agreeTerms && termsError"
                />
                <span class="ml-2 text-sm text-gray-600">
                  我已阅读并同意<a @click.prevent="handleTermsClick" href="#" class="text-blue-600 hover:underline focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-1 rounded" aria-label="查看服务条款">服务条款</a>和<a @click.prevent="handlePrivacyClick" href="#" class="text-blue-600 hover:underline focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-1 rounded" aria-label="查看隐私政策">隐私政策</a>
                </span>
              </label>
              <span v-if="!agreeTerms && termsError" id="terms-error" class="text-red-500 text-xs block mt-1 animate-fade-in" role="alert" aria-live="polite">{{ termsError }}</span>
            </div>
          </div>

          <div class="flex space-x-3">
            <button
                type="button"
                @click="showRegisterModal = false"
                class="flex-1 py-3 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-all font-medium focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-offset-2"
                aria-label="取消注册操作"
            >
              取消
            </button>
            <button
                type="submit"
                @click="registerAccount"
                class="flex-1 py-3 bg-blue-600 text-white rounded-lg font-medium hover:bg-blue-700 transition-all focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
                aria-label="提交注册信息"
            >
              注册账号
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- 服务条款弹窗 -->
  <div v-if="showTermsModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
    <div class="bg-white rounded-lg max-w-4xl w-full max-h-[90vh] overflow-hidden">
      <div class="bg-gradient-to-r from-blue-600 to-indigo-600 px-6 py-4">
        <h2 class="text-2xl font-bold text-white">服务条款</h2>
        <p class="text-blue-100 mt-1">广州文际信息科技有限公司</p>
      </div>
      <div class="p-6 overflow-y-auto max-h-[70vh]">
        <div class="space-y-6 text-gray-700">
          <section>
            <h3 class="text-lg font-semibold text-gray-900 mb-3">1. 服务说明</h3>
            <p class="mb-3">广州文际信息科技有限公司（以下简称"我们"或"公司"）通过本平台为用户提供信息技术服务。使用本服务即表示您同意遵守本服务条款。</p>
            <ul class="list-disc list-inside space-y-1 ml-4">
              <li>本服务仅供合法商业用途使用</li>
              <li>用户需确保提供信息的真实性和准确性</li>
              <li>禁止利用本服务从事违法违规活动</li>
            </ul>
          </section>

          <section>
            <h3 class="text-lg font-semibold text-gray-900 mb-3">2. 用户权利与义务</h3>
            <div class="space-y-3">
              <div>
                <h4 class="font-medium text-gray-800 mb-2">用户权利：</h4>
                <ul class="list-disc list-inside space-y-1 ml-4">
                  <li>享受本平台提供的各项服务</li>
                  <li>对个人信息享有查阅、更正、删除等权利</li>
                  <li>获得技术支持和客户服务</li>
                </ul>
              </div>
              <div>
                <h4 class="font-medium text-gray-800 mb-2">用户义务：</h4>
                <ul class="list-disc list-inside space-y-1 ml-4">
                  <li>遵守国家法律法规和本服务条款</li>
                  <li>不得恶意攻击或破坏系统安全</li>
                  <li>保护账户安全，不得与他人共享账户</li>
                  <li>及时更新个人信息，确保信息准确性</li>
                </ul>
              </div>
            </div>
          </section>

          <section>
            <h3 class="text-lg font-semibold text-gray-900 mb-3">3. 服务变更与终止</h3>
            <p class="mb-3">我们保留随时修改、暂停或终止服务的权利。如有重大变更，我们将提前通知用户。</p>
            <ul class="list-disc list-inside space-y-1 ml-4">
              <li>服务升级或维护时可能暂时中断</li>
              <li>违反服务条款的用户账户可能被暂停或终止</li>
              <li>用户可随时申请注销账户</li>
            </ul>
          </section>

          <section>
            <h3 class="text-lg font-semibold text-gray-900 mb-3">4. 免责声明</h3>
            <p class="mb-3">在法律允许的范围内，我们对以下情况不承担责任：</p>
            <ul class="list-disc list-inside space-y-1 ml-4">
              <li>因不可抗力导致的服务中断</li>
              <li>用户自身操作失误造成的损失</li>
              <li>第三方服务提供商的问题</li>
              <li>网络传输过程中的数据丢失或损坏</li>
            </ul>
          </section>

          <section>
            <h3 class="text-lg font-semibold text-gray-900 mb-3">5. 联系我们</h3>
            <p>如有任何问题，请通过以下方式联系我们：</p>
            <ul class="list-none space-y-1 ml-4 mt-2">
              <li><strong>邮箱：</strong> support@wenji-info.com</li>
              <li><strong>电话：</strong> 400-123-4567</li>
              <li><strong>地址：</strong> 广州市天河区珠江新城</li>
            </ul>
          </section>
        </div>
      </div>
      <div class="flex justify-end p-6 border-t border-gray-200 space-x-3">
        <button 
          type="button"
          @click="showTermsModal = false" 
          class="px-6 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-all focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-offset-2"
          aria-label="关闭服务条款弹窗"
        >
          关闭
        </button>
        <button 
          type="button"
          @click="agreeTerms = true; showTermsModal = false" 
          class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-all focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
          aria-label="同意服务条款并关闭弹窗"
        >
          同意并关闭
        </button>
      </div>
    </div>
  </div>

  <!-- 隐私政策弹窗 -->
  <div v-if="showPrivacyModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
    <div class="bg-white rounded-lg max-w-4xl w-full max-h-[90vh] overflow-hidden">
      <div class="bg-gradient-to-r from-green-600 to-emerald-600 px-6 py-4">
        <h2 class="text-2xl font-bold text-white">隐私政策</h2>
        <p class="text-green-100 mt-1">广州文际信息科技有限公司</p>
      </div>
      <div class="p-6 overflow-y-auto max-h-[70vh]">
        <div class="space-y-6 text-gray-700">
          <section>
            <h3 class="text-lg font-semibold text-gray-900 mb-3">1. 我们收集的信息</h3>
            <div class="space-y-3">
              <div>
                <h4 class="font-medium text-gray-800 mb-2">主动提供的信息：</h4>
                <ul class="list-disc list-inside space-y-1 ml-4">
                  <li>账户信息：邮箱地址、用户名、密码等</li>
                  <li>个人资料：姓名、电话号码、公司信息等</li>
                  <li>业务信息：使用服务过程中提供的内容和设置</li>
                </ul>
              </div>
              <div>
                <h4 class="font-medium text-gray-800 mb-2">自动收集的信息：</h4>
                <ul class="list-disc list-inside space-y-1 ml-4">
                  <li>设备信息：设备型号、操作系统、浏览器类型</li>
                  <li>日志信息：IP地址、访问时间、访问页面</li>
                  <li>使用数据：功能使用情况、点击行为</li>
                </ul>
              </div>
            </div>
          </section>

          <section>
            <h3 class="text-lg font-semibold text-gray-900 mb-3">2. 信息使用目的</h3>
            <p class="mb-3">我们收集和使用您的个人信息主要用于：</p>
            <ul class="list-disc list-inside space-y-1 ml-4">
              <li>提供、维护和改进我们的产品和服务</li>
              <li>创建和管理您的账户，验证身份</li>
              <li>处理您的请求和提供客户支持</li>
              <li>发送重要通知和服务更新</li>
              <li>分析使用情况，优化用户体验</li>
              <li>防范欺诈和确保系统安全</li>
            </ul>
          </section>

          <section>
            <h3 class="text-lg font-semibold text-gray-900 mb-3">3. 信息保护</h3>
            <p class="mb-3">我们采取多种安全措施保护您的个人信息：</p>
            <ul class="list-disc list-inside space-y-1 ml-4">
              <li>数据加密传输和存储</li>
              <li>访问权限控制和身份验证</li>
              <li>定期安全审计和漏洞修复</li>
              <li>员工隐私保护培训</li>
              <li>第三方安全认证</li>
            </ul>
          </section>

          <section>
            <h3 class="text-lg font-semibold text-gray-900 mb-3">4. 您的权利</h3>
            <p class="mb-3">您对自己的个人信息享有以下权利：</p>
            <ul class="list-disc list-inside space-y-1 ml-4">
              <li>访问权：查阅我们持有的您的个人信息</li>
              <li>更正权：更正不准确或不完整的个人信息</li>
              <li>删除权：在特定情况下要求删除您的个人信息</li>
              <li>限制处理权：限制我们处理您的个人信息</li>
              <li>撤回同意权：撤回您之前给予的同意</li>
            </ul>
          </section>

          <section>
            <h3 class="text-lg font-semibold text-gray-900 mb-3">5. Cookie使用</h3>
            <p class="mb-3">我们使用Cookie和类似技术来：</p>
            <ul class="list-disc list-inside space-y-1 ml-4">
              <li>记住您的登录状态和偏好设置</li>
              <li>分析网站使用情况，改进用户体验</li>
              <li>提供个性化的内容</li>
              <li>防止欺诈和提高安全性</li>
            </ul>
          </section>

          <section>
            <h3 class="text-lg font-semibold text-gray-900 mb-3">6. 联系我们</h3>
            <p>如有隐私相关问题，请联系我们：</p>
            <ul class="list-none space-y-1 ml-4 mt-2">
              <li><strong>邮箱：</strong> privacy@wenji-info.com</li>
              <li><strong>电话：</strong> 400-123-4567</li>
              <li><strong>地址：</strong> 广州市天河区珠江新城</li>
            </ul>
          </section>
        </div>
      </div>
      <div class="flex justify-end p-6 border-t border-gray-200 space-x-3">
        <button 
          type="button"
          @click="showPrivacyModal = false" 
          class="px-6 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-all focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-offset-2"
          aria-label="关闭隐私政策弹窗"
        >
          关闭
        </button>
        <button 
          type="button"
          @click="agreeTerms = true; showPrivacyModal = false" 
          class="px-6 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-all focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-offset-2"
          aria-label="同意隐私政策并关闭弹窗"
        >
          同意并关闭
        </button>
      </div>
    </div>
  </div>

  <!-- Toast 提示组件 -->
  <div v-if="showToast" class="fixed top-4 right-4 z-[9999] animate-toast-enter">
    <div class="flex items-center p-4 rounded-lg shadow-lg max-w-sm"
         :class="{
           'bg-green-500 text-white': toastType === 'success',
           'bg-red-500 text-white': toastType === 'error',
           'bg-yellow-500 text-white': toastType === 'warning',
           'bg-blue-500 text-white': toastType === 'info'
         }">
      <!-- 图标 -->
      <div class="flex-shrink-0 mr-3">
        <!-- 成功图标 -->
        <svg v-if="toastType === 'success'" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
        </svg>
        <!-- 错误图标 -->
        <svg v-else-if="toastType === 'error'" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
        </svg>
        <!-- 警告图标 -->
        <svg v-else-if="toastType === 'warning'" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z"></path>
        </svg>
        <!-- 信息图标 -->
        <svg v-else class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
        </svg>
      </div>
      <!-- 消息内容 -->
      <div class="flex-1 text-sm font-medium">{{ toastMessage }}</div>
      <!-- 关闭按钮 -->
      <button @click="showToast = false" class="flex-shrink-0 ml-3 text-white/80 hover:text-white transition-colors">
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
        </svg>
      </button>
    </div>
  </div>
</template>

<script>
import { saveAuthInfo } from '../utils/auth.js';
import { authAPI } from '../utils/api.js';
import { showSuccess, showError } from '../utils/notification.js';

export default {
  data() {
    return {
      // 登录表单数据
      currentTab: 'email',
      email: '',
      password: '',
      verificationCode: '',
      captchaImage: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTAwIiBoZWlnaHQ9IjQwIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciPjxwYXRoIGZpbGw9IiM3QjVBQUQiIGQ9Ik0wIDBoMTAwdjQwSDB6Ii8+PHJlY3QgeD0iMjAiIHk9IjEwIiByPSIxNSIgZmlsbD0iI0ZGRkZGRiIvPjxwYXRoIGQ9Ik00NSAxMCBMMTUgMzAgTDE1IDEwIE01NSAxMCBMNzUgMzAgTDc1IDEwIiBzdHJva2U9IiMzQjU0NjYiIHN0cm9rZS13aWR0aD0iMyIvPjxwYXRoIGZpbGw9IiMzQjU0NjYiIGQ9Ik0zMCAxNSBNNTAgMTUgTDMwIDI1IE01MCAxNSBMNTAgMjUgTTMwIDI1IEw1MCAyNSIvPjwvc3ZnPg==',

      // 错误信息
      emailError: '',
      passwordError: '',
      verificationCodeError: '',

      // 模态框状态
      showForgotPasswordModal: false,
      showRegisterModal: false,

      // 忘记密码表单数据
      forgotEmail: '',
      resetCode: '',
      newPassword: '',
      forgotEmailError: '',
      resetCodeError: '',
      newPasswordError: '',
      isSendingCode: false,
      codeCountdown: 0,

      // 注册表单数据
      registerEmail: '',
      registerCode: '',
      registerPassword: '',
      confirmPassword: '',
      agreeTerms: false,
      agreeTermsLogin: false, // 登录页面的用户协议确认
      registerEmailError: '',
      registerCodeError: '',
      registerPasswordError: '',
      confirmPasswordError: '',
      termsError: '',
      termsLoginError: '', // 登录页面的用户协议错误信息
      isRegisterSendingCode: false,
      registerCodeCountdown: 0,

      // 飞书登录数据
      feishuQrCodeUrl: '',
      feishuState: '',
      feishuTempToken: '',
      feishuUserInfo: null,
      isFeishuLoading: false,
      feishuBindEmail: '',
      feishuBindEmailError: '',

      // Toast提示数据
      showToast: false,
      toastMessage: '',
      toastType: 'success', // 'success' | 'error' | 'warning' | 'info'

      // 弹窗状态
      showTermsModal: false,
      showPrivacyModal: false,

      // 核心功能轮播数据
      currentFeatureIndex: 0,
      featureCarouselTimer: null,
      featurePositions: [
        { x: 0, y: 0, scale: 0.9, opacity: 1, zIndex: 5 },        // 主要位置 - 缩小主卡片
        { x: 250, y: 120, scale: 0.6, opacity: 0.8, zIndex: 3 },  // 右上 - 增大后面卡片
        { x: -230, y: 140, scale: 0.7, opacity: 0.8, zIndex: 2 }, // 左下 - 增大后面卡片
        { x: 280, y: -100, scale: 0.5, opacity: 0.6, zIndex: 1 }, // 右上小 - 增大后面卡片
        { x: -260, y: -120, scale: 0.5, opacity: 0.6, zIndex: 1 } // 左上小 - 增大后面卡片
      ]
    };
  },
  async mounted() {
    // 检查用户登录状态
    this.checkLoginStatus();
    // 自动加载验证码
    try {
      await this.refreshCaptcha();
    } catch (error) {
      console.error('初始化验证码失败:', error);
    }
    // 初始化飞书登录
    this.initFeishuLogin();
    // 启动核心功能轮播
    this.startFeatureCarousel();
  },
  beforeDestroy() {
    // 清理定时器
    if (this.featureCarouselTimer) {
      clearInterval(this.featureCarouselTimer);
    }
  },
  methods: {
    // 跳转到首页
      goToHome() {
        window.location.hash = '#/';
      },
      
      // 检查登录状态
      checkLoginStatus() {
        // 在实际部署环境中，通常的做法是：
        // 1. 检查localStorage或cookie中是否有保存的登录凭证
        // 2. 或者调用服务器API验证用户会话是否有效
        
        // 示例检查localStorage中的token或用户信息
        const token = localStorage.getItem('authToken');
        const userInfo = localStorage.getItem('userInfo');
        
        // 在真实项目中，这里应该有更严格的验证逻辑
        if (token || userInfo) {
          // 用户已登录，跳转到主页面
          
          // 跳转到首页
          this.goToHome();
        }
      },

    // 处理服务条款点击
    handleTermsClick() {
      if (this.agreeTerms) {
        // 已经勾选同意，直接显示弹窗
        this.showTermsModal = true;
      } else {
        // 未勾选同意，显示弹窗
        this.showTermsModal = true;
      }
    },

    // 处理隐私政策点击
    handlePrivacyClick() {
      if (this.agreeTerms) {
        // 已经勾选同意，直接显示弹窗
        this.showPrivacyModal = true;
      } else {
        // 未勾选同意，显示弹窗
        this.showPrivacyModal = true;
      }
    },
    // 刷新验证码
    async refreshCaptcha(keepError = false) {
      try {
        const response = await authAPI.getCaptcha();
        if (response && response.image) {
          this.captchaImage = response.image;
          // 保存验证码ID用于后续验证
          if (response.captchaId) {
            localStorage.setItem('captchaId', response.captchaId);
          }
        } else {
          // 如果API返回格式不正确，使用默认占位图
          this.captchaImage = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTAwIiBoZWlnaHQ9IjQwIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciPjxwYXRoIGZpbGw9IiM3QjVBQUQiIGQ9Ik0wIDBoMTAwdjQwSDB6Ii8+PHJlY3QgeD0iMjAiIHk9IjEwIiByPSIxNSIgZmlsbD0iI0ZGRkZGRiIvPjxwYXRoIGQ9Ik00NSAxMCBMMTUgMzAgTDE1IDEwIE01NSAxMCBMNzUgMzAgTDc1IDEwIiBzdHJva2U9IiMzQjU0NjYiIHN0cm9rZS13aWR0aD0iMyIvPjxwYXRoIGZpbGw9IiMzQjU0NjYiIGQ9Ik0zMCAxNSBNNTAgMTUgTDMwIDI1IE01MCAyNSBMNTAgMTUgTTMwIDI1IEw1MCAyNSIvPjwvc3ZnPg==';
        }
        
        this.verificationCode = '';
        // 只有在不是因为错误而刷新时，才清除错误提示
        if (!keepError) {
          this.verificationCodeError = '';
        }
      } catch (error) {
        console.error('刷新验证码失败:', error);
        // 提供一个备用的占位图
        this.captchaImage = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTAwIiBoZWlnaHQ9IjQwIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciPjx0ZXh0IHg9IjUwIiB5PSIyNSIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjE0IiBmaWxsPSIjNjY2IiB0ZXh0LWFuY2hvcj0ibWlkZGxlIj7liqDovb3lpLHotKU8L3RleHQ+PC9zdmc+';
        
        // 显示友好的错误提示
        if (!keepError) {
          this.verificationCodeError = '验证码加载失败';
        }
      }
    },

    // 处理登录
    async handleLogin() {
      // 表单验证
      let isValid = true;

      if (!this.email) {
        this.emailError = '请输入邮箱地址';
        isValid = false;
      } else if (!this.isValidEmail(this.email)) {
        this.emailError = '请输入有效的邮箱地址';
        isValid = false;
      } else {
        this.emailError = '';
      }

      if (!this.password) {
        this.passwordError = '请输入密码';
        isValid = false;
      } else if (this.password.length < 6) {
        this.passwordError = '密码长度不能少于6位';
        isValid = false;
      } else {
        this.passwordError = '';
      }

      if (!this.verificationCode) {
        this.verificationCodeError = '请输入验证码';
        isValid = false;
      } else {
        this.verificationCodeError = '';
      }

      // 验证用户协议确认
      if (!this.agreeTermsLogin) {
        this.termsLoginError = '请阅读并同意服务条款和隐私政策';
        isValid = false;
      } else {
        this.termsLoginError = '';
      }

      // 获取验证码ID
      const captchaId = localStorage.getItem('captchaId');
      if (!captchaId) {
        this.verificationCodeError = '验证码已过期';
        isValid = false;
      }

      if (isValid) {
        // 登录逻辑
        console.log('登录信息处理', {
          email: this.email,
          password: this.password,
          verificationCode: this.verificationCode
        });

        // 设置按钮状态
        const loginButton = document.querySelector('button[class*="bg-blue-600"][class*="text-white"]');
        if (loginButton) {
          loginButton.disabled = true;
          loginButton.innerHTML = '<svg class="w-5 h-5 animate-spin mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg> 登录中...';
        }

        try {
          // 首先在客户端验证验证码
          const captchaId = localStorage.getItem('captchaId');
          if (!captchaId) {
            this.verificationCodeError = '验证码已过期，请刷新后重试';
            this.refreshCaptcha(true);
            
            // 恢复按钮状态
            if (loginButton) {
              loginButton.disabled = false;
              loginButton.innerHTML = '登录 <svg class="w-5 h-5 ml-2 transform hover:translate-x-1 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 5l7 7m0 0l-7 7m7-7H3"></path></svg>';
            }
            return;
          }

          // 调用后端登录API，传入验证码ID
          const result = await authAPI.login(this.email, this.password, this.verificationCode, captchaId);
          
          // 显示成功提示
          const notification = document.createElement('div');
          notification.className = 'fixed top-4 right-4 bg-green-500 text-white px-4 py-2 rounded-lg shadow-lg flex items-center z-50 animate-fadeIn';
          notification.innerHTML = '<svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path></svg> 登录成功，正在跳转...';
          document.body.appendChild(notification);

          // 根据Java后端返回的格式，提取user和token
          const userData = result.user || result;
          const token = result.token;
          
          // 保存认证信息
          const userInfo = {
            name: userData.username || userData.name || this.email.split('@')[0],
            email: userData.email || this.email,
            userId: userData.id || userData.userId,
            role: userData.role || 0  // 从后端获取角色信息，如果没有则默认为用户角色
          };
          
          saveAuthInfo(userInfo, token);
          
          // 跳转至原来的页面或首页
          setTimeout(() => {
            notification.classList.add('opacity-0', 'transition-opacity', 'duration-300');
            setTimeout(() => notification.remove(), 300);

            // 恢复按钮状态
            if (loginButton) {
              loginButton.disabled = false;
              loginButton.innerHTML = '登录系统 <svg class="w-5 h-5 ml-2 transform hover:translate-x-1 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 5l7 7m0 0l-7 7m7-7H3"></path></svg>';
            }

            // 获取之前保存的重定向URL，如果没有则跳转到首页
            const redirectUrl = localStorage.getItem('redirectUrl');
            if (redirectUrl && redirectUrl !== '#/login') {
              // 清除保存的重定向URL
              localStorage.removeItem('redirectUrl');
              // 跳转到保存的URL
              window.location.hash = redirectUrl;
            } else {
              // 跳转到首页
              this.goToHome();
            }
          }, 2000);
        } catch (error) {
          // 显示错误提示
          showError(error.message || '登录失败，请重试', '登录失败');
          
          // 根据错误类型设置具体的错误信息
          if (error.message && (error.message.includes('验证码') || error.message.includes('Incorrect captcha'))) {
            // 如果是验证码错误，提供友好的中文提示
            this.verificationCodeError = error.message.includes('Incorrect captcha') ? '验证码不正确，请重新输入' : error.message;
            // 刷新验证码，让用户可以立即重新输入，同时保留错误提示
            this.refreshCaptcha(true);
          } else if (error.message && error.message.includes('Email does not exist')) {
            // 邮箱不存在错误，显示在邮箱输入框下方
            this.emailError = '邮箱不存在';
          } else if (error.message && error.message.includes('Password is incorrect')) {
            // 密码错误，显示在密码输入框下方
            this.passwordError = '密码错误';
          } else if (error.message && ((error.message.includes('邮箱') || error.message.includes('账号') || error.message.includes('Email')) && !error.message.includes('密码') && !error.message.includes('password'))) {
            // 只包含邮箱相关词汇的错误，显示在邮箱输入框下方
            this.emailError = error.message.includes('Invalid email') ? '邮箱格式不正确或不存在' : error.message;
          } else if (error.message && ((error.message.includes('密码') || error.message.includes('password') || error.message.includes('Password')) || error.message.includes('邮箱或密码') || error.message.includes('Invalid email or password'))) {
            // 包含密码相关词汇或同时包含邮箱和密码的错误，显示在密码输入框下方
            this.passwordError = error.message.includes('password') || error.message.includes('Password') || error.message.includes('Invalid email or password') ? '邮箱或密码错误' : error.message;
          }
          
          // 恢复按钮状态
          if (loginButton) {
            loginButton.disabled = false;
            loginButton.innerHTML = '登录 <svg class="w-5 h-5 ml-2 transform hover:translate-x-1 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 5l7 7m0 0l-7 7m7-7H3"></path></svg>';
          }
        }
      }
    },

    // 实时验证邮箱
    validateEmail() {
      if (!this.email) {
        this.emailError = '请输入邮箱地址';
      } else if (!this.isValidEmail(this.email)) {
        this.emailError = '请输入有效的邮箱地址';
      } else {
        this.emailError = '';
      }
    },

    // 实时验证密码
    validatePassword() {
      if (!this.password) {
        this.passwordError = '请输入密码';
      } else if (this.password.length < 6) {
        this.passwordError = '密码长度不能少于6位';
      } else {
        this.passwordError = '';
      }
    },

    // 实时验证验证码
    validateVerificationCode() {
      if (!this.verificationCode) {
        this.verificationCodeError = '请输入验证码';
      } else if (this.verificationCode.length < 4) {
        this.verificationCodeError = '验证码长度不足';
      } else {
        this.verificationCodeError = '';
      }
    },

    // 邮箱验证规则
    isValidEmail(email) {
      const re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
      return re.test(email);
    },

    // 发送重置密码验证码
    async sendResetCode() {
      if (!this.forgotEmail) {
        this.forgotEmailError = '请输入邮箱地址';
        return;
      } else if (!this.isValidEmail(this.forgotEmail)) {
        this.forgotEmailError = '请输入有效的邮箱地址';
        return;
      } else {
        this.forgotEmailError = '';
      }

      this.isSendingCode = true;

      try {
          // 调用后端发送验证码API
          await authAPI.sendResetPasswordCode(this.forgotEmail);
          this.isSendingCode = false;
          this.codeCountdown = 60;
          this.startCodeCountdown();
          
          // 显示成功提示
          showSuccess('验证码已发送到您的邮箱，请查收', '发送成功');
      } catch (error) {
        this.isSendingCode = false;
        // 显示错误提示
        showError(error.message || '发送验证码失败，请重试', '发送失败');
      }
    },

    // 验证码倒计时
    startCodeCountdown() {
      const timer = setInterval(() => {
        this.codeCountdown--;
        if (this.codeCountdown <= 0) {
          clearInterval(timer);
        }
      }, 1000);
    },

    // 重置密码
    async resetPassword() {
      let isValid = true;

      if (!this.forgotEmail) {
        this.forgotEmailError = '请输入邮箱地址';
        isValid = false;
      } else if (!this.isValidEmail(this.forgotEmail)) {
        this.forgotEmailError = '请输入有效的邮箱地址';
        isValid = false;
      } else {
        this.forgotEmailError = '';
      }

      if (!this.resetCode) {
        this.resetCodeError = '请输入验证码';
        isValid = false;
      } else {
        this.resetCodeError = '';
      }

      if (!this.newPassword) {
        this.newPasswordError = '请设置新密码';
        isValid = false;
      } else if (this.newPassword.length < 6) {
        this.newPasswordError = '密码长度不能少于6位';
        isValid = false;
      } else {
        this.newPasswordError = '';
      }

      if (isValid) {
        try {
          // 调用后端重置密码API
          await authAPI.resetPassword(this.forgotEmail, this.resetCode, this.newPassword);
          
          // 显示成功提示
          showSuccess('密码重置成功，请使用新密码登录', '重置成功');
          
          setTimeout(() => {
            this.showForgotPasswordModal = false;
            // 重置表单
            this.forgotEmail = '';
            this.resetCode = '';
            this.newPassword = '';
          }, 2000);
        } catch (error) {
          // 显示错误提示
          showError(error.message || '密码重置失败，请重试', '重置失败');
        }
      }
    },

    // 发送注册验证码
    async sendRegisterCode() {
      if (!this.registerEmail) {
        this.registerEmailError = '请输入邮箱地址';
        return;
      } else if (!this.isValidEmail(this.registerEmail)) {
        this.registerEmailError = '请输入有效的邮箱地址';
        return;
      } else {
        this.registerEmailError = '';
      }

      this.isRegisterSendingCode = true;

      try {
        // 调用后端发送注册验证码API
        await authAPI.sendRegisterCode(this.registerEmail);
        this.isRegisterSendingCode = false;
        this.registerCodeCountdown = 60;
        this.startRegisterCodeCountdown();
        
        // 显示成功提示
        showSuccess('注册验证码已发送到您的邮箱，请查收', '发送成功');
      } catch (error) {
        this.isRegisterSendingCode = false;
        // 显示错误提示
        showError(error.message || '发送注册验证码失败，请重试', '发送失败');
      }
    },

    // 注册验证码倒计时
    startRegisterCodeCountdown() {
      const timer = setInterval(() => {
        this.registerCodeCountdown--;
        if (this.registerCodeCountdown <= 0) {
          clearInterval(timer);
        }
      }, 1000);
    },

    // 注册账号
    async registerAccount() {
      let isValid = true;

      if (!this.registerEmail) {
        this.registerEmailError = '请输入邮箱地址';
        isValid = false;
      } else if (!this.isValidEmail(this.registerEmail)) {
        this.registerEmailError = '请输入有效的邮箱地址';
        isValid = false;
      } else {
        this.registerEmailError = '';
      }

      if (!this.registerCode) {
        this.registerCodeError = '请输入验证码';
        isValid = false;
      } else {
        this.registerCodeError = '';
      }

      if (!this.registerPassword) {
        this.registerPasswordError = '请设置密码';
        isValid = false;
      } else if (this.registerPassword.length < 6) {
        this.registerPasswordError = '密码长度不能少于6位';
        isValid = false;
      } else {
        this.registerPasswordError = '';
      }

      if (!this.confirmPassword) {
        this.confirmPasswordError = '请再次输入密码';
        isValid = false;
      } else if (this.confirmPassword !== this.registerPassword) {
        this.confirmPasswordError = '两次输入的密码不一致';
        isValid = false;
      } else {
        this.confirmPasswordError = '';
      }

      if (!this.agreeTerms) {
        this.termsError = '请阅读并同意服务条款和隐私政策';
        isValid = false;
      } else {
        this.termsError = '';
      }

      if (isValid) {
        try {
          // 调用后端注册API
          await authAPI.registerAccount(this.registerEmail, this.registerCode, this.registerPassword, this.confirmPassword, this.agreeTerms);
          
          // 显示成功提示
          showSuccess('注册成功，请登录', '注册成功');
          
          setTimeout(() => {
            this.showRegisterModal = false;
            // 重置表单
            this.registerEmail = '';
            this.registerCode = '';
            this.registerPassword = '';
            this.confirmPassword = '';
            this.agreeTerms = false;
          }, 2000);
        } catch (error) {
          // 显示错误提示
          showError(error.message || '注册失败，请重试', '注册失败');
        }
      }
    },

    // 添加goToHome方法
    goToHome() {
      this.$router.push('/home');
    },

    // 输入框交互反馈方法
    onInputFocus(event) {
      // 添加聚焦时的视觉反馈
      event.target.parentElement.classList.add('input-focused');
    },

    onInputBlur(event) {
      // 移除聚焦时的视觉反馈
      event.target.parentElement.classList.remove('input-focused');
    },

    // 显示成功Toast
    showSuccessToast(message) {
      this.toastMessage = message;
      this.toastType = 'success';
      this.showToast = true;
      setTimeout(() => {
        this.showToast = false;
      }, 3000);
    },

    // 显示错误Toast
    showErrorToast(message) {
      this.toastMessage = message;
      this.toastType = 'error';
      this.showToast = true;
      setTimeout(() => {
        this.showToast = false;
      }, 3000);
    },

    async initFeishuLogin() {
      try {
        this.isFeishuLoading = true;
        // 检查是否有飞书登录API
        if (authAPI.getFeishuQrCode) {
          const response = await authAPI.getFeishuQrCode();
          if (response && response.qrCodeUrl) {
            this.feishuQrCodeUrl = response.qrCodeUrl;
            this.feishuState = response.state;
          } else {
            console.warn('飞书二维码响应格式不正确:', response);
            this.feishuQrCodeUrl = '';
          }
        } else {
          console.warn('飞书登录API未实现');
          this.feishuQrCodeUrl = '';
        }
      } catch (error) {
        console.error('获取飞书二维码失败:', error);
        this.feishuQrCodeUrl = '';
        // 不显示错误提示，因为这是可选功能
      } finally {
        this.isFeishuLoading = false;
      }
    },

    async handleFeishuCallback() {
      // 检查URL参数中是否有飞书回调
      const urlParams = new URLSearchParams(window.location.search);
      const code = urlParams.get('code');
      const state = urlParams.get('state');
      
      if (code && state && state === this.feishuState) {
        try {
          this.isFeishuLoading = true;
          // 检查是否有飞书回调处理API
          if (authAPI.handleFeishuCallback) {
            const response = await authAPI.handleFeishuCallback(code, state);
            if (response && response.success) {
              showSuccess('飞书登录成功，正在跳转...');
              // 保存登录信息并跳转
              if (response.token && response.user) {
                saveAuthInfo(response.user, response.token);
                setTimeout(() => {
                  this.goToHome();
                }, 1500);
              }
            } else {
              showError('飞书登录失败，请重试');
            }
          } else {
            console.warn('飞书回调处理API未实现');
            showError('飞书登录功能暂未完全实现');
          }
        } catch (error) {
          console.error('处理飞书回调失败:', error);
          showError('飞书登录失败，请重试');
        } finally {
          this.isFeishuLoading = false;
        }
      }
    },

    async bindFeishuEmail() {
      if (!this.feishuBindEmail) {
        this.feishuBindEmailError = '请输入邮箱地址';
        return;
      }

      if (!this.isValidEmail(this.feishuBindEmail)) {
        this.feishuBindEmailError = '请输入有效的邮箱地址';
        return;
      }

      if (!this.feishuTempToken) {
        showError('临时令牌无效，请重新扫码登录');
        return;
      }

      try {
        this.isFeishuLoading = true;
        // 检查是否有飞书邮箱绑定API
        if (authAPI.bindFeishuEmail) {
          const response = await authAPI.bindFeishuEmail(this.feishuTempToken, this.feishuBindEmail);
          
          if (response && response.success) {
            // 保存登录信息
            saveAuthInfo(response.user, response.token);
            showSuccess('飞书账号绑定成功！');
            setTimeout(() => {
              this.goToHome();
            }, 1500);
          } else {
            showError(response.message || '绑定飞书账号失败，请重试');
          }
        } else {
          console.warn('飞书邮箱绑定API未实现');
          showError('飞书邮箱绑定功能暂未实现');
        }
      } catch (error) {
        console.error('绑定飞书账号失败:', error);
        showError(error.message || '绑定飞书账号失败，请重试');
      } finally {
        this.isFeishuLoading = false;
      }
    },

    validateFeishuEmail() {
      if (!this.feishuBindEmail) {
        this.feishuBindEmailError = '请输入邮箱地址';
      } else if (!this.isValidEmail(this.feishuBindEmail)) {
        this.feishuBindEmailError = '请输入有效的邮箱地址';
      } else {
        this.feishuBindEmailError = '';
      }
    },

    // 启动核心功能轮播
    startFeatureCarousel() {
      this.featureCarouselTimer = setInterval(() => {
        this.currentFeatureIndex = (this.currentFeatureIndex + 1) % 5;
      }, 3000); // 每3秒切换一次
    },

    // 获取功能卡片的样式类
    getFeatureCardClass(index) {
      const position = (index - this.currentFeatureIndex + 5) % 5;
      return {
        'opacity-100': position === 0,
        'opacity-70': position === 1,
        'opacity-60': position === 2,
        'opacity-50': position === 3,
        'opacity-40': position === 4
      };
    },

    // 获取功能卡片的样式
    getFeatureCardStyle(index) {
      const position = (index - this.currentFeatureIndex + 5) % 5;
      const config = this.featurePositions[position];
      
      return {
        transform: `translate(${config.x}px, ${config.y}px) scale(${config.scale})`,
        opacity: config.opacity,
        zIndex: config.zIndex
      };
    }
  },
  computed: {
    // 验证码按钮文本
    codeButtonText() {
      return this.codeCountdown > 0 ? `${this.codeCountdown}s后重发` : '获取验证码';
    },

    // 验证码按钮是否可点击
    isCodeButtonDisabled() {
      return this.codeCountdown > 0 || this.isSendingCode;
    },

    // 注册验证码按钮是否可点击
    isRegisterCodeButtonDisabled() {
      return this.registerCodeCountdown > 0 || this.isRegisterSendingCode;
    },

    // 注册验证码按钮文本
    registerCodeButtonText() {
      return this.registerCodeCountdown > 0 ? `${this.registerCodeCountdown}s后重发` : '获取验证码';
    }
  }
};
</script>

<style scoped>
/* 动画效果 */
@keyframes pulse-slow {
  0%, 100% {
    transform: scale(1);
    opacity: 0.6;
  }
  50% {
    transform: scale(1.1);
    opacity: 1;
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-15px);
  }
}

@keyframes float-right {
  0%, 100% {
    transform: translateY(0) translateX(0);
  }
  33% {
    transform: translateY(-10px) translateX(5px);
  }
  66% {
    transform: translateY(5px) translateX(-5px);
  }
}

@keyframes bg-move {
  0% {
    background-position: 0% 0%;
  }
  50% {
    background-position: 100% 100%;
  }
  100% {
    background-position: 0% 0%;
  }
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes data-count {
  from {
    opacity: 0;
    transform: translateY(20px) scale(0.8);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes shine {
  0% {
    opacity: 0.8;
  }
  50% {
    opacity: 1;
    text-shadow: 0 0 10px rgba(255, 255, 255, 0.3);
  }
  100% {
    opacity: 0.8;
  }
}

.animate-pulse-slow {
  animation: pulse-slow 8s ease-in-out infinite;
}

.animate-float {
  animation: float 6s ease-in-out infinite;
}

.animate-float-right {
  animation: float-right 8s ease-in-out infinite;
}

.animate-bg-move {
  animation: bg-move 20s ease-in-out infinite;
}

.animate-slideIn {
  animation: slideIn 0.3s ease-out forwards;
}

/* 新增动画效果 */
.animate-breathe {
  animation: breathe 3s ease-in-out infinite;
}

.animate-text-reveal {
  opacity: 0;
  animation: text-reveal 0.8s ease-out forwards;
}

.animate-float-slow {
  animation: float-slow 10s ease-in-out infinite;
}

.animate-card-enter {
  opacity: 0;
  animation: card-enter 0.6s ease-out forwards;
}

/* 径向渐变背景 */
.bg-gradient-radial {
  background: radial-gradient(circle at center, var(--tw-gradient-stops));
}

@keyframes breathe {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

@keyframes text-reveal {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes float-slow {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  33% {
    transform: translateY(-10px) rotate(1deg);
  }
  66% {
    transform: translateY(5px) rotate(-1deg);
  }
}

@keyframes card-enter {
  from {
    opacity: 0;
    transform: translateY(20px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.animate-fadeIn {
  animation: fadeIn 0.2s ease-out forwards;
}

.animate-data-count {
  animation: data-count 0.6s ease-out forwards;
}

.animate-shine {
  animation: shine 3s ease-in-out infinite;
}

/* 禁止文本选择 */
.no-select {
  user-select: none;
  -webkit-user-select: none;
}

/* 新增动画效果 */
@keyframes breathe {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.02);
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-15px) rotate(2deg);
  }
}

@keyframes page-enter {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slide-down {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fade-in-up {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fade-in {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes shake {
  0%, 100% {
    transform: translateX(0);
  }
  10%, 30%, 50%, 70%, 90% {
    transform: translateX(-2px);
  }
  20%, 40%, 60%, 80% {
    transform: translateX(2px);
  }
}

@keyframes toast-enter {
  from {
    opacity: 0;
    transform: translateX(100%) scale(0.8);
  }
  to {
    opacity: 1;
    transform: translateX(0) scale(1);
  }
}

/* 动画类 */
.animate-breathe {
  animation: breathe 3s ease-in-out infinite;
}

.animate-float {
  animation: float 6s ease-in-out infinite;
}

.animate-page-enter {
  animation: page-enter 0.8s ease-out forwards;
}

.animate-slide-down {
  animation: slide-down 0.6s ease-out forwards;
}

.animate-fade-in-up {
  animation: fade-in-up 0.6s ease-out forwards;
  opacity: 0;
}

.animate-fade-in {
  animation: fade-in 0.4s ease-out forwards;
  opacity: 0;
}

.animate-shake {
  animation: shake 0.5s ease-in-out;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .grid-cols-2 {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
  
  .text-5xl {
    font-size: 2.5rem;
  }
  
  .text-6xl {
    font-size: 3rem;
  }
  
  .space-y-3 > * + * {
    margin-top: 0.75rem;
  }
}

.animate-toast-enter {
  animation: toast-enter 0.3s ease-out forwards;
}

/* 输入框聚焦状态 */
.input-focused {
  transform: scale(1.02);
  transition: transform 0.2s ease-out;
}

/* ==================== 现代化UI增强样式 ==================== */

/* 增强的玻璃态效果 */
.glass-effect {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.glass-effect-dark {
  background: rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

/* 增强的按钮样式 */
.btn-primary-enhanced {
  background: linear-gradient(135deg, #8b5cf6 0%, #06b6d4 100%);
  border: none;
  border-radius: 12px;
  color: white;
  font-weight: 600;
  padding: 14px 28px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 16px rgba(139, 92, 246, 0.3);
  position: relative;
  overflow: hidden;
}

.btn-primary-enhanced::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.btn-primary-enhanced:hover::before {
  left: 100%;
}

.btn-primary-enhanced:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(139, 92, 246, 0.4);
}

.btn-primary-enhanced:active {
  transform: translateY(0);
}

/* 增强的输入框样式 */
.input-enhanced {
  background: rgba(255, 255, 255, 0.95);
  border: 2px solid rgba(139, 92, 246, 0.1);
  border-radius: 12px;
  padding: 16px 20px;
  font-size: 16px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.input-enhanced:focus {
  border-color: #8b5cf6;
  box-shadow: 0 0 0 4px rgba(139, 92, 246, 0.1);
  outline: none;
  transform: translateY(-1px);
}

.input-enhanced::placeholder {
  color: #9ca3af;
  font-weight: 400;
}

/* 增强的卡片样式 */
.card-enhanced {
  background: rgba(255, 255, 255, 0.98);
  border-radius: 20px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.card-enhanced:hover {
  transform: translateY(-4px);
  box-shadow: 0 30px 60px rgba(0, 0, 0, 0.15);
}

/* 标签页样式增强 */
.tab-enhanced {
  position: relative;
  padding: 12px 24px;
  border-radius: 12px;
  font-weight: 500;
  transition: all 0.3s ease;
  overflow: hidden;
}

.tab-enhanced::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #8b5cf6 0%, #06b6d4 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.tab-enhanced.active::before {
  opacity: 1;
}

.tab-enhanced > * {
  position: relative;
  z-index: 1;
}

.tab-enhanced.active {
  color: white;
  box-shadow: 0 4px 16px rgba(139, 92, 246, 0.3);
}

/* 模态框增强样式 */
.modal-enhanced {
  background: rgba(255, 255, 255, 0.98);
  border-radius: 20px;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.modal-overlay-enhanced {
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(4px);
}

/* 新增动画效果 */
@keyframes pulse-slow {
  0%, 100% { opacity: 0.3; }
  50% { opacity: 0.6; }
}

@keyframes bg-move {
  0% { transform: translateX(0) translateY(0); }
  25% { transform: translateX(10px) translateY(-10px); }
  50% { transform: translateX(-5px) translateY(5px); }
  75% { transform: translateX(-10px) translateY(-5px); }
  100% { transform: translateX(0) translateY(0); }
}

@keyframes glow {
  0%, 100% { box-shadow: 0 0 20px rgba(139, 92, 246, 0.3); }
  50% { box-shadow: 0 0 30px rgba(139, 92, 246, 0.5); }
}

.animate-pulse-slow {
  animation: pulse-slow 3s ease-in-out infinite;
}

.animate-bg-move {
  animation: bg-move 20s ease-in-out infinite;
}

.animate-glow {
  animation: glow 2s ease-in-out infinite;
}

/* 渐变背景 */
.bg-gradient-radial {
  background: radial-gradient(circle, var(--brand-primary) 0%, transparent 70%);
}

/* 响应式优化 */
@media (max-width: 768px) {
  .card-enhanced {
    border-radius: 16px;
    margin: 16px;
  }
  
  .btn-primary-enhanced {
    padding: 12px 24px;
    font-size: 16px;
  }
  
  .input-enhanced {
    padding: 14px 16px;
    font-size: 16px;
  }
}

@media (max-width: 640px) {
  .animate-breathe {
    animation: none; /* 在小屏幕上禁用动画以提升性能 */
  }
  
  .animate-bg-move {
    animation: none;
  }
}

/* 深色模式支持 */
@media (prefers-color-scheme: dark) {
  .card-enhanced {
    background: rgba(30, 30, 30, 0.95);
    border: 1px solid rgba(255, 255, 255, 0.1);
  }
  
  .input-enhanced {
    background: rgba(40, 40, 40, 0.95);
    color: white;
    border-color: rgba(139, 92, 246, 0.2);
  }
  
  .input-enhanced::placeholder {
    color: #6b7280;
  }
}

/* 无障碍优化 */
@media (prefers-reduced-motion: reduce) {
  * {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }
}
</style>