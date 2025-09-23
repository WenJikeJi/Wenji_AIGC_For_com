<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100">
    <!-- 导航栏 -->
    <nav class="bg-white shadow-sm border-b border-gray-200">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center h-16">
          <div class="flex items-center">
            <router-link to="/" class="flex items-center space-x-3">
              <div class="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center">
                <span class="text-white font-bold text-sm">文</span>
              </div>
              <span class="text-xl font-bold text-gray-900">文际信息</span>
            </router-link>
          </div>
          <div class="flex items-center space-x-4">
            <router-link to="/login" class="text-gray-600 hover:text-blue-600 transition-colors">
              返回登录
            </router-link>
          </div>
        </div>
      </div>
    </nav>

    <!-- 主要内容 -->
    <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <div class="bg-white rounded-xl shadow-lg overflow-hidden">
        <!-- 页面标题 -->
        <div class="bg-gradient-to-r from-blue-600 to-indigo-600 px-8 py-6">
          <h1 class="text-3xl font-bold text-white">服务条款</h1>
          <p class="text-blue-100 mt-2">广州文际信息科技有限公司</p>
        </div>

        <!-- 条款内容 -->
        <div class="px-8 py-6 space-y-8">
          <!-- 更新日期 -->
          <div class="text-sm text-gray-500 border-b border-gray-200 pb-4">
            <p>最后更新日期：{{ lastUpdated }}</p>
            <p>生效日期：{{ effectiveDate }}</p>
          </div>

          <!-- 第一条：接受条款 -->
          <section>
            <h2 class="text-xl font-semibold text-gray-900 mb-4">1. 接受条款</h2>
            <div class="text-gray-700 space-y-3">
              <p>欢迎使用广州文际信息科技有限公司（以下简称"我们"或"文际信息"）提供的服务。通过访问或使用我们的网站、应用程序或服务（统称为"服务"），您同意受本服务条款（以下简称"条款"）的约束。</p>
              <p>如果您不同意这些条款，请不要使用我们的服务。我们保留随时修改这些条款的权利，修改后的条款将在网站上公布后生效。</p>
            </div>
          </section>

          <!-- 第二条：服务描述 -->
          <section>
            <h2 class="text-xl font-semibold text-gray-900 mb-4">2. 服务描述</h2>
            <div class="text-gray-700 space-y-3">
              <p>文际信息提供包括但不限于以下服务：</p>
              <ul class="list-disc list-inside ml-4 space-y-2">
                <li>社交媒体管理和营销服务</li>
                <li>数据分析和报告服务</li>
                <li>内容创作和发布工具</li>
                <li>客户关系管理系统</li>
                <li>其他相关的数字营销服务</li>
              </ul>
              <p>我们保留随时修改、暂停或终止任何服务的权利，恕不另行通知。</p>
            </div>
          </section>

          <!-- 第三条：用户账户 -->
          <section>
            <h2 class="text-xl font-semibold text-gray-900 mb-4">3. 用户账户</h2>
            <div class="text-gray-700 space-y-3">
              <p>为了使用某些服务，您需要创建一个账户。您同意：</p>
              <ul class="list-disc list-inside ml-4 space-y-2">
                <li>提供准确、完整和最新的注册信息</li>
                <li>维护账户信息的准确性</li>
                <li>保护您的账户密码和安全</li>
                <li>对您账户下发生的所有活动负责</li>
                <li>立即通知我们任何未经授权的账户使用</li>
              </ul>
            </div>
          </section>

          <!-- 第四条：用户行为规范 -->
          <section>
            <h2 class="text-xl font-semibold text-gray-900 mb-4">4. 用户行为规范</h2>
            <div class="text-gray-700 space-y-3">
              <p>在使用我们的服务时，您同意不会：</p>
              <ul class="list-disc list-inside ml-4 space-y-2">
                <li>违反任何适用的法律法规</li>
                <li>侵犯他人的知识产权或其他权利</li>
                <li>发布虚假、误导性或有害的内容</li>
                <li>进行任何可能损害服务安全性或完整性的活动</li>
                <li>尝试未经授权访问我们的系统或其他用户的账户</li>
                <li>使用自动化工具恶意访问或使用服务</li>
              </ul>
            </div>
          </section>

          <!-- 第五条：知识产权 -->
          <section>
            <h2 class="text-xl font-semibold text-gray-900 mb-4">5. 知识产权</h2>
            <div class="text-gray-700 space-y-3">
              <p>我们的服务及其所有内容、功能和特性（包括但不限于所有信息、软件、文本、显示、图像、视频和音频，以及设计、选择和排列）均为文际信息或其许可方所有，受版权、商标、专利和其他知识产权法律保护。</p>
              <p>您可以在遵守本条款的前提下使用我们的服务，但不得复制、修改、分发、销售或出租服务的任何部分。</p>
            </div>
          </section>

          <!-- 第六条：隐私保护 -->
          <section>
            <h2 class="text-xl font-semibold text-gray-900 mb-4">6. 隐私保护</h2>
            <div class="text-gray-700 space-y-3">
              <p>我们重视您的隐私。我们收集、使用和保护您个人信息的方式详见我们的<router-link to="/privacy" class="text-blue-600 hover:text-blue-800 underline">隐私政策</router-link>。</p>
              <p>通过使用我们的服务，您同意我们按照隐私政策收集和使用您的信息。</p>
            </div>
          </section>

          <!-- 第七条：免责声明 -->
          <section>
            <h2 class="text-xl font-semibold text-gray-900 mb-4">7. 免责声明</h2>
            <div class="text-gray-700 space-y-3">
              <p>我们的服务按"现状"提供，不提供任何明示或暗示的保证。我们不保证服务将不间断、无错误或完全安全。</p>
              <p>在法律允许的最大范围内，我们不对因使用或无法使用服务而产生的任何直接、间接、偶然、特殊或后果性损害承担责任。</p>
            </div>
          </section>

          <!-- 第八条：服务变更和终止 -->
          <section>
            <h2 class="text-xl font-semibold text-gray-900 mb-4">8. 服务变更和终止</h2>
            <div class="text-gray-700 space-y-3">
              <p>我们保留随时修改、暂停或终止服务的权利。我们可能会因维护、升级或其他原因暂时中断服务。</p>
              <p>您可以随时停止使用服务并删除您的账户。我们也可能因违反本条款或其他原因终止您的账户。</p>
            </div>
          </section>

          <!-- 第九条：争议解决 -->
          <section>
            <h2 class="text-xl font-semibold text-gray-900 mb-4">9. 争议解决</h2>
            <div class="text-gray-700 space-y-3">
              <p>本条款受中华人民共和国法律管辖。因本条款引起的任何争议，双方应首先通过友好协商解决。</p>
              <p>如协商不成，任何一方均可向广州市有管辖权的人民法院提起诉讼。</p>
            </div>
          </section>

          <!-- 第十条：联系方式 -->
          <section>
            <h2 class="text-xl font-semibold text-gray-900 mb-4">10. 联系方式</h2>
            <div class="text-gray-700 space-y-3">
              <p>如果您对本服务条款有任何疑问或建议，请通过以下方式联系我们：</p>
              <div class="bg-gray-50 p-4 rounded-lg">
                <p><strong>公司名称：</strong>广州文际信息科技有限公司</p>
                <p><strong>联系邮箱：</strong>service@wenji.com（上线后更新）</p>
                <p><strong>客服热线：</strong>400-XXX-XXXX（上线后更新）</p>
                <p><strong>办公地址：</strong>广州市天河区XXX（具体地址待更新）</p>
              </div>
            </div>
          </section>
        </div>

        <!-- 页脚 -->
        <div class="bg-gray-50 px-8 py-6 border-t border-gray-200">
          <div class="flex flex-col sm:flex-row justify-between items-center space-y-4 sm:space-y-0">
            <div class="text-sm text-gray-600">
              © 2024 广州文际信息科技有限公司. 保留所有权利.
            </div>
            <div class="flex space-x-6 text-sm">
              <router-link to="/privacy" class="text-gray-600 hover:text-blue-600 transition-colors">
                隐私政策
              </router-link>
              <router-link to="/contact" class="text-gray-600 hover:text-blue-600 transition-colors">
                联系我们
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TermsOfService',
  data() {
    return {
      lastUpdated: '2024年1月15日',
      effectiveDate: '2024年1月15日'
    };
  },
  mounted() {
    // 设置页面标题
    document.title = '服务条款 - 文际AIGC社交媒体管理平台';
  }
};
</script>

<style scoped>
/* 自定义样式 */
section {
  scroll-margin-top: 2rem;
}

/* 链接样式 */
a {
  transition: color 0.2s ease-in-out;
}

/* 响应式调整 */
@media (max-width: 640px) {
  .px-8 {
    padding-left: 1rem;
    padding-right: 1rem;
  }
  
  .py-12 {
    padding-top: 2rem;
    padding-bottom: 2rem;
  }
  
  h1 {
    font-size: 1.875rem;
  }
  
  h2 {
    font-size: 1.25rem;
  }
}
</style>