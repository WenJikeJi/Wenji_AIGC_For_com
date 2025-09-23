<template>
  <div class="min-h-screen bg-gradient-to-br from-green-50 to-emerald-100">
    <!-- 导航栏 -->
    <nav class="bg-white shadow-sm border-b border-gray-200">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center h-16">
          <div class="flex items-center">
            <router-link to="/" class="flex items-center space-x-3">
              <div class="w-8 h-8 bg-green-600 rounded-lg flex items-center justify-center">
                <span class="text-white font-bold text-sm">文</span>
              </div>
              <span class="text-xl font-bold text-gray-900">文际信息</span>
            </router-link>
          </div>
          <div class="flex items-center space-x-4">
            <router-link to="/login" class="text-gray-600 hover:text-green-600 transition-colors">
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
        <div class="bg-gradient-to-r from-green-600 to-emerald-600 px-8 py-6">
          <h1 class="text-3xl font-bold text-white">隐私政策</h1>
          <p class="text-green-100 mt-2">广州文际信息科技有限公司</p>
        </div>

        <!-- 政策内容 -->
        <div class="px-8 py-6 space-y-8">
          <!-- 更新日期 -->
          <div class="text-sm text-gray-500 border-b border-gray-200 pb-4">
            <p>最后更新日期：{{ lastUpdated }}</p>
            <p>生效日期：{{ effectiveDate }}</p>
          </div>

          <!-- 引言 -->
          <section>
            <h2 class="text-xl font-semibold text-gray-900 mb-4">引言</h2>
            <div class="text-gray-700 space-y-3">
              <p>广州文际信息科技有限公司（以下简称"我们"、"文际信息"或"公司"）深知个人信息对您的重要性，并会尽全力保护您的个人信息安全可靠。我们致力于维持您对我们的信任，恪守以下原则，保护您的个人信息：</p>
              <ul class="list-disc list-inside ml-4 space-y-2">
                <li><strong>权责一致原则：</strong>清晰告知您个人信息的处理目的、方式和范围，征求您的同意</li>
                <li><strong>目的明确原则：</strong>只为实现特定、明确、合法的目的处理您的个人信息</li>
                <li><strong>选择同意原则：</strong>充分尊重您的选择权，您有权选择是否提供个人信息</li>
                <li><strong>最少够用原则：</strong>只收集实现产品功能所必需的个人信息</li>
                <li><strong>确保安全原则：</strong>采用业界领先的安全保护措施，保护您的个人信息</li>
                <li><strong>主体参与原则：</strong>保障您对自己的个人信息享有查阅、更正、删除等权利</li>
              </ul>
            </div>
          </section>

          <!-- 第一条：我们收集的信息 -->
          <section>
            <h2 class="text-xl font-semibold text-gray-900 mb-4">1. 我们收集的信息</h2>
            <div class="text-gray-700 space-y-4">
              <div>
                <h3 class="text-lg font-medium text-gray-800 mb-2">1.1 您主动提供的信息</h3>
                <ul class="list-disc list-inside ml-4 space-y-2">
                  <li><strong>账户信息：</strong>注册时提供的邮箱地址、用户名、密码等</li>
                  <li><strong>个人资料：</strong>姓名、电话号码、公司信息等</li>
                  <li><strong>业务信息：</strong>您在使用服务过程中提供的内容、设置和偏好</li>
                  <li><strong>联系信息：</strong>客服咨询时提供的联系方式和问题描述</li>
                </ul>
              </div>
              
              <div>
                <h3 class="text-lg font-medium text-gray-800 mb-2">1.2 自动收集的信息</h3>
                <ul class="list-disc list-inside ml-4 space-y-2">
                  <li><strong>设备信息：</strong>设备型号、操作系统、浏览器类型和版本</li>
                  <li><strong>日志信息：</strong>IP地址、访问时间、访问页面、操作记录</li>
                  <li><strong>使用数据：</strong>功能使用情况、点击行为、停留时间</li>
                  <li><strong>位置信息：</strong>基于IP地址的大致地理位置（如需要）</li>
                </ul>
              </div>

              <div>
                <h3 class="text-lg font-medium text-gray-800 mb-2">1.3 第三方信息</h3>
                <ul class="list-disc list-inside ml-4 space-y-2">
                  <li><strong>社交媒体：</strong>当您授权连接社交媒体账户时获得的信息</li>
                  <li><strong>合作伙伴：</strong>从合法的第三方合作伙伴获得的信息</li>
                </ul>
              </div>
            </div>
          </section>

          <!-- 第二条：信息使用目的 -->
          <section>
            <h2 class="text-xl font-semibold text-gray-900 mb-4">2. 信息使用目的</h2>
            <div class="text-gray-700 space-y-3">
              <p>我们收集和使用您的个人信息主要用于以下目的：</p>
              <ul class="list-disc list-inside ml-4 space-y-2">
                <li><strong>提供服务：</strong>为您提供、维护和改进我们的产品和服务</li>
                <li><strong>账户管理：</strong>创建和管理您的账户，验证身份</li>
                <li><strong>客户支持：</strong>响应您的询问，提供技术支持和客户服务</li>
                <li><strong>安全保护：</strong>检测、预防和解决技术问题，保护服务安全</li>
                <li><strong>产品优化：</strong>分析使用情况，改进产品功能和用户体验</li>
                <li><strong>合规要求：</strong>遵守适用的法律法规和监管要求</li>
                <li><strong>营销推广：</strong>在您同意的情况下，向您发送产品信息和营销内容</li>
              </ul>
            </div>
          </section>

          <!-- 第三条：信息共享和披露 -->
          <section>
            <h2 class="text-xl font-semibold text-gray-900 mb-4">3. 信息共享和披露</h2>
            <div class="text-gray-700 space-y-4">
              <p>我们不会出售、出租或以其他方式向第三方披露您的个人信息，除非：</p>
              
              <div>
                <h3 class="text-lg font-medium text-gray-800 mb-2">3.1 获得您的明确同意</h3>
                <p>在获得您明确同意的情况下，我们可能与第三方共享您的个人信息。</p>
              </div>

              <div>
                <h3 class="text-lg font-medium text-gray-800 mb-2">3.2 服务提供商</h3>
                <p>我们可能与为我们提供服务的第三方服务提供商共享信息，包括：</p>
                <ul class="list-disc list-inside ml-4 space-y-2">
                  <li>云存储和数据处理服务商</li>
                  <li>支付处理服务商</li>
                  <li>客户支持服务商</li>
                  <li>数据分析服务商</li>
                </ul>
              </div>

              <div>
                <h3 class="text-lg font-medium text-gray-800 mb-2">3.3 法律要求</h3>
                <p>在以下情况下，我们可能披露您的个人信息：</p>
                <ul class="list-disc list-inside ml-4 space-y-2">
                  <li>遵守适用的法律、法规、法律程序或政府要求</li>
                  <li>保护我们的权利、财产或安全</li>
                  <li>保护用户或公众的权利、财产或安全</li>
                  <li>防止或调查可能的违法行为</li>
                </ul>
              </div>
            </div>
          </section>

          <!-- 第四条：信息存储和保护 -->
          <section>
            <h2 class="text-xl font-semibold text-gray-900 mb-4">4. 信息存储和保护</h2>
            <div class="text-gray-700 space-y-4">
              <div>
                <h3 class="text-lg font-medium text-gray-800 mb-2">4.1 存储位置</h3>
                <p>您的个人信息主要存储在中华人民共和国境内。如需跨境传输，我们将严格按照相关法律法规的要求进行。</p>
              </div>

              <div>
                <h3 class="text-lg font-medium text-gray-800 mb-2">4.2 存储期限</h3>
                <p>我们仅在实现信息收集目的所需的期间内保留您的个人信息，除非法律要求或允许更长的保留期。</p>
              </div>

              <div>
                <h3 class="text-lg font-medium text-gray-800 mb-2">4.3 安全措施</h3>
                <p>我们采用业界标准的安全措施保护您的个人信息：</p>
                <ul class="list-disc list-inside ml-4 space-y-2">
                  <li>数据加密：使用SSL/TLS加密传输，敏感数据加密存储</li>
                  <li>访问控制：严格的身份验证和权限管理</li>
                  <li>安全监控：24/7安全监控和威胁检测</li>
                  <li>定期审计：定期进行安全评估和漏洞扫描</li>
                  <li>员工培训：定期对员工进行数据保护培训</li>
                </ul>
              </div>
            </div>
          </section>

          <!-- 第五条：您的权利 -->
          <section>
            <h2 class="text-xl font-semibold text-gray-900 mb-4">5. 您的权利</h2>
            <div class="text-gray-700 space-y-3">
              <p>根据相关法律法规，您对自己的个人信息享有以下权利：</p>
              <ul class="list-disc list-inside ml-4 space-y-2">
                <li><strong>知情权：</strong>了解我们收集、使用您个人信息的情况</li>
                <li><strong>访问权：</strong>查阅我们持有的您的个人信息</li>
                <li><strong>更正权：</strong>更正或更新不准确或不完整的个人信息</li>
                <li><strong>删除权：</strong>在特定情况下要求删除您的个人信息</li>
                <li><strong>限制处理权：</strong>在特定情况下限制我们处理您的个人信息</li>
                <li><strong>数据可携权：</strong>在技术可行的情况下，获取您的个人信息副本</li>
                <li><strong>撤回同意权：</strong>撤回您之前给予的同意</li>
              </ul>
              <p class="mt-4">如需行使上述权利，请通过本政策末尾的联系方式与我们联系。</p>
            </div>
          </section>

          <!-- 第六条：Cookie和类似技术 -->
          <section>
            <h2 class="text-xl font-semibold text-gray-900 mb-4">6. Cookie和类似技术</h2>
            <div class="text-gray-700 space-y-3">
              <p>我们使用Cookie和类似技术来：</p>
              <ul class="list-disc list-inside ml-4 space-y-2">
                <li>记住您的登录状态和偏好设置</li>
                <li>分析网站使用情况，改进用户体验</li>
                <li>提供个性化的内容和广告</li>
                <li>防止欺诈和提高安全性</li>
              </ul>
              <p>您可以通过浏览器设置管理Cookie偏好，但这可能影响某些功能的正常使用。</p>
            </div>
          </section>

          <!-- 第七条：未成年人保护 -->
          <section>
            <h2 class="text-xl font-semibold text-gray-900 mb-4">7. 未成年人保护</h2>
            <div class="text-gray-700 space-y-3">
              <p>我们的服务主要面向成年人。如果您是未满18周岁的未成年人，请在监护人的陪同下阅读本政策，并在监护人同意后使用我们的服务。</p>
              <p>我们不会故意收集未满14周岁儿童的个人信息。如果我们发现收集了此类信息，将立即删除。</p>
            </div>
          </section>

          <!-- 第八条：政策更新 -->
          <section>
            <h2 class="text-xl font-semibold text-gray-900 mb-4">8. 政策更新</h2>
            <div class="text-gray-700 space-y-3">
              <p>我们可能会不时更新本隐私政策。更新后的政策将在网站上公布，并通过适当方式通知您。</p>
              <p>如果更新涉及个人信息处理目的、方式的重大变化，我们将重新征求您的同意。</p>
            </div>
          </section>

          <!-- 第九条：联系我们 -->
          <section>
            <h2 class="text-xl font-semibold text-gray-900 mb-4">9. 联系我们</h2>
            <div class="text-gray-700 space-y-3">
              <p>如果您对本隐私政策有任何疑问、意见或建议，或需要行使您的权利，请通过以下方式联系我们：</p>
              <div class="bg-gray-50 p-4 rounded-lg">
                <p><strong>公司名称：</strong>广州文际信息科技有限公司</p>
                <p><strong>数据保护联系邮箱：</strong>privacy@wenji.com（上线后更新）</p>
                <p><strong>客服热线：</strong>400-XXX-XXXX（上线后更新）</p>
                <p><strong>办公地址：</strong>广州市天河区XXX（具体地址待更新）</p>
                <p><strong>邮政编码：</strong>510000</p>
              </div>
              <p class="mt-4">我们将在收到您的请求后15个工作日内回复。</p>
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
              <router-link to="/terms" class="text-gray-600 hover:text-green-600 transition-colors">
                服务条款
              </router-link>
              <router-link to="/contact" class="text-gray-600 hover:text-green-600 transition-colors">
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
  name: 'PrivacyPolicy',
  data() {
    return {
      lastUpdated: '2024年1月15日',
      effectiveDate: '2024年1月15日'
    };
  },
  mounted() {
    // 设置页面标题
    document.title = '隐私政策 - 文际AIGC社交媒体管理平台';
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
  
  h3 {
    font-size: 1.125rem;
  }
}
</style>