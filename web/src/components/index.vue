<template>
  <div class="min-h-screen bg-gradient-to-b from-gray-50 to-gray-100">
    <!-- 顶部导航 - 增强的毛玻璃效果 -->
    <header class="fixed top-0 left-0 right-0 bg-white/60 backdrop-blur-xl border-b border-white/30 shadow-sm z-50 transition-all duration-500">
      <div class="container mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center h-16">
          <div class="flex items-center">
            <i class="fas fa-robot text-blue-600 text-xl mr-2"></i>
            <span class="text-xl font-bold bg-gradient-to-r from-blue-600 to-blue-800 bg-clip-text text-transparent">AI智能管家</span>
          </div>
          
          <!-- 桌面端导航 -->
          <nav class="hidden md:flex space-x-8">
            <a href="#features" class="text-gray-700 hover:text-purple-600 text-sm font-medium transition-colors">产品特性</a>
            <a href="#showcase" class="text-gray-700 hover:text-purple-600 text-sm font-medium transition-colors">功能展示</a>
            <a href="#solutions" class="text-gray-700 hover:text-purple-600 text-sm font-medium transition-colors">解决方案</a>
            <a href="#customers" class="text-gray-700 hover:text-purple-600 text-sm font-medium transition-colors">客户案例</a>
            <a href="#pricing" class="text-gray-700 hover:text-purple-600 text-sm font-medium transition-colors">价格方案</a>
          </nav>
          
          <div class="hidden md:flex items-center space-x-4">
            <a href="#" class="text-gray-700 hover:text-purple-600 text-sm font-medium transition-colors">联系我们</a>
            <!-- 登录状态显示 -->
            <div v-if="isLoggedIn" class="flex items-center space-x-2">
              <span class="text-sm font-medium text-gray-700">欢迎，{{ currentUser?.name || '用户' }}</span>
              <button class="text-gray-500 hover:text-gray-700" @click="logout">
                <i class="fas fa-sign-out-alt"></i>
              </button>
            </div>
            <!-- 未登录状态 -->
            <div v-else>
              <button class="px-4 py-2 bg-white text-blue-600 border border-blue-600 px-4 py-2 rounded-lg text-sm font-medium hover:bg-blue-50 transition-colors" @click="goToLogin">
                登录
              </button>
            </div>
            <button class="px-4 py-2 bg-gradient-to-r from-blue-600 to-blue-800 text-white rounded-lg text-sm font-medium hover:opacity-90 transition-opacity" @click="goToProject">进入项目</button>
          </div>
          
          <!-- 移动端菜单按钮 -->
          <button class="md:hidden text-gray-700">
            <i class="fas fa-bars text-xl"></i>
          </button>
        </div>
      </div>
    </header>

    <!-- 英雄区域 -->
    <section class="pt-32 pb-20 px-4 sm:px-6 lg:px-8 bg-gradient-to-br from-purple-50 via-blue-50 to-indigo-50">
      <div class="container mx-auto">
        <div class="flex flex-col lg:flex-row items-center">
          <div class="w-full lg:w-1/2 mb-10 lg:mb-0 lg:pr-10">
            <div class="inline-block px-3 py-1 bg-blue-100 text-blue-800 text-xs font-medium rounded-full mb-4">
              AI赋能 · 全流程自动化
            </div>
            <h1 class="text-4xl sm:text-5xl md:text-6xl font-bold text-gray-900 leading-tight mb-6">
              AI智能管家<br />
              <span class="bg-gradient-to-r from-blue-600 to-blue-800 bg-clip-text text-transparent">一体化管理系统</span>
            </h1>
            <p class="text-lg text-gray-600 mb-8 leading-relaxed">
              集成社交媒体运营、广告投放、商品上架与数据分析，
              AI赋能全流程自动化，助力企业高效运营和业务增长。
            </p>
            <div class="flex flex-col sm:flex-row space-y-4 sm:space-y-0 sm:space-x-4">
              <button class="px-6 py-3 bg-gradient-to-r from-blue-600 to-blue-800 text-white rounded-lg font-medium hover:opacity-90 transition-opacity shadow-lg shadow-blue-500/20" @click="goToProject">
                进入项目
              </button>
              <button class="px-6 py-3 bg-white text-gray-900 border border-gray-300 rounded-lg font-medium hover:bg-gray-50 transition-colors flex items-center justify-center">
                <i class="fas fa-play-circle text-purple-600 mr-2"></i>观看演示
              </button>
            </div>
            <div class="mt-10 flex items-center space-x-6">
              <div class="flex -space-x-2">
                <img src="https://picsum.photos/id/1001/100/100" alt="用户头像" class="w-10 h-10 rounded-full border-2 border-white">
                <img src="https://picsum.photos/id/1002/100/100" alt="用户头像" class="w-10 h-10 rounded-full border-2 border-white">
                <img src="https://picsum.photos/id/1003/100/100" alt="用户头像" class="w-10 h-10 rounded-full border-2 border-white">
                <img src="https://picsum.photos/id/1004/100/100" alt="用户头像" class="w-10 h-10 rounded-full border-2 border-white">
              </div>
              <div>
                <div class="flex items-center mb-1">
                  <div class="flex">
                    <i class="fas fa-star text-yellow-400"></i>
                    <i class="fas fa-star text-yellow-400"></i>
                    <i class="fas fa-star text-yellow-400"></i>
                    <i class="fas fa-star text-yellow-400"></i>
                    <i class="fas fa-star text-yellow-400"></i>
                  </div>
                  <span class="ml-2 text-sm font-medium text-gray-900">4.9/5</span>
                </div>
                <p class="text-xs text-gray-500">来自超过1000+满意客户</p>
              </div>
            </div>
          </div>
          <div class="w-full lg:w-1/2">
            <div class="relative">
              <div class="absolute inset-0 bg-gradient-to-r from-purple-500/20 to-blue-500/20 rounded-2xl blur-3xl"></div>
              <img src="https://picsum.photos/id/1/800/600" alt="平台预览" class="relative z-10 w-full h-auto rounded-2xl shadow-2xl">
              <div class="absolute -bottom-6 -right-6 bg-white p-4 rounded-xl shadow-lg z-20 hidden md:block">
                <div class="flex items-center space-x-2">
                  <div class="w-3 h-3 bg-red-500 rounded-full"></div>
                  <div class="w-3 h-3 bg-yellow-500 rounded-full"></div>
                  <div class="w-3 h-3 bg-green-500 rounded-full"></div>
                </div>
                <div class="mt-2">
                  <p class="text-xs text-gray-900 font-medium">实时数据分析</p>
                  <p class="text-xs text-green-600">+12.5% 性能提升</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 合作伙伴 -->
    <section class="py-12 bg-white">
      <div class="container mx-auto px-4 sm:px-6 lg:px-8">
        <p class="text-center text-sm text-gray-500 mb-8">值得信赖的合作伙伴</p>
        <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-8 items-center justify-items-center">
          <div class="text-gray-400 text-2xl font-bold">公司A</div>
          <div class="text-gray-400 text-2xl font-bold">公司B</div>
          <div class="text-gray-400 text-2xl font-bold">公司C</div>
          <div class="text-gray-400 text-2xl font-bold">公司D</div>
          <div class="text-gray-400 text-2xl font-bold">公司E</div>
          <div class="text-gray-400 text-2xl font-bold">公司F</div>
        </div>
      </div>
    </section>

    <!-- 产品特性 -->
    <section id="features" class="py-20 bg-gray-50">
      <div class="container mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center max-w-3xl mx-auto mb-16">
          <div class="inline-block px-3 py-1 bg-blue-100 text-blue-800 text-xs font-medium rounded-full mb-4">
            核心优势
          </div>
          <h2 class="text-3xl md:text-4xl font-bold text-gray-900 mb-6">
            为什么选择我们的AI智能管理系统
          </h2>
          <p class="text-lg text-gray-600">
            我们提供全方位的AI智能解决方案，从内容创作到广告投放，从商品上架到订单分析，
            赋能全流程自动化，提升运营效率和业务表现。
          </p>
        </div>
        
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          <!-- 特性卡片1 -->
          <div class="bg-white p-8 rounded-xl shadow-sm border border-gray-100 hover:shadow-md transition-shadow">
            <div class="w-14 h-14 bg-blue-100 rounded-lg flex items-center justify-center mb-6">
              <i class="fas fa-post-it text-2xl text-blue-600"></i>
            </div>
            <h3 class="text-xl font-semibold text-gray-900 mb-4">社交媒体管理</h3>
            <p class="text-gray-600">
              统一管理多平台社交媒体帖子发布、评论互动，实时数据分析，提升品牌影响力。
            </p>
          </div>
          
          <!-- 特性卡片2 -->
          <div class="bg-white p-8 rounded-xl shadow-sm border border-gray-100 hover:shadow-md transition-shadow">
            <div class="w-14 h-14 bg-purple-100 rounded-lg flex items-center justify-center mb-6">
              <i class="fas fa-image text-2xl text-purple-600"></i>
            </div>
            <h3 class="text-xl font-semibold text-gray-900 mb-4">AI内容生成</h3>
            <p class="text-gray-600">
              通过AI自动化生成高质量帖子图片、FEED图和短视频，提升内容创作效率。
            </p>
          </div>
          
          <!-- 特性卡片3 -->
          <div class="bg-white p-8 rounded-xl shadow-sm border border-gray-100 hover:shadow-md transition-shadow">
            <div class="w-14 h-14 bg-green-100 rounded-lg flex items-center justify-center mb-6">
              <i class="fas fa-bullhorn text-2xl text-green-600"></i>
            </div>
            <h3 class="text-xl font-semibold text-gray-900 mb-4">广告投放管理</h3>
            <p class="text-gray-600">
              自动化配置和管理广告投放，AI生成广告素材，实时监控广告效果并优化。
            </p>
          </div>
          
          <!-- 特性卡片4 -->
          <div class="bg-white p-8 rounded-xl shadow-sm border border-gray-100 hover:shadow-md transition-shadow">
            <div class="w-14 h-14 bg-amber-100 rounded-lg flex items-center justify-center mb-6">
              <i class="fas fa-shopping-bag text-2xl text-amber-600"></i>
            </div>
            <h3 class="text-xl font-semibold text-gray-900 mb-4">商品AI上架</h3>
            <p class="text-gray-600">
              支持1688商品采集，AI智能定价，自动完成多平台商品上架流程。
            </p>
          </div>
          
          <!-- 特性卡片5 -->
          <div class="bg-white p-8 rounded-xl shadow-sm border border-gray-100 hover:shadow-md transition-shadow">
            <div class="w-14 h-14 bg-red-100 rounded-lg flex items-center justify-center mb-6">
              <i class="fas fa-shopping-cart text-2xl text-red-600"></i>
            </div>
            <h3 class="text-xl font-semibold text-gray-900 mb-4">订单分析</h3>
            <p class="text-gray-600">
              通过AI分析订单数据，对商品销量进行深度分析，提供精准的业务洞察。
            </p>
          </div>
          
          <!-- 特性卡片6 -->
          <div class="bg-white p-8 rounded-xl shadow-sm border border-gray-100 hover:shadow-md transition-shadow">
            <div class="w-14 h-14 bg-indigo-100 rounded-lg flex items-center justify-center mb-6">
              <i class="fas fa-chart-line text-2xl text-indigo-600"></i>
            </div>
            <h3 class="text-xl font-semibold text-gray-900 mb-4">广告扩量策略</h3>
            <p class="text-gray-600">
              针对热销商品自动生成广告扩量策略，一键发布并监控效果。
            </p>
          </div>
        </div>
      </div>
    </section>

    <!-- 功能展示 -->
    <section id="showcase" class="py-20 bg-white">
      <div class="container mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex flex-col lg:flex-row items-center gap-12">
          <div class="w-full lg:w-1/2">
            <div class="relative">
              <div class="absolute inset-0 bg-gradient-to-r from-blue-500/20 to-purple-500/20 rounded-2xl blur-3xl"></div>
              <img src="https://picsum.photos/id/2/800/600" alt="功能展示" class="relative z-10 w-full h-auto rounded-2xl shadow-2xl">
            </div>
          </div>
          <div class="w-full lg:w-1/2">
            <div class="inline-block px-3 py-1 bg-purple-100 text-purple-800 text-xs font-medium rounded-full mb-4">
              功能展示
            </div>
            <h2 class="text-3xl md:text-4xl font-bold text-gray-900 mb-6">
              全链路智能解决方案
            </h2>
            <p class="text-lg text-gray-600 mb-8">
              从内容创作到广告投放，从商品上架到订单分析，AI赋能每一个业务环节，
              实现全流程自动化运营。
            </p>
            
            <div class="space-y-6">
              <div class="flex">
                <div class="flex-shrink-0 w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center mr-4">
                  <i class="fas fa-database text-blue-600"></i>
                </div>
                <div>
                  <h3 class="text-lg font-semibold text-gray-900 mb-1">多源数据整合</h3>
                  <p class="text-gray-600">
                    轻松连接各类数据源，包括数据库、API、Excel文件等，实现数据的统一管理和分析。
                  </p>
                </div>
              </div>
              
              <div class="flex">
                <div class="flex-shrink-0 w-10 h-10 bg-green-100 rounded-lg flex items-center justify-center mr-4">
                  <i class="fas fa-chart-pie text-green-600"></i>
                </div>
                <div>
                  <h3 class="text-lg font-semibold text-gray-900 mb-1">交互式分析仪表盘</h3>
                  <p class="text-gray-600">
                    自定义拖拽式仪表盘，支持实时数据刷新和交互式分析，直观呈现业务关键指标。
                  </p>
                </div>
              </div>
              
              <div class="flex">
                <div class="flex-shrink-0 w-10 h-10 bg-amber-100 rounded-lg flex items-center justify-center mr-4">
                  <i class="fas fa-robot text-amber-600"></i>
                </div>
                <div>
                  <h3 class="text-lg font-semibold text-gray-900 mb-1">智能数据洞察</h3>
                  <p class="text-gray-600">
                    AI驱动的数据异常检测和趋势分析，自动发现数据中的隐藏规律和业务机会。
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 解决方案 -->
    <section id="solutions" class="py-20 bg-gradient-to-br from-purple-50 via-blue-50 to-indigo-50">
      <div class="container mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center max-w-3xl mx-auto mb-16">
          <div class="inline-block px-3 py-1 bg-indigo-100 text-indigo-800 text-xs font-medium rounded-full mb-4">
            行业解决方案
          </div>
          <h2 class="text-3xl md:text-4xl font-bold text-gray-900 mb-6">
            为不同行业提供定制化解决方案
          </h2>
          <p class="text-lg text-gray-600">
            我们深入了解各行业的特点和需求，提供针对性的数据分析解决方案，
            帮助企业解决实际业务问题。
          </p>
        </div>
        
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          <!-- 解决方案1 -->
          <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-md transition-shadow">
            <img src="https://picsum.photos/id/3/600/400" alt="金融解决方案" class="w-full h-48 object-cover">
            <div class="p-6">
              <h3 class="text-xl font-semibold text-gray-900 mb-3">金融行业解决方案</h3>
              <p class="text-gray-600 mb-4">
                帮助金融机构进行风险控制、客户行为分析、市场预测和投资组合优化，提升业务效率和竞争力。
              </p>
              <a href="#" class="text-purple-600 hover:text-purple-800 font-medium text-sm inline-flex items-center">
                了解更多
                <i class="fas fa-arrow-right ml-2 text-xs"></i>
              </a>
            </div>
          </div>
          
          <!-- 解决方案2 -->
          <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-md transition-shadow">
            <img src="https://picsum.photos/id/4/600/400" alt="零售解决方案" class="w-full h-48 object-cover">
            <div class="p-6">
              <h3 class="text-xl font-semibold text-gray-900 mb-3">零售行业解决方案</h3>
              <p class="text-gray-600 mb-4">
                优化商品定价、库存管理、客户细分和精准营销，提高销售转化率和客户满意度。
              </p>
              <a href="#" class="text-purple-600 hover:text-purple-800 font-medium text-sm inline-flex items-center">
                了解更多
                <i class="fas fa-arrow-right ml-2 text-xs"></i>
              </a>
            </div>
          </div>
          
          <!-- 解决方案3 -->
          <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-md transition-shadow">
            <img src="https://picsum.photos/id/5/600/400" alt="医疗解决方案" class="w-full h-48 object-cover">
            <div class="p-6">
              <h3 class="text-xl font-semibold text-gray-900 mb-3">医疗健康解决方案</h3>
              <p class="text-gray-600 mb-4">
                辅助医疗诊断、患者数据分析、医疗资源优化配置和健康趋势预测，提升医疗服务质量。
              </p>
              <a href="#" class="text-purple-600 hover:text-purple-800 font-medium text-sm inline-flex items-center">
                了解更多
                <i class="fas fa-arrow-right ml-2 text-xs"></i>
              </a>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 客户案例 -->
    <section id="customers" class="py-20 bg-white">
      <div class="container mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center max-w-3xl mx-auto mb-16">
          <div class="inline-block px-3 py-1 bg-green-100 text-green-800 text-xs font-medium rounded-full mb-4">
            客户成功案例
          </div>
          <h2 class="text-3xl md:text-4xl font-bold text-gray-900 mb-6">
            我们的客户如何取得成功
          </h2>
          <p class="text-lg text-gray-600">
            来自各行业的客户通过我们的数据分析平台，实现了业务增长和效率提升。
          </p>
        </div>
        
        <div class="grid grid-cols-1 md:grid-cols-2 gap-12">
          <!-- 客户案例1 -->
          <div class="bg-gray-50 p-8 rounded-xl">
            <div class="flex items-center mb-6">
              <img src="https://picsum.photos/id/10/100/100" alt="客户Logo" class="w-16 h-16 object-contain mr-4">
              <div>
                <h3 class="text-xl font-semibold text-gray-900">某知名金融机构</h3>
                <p class="text-gray-500">风险控制与客户分析</p>
              </div>
            </div>
            <p class="text-gray-600 mb-6">
              "通过使用智能多维数据分析平台，我们成功降低了30%的信贷风险，同时提升了25%的客户满意度。平台的AI分析能力帮助我们发现了以前被忽略的客户需求和市场机会。"
            </p>
            <div class="flex items-center">
              <img src="https://picsum.photos/id/1005/100/100" alt="客户代表" class="w-12 h-12 rounded-full mr-4">
              <div>
                <p class="font-medium text-gray-900">张明</p>
                <p class="text-sm text-gray-500">风险控制总监</p>
              </div>
            </div>
          </div>
          
          <!-- 客户案例2 -->
          <div class="bg-gray-50 p-8 rounded-xl">
            <div class="flex items-center mb-6">
              <img src="https://picsum.photos/id/11/100/100" alt="客户Logo" class="w-16 h-16 object-contain mr-4">
              <div>
                <h3 class="text-xl font-semibold text-gray-900">大型零售连锁品牌</h3>
                <p class="text-gray-500">销售预测与库存优化</p>
              </div>
            </div>
            <p class="text-gray-600 mb-6">
              "多维数据分析平台帮助我们实现了精准的销售预测和库存优化，减少了15%的库存成本，同时提高了产品周转率和客户满意度。实时数据分析功能让我们能够快速响应市场变化。"
            </p>
            <div class="flex items-center">
              <img src="https://picsum.photos/id/1006/100/100" alt="客户代表" class="w-12 h-12 rounded-full mr-4">
              <div>
                <p class="font-medium text-gray-900">李婷</p>
                <p class="text-sm text-gray-500">供应链总监</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 价格方案 -->
    <section id="pricing" class="py-20 bg-gray-50">
      <div class="container mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center max-w-3xl mx-auto mb-16">
            <div class="inline-block px-3 py-1 bg-blue-100 text-blue-800 text-xs font-medium rounded-full mb-4">
              灵活定价
            </div>
            <h2 class="text-3xl md:text-4xl font-bold text-gray-900 mb-6">
              为AI智能管家定制的价格方案
            </h2>
            <p class="text-lg text-gray-600 mb-8">
              无论您是个人创作者还是大型企业，我们都有适合您需求和预算的AI智能解决方案。
            </p>
          <div class="inline-flex items-center p-1 bg-gray-200 rounded-full">
            <button :class="['px-6 py-2 rounded-full text-sm font-medium shadow-sm', billingType === 'monthly' ? 'bg-white text-gray-900' : 'text-gray-700']" @click="toggleBillingType('monthly')">按月付费</button>
            <button :class="['px-6 py-2 text-sm font-medium', billingType === 'yearly' ? 'bg-white text-gray-900 rounded-full shadow-sm' : 'text-gray-700']" @click="toggleBillingType('yearly')">按年付费 <span class="text-green-600 text-xs">省20%</span></button>
          </div>
        </div>
        
        <div class="grid grid-cols-1 md:grid-cols-3 gap-8 max-w-5xl mx-auto">
          <!-- 基础版 -->
          <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-md transition-shadow">
            <div class="p-8">
              <h3 class="text-xl font-semibold text-gray-900 mb-2">AI基础版</h3>
              <p class="text-gray-500 mb-6">适合个人创作者和小型团队</p>
              <div class="flex items-baseline mb-6">
                <span class="text-4xl font-bold text-gray-900">{{ billingType === 'monthly' ? '¥99' : '¥79' }}</span>
                <span class="text-gray-500 ml-2">{{ billingType === 'monthly' ? '/月' : '/月 (年付)' }}</span>
              </div>
              <button class="w-full py-3 border border-gray-300 text-gray-900 rounded-lg font-medium hover:bg-gray-50 transition-colors mb-8">
                开始使用
              </button>
              <ul class="space-y-3">
                <li class="flex items-center text-gray-600">
                  <i class="fas fa-check text-green-600 mr-2"></i>
                  <span>最多5个社交媒体账号管理</span>
                </li>
                <li class="flex items-center text-gray-600">
                  <i class="fas fa-check text-green-600 mr-2"></i>
                  <span>每月50篇AI内容生成</span>
                </li>
                <li class="flex items-center text-gray-600">
                  <i class="fas fa-check text-green-600 mr-2"></i>
                  <span>基础数据分析功能</span>
                </li>
                <li class="flex items-center text-gray-600">
                  <i class="fas fa-check text-green-600 mr-2"></i>
                  <span>邮件支持</span>
                </li>
                <li class="flex items-center text-gray-400">
                  <i class="fas fa-times mr-2"></i>
                  <span>高级AI分析功能</span>
                </li>
                <li class="flex items-center text-gray-400">
                  <i class="fas fa-times mr-2"></i>
                  <span>API访问权限</span>
                </li>
              </ul>
            </div>
          </div>
          
          <!-- 专业版 -->
          <div class="bg-white rounded-xl shadow-lg border-2 border-purple-500 overflow-hidden transform scale-105 relative">
            <div class="absolute top-0 left-0 right-0 bg-gradient-to-r from-purple-600 to-blue-500 text-white text-center py-1 text-xs font-medium">
              最受欢迎
            </div>
            <div class="p-8 pt-12">
              <h3 class="text-xl font-semibold text-gray-900 mb-2">AI专业版</h3>
              <p class="text-gray-500 mb-6">适合中小企业和专业团队</p>
              <div class="flex items-baseline mb-6">
                <span class="text-4xl font-bold text-gray-900">{{ billingType === 'monthly' ? '¥299' : '¥239' }}</span>
                <span class="text-gray-500 ml-2">{{ billingType === 'monthly' ? '/月' : '/月 (年付)' }}</span>
              </div>
              <button class="w-full py-3 bg-gradient-to-r from-purple-600 to-blue-500 text-white rounded-lg font-medium hover:opacity-90 transition-opacity mb-8 shadow-lg shadow-purple-500/20">
                开始使用
              </button>
              <ul class="space-y-3">
                <li class="flex items-center text-gray-600">
                  <i class="fas fa-check text-green-600 mr-2"></i>
                  <span>最多20个社交媒体账号管理</span>
                </li>
                <li class="flex items-center text-gray-600">
                  <i class="fas fa-check text-green-600 mr-2"></i>
                  <span>每月500篇AI内容生成</span>
                </li>
                <li class="flex items-center text-gray-600">
                  <i class="fas fa-check text-green-600 mr-2"></i>
                  <span>广告投放自动化管理</span>
                </li>
                <li class="flex items-center text-gray-600">
                  <i class="fas fa-check text-green-600 mr-2"></i>
                  <span>优先邮件和在线支持</span>
                </li>
                <li class="flex items-center text-gray-600">
                  <i class="fas fa-check text-green-600 mr-2"></i>
                  <span>基础AI分析功能</span>
                </li>
                <li class="flex items-center text-gray-600">
                  <i class="fas fa-check text-green-600 mr-2"></i>
                  <span>有限API访问权限</span>
                </li>
              </ul>
            </div>
          </div>
          
          <!-- 企业版 -->
          <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-md transition-shadow">
            <div class="p-8">
              <h3 class="text-xl font-semibold text-gray-900 mb-2">AI企业版</h3>
              <p class="text-gray-500 mb-6">适合大型企业和组织</p>
              <div class="flex items-baseline mb-6">
                <span class="text-4xl font-bold text-gray-900">{{ billingType === 'monthly' ? '¥999' : '¥799' }}</span>
                <span class="text-gray-500 ml-2">{{ billingType === 'monthly' ? '/月' : '/月 (年付)' }}</span>
              </div>
              <button class="w-full py-3 border border-gray-300 text-gray-900 rounded-lg font-medium hover:bg-gray-50 transition-colors mb-8">
                联系销售
              </button>
              <ul class="space-y-3">
                <li class="flex items-center text-gray-600">
                  <i class="fas fa-check text-green-600 mr-2"></i>
                  <span>无限社交媒体账号管理</span>
                </li>
                <li class="flex items-center text-gray-600">
                  <i class="fas fa-check text-green-600 mr-2"></i>
                  <span>无限制AI内容生成</span>
                </li>
                <li class="flex items-center text-gray-600">
                  <i class="fas fa-check text-green-600 mr-2"></i>
                  <span>商品AI上架与订单分析</span>
                </li>
                <li class="flex items-center text-gray-600">
                  <i class="fas fa-check text-green-600 mr-2"></i>
                  <span>24/7专属支持</span>
                </li>
                <li class="flex items-center text-gray-600">
                  <i class="fas fa-check text-green-600 mr-2"></i>
                  <span>全部高级AI分析功能</span>
                </li>
                <li class="flex items-center text-gray-600">
                  <i class="fas fa-check text-green-600 mr-2"></i>
                  <span>完整API访问权限</span>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 行动召唤 - 增强视觉效果 -->
    <section class="py-20 bg-gradient-to-br from-blue-900 via-blue-800 to-blue-950 text-white relative overflow-hidden">
      <!-- 背景装饰 -->
      <div class="absolute inset-0 overflow-hidden">
        <!-- 主装饰圆形 -->
        <div class="absolute top-0 right-0 w-[600px] h-[600px] bg-blue-600/10 rounded-full blur-3xl animate-pulse"></div>
        <div class="absolute bottom-0 left-0 w-[600px] h-[600px] bg-blue-500/10 rounded-full blur-3xl animate-pulse" style="animation-delay: 1s;"></div>
        <div class="absolute top-1/3 left-1/4 w-[400px] h-[400px] bg-blue-700/10 rounded-full blur-3xl animate-pulse" style="animation-delay: 2s;"></div>
        
        <!-- 新增装饰元素 -->
        <div class="absolute top-1/4 right-1/3 w-[100px] h-[100px] bg-blue-400/20 rounded-full blur-xl animate-bounce"></div>
        <div class="absolute bottom-1/4 left-1/3 w-[150px] h-[150px] bg-blue-300/15 rounded-full blur-xl animate-bounce" style="animation-delay: 1.5s;"></div>
        <div class="absolute top-2/3 right-1/4 w-[80px] h-[80px] bg-blue-500/20 rounded-full blur-xl animate-bounce" style="animation-delay: 3s;"></div>
      </div>
      
      <!-- 网格装饰 -->
      <div class="absolute inset-0 bg-[url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHZpZXdCb3g9IjAgMCA2MCA2MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48ZyBmaWxsPSJub25lIiBmaWxsLXJ1bGU9ImV2ZW5vZGQiPjxnIGZpbGw9IiMyMDIwMjAiIGZpbGwtb3BhY2l0eT0iMC4wNSI+PHBhdGggZD0iTTEwIDBIMHoiLz48cGF0aCBkPSJNMzAgMEg0MHYxMCIvPjxwYXRoIGQ9Ik02MCAzMEg1MHYxMCIvPjxwYXRoIGQ9Ik01MCA2MEg0MHYtMTAiLz48cGF0aCBkPSJNMzAgNUgzMHYxMCIvPjxwYXRoIGQ9Ik0xMCAzMEgwdjEwIi8+PHBhdGggZD0iTTYwIDUwSDUwdjEwIi8+PHBhdGggZD0iTTMwIDYwSDIwdjEwIi8+PC9nPjwvZz48L3N2Zz4=')] opacity-30"></div>
      
      <div class="container mx-auto px-4 sm:px-6 lg:px-8 text-center relative z-10">
        <h2 class="text-4xl md:text-5xl font-bold mb-6 leading-tight tracking-tight">
          <span class="block bg-clip-text text-transparent bg-gradient-to-r from-white to-blue-200">准备好开始您的</span>
          <span class="block bg-clip-text text-transparent bg-gradient-to-r from-blue-300 to-blue-100 mt-2">数据分析之旅了吗？</span>
        </h2>
        <p class="text-lg text-blue-200/90 max-w-2xl mx-auto mb-10">
          立即注册，享受14天免费试用，体验我们强大的数据分析平台如何帮助您的企业实现增长。
        </p>
        <div class="flex flex-col sm:flex-row justify-center space-y-4 sm:space-y-0 sm:space-x-6">
          <button class="px-8 py-4 bg-white text-blue-800 rounded-lg font-bold hover:bg-opacity-90 transition-all duration-300 transform hover:-translate-y-1 shadow-xl">
            免费试用14天
          </button>
          <button class="px-8 py-4 bg-transparent border-2 border-white text-white rounded-lg font-bold hover:bg-white/10 transition-all duration-300 transform hover:-translate-y-1">
            预约演示
          </button>
        </div>
        
        <!-- 核心优势简介 -->
        <div class="mt-16 grid grid-cols-1 md:grid-cols-3 gap-8 max-w-5xl mx-auto">
          <div class="bg-blue-800/20 backdrop-blur-sm rounded-lg p-6 border border-blue-700/30 transform hover:translate-y-[-5px] transition-all duration-300">
            <div class="w-12 h-12 bg-blue-600/30 rounded-full flex items-center justify-center mb-4">
              <i class="fas fa-rocket text-blue-200 text-xl"></i>
            </div>
            <h3 class="text-xl font-semibold text-white mb-2">快速部署</h3>
            <p class="text-blue-300/80">无需复杂配置，快速开始您的数据分析工作</p>
          </div>
          <div class="bg-blue-800/20 backdrop-blur-sm rounded-lg p-6 border border-blue-700/30 transform hover:translate-y-[-5px] transition-all duration-300">
            <div class="w-12 h-12 bg-blue-600/30 rounded-full flex items-center justify-center mb-4">
              <i class="fas fa-shield-alt text-blue-200 text-xl"></i>
            </div>
            <h3 class="text-xl font-semibold text-white mb-2">企业级安全</h3>
            <p class="text-blue-300/80">端到端加密保护您的数据安全和隐私</p>
          </div>
          <div class="bg-blue-800/20 backdrop-blur-sm rounded-lg p-6 border border-blue-700/30 transform hover:translate-y-[-5px] transition-all duration-300">
            <div class="w-12 h-12 bg-blue-600/30 rounded-full flex items-center justify-center mb-4">
              <i class="fas fa-headset text-blue-200 text-xl"></i>
            </div>
            <h3 class="text-xl font-semibold text-white mb-2">专业支持</h3>
            <p class="text-blue-300/80">全天候技术支持，确保您的业务顺利运行</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 页脚 - 增强视觉效果 -->
    <footer class="bg-blue-950 text-blue-300 py-16 relative overflow-hidden">
      <!-- 背景装饰 -->
      <div class="absolute inset-0 overflow-hidden opacity-20">
        <div class="absolute top-0 right-0 w-[400px] h-[400px] bg-blue-600/20 rounded-full blur-3xl"></div>
        <div class="absolute bottom-0 left-0 w-[400px] h-[400px] bg-blue-500/20 rounded-full blur-3xl"></div>
      </div>
      
      <!-- 网格装饰 -->
      <div class="absolute inset-0 bg-[url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHZpZXdCb3g9IjAgMCA2MCA2MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48ZyBmaWxsPSJub25lIiBmaWxsLXJ1bGU9ImV2ZW5vZGQiPjxnIGZpbGw9IiMyMDIwMjAiIGZpbGwtb3BhY2l0eT0iMC4wNSI+PHBhdGggZD0iTTEwIDBIMHoiLz48cGF0aCBkPSJNMzAgMEg0MHYxMCIvPjxwYXRoIGQ9Ik02MCAzMEg1MHYxMCIvPjxwYXRoIGQ9Ik01MCA2MEg0MHYtMTAiLz48cGF0aCBkPSJNMzAgNUgzMHYxMCIvPjxwYXRoIGQ9Ik0xMCAzMEgwdjEwIi8+PHBhdGggZD0iTTYwIDUwSDUwdjEwIi8+PHBhdGggZD0iTTMwIDYwSDIwdjEwIi8+PC9nPjwvZz48L3N2Zz4=')] opacity-10"></div>
      
      <div class="container mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-8">
          <div class="lg:col-span-2">
            <span class="text-xl font-bold bg-gradient-to-r from-blue-400 to-blue-600 bg-clip-text text-transparent">AI智能管家 - 一体化管理系统</span>
            <p class="mt-4 mb-6 text-blue-300/80">
              集成社交媒体运营、广告投放、商品上架与数据分析，AI赋能全流程自动化。
            </p>
            <div class="flex space-x-4">
              <a href="#" class="text-blue-400 hover:text-white transition-colors">
                <i class="fab fa-weixin text-xl"></i>
              </a>
              <a href="#" class="text-blue-400 hover:text-white transition-colors">
                <i class="fab fa-weibo text-xl"></i>
              </a>
              <a href="#" class="text-blue-400 hover:text-white transition-colors">
                <i class="fab fa-linkedin text-xl"></i>
              </a>
              <a href="#" class="text-blue-400 hover:text-white transition-colors">
                <i class="fab fa-github text-xl"></i>
              </a>
            </div>
          </div>
          
          <div>
            <h4 class="text-white text-lg font-semibold mb-4">产品</h4>
            <ul class="space-y-3">
              <li><a href="#" class="text-blue-300 hover:text-white transition-all duration-300">特性</a></li>
              <li><a href="#" class="text-blue-300 hover:text-white transition-all duration-300">解决方案</a></li>
              <li><a href="#" class="text-blue-300 hover:text-white transition-all duration-300">定价</a></li>
              <li><a href="#" class="text-blue-300 hover:text-white transition-all duration-300">更新日志</a></li>
              <li><a href="#" class="text-blue-300 hover:text-white transition-all duration-300">API文档</a></li>
            </ul>
          </div>
          
          <div>
            <h4 class="text-white text-lg font-semibold mb-4">资源</h4>
            <ul class="space-y-3">
              <li><a href="#" class="text-blue-300 hover:text-white transition-all duration-300">文档中心</a></li>
              <li><a href="#" class="text-blue-300 hover:text-white transition-all duration-300">教程指南</a></li>
              <li><a href="#" class="text-blue-300 hover:text-white transition-all duration-300">案例研究</a></li>
              <li><a href="#" class="text-blue-300 hover:text-white transition-all duration-300">博客</a></li>
              <li><a href="#" class="text-blue-300 hover:text-white transition-all duration-300">社区</a></li>
            </ul>
          </div>
          
          <div>
            <h4 class="text-white text-lg font-semibold mb-4">公司</h4>
            <ul class="space-y-3">
              <li><a href="#" class="text-blue-300 hover:text-white transition-all duration-300">关于我们</a></li>
              <li><a href="#" class="text-blue-300 hover:text-white transition-all duration-300">联系我们</a></li>
              <li><a href="#" class="text-blue-300 hover:text-white transition-all duration-300">加入团队</a></li>
              <li><a href="#" class="text-blue-300 hover:text-white transition-all duration-300">隐私政策</a></li>
              <li><a href="#" class="text-blue-300 hover:text-white transition-all duration-300">服务条款</a></li>
            </ul>
          </div>
        </div>
        
        <div class="mt-12 pt-8 border-t border-blue-800/50 text-center">
          <p class="text-blue-400/70">&copy; 2024 AI智能管家 - 一体化管理系统. 保留所有权利.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script>
import { isLoggedIn, getCurrentUser, logout as logoutUser } from '../utils/auth.js';

export default {
  name: 'IndexPage',
  data() {
    return {
      // 页面数据
      isLoggedIn: false,
      currentUser: null,
      billingType: 'monthly' // monthly or yearly
    }
  },
  mounted() {
    console.log('项目介绍首页已加载');
    
    // 导航栏滚动效果
    window.addEventListener('scroll', this.handleScroll);
    
    // 检查登录状态
    this.checkLoginStatus();
    // 监听登录状态变化
    this.setupStatusChecker();
    
    // 确保页面内容居中显示
    this.$nextTick(() => {
      document.querySelectorAll('section > div.container').forEach(container => {
        container.classList.add('mx-auto');
      });
    });
  },
  beforeUnmount() {
    window.removeEventListener('scroll', this.handleScroll);
  },
  methods: {
    toggleBillingType(type) {
      this.billingType = type;
    },
    handleScroll() {
      const header = document.querySelector('header');
      if (window.scrollY > 50) {
        header.classList.add('shadow-lg', 'bg-white/70');
        header.classList.remove('border-b', 'bg-white/60');
      } else {
        header.classList.remove('shadow-lg', 'bg-white/70');
        header.classList.add('border-b', 'bg-white/60');
      }
    },
    // 检查登录状态
    checkLoginStatus() {
      this.isLoggedIn = isLoggedIn();
      this.currentUser = getCurrentUser();
    },
    // 设置定时检查登录状态
    setupStatusChecker() {
      setInterval(() => {
        this.checkLoginStatus();
      }, 1000);
    },
    // 登出功能
    logout() {
        logoutUser();
      },
    
    // 跳转到登录页面
    goToLogin() {
      window.location.hash = '#/login';
    },
    
    // 跳转到项目页面（数据页面）
    goToProject() {
      if (this.isLoggedIn) {
        window.location.hash = '#/data';
      } else {
        window.location.hash = '#/login';
      }
    }
  }
}
</script>

<style scoped>
/* 自定义样式 */
/* 确保内容完全铺满页面宽度 */
.container {
  width: 100%;
  margin: 0 auto;
  padding: 0 1.5rem;
}

/* 响应式调整 */
@media (max-width: 640px) {
  .container {
    padding: 0 1rem;
  }
  
  /* 优化移动设备上的英雄区域 */
  .hero-title {
    font-size: 2.5rem !important;
  }
  
  /* 优化移动设备上的价格方案展示 */
  #pricing .grid {
    grid-template-columns: 1fr !important;
  }
  
  /* 优化移动设备上的特性卡片 */
  .feature-grid {
    grid-template-columns: 1fr !important;
  }
}

/* 确保所有section内容都能正确铺满页面 */
section {
  width: 100%;
}

/* 确保头部导航也能正确显示 */
header .container {
  max-width: 100%;
}

/* 自定义动画效果 */
@keyframes pulse {
  0%, 100% {
    opacity: 0.6;
  }
  50% {
    opacity: 0.9;
  }
}

.animate-pulse {
  animation: pulse 4s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.animate-bounce {
  animation: bounce 4s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

/* 价格方案卡片优化 */
#pricing .grid {
  transition: all 0.3s ease;
}

/* 确保内容在各种屏幕尺寸下都能正确显示 */
@media (max-width: 1024px) {
  .container {
    padding: 0 2rem;
  }
}

@media (max-width: 768px) {
  /* 优化平板设备上的布局 */
  #pricing .grid {
    grid-template-columns: 1fr;
    gap: 2rem;
  }
  
  .hero-content {
    flex-direction: column;
    text-align: center;
  }
  
  .hero-image {
    margin-top: 2rem;
  }
}

/* 增加触摸反馈 */
button, a {
  touch-action: manipulation;
  -webkit-tap-highlight-color: transparent;
}

/* 平滑滚动 */
html {
  scroll-behavior: smooth;
}

/* 确保导航栏在移动端也能正常工作 */
@media (max-width: 640px) {
  .nav-container {
    flex-direction: column;
    padding: 1rem 0;
  }
  
  .nav-links {
    flex-direction: column;
    margin-top: 1rem;
    width: 100%;
  }
  
  .nav-links li {
    margin: 0.5rem 0;
    width: 100%;
    text-align: center;
  }
}
</style>