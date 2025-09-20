<template>
  <div class="w-screen h-screen flex flex-col overflow-hidden">
    <!-- 移动端顶部Logo -->
    <div class="lg:hidden flex items-center justify-between p-4 bg-white shadow-sm z-10">
      <div class="flex items-center cursor-pointer" @click="goToHome">
        <div class="w-10 h-10 rounded-lg bg-gradient-to-br from-blue-500 to-blue-600 flex items-center justify-center mr-3 shadow-md">
          <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"></path>
          </svg>
        </div>
        <div>
          <h1 class="text-lg font-bold bg-clip-text text-transparent bg-gradient-to-r from-blue-900 to-blue-600">GlobalPulse</h1>
          <p class="text-xs text-blue-500">跨境智能运营平台</p>
        </div>
      </div>
      <div class="text-xs text-gray-500">
        <span v-if="currentTab === 'email'">已有账号</span>
        <span v-else>企业登录</span>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="flex-1 flex flex-col lg:flex-row overflow-hidden">
      <!-- 左侧：视觉区域 - 禁止文案复制 -->
      <div class="hidden lg:flex lg:w-5/12 xl:w-1/2 h-full relative bg-gradient-to-br from-blue-900 via-blue-800 to-blue-950 text-white overflow-hidden">
        <!-- 增强的背景装饰 -->
        <div class="absolute inset-0 overflow-hidden animate-bg-move">
          <!-- 主装饰圆形 -->
          <div class="absolute top-0 right-0 w-[600px] h-[600px] bg-blue-600/10 rounded-full blur-3xl animate-pulse-slow"></div>
          <div class="absolute bottom-0 left-0 w-[600px] h-[600px] bg-blue-500/10 rounded-full blur-3xl animate-pulse-slow" style="animation-delay: 1s;"></div>
          <div class="absolute top-1/3 left-1/4 w-[400px] h-[400px] bg-blue-700/10 rounded-full blur-3xl animate-pulse-slow" style="animation-delay: 2s;"></div>

          <!-- 新增装饰元素 -->
          <div class="absolute top-1/4 right-1/3 w-[100px] h-[100px] bg-blue-400/20 rounded-full blur-xl animate-float"></div>
          <div class="absolute bottom-1/4 left-1/3 w-[150px] h-[150px] bg-blue-300/15 rounded-full blur-xl animate-float" style="animation-delay: 1.5s;"></div>
          <div class="absolute top-2/3 right-1/4 w-[80px] h-[80px] bg-blue-500/20 rounded-full blur-xl animate-float" style="animation-delay: 3s;"></div>
        </div>

        <!-- 网格装饰 -->
        <div class="absolute inset-0 bg-[url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHZpZXdCb3g9IjAgMCA2MCA2MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48ZyBmaWxsPSJub25lIiBmaWxsLXJ1bGU9ImV2ZW5vZGQiPjxnIGZpbGw9IiMyMDIwMjAiIGZpbGwtb3BhY2l0eT0iMC4wNSI+PHBhdGggZD0iTTEwIDBIMHoiLz48cGF0aCBkPSJNMzAgMEg0MHYxMCIvPjxwYXRoIGQ9Ik02MCAzMEg1MHYxMCIvPjxwYXRoIGQ9Ik01MCA2MEg0MHYtMTAiLz48cGF0aCBkPSJNMzAgNUgzMHYxMCIvPjxwYXRoIGQ9Ik0xMCAzMEgwdjEwIi8+PHBhdGggZD0iTTYwIDUwSDUwdjEwIi8+PHBhdGggZD0iTTMwIDYwSDIwdjEwIi8+PC9nPjwvZz48L3N2Zz4=')] opacity-30"></div>

        <!-- 顶部Logo -->
        <div class="absolute top-8 left-8 z-10 flex items-center hidden xl:flex cursor-pointer" @click="goToHome">
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
        <div class="h-full flex flex-col justify-center px-6 sm:px-8 md:px-12 lg:px-16 relative z-10 max-w-2xl mx-auto no-select">
          <!-- 标题 -->
          <div class="mb-12 max-w-lg">
            <h2 class="text-4xl md:text-5xl font-bold mb-4 leading-tight tracking-tight">
              <span class="block bg-clip-text text-transparent bg-gradient-to-r from-white to-blue-200">全球业务</span>
              <span class="block bg-clip-text text-transparent bg-gradient-to-r from-blue-300 to-blue-100 mt-2">一键掌控</span>
            </h2>
            <p class="text-blue-200/90 text-base md:text-lg max-w-md">
              集成多平台管理，智能数据分析，助力企业全球化布局与增长
            </p>
          </div>

          <!-- 核心数据指标 -->
          <div class="grid grid-cols-2 sm:grid-cols-4 gap-5 mb-12">
            <div class="bg-blue-800/20 backdrop-blur-sm rounded-lg p-5 border border-blue-700/30 transform hover:translate-y-[-5px] transition-all duration-300 animate-data-count">
              <div class="text-2xl md:text-3xl font-bold mb-1 bg-clip-text text-transparent bg-gradient-to-r from-white to-blue-200" id="userCount">1.3K+</div>
              <p class="text-blue-300 text-xs">活跃企业用户</p>
            </div>
            <div class="bg-blue-800/20 backdrop-blur-sm rounded-lg p-5 border border-blue-700/30 transform hover:translate-y-[-5px] transition-all duration-300 animate-data-count" style="animation-delay: 0.5s;">
              <div class="text-2xl md:text-3xl font-bold mb-1 bg-clip-text text-transparent bg-gradient-to-r from-white to-blue-200" id="countryCount">19</div>
              <p class="text-blue-300 text-xs">覆盖国家/地区</p>
            </div>
            <div class="bg-blue-800/20 backdrop-blur-sm rounded-lg p-5 border border-blue-700/30 transform hover:translate-y-[-5px] transition-all duration-300 animate-data-count" style="animation-delay: 0.7s;">
              <div class="text-2xl md:text-3xl font-bold mb-1 bg-clip-text text-transparent bg-gradient-to-r from-white to-blue-200" id="platformCount">7</div>
              <p class="text-blue-300 text-xs">集成平台数量</p>
            </div>
            <div class="bg-blue-800/20 backdrop-blur-sm rounded-lg p-5 border border-blue-700/30 transform hover:translate-y-[-5px] transition-all duration-300 animate-data-count" style="animation-delay: 0.9s;">
              <div class="text-2xl md:text-3xl font-bold mb-1 bg-clip-text text-transparent bg-gradient-to-r from-white to-blue-200" id="growthRate">+14%</div>
              <p class="text-blue-300 text-xs">年业务增长率</p>
            </div>
          </div>

          <!-- 平台优势 -->
          <div class="max-w-lg">
            <h3 class="text-xl font-semibold mb-5 text-blue-100 flex items-center animate-shine">
              <span class="w-2 h-6 bg-blue-400 rounded-full mr-3"></span>
              平台核心优势
            </h3>

            <div class="space-y-4">
              <div class="flex items-start group opacity-0 transition-all duration-500" style="animation: slideIn 0.5s ease-out 1s both;">
                <div class="mt-0.5 w-5 h-5 rounded-full bg-blue-600/30 flex items-center justify-center flex-shrink-0 group-hover:bg-blue-500/50 transition-colors">
                  <i class="fa fa-check text-blue-200 text-xs"></i>
                </div>
                <p class="ml-3 text-blue-200 group-hover:text-white transition-colors">多平台数据聚合，统一管理界面</p>
              </div>

              <div class="flex items-start group opacity-0 transition-all duration-500" style="animation: slideIn 0.5s ease-out 1.2s both;">
                <div class="mt-0.5 w-5 h-5 rounded-full bg-blue-600/30 flex items-center justify-center flex-shrink-0 group-hover:bg-blue-500/50 transition-colors">
                  <i class="fa fa-check text-blue-200 text-xs"></i>
                </div>
                <p class="ml-3 text-blue-200 group-hover:text-white transition-colors">AI智能分析，识别市场趋势</p>
              </div>

              <div class="flex items-start group opacity-0 transition-all duration-500" style="animation: slideIn 0.5s ease-out 1.4s both;">
                <div class="mt-0.5 w-5 h-5 rounded-full bg-blue-600/30 flex items-center justify-center flex-shrink-0 group-hover:bg-blue-500/50 transition-colors">
                  <i class="fa fa-check text-blue-200 text-xs"></i>
                </div>
                <p class="ml-3 text-blue-200 group-hover:text-white transition-colors">实时数据同步，业务状态一目了然</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 底部信息 -->
        <div class="absolute bottom-6 left-0 right-0 px-6 flex flex-col md:flex-row justify-between items-center z-10 no-select">
          <div class="text-blue-400/70 text-sm mb-3 md:mb-0">© 2023 GlobalPulse. 保留所有权利</div>
          <div class="flex space-x-6">
            <a href="#" class="text-blue-400/70 hover:text-white text-sm transition-colors">服务条款</a>
            <a href="#" class="text-blue-400/70 hover:text-white text-sm transition-colors">隐私政策</a>
            <a href="#" class="text-blue-400/70 hover:text-white text-sm transition-colors">联系我们</a>
          </div>
        </div>
      </div>

      <!-- 右侧：登录区 - 增大宽度 -->
      <div class="w-full lg:w-7/12 xl:w-1/2 h-full flex items-center justify-center bg-gray-50 p-4 md:p-6 relative overflow-hidden">
        <!-- 增强的右侧背景装饰 -->
        <div class="absolute -top-64 -right-64 w-[500px] h-[500px] bg-blue-100 rounded-full opacity-70 animate-float-right"></div>
        <div class="absolute -bottom-64 -left-64 w-[500px] h-[500px] bg-blue-200 rounded-full opacity-50 animate-float-right" style="animation-delay: 1s;"></div>

        <!-- 新增装饰元素 -->
        <div class="absolute top-1/4 left-1/4 w-[200px] h-[200px] bg-blue-50 rounded-full opacity-60 animate-float-right" style="animation-delay: 2s;"></div>
        <div class="absolute bottom-1/3 right-1/3 w-[150px] h-[150px] bg-indigo-50 rounded-full opacity-70 animate-float-right" style="animation-delay: 3s;"></div>

        <!-- 更多装饰元素 -->
        <div class="absolute top-1/2 right-1/4 w-[80px] h-[80px] bg-purple-50 rounded-full opacity-50 animate-float-right" style="animation-delay: 1.5s;"></div>
        <div class="absolute bottom-1/4 left-1/3 w-[60px] h-[60px] bg-teal-50 rounded-full opacity-60 animate-float-right" style="animation-delay: 2.5s;"></div>

        <!-- 登录卡片 - 增大宽度 -->
        <div class="w-full max-w-lg relative z-10">
          <div class="bg-white rounded-2xl shadow-xl overflow-hidden border border-gray-100 transform transition-all duration-300 hover:shadow-2xl">
            <!-- 顶部装饰条 -->
            <div class="h-1.5 bg-gradient-to-r from-blue-600 to-blue-400"></div>

            <!-- 登录内容 -->
            <div class="p-6 md:p-8">
              <h2 class="text-xl md:text-2xl font-bold text-gray-900 mb-3 md:mb-5">账户登录</h2>

              <!-- 登录标签页 -->
              <div class="flex space-x-4 md:space-x-6 mb-3 md:mb-5">
                <button
                    @click="currentTab = 'email'"
                    class="pb-2 md:pb-3 px-3 md:px-4 font-medium transition-all relative border-b-2"
                    :class="currentTab === 'email' ? 'text-blue-700 border-blue-600' : 'text-gray-500 hover:text-gray-700 border-transparent hover:border-gray-300'"
                >
                  邮箱登录
                </button>
                <button
                    @click="currentTab = 'feishu'"
                    class="pb-2 md:pb-3 px-3 md:px-4 font-medium transition-all relative border-b-2"
                    :class="currentTab === 'feishu' ? 'text-blue-700 border-blue-600' : 'text-gray-500 hover:text-gray-700 border-transparent hover:border-gray-300'"
                >
                  飞书登录
                </button>
              </div>

              <!-- 登录区域容器 - 固定高度确保两个标签页完全一致 -->
              <div class="h-[430px] flex flex-col">
                <!-- 邮箱登录表单 -->
                <div v-if="currentTab === 'email'" class="space-y-3 md:space-y-4 animate-slideIn flex-grow">
                  <div>
                    <label for="email" class="block text-sm font-medium text-gray-700 mb-1">邮箱地址</label>
                    <div class="relative">
                      <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                        <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"></path>
                        </svg>
                      </div>
                      <input
                          type="email"
                          v-model="email"
                          id="email"
                          placeholder="请输入您的邮箱"
                          class="w-full pl-10 pr-4 py-3 border md:py-3.5 border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all text-gray-900 bg-gray-50"
                          :class="emailError ? 'border-red-400 focus:ring-red-300 focus:border-red-300' : ''"
                          @input="validateEmail"
                      />
                    </div>
                    <span v-if="emailError" class="text-red-500 text-xs mt-1 block">{{ emailError }}</span>
                  </div>

                  <div>
                    <label for="password" class="block text-sm font-medium text-gray-700 mb-1">密码</label>
                    <div class="relative">
                      <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                        <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"></path>
                        </svg>
                      </div>
                      <input
                          type="password"
                          v-model="password"
                          id="password"
                          placeholder="请输入您的密码"
                          class="w-full pl-10 pr-4 py-3 border md:py-3.5 border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all text-gray-900 bg-white shadow-sm placeholder-gray-400"
                          :class="passwordError ? 'border-red-400 focus:ring-red-300 focus:border-red-300' : ''"
                          @input="validatePassword"
                      />
                    </div>
                    <span v-if="passwordError" class="text-red-500 text-xs mt-1 block">{{ passwordError }}</span>
                  </div>

                  <!-- 验证码区域 -->
                  <div>
                    <label for="verificationCode" class="block text-sm font-medium text-gray-700 mb-1">验证码</label>
                    <div class="flex space-x-2 md:space-x-3">
                      <div class="relative flex-1 min-w-0">
                        <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                          <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"></path>
                          </svg>
                        </div>
                        <input
                            type="text"
                            v-model="verificationCode"
                            id="verificationCode"
                            placeholder="请输入验证码"
                            class="w-full pl-10 pr-4 py-3 border md:py-3.5 border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all text-gray-900 bg-white shadow-sm placeholder-gray-400"
                            :class="verificationCodeError ? 'border-red-400 focus:ring-red-300 focus:border-red-300' : ''"
                            @input="validateVerificationCode"
                        />
                      </div>
                      <div class="w-40 h-14 bg-gray-100 rounded-lg overflow-hidden flex items-center justify-center relative flex-shrink-0">
                        <img :src="captchaImage" alt="验证码图片，包含随机字符用于验证用户身份" class="w-full h-full object-cover" />
                        <button
                            type="button"
                            @click="refreshCaptcha"
                            class="absolute inset-0 bg-black/30 text-white text-xs flex items-center justify-center opacity-0 hover:opacity-100 transition-opacity"
                            aria-label="刷新验证码图片"
                        >
                          换一张
                        </button>
                      </div>
                    </div>
                    <span class="text-gray-500 text-xs block mt-1">点击图片可刷新验证码</span>
                    <span v-if="verificationCodeError" class="text-red-500 text-xs mt-1 block">{{ verificationCodeError }}</span>
                  </div>

                  <div class="flex items-center justify-between">
                    <label class="flex items-center cursor-pointer">
                      <input type="checkbox" v-model="rememberMe" class="h-4 w-4 text-blue-600 rounded border-gray-300 focus:ring-blue-500" />
                      <span class="ml-2 text-sm text-gray-700">记住我 30 天</span>
                    </label>
                    <button
                        type="button"
                        class="text-sm text-blue-600 hover:text-blue-800 transition-colors"
                        @click="showForgotPasswordModal = true"
                    >
                      忘记密码?
                    </button>
                  </div>

                  <!-- 登录按钮 -->
                  <button
                      @click="handleLogin"
                      class="w-full bg-blue-600 text-white py-3 md:py-3.5 rounded-lg font-medium hover:bg-blue-700 transition-all flex items-center justify-center mt-3 md:mt-4 transform active:scale-[0.98] shadow-md hover:shadow-lg"
                  >
                    登录
                    <svg class="w-5 h-5 ml-2 transform hover:translate-x-1 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 5l7 7m0 0l-7 7m7-7H3"></path>
                    </svg>
                  </button>
                </div>

                <!-- 飞书登录 - 增大二维码 -->
                <div v-if="currentTab === 'feishu'" class="flex flex-col items-center animate-slideIn h-full justify-between">
                  <div class="w-full">
                    <!-- 飞书二维码容器 -->
                    <div class="w-full bg-gray-50 rounded-xl flex items-center justify-center mb-6 border border-gray-200 shadow-sm relative py-4">
                      <div class="text-center px-4">
                        <p class="text-sm text-gray-500 mb-3">通过飞书账号登录，享受更便捷的服务</p>
                        
                        <!-- 动态二维码 -->
                        <div v-if="isFeishuLoading" class="w-64 h-64 flex items-center justify-center">
                          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
                        </div>
                        
                        <div v-else-if="feishuQrCodeUrl" class="w-64 h-64 mx-auto">
                          <iframe 
                            :src="feishuQrCodeUrl" 
                            class="w-full h-full border-0 rounded-lg"
                            title="飞书登录二维码">
                          </iframe>
                        </div>
                        
                        <div v-else class="w-64 h-64 flex items-center justify-center text-gray-400">
                          <div class="text-center">
                            <svg class="w-16 h-16 mx-auto mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.732-.833-2.5 0L4.268 18.5c-.77.833.192 2.5 1.732 2.5z"></path>
                            </svg>
                            <p class="text-sm">二维码加载失败</p>
                            <button @click="initFeishuLogin" class="mt-2 text-blue-600 hover:text-blue-800 text-sm">
                              重新加载
                            </button>
                          </div>
                        </div>
                        
                        <div class="absolute bottom-0 left-0 right-0 bg-blue-800 text-white text-xs text-center py-2 rounded-b-xl">
                          扫码登录飞书账号
                        </div>
                      </div>
                    </div>

                    <button class="w-full flex items-center justify-center px-4 py-3 md:py-3.5 border border-gray-300 rounded-lg text-gray-800 hover:bg-gray-50 transition-all shadow-sm hover:shadow mb-4">
                      <svg class="w-5 h-5 mr-2.5 text-blue-600" viewBox="0 0 24 24" fill="currentColor">
                        <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"></path>
                      </svg>
                      打开飞书客户端扫码
                    </button>
                  </div>

                  <!-- 精确匹配邮箱登录表单高度的占位区域 -->
                  <div class="w-full space-y-4 mt-auto">
                    <div class="h-4"></div> <!-- 模拟验证码下方的说明文字高度 -->
                    <div class="h-8"></div> <!-- 模拟"记住我"和"忘记密码"区域高度 -->
                    <div class="h-12"></div> <!-- 模拟登录按钮高度 -->
                  </div>
                </div>
              </div>

              <!-- 其他登录方式 -->
              <div class="mt-8 md:mt-10">
                <div class="relative flex items-center justify-center">
                  <div class="h-px w-full bg-gray-200"></div>
                  <span class="absolute px-4 bg-white text-gray-500 text-sm font-medium">其他登录方式</span>
                </div>

                <div class="flex justify-center space-x-5 md:space-x-6 mt-4 md:mt-5">
                  <button class="w-10 h-10 rounded-full bg-white border border-gray-200 flex items-center justify-center text-blue-600 hover:bg-blue-50 transition-all transform hover:scale-110 active:scale-95 shadow-sm" aria-label="使用Google账号登录">
                    <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                      <path d="M22.675 0h-21.35c-.732 0-1.325.593-1.325 1.325v21.351c0 .731.593 1.324 1.325 1.324h11.495v-9.294h-3.128v-3.622h3.128v-2.671c0-3.1 1.893-4.788 4.659-4.788 1.325 0 2.463.099 2.795.143v3.24l-1.918.001c-1.504 0-1.795.715-1.795 1.763v2.313h3.587l-.467 3.622h-3.12v9.293h6.116c.73 0 1.323-.593 1.323-1.325v-21.35c0-.732-.593-1.325-1.325-1.325z"/>
                    </svg>
                  </button>
                  <button class="w-10 h-10 rounded-full bg-white border border-gray-200 flex items-center justify-center text-green-600 hover:bg-green-50 transition-all transform hover:scale-110 active:scale-95 shadow-sm" aria-label="使用微信账号登录">
                    <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                      <path d="M7.107 18.925c.228-.32.498-.609.803-.859.305-.25.647-.463 1.016-.632.37-.169.759-.305 1.16-.398.4-.093.806-.14 1.214-.14.408 0 .814.047 1.214.14.401.093.79.229 1.16.398.369.169.711.382 1.016.632.305.25.575.539.803.859.228.32.427.655.592 1.001.165.346.293.705.379 1.073.086.368.129.742.129 1.121 0 .379-.043.753-.129 1.121.086-.368.214-.727.379-1.073.165-.346.364-.681.592-1.001.228-.32.427-.655.592-1.001-.228-.32-.507-.593-.838-.798-.331-.205-.691-.362-1.075-.465-.384-.103-.782-.155-1.188-.155-.406 0-.804-.052-1.188-.155-.384-.103-.744-.26-1.075-.465-.331-.205-.61-.478-.838-.798-.228-.32-.427-.655-.592-1.001-.165-.346-.293-.705-.379-1.073-.086-.368-.129-.742-.129-1.121 0-.379.043-.753.129-1.121-.086-.368-.214-.727-.379-1.073-.165-.346-.364-.681-.592-1.001-.23-.32-.509-.593-.84-.8zm-2.63-8.518c-.739 0-1.339-.598-1.339-1.338 0-.74.6-1.338 1.339-1.338.74 0 1.338.598 1.338 1.338 0 .74-.598 1.338-1.338 1.338zm3.978-3.247c-.789 0-1.429.64-1.429 1.429 0 .789.64 1.429 1.429 1.429.789 0 1.429-.64 1.429-1.429 0-.789-.64-1.429-1.429-1.429zm7.828 3.247c-.739 0-1.339-.598-1.339-1.338 0-.74.6-1.338 1.339-1.338.74 0 1.338.598 1.338 1.338 0 .74-.598 1.338-1.338 1.338z"/>
                    </svg>
                  </button>
                  <button class="w-10 h-10 rounded-full bg-white border border-gray-200 flex items-center justify-center text-blue-800 hover:bg-blue-50 transition-all transform hover:scale-110 active:scale-95 shadow-sm" aria-label="使用Facebook账号登录">
                    <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                      <path d="M12 0c-6.627 0-12 5.373-12 12s5.373 12 12 12 12-5.373 12-12-5.373-12-12-12zm3 8h-1.35c-.538 0-.65.221-.65.778v1.222h2l-.209 2h-1.791v7h-3v-7h-2v-2h2v-2.308c0-1.769.931-2.692 3.029-2.692h1.971v3z"/>
                    </svg>
                  </button>
                </div>
              </div>

              <!-- 注册提示 -->
              <div class="mt-5 md:mt-7 text-center">
                <span class="text-gray-600">还没有账号? </span>
                <button
                    class="text-blue-600 font-medium hover:text-blue-800 transition-colors"
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
                  <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"></path>
                  </svg>
                </div>
                <input
                    type="email"
                    v-model="forgotEmail"
                    id="forgotEmail"
                    placeholder="请输入您的邮箱"
                    class="w-full pl-10 pr-4 py-3 border md:py-3.5 border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all text-gray-900 bg-gray-50"
                    :class="forgotEmailError ? 'border-red-400 focus:ring-red-300 focus:border-red-300' : ''"
                />
              </div>
              <span v-if="forgotEmailError" class="text-red-500 text-xs mt-1 block">{{ forgotEmailError }}</span>
            </div>

            <div>
              <label for="resetCode" class="block text-sm font-medium text-gray-700 mb-1">验证码</label>
              <div class="flex space-x-2">
                <div class="relative flex-1">
                  <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"></path>
                    </svg>
                  </div>
                  <input
                      type="text"
                      v-model="resetCode"
                      id="resetCode"
                      placeholder="请输入验证码"
                      class="w-full pl-10 pr-4 py-3 border md:py-3.5 border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all text-gray-900 bg-gray-50"
                      :class="resetCodeError ? 'border-red-400 focus:ring-red-300 focus:border-red-300' : ''"
                  />
                </div>
                <button
                    type="button"
                    @click="sendResetCode"
                    class="whitespace-nowrap px-4 py-3 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-all font-medium text-sm"
                    :disabled="isCodeButtonDisabled"
                >
                  {{ isSendingCode ? '发送中...' : codeButtonText }}
                </button>
              </div>
              <span v-if="resetCodeError" class="text-red-500 text-xs mt-1 block">{{ resetCodeError }}</span>
            </div>

            <div>
              <label for="newPassword" class="block text-sm font-medium text-gray-700 mb-1">新密码</label>
              <div class="relative">
                <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"></path>
                  </svg>
                </div>
                <input
                    type="password"
                    v-model="newPassword"
                    id="newPassword"
                    placeholder="请设置新密码"
                    class="w-full pl-10 pr-4 py-3 border md:py-3.5 border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all text-gray-900 bg-gray-50"
                    :class="newPasswordError ? 'border-red-400 focus:ring-red-300 focus:border-red-300' : ''"
                />
              </div>
              <span v-if="newPasswordError" class="text-red-500 text-xs mt-1 block">{{ newPasswordError }}</span>
              <span class="text-gray-500 text-xs block mt-1">密码长度不能少于6位，建议包含字母和数字</span>
            </div>
          </div>

          <div class="flex space-x-3">
            <button
                @click="showForgotPasswordModal = false"
                class="flex-1 py-3 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-all font-medium"
            >
              取消
            </button>
            <button
                @click="resetPassword"
                class="flex-1 py-3 bg-blue-600 text-white rounded-lg font-medium hover:bg-blue-700 transition-all"
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
                  <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"></path>
                  </svg>
                </div>
                <input
                    type="email"
                    v-model="registerEmail"
                    id="registerEmail"
                    placeholder="请输入您的邮箱"
                    class="w-full pl-10 pr-4 py-3 border md:py-3.5 border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all text-gray-900 bg-gray-50"
                    :class="registerEmailError ? 'border-red-400 focus:ring-red-300 focus:border-red-300' : ''"
                />
              </div>
              <span v-if="registerEmailError" class="text-red-500 text-xs mt-1 block">{{ registerEmailError }}</span>
            </div>

            <div>
              <label for="registerCode" class="block text-sm font-medium text-gray-700 mb-1">验证码</label>
              <div class="flex space-x-2">
                <div class="relative flex-1">
                  <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"></path>
                    </svg>
                  </div>
                  <input
                      type="text"
                      v-model="registerCode"
                      id="registerCode"
                      placeholder="请输入验证码"
                      class="w-full pl-10 pr-4 py-3 border md:py-3.5 border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all text-gray-900 bg-gray-50"
                      :class="registerCodeError ? 'border-red-400 focus:ring-red-300 focus:border-red-300' : ''"
                  />
                </div>
                <button
                    type="button"
                    @click="sendRegisterCode"
                    class="whitespace-nowrap px-4 py-3 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-all font-medium text-sm"
                    :disabled="isRegisterCodeButtonDisabled"
                >
                  {{ isRegisterSendingCode ? '发送中...' : registerCodeButtonText }}
                </button>
              </div>
              <span v-if="registerCodeError" class="text-red-500 text-xs mt-1 block">{{ registerCodeError }}</span>
            </div>

            <div>
              <label for="registerPassword" class="block text-sm font-medium text-gray-700 mb-1">设置密码</label>
              <div class="relative">
                <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"></path>
                  </svg>
                </div>
                <input
                    type="password"
                    v-model="registerPassword"
                    id="registerPassword"
                    placeholder="请设置密码"
                    class="w-full pl-10 pr-4 py-3 border md:py-3.5 border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all text-gray-900 bg-gray-50"
                    :class="registerPasswordError ? 'border-red-400 focus:ring-red-300 focus:border-red-300' : ''"
                />
              </div>
              <span v-if="registerPasswordError" class="text-red-500 text-xs mt-1 block">{{ registerPasswordError }}</span>
              <span class="text-gray-500 text-xs block mt-1">密码长度不能少于6位，建议包含字母和数字</span>
            </div>

            <div>
              <label for="confirmPassword" class="block text-sm font-medium text-gray-700 mb-1">确认密码</label>
              <div class="relative">
                <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"></path>
                  </svg>
                </div>
                <input
                    type="password"
                    v-model="confirmPassword"
                    id="confirmPassword"
                    placeholder="请再次输入密码"
                    class="w-full pl-10 pr-4 py-3 border md:py-3.5 border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all text-gray-900 bg-gray-50"
                    :class="confirmPasswordError ? 'border-red-400 focus:ring-red-300 focus:border-red-300' : ''"
                />
              </div>
              <span v-if="confirmPasswordError" class="text-red-500 text-xs mt-1 block">{{ confirmPasswordError }}</span>
            </div>

            <label class="flex items-start cursor-pointer">
              <input type="checkbox" v-model="agreeTerms" class="h-4 w-4 text-blue-600 rounded border-gray-300 focus:ring-blue-500 mt-0.5" />
              <span class="ml-2 text-sm text-gray-600">
                我已阅读并同意<a href="#" class="text-blue-600 hover:underline">服务条款</a>和<a href="#" class="text-blue-600 hover:underline">隐私政策</a>
              </span>
            </label>
            <span v-if="!agreeTerms && termsError" class="text-red-500 text-xs block">{{ termsError }}</span>
          </div>

          <div class="flex space-x-3">
            <button
                @click="showRegisterModal = false"
                class="flex-1 py-3 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-all font-medium"
            >
              取消
            </button>
            <button
                @click="registerAccount"
                class="flex-1 py-3 bg-blue-600 text-white rounded-lg font-medium hover:bg-blue-700 transition-all"
            >
              注册账号
            </button>
          </div>
        </div>
      </div>
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
      rememberMe: false,
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
      registerEmailError: '',
      registerCodeError: '',
      registerPasswordError: '',
      confirmPasswordError: '',
      termsError: '',
      isRegisterSendingCode: false,
      registerCodeCountdown: 0,

      // 飞书登录数据
      feishuQrCodeUrl: '',
      feishuState: '',
      feishuTempToken: '',
      feishuUserInfo: null,
      isFeishuLoading: false,
      feishuBindEmail: '',
      feishuBindEmailError: ''
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
    // 刷新验证码
    async refreshCaptcha(keepError = false) {
      // 正确处理异步的getCaptcha方法
      try {
        this.captchaImage = await authAPI.getCaptcha();
        this.verificationCode = '';
        // 只有在不是因为错误而刷新时，才清除错误提示
        if (!keepError) {
          this.verificationCodeError = '';
        }
      } catch (error) {
        console.error('刷新验证码失败:', error);
        // 提供一个备用的占位图
        this.captchaImage = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTAwIiBoZWlnaHQ9IjQwIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciPjxwYXRoIGZpbGw9IiM3QjVBQUQiIGQ9Ik0wIDBoMTAwdjQwSDB6Ii8+PHJlY3QgeD0iMjAiIHk9IjEwIiByPSIxNSIgZmlsbD0iI0ZGRkZGRiIvPjxwYXRoIGQ9Ik00NSAxMCBMMTUgMzAgTDE1IDEwIE01NSAxMCBMNzUgMzAgTDc1IDEwIi8+PC9zdmc+';
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

      // 获取验证码ID
      const captchaId = localStorage.getItem('captchaId');
      if (!captchaId) {
        this.verificationCodeError = '请先刷新验证码';
        isValid = false;
      }

      if (isValid) {
        // 登录逻辑
        console.log('登录信息处理', {
          email: this.email,
          password: this.password,
          verificationCode: this.verificationCode,
          rememberMe: this.rememberMe
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
          const captchaHash = localStorage.getItem(`captcha_${captchaId}`);
          
          if (captchaHash) {
            // 解码存储的验证码哈希
            const storedCaptcha = atob(decodeURIComponent(escape(captchaHash)));
            // 比较用户输入和存储的验证码（不区分大小写）
            if (this.verificationCode.toUpperCase() !== storedCaptcha.toUpperCase()) {
              this.verificationCodeError = '验证码错误，请重新输入';
              this.refreshCaptcha(true);
              
              // 恢复按钮状态
              if (loginButton) {
                loginButton.disabled = false;
                loginButton.innerHTML = '登录 <svg class="w-5 h-5 ml-2 transform hover:translate-x-1 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 5l7 7m0 0l-7 7m7-7H3"></path></svg>';
              }
              return;
            }
          } else {
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
          const result = await authAPI.login(this.email, this.password, this.verificationCode, this.rememberMe, captchaId);
          
          // 保存用户登录状态
          if (this.rememberMe) {
            localStorage.setItem('userEmail', this.email);
          }

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

    // 飞书登录相关方法
    async initFeishuLogin() {
      try {
        this.isFeishuLoading = true;
        const response = await authAPI.getFeishuQrCode();
        this.feishuQrCodeUrl = response.qrCodeUrl;
        this.feishuState = response.state;
      } catch (error) {
        console.error('获取飞书二维码失败:', error);
        showError('获取飞书登录二维码失败，请稍后重试');
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
          // 这里应该调用后端处理飞书回调
          // 由于前端无法直接调用回调接口，我们需要通过其他方式处理
          showSuccess('飞书登录成功，正在处理...');
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

      if (!this.feishuTempToken) {
        showError('临时令牌无效，请重新扫码登录');
        return;
      }

      try {
        this.isFeishuLoading = true;
        const response = await authAPI.bindFeishuEmail(this.feishuTempToken, this.feishuBindEmail);
        
        // 保存登录信息
        saveAuthInfo(response.token, {
          id: response.userId,
          username: response.username,
          email: response.email,
          role: response.role
        });

        showSuccess('飞书账号绑定成功！');
        this.goToHome();
      } catch (error) {
        console.error('绑定飞书账号失败:', error);
        showError(error.message || '绑定飞书账号失败，请重试');
      } finally {
        this.isFeishuLoading = false;
      }
    },

    validateFeishuEmail() {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!this.feishuBindEmail) {
        this.feishuBindEmailError = '请输入邮箱地址';
      } else if (!emailRegex.test(this.feishuBindEmail)) {
        this.feishuBindEmailError = '请输入有效的邮箱地址';
      } else {
        this.feishuBindEmailError = '';
      }
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

.animate-pulse-slow {
  animation: pulse-slow 8s ease-in-out infinite;
}

.animate-float {
  animation: float 6s ease-in-out infinite;
}

.animate-bg-move {
  animation: bg-move 20s ease-in-out infinite;
}

.animate-slideIn {
  animation: slideIn 0.3s ease-out forwards;
}

.animate-fadeIn {
  animation: fadeIn 0.2s ease-out forwards;
}

/* 禁止文本选择 */
.no-select {
  user-select: none;
  -webkit-user-select: none;
}
</style>
