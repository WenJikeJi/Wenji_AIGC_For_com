<template>
  <div class="flex h-screen bg-gray-50 overflow-hidden">
    <!-- 通用侧边栏 -->
    <CommonSidebar title="智能管理平台" currentPage="users" :isSuperUser="isSuperUser" />

    <!-- 主内容区域 -->
    <div class="flex-1 flex flex-col overflow-hidden">
      <!-- 通用标题栏 -->
      <CommonHeader 
        title="用户管理"
        :showSearch="isSuperUser"
        searchPlaceholder="搜索用户..."
        :searchValue="searchTerm"
        @search-input="handleSearchInput"
        :notificationCount="notifications.length"
        :isSuperUser="isSuperUser"
      >
        <template #actions>
          <!-- 移除原来的添加成员按钮，现在在表格标题栏中 -->
        </template>
      </CommonHeader>

      <!-- 内容区域 -->
      <main class="flex-1 flex flex-col bg-gradient-to-br from-gray-50 to-blue-50 p-4">
        
        <!-- 编辑成员弹窗 -->
        <div v-if="showEditMemberModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div class="bg-white rounded-lg w-full max-w-2xl p-6 relative shadow-xl border border-gray-200 mx-auto max-h-[90vh] overflow-y-auto">
            <!-- 关闭按钮 -->
            <button 
              @click="closeEditMemberModal"
              class="absolute top-4 right-4 text-gray-400 hover:text-gray-600 transition-colors duration-200 z-10"
            >
              <i class="fas fa-times text-lg"></i>
            </button>

            <!-- 弹窗标题 -->
            <div class="mb-6">
              <h3 class="text-lg font-semibold text-gray-900 flex items-center">
                <i class="fas fa-user-edit text-blue-600 mr-2"></i>
                编辑成员信息
              </h3>
              <p class="text-sm text-gray-500 mt-1">修改成员的基本信息和权限设置</p>
            </div>

            <!-- 标签页导航 -->
            <div class="border-b border-gray-200 mb-6">
              <nav class="-mb-px flex space-x-8">
                <button
                  @click="editMemberActiveTab = 'basic'"
                  :class="[
                    'py-2 px-1 border-b-2 font-medium text-sm transition-colors duration-200',
                    editMemberActiveTab === 'basic'
                      ? 'border-blue-500 text-blue-600'
                      : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                  ]"
                >
                  <i class="fas fa-user mr-2"></i>
                  基本信息
                </button>
                <button
                  @click="editMemberActiveTab = 'logs'"
                  :class="[
                    'py-2 px-1 border-b-2 font-medium text-sm transition-colors duration-200',
                    editMemberActiveTab === 'logs'
                      ? 'border-blue-500 text-blue-600'
                      : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                  ]"
                >
                  <i class="fas fa-history mr-2"></i>
                  操作日志
                </button>
              </nav>
            </div>

            <!-- 基本信息标签页 -->
            <div v-if="editMemberActiveTab === 'basic'" class="space-y-4">
              <!-- 错误提示 -->
              <div v-if="editMemberFormErrors.length > 0" class="bg-red-50 border border-red-200 rounded-md p-3">
                <div class="flex">
                  <div class="flex-shrink-0">
                    <i class="fas fa-exclamation-circle text-red-400"></i>
                  </div>
                  <div class="ml-3">
                    <h3 class="text-sm font-medium text-red-800">请修正以下错误：</h3>
                    <div class="mt-2 text-sm text-red-700">
                      <ul class="list-disc pl-5 space-y-1">
                        <li v-for="error in editMemberFormErrors" :key="error">{{ error }}</li>
                      </ul>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 表单字段 -->
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <!-- 姓名 -->
                <div>
                  <label for="edit-member-name" class="block text-sm font-medium text-gray-700 mb-1">
                    <i class="fas fa-user text-gray-400 mr-1"></i>
                    姓名
                  </label>
                  <input
                    id="edit-member-name"
                    v-model="editMemberForm.username"
                    type="text"
                    class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200"
                    placeholder="请输入姓名"
                  />
                </div>

                <!-- 电话 -->
                <div>
                  <label for="edit-member-phone" class="block text-sm font-medium text-gray-700 mb-1">
                    <i class="fas fa-phone text-gray-400 mr-1"></i>
                    电话
                  </label>
                  <input
                    id="edit-member-phone"
                    v-model="editMemberForm.phone"
                    type="tel"
                    class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200"
                    placeholder="请输入电话号码"
                  />
                </div>

                <!-- 邮箱 -->
                <div class="md:col-span-2">
                  <label for="edit-member-email" class="block text-sm font-medium text-gray-700 mb-1">
                    <i class="fas fa-envelope text-gray-400 mr-1"></i>
                    邮箱
                  </label>
                  <input
                    id="edit-member-email"
                    v-model="editMemberForm.email"
                    type="email"
                    class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200"
                    placeholder="请输入邮箱地址"
                  />
                </div>

                <!-- 状态 -->
                <div>
                  <label for="edit-member-status" class="block text-sm font-medium text-gray-700 mb-1">
                    <i class="fas fa-toggle-on text-gray-400 mr-1"></i>
                    状态
                  </label>
                  <select
                    id="edit-member-status"
                    v-model="editMemberForm.account_status"
                    class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200"
                  >
                    <option value="NORMAL">正常</option>
                    <option value="STOPPED">停用</option>
                    <option value="INVITING">邀请中</option>
                    <option value="INVITE_FAILED">邀请失败</option>
                  </select>
                </div>

                <!-- 角色 -->
                <div>
                  <label for="edit-member-role" class="block text-sm font-medium text-gray-700 mb-1">
                    <i class="fas fa-user-tag text-gray-400 mr-1"></i>
                    角色
                  </label>
                  <select
                    id="edit-member-role"
                    v-model="editMemberForm.role"
                    class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200"
                  >
                    <option :value="0">管理员</option>
                    <option :value="1">成员</option>
                  </select>
                </div>
              </div>

              <!-- 操作按钮 -->
              <div class="flex justify-end space-x-3 pt-4 border-t border-gray-200">
                <button
                  @click="closeEditMemberModal"
                  type="button"
                  class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md shadow-sm hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-colors duration-200"
                >
                  取消
                </button>
                <button
                  @click="saveEditMember"
                  :disabled="editMemberLoading"
                  type="button"
                  class="px-4 py-2 text-sm font-medium text-white bg-gradient-to-r from-blue-500 to-purple-600 border border-transparent rounded-md shadow-sm hover:from-blue-600 hover:to-purple-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200"
                >
                  <i v-if="editMemberLoading" class="fas fa-spinner fa-spin mr-2"></i>
                  <i v-else class="fas fa-save mr-2"></i>
                  {{ editMemberLoading ? '保存中...' : '保存' }}
                </button>
              </div>
            </div>

            <!-- 操作日志标签页 -->
            <div v-if="editMemberActiveTab === 'logs'" class="space-y-4">
              <!-- 日志筛选 -->
              <div class="bg-gray-50 p-4 rounded-lg">
                <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">时间范围</label>
                    <select v-model="memberLogFilter.timeRange" class="w-full px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-blue-500">
                      <option value="">全部时间</option>
                      <option value="today">今天</option>
                      <option value="week">最近一周</option>
                      <option value="month">最近一月</option>
                    </select>
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">操作类型</label>
                    <select v-model="memberLogFilter.operationType" class="w-full px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-blue-500">
                      <option value="">全部类型</option>
                      <option value="UPDATE">信息修改</option>
                      <option value="STATUS_CHANGE">状态变更</option>
                      <option value="ROLE_CHANGE">角色变更</option>
                    </select>
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">关键字</label>
                    <input
                      v-model="memberLogFilter.keyword"
                      type="text"
                      placeholder="搜索操作描述..."
                      class="w-full px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                  </div>
                </div>
              </div>

              <!-- 日志列表 -->
              <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
                <div class="max-h-96 overflow-y-auto">
                  <div v-if="memberLogs.length === 0" class="p-8 text-center text-gray-500">
                    <i class="fas fa-history text-3xl text-gray-300 mb-3"></i>
                    <p>暂无操作日志</p>
                  </div>
                  <div v-else class="divide-y divide-gray-200">
                    <div
                      v-for="log in filteredMemberLogs"
                      :key="log.id"
                      class="p-4 hover:bg-gray-50 transition-colors duration-200 cursor-pointer"
                      @click="toggleLogDetail(log.id)"
                    >
                      <div class="flex items-start justify-between">
                        <div class="flex-1">
                          <div class="flex items-center space-x-2 mb-1">
                            <span class="text-sm font-medium text-gray-900">{{ formatLogTime(log.created_at) }}</span>
                            <span :class="getLogTypeClass(log.operation_type)" class="px-2 py-1 text-xs font-medium rounded-full">
                              {{ getLogTypeText(log.operation_type) }}
                            </span>
                          </div>
                          <p class="text-sm text-gray-600 mb-1">{{ log.description }}</p>
                          <p class="text-xs text-gray-400">
                            操作人：{{ log.operator_name || '系统' }} | IP：{{ log.ip_address || '未知' }}
                          </p>
                        </div>
                        <button class="text-gray-400 hover:text-gray-600 ml-2">
                          <i :class="expandedLogs.includes(log.id) ? 'fas fa-chevron-up' : 'fas fa-chevron-down'"></i>
                        </button>
                      </div>
                      
                      <!-- 详细变更信息 -->
                      <div v-if="expandedLogs.includes(log.id)" class="mt-3 pt-3 border-t border-gray-100">
                        <div v-if="log.changes && log.changes.length > 0" class="space-y-2">
                          <h4 class="text-xs font-medium text-gray-700 mb-2">变更详情：</h4>
                          <div v-for="change in log.changes" :key="change.field" class="bg-gray-50 p-2 rounded text-xs">
                            <div class="font-medium text-gray-700">{{ change.field_name }}：</div>
                            <div class="flex items-center space-x-2 mt-1">
                              <span class="text-red-600 bg-red-50 px-2 py-1 rounded">{{ change.old_value || '空' }}</span>
                              <i class="fas fa-arrow-right text-gray-400"></i>
                              <span class="text-green-600 bg-green-50 px-2 py-1 rounded">{{ change.new_value || '空' }}</span>
                            </div>
                          </div>
                        </div>
                        <div v-else class="text-xs text-gray-500">
                          无详细变更信息
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
        </div>

        <!-- 编辑用户名弹窗 -->
        <div v-if="showEditUserNameModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
            <!-- 关闭按钮 -->
            <button 
              @click="showEditUserNameModal = false"
              class="absolute top-4 right-4 text-gray-500 hover:text-gray-700 bg-gray-100 hover:bg-gray-200 rounded-full w-8 h-8 flex items-center justify-center transform hover:scale-110 transition-all duration-200 shadow-sm"
            >
              <i class="fas fa-times text-sm"></i>
            </button>
            <h3 class="text-lg font-medium text-gray-800 mb-4">编辑用户名</h3>
            
            <!-- 表单错误信息 -->
            <div v-if="editUserNameErrors.length > 0" class="mb-4">
              <div v-for="error in editUserNameErrors" :key="error" class="bg-red-50 border border-red-200 text-red-700 text-sm mb-1 p-2 rounded">
                {{ error }}
              </div>
            </div>
            
            <form @submit.prevent="handleEditUserName">
              <div class="mb-4">
                <label for="newUserName" class="block text-sm font-medium text-gray-700 mb-1">新用户名</label>
                <input
                  id="newUserName"
                  type="text"
                  v-model="editUserNameForm.newUserName"
                  class="w-full px-3 py-2 bg-white border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 text-gray-900 placeholder-gray-500"
                  style="font-family: 'Arial', 'Microsoft YaHei', sans-serif;"
                  placeholder="请输入新的用户名"
                  required
                />
              </div>
              
              <div class="flex justify-end space-x-3">
                <button
                  type="button"
                  @click="showEditUserNameModal = false"
                  class="px-4 py-2 text-sm font-medium text-gray-700 border border-gray-300 hover:bg-gray-50 transform hover:scale-105 transition-all duration-300 rounded-md"
                >
                  取消
                </button>
                <button
                  type="submit"
                  :disabled="editUserNameLoading"
                  class="px-4 py-2 text-sm font-medium text-white bg-gradient-to-r from-blue-500 to-purple-600 hover:from-blue-600 hover:to-purple-700 transform hover:scale-105 transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none rounded-md flex items-center shadow-sm"
                >
                  <i v-if="editUserNameLoading" class="fas fa-spinner fa-spin mr-2"></i>
                  {{ editUserNameLoading ? '保存中...' : '保存' }}
                </button>
              </div>
            </form>
          </div>
        </div>

        <!-- 修改密码弹窗 -->
        <div v-if="showChangePasswordModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div class="bg-white rounded-lg w-full max-w-md p-6 relative shadow-xl border border-gray-200 mx-auto">
            <!-- 关闭按钮 -->
            <button 
              @click="showChangePasswordModal = false"
              class="absolute top-4 right-4 text-gray-500 hover:text-gray-700 bg-gray-100 hover:bg-gray-200 rounded-full w-8 h-8 flex items-center justify-center transform hover:scale-110 transition-all duration-200 shadow-sm"
            >
              <i class="fas fa-times text-sm"></i>
            </button>
            <h3 class="text-lg font-medium text-gray-800 mb-4">修改密码</h3>
            
            <!-- 修改方式选择 -->
            <div class="mb-4">
              <div class="flex space-x-4">
                <label class="flex items-center">
                  <input
                    type="radio"
                    v-model="changePasswordMethod"
                    value="original"
                    class="mr-2 text-blue-600 bg-white border-gray-300 focus:ring-blue-500"
                  />
                  <span class="text-sm text-gray-700">原密码验证</span>
                </label>
                <label class="flex items-center">
                  <input
                    type="radio"
                    v-model="changePasswordMethod"
                    value="email"
                    class="mr-2 text-blue-600 bg-white border-gray-300 focus:ring-blue-500"
                  />
                  <span class="text-sm text-gray-700">邮箱验证码</span>
                </label>
              </div>
            </div>
            
            <!-- 表单错误信息 -->
            <div v-if="changePasswordErrors.length > 0" class="mb-4">
              <div v-for="error in changePasswordErrors" :key="error" class="bg-red-50 border border-red-200 text-red-700 text-sm mb-1 p-2 rounded">
                {{ error }}
              </div>
            </div>
            
            <form @submit.prevent="handleChangePassword">
              <!-- 原密码验证方式 -->
              <div v-if="changePasswordMethod === 'original'">
                <div class="mb-4">
                  <label for="currentPassword" class="block text-sm font-medium text-gray-700 mb-1">当前密码</label>
                  <div class="relative">
                    <input
                      id="currentPassword"
                      :type="showCurrentPassword ? 'text' : 'password'"
                      v-model="changePasswordForm.currentPassword"
                      class="w-full px-3 py-2 bg-white border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 text-gray-900 placeholder-gray-500"
                      style="font-family: 'Arial', 'Microsoft YaHei', sans-serif;"
                      placeholder="请输入当前密码"
                      required
                    />
                    <button
                      type="button"
                      class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-500 hover:text-gray-700"
                      @click="showCurrentPassword = !showCurrentPassword"
                    >
                      <i :class="showCurrentPassword ? 'fas fa-eye-slash' : 'fas fa-eye'" />
                    </button>
                  </div>
                </div>
              </div>
              
              <!-- 邮箱验证码方式 -->
              <div v-if="changePasswordMethod === 'email'">
                <div class="mb-4">
                  <label for="emailCode" class="block text-sm font-medium text-gray-700 mb-1">邮箱验证码</label>
                  <div class="flex space-x-2">
                    <input
                      id="emailCode"
                      type="text"
                      v-model="changePasswordForm.emailCode"
                      class="flex-1 px-3 py-2 bg-white border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 text-gray-900 placeholder-gray-500"
                      style="font-family: 'Arial', 'Microsoft YaHei', sans-serif;"
                      placeholder="请输入验证码"
                      required
                    />
                    <button
                      type="button"
                      @click="sendEmailCode"
                      :disabled="emailCodeLoading || emailCodeCountdown > 0"
                      class="px-3 py-2 text-sm font-medium text-white bg-gradient-to-r from-blue-500 to-purple-600 hover:from-blue-600 hover:to-purple-700 transform hover:scale-105 transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none rounded-md shadow-sm"
                    >
                      <span v-if="emailCodeCountdown > 0">{{ emailCodeCountdown }}s</span>
                      <span v-else-if="emailCodeLoading">发送中...</span>
                      <span v-else>发送验证码</span>
                    </button>
                  </div>
                </div>
              </div>
              
              <!-- 新密码 -->
              <div class="mb-4">
                <label for="newPassword" class="block text-sm font-medium text-gray-700 mb-1">新密码</label>
                <div class="relative">
                  <input
                    id="newPassword"
                    :type="showNewPassword ? 'text' : 'password'"
                    v-model="changePasswordForm.newPassword"
                    class="w-full px-3 py-2 bg-white border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 text-gray-900 placeholder-gray-500"
                    style="font-family: 'Arial', 'Microsoft YaHei', sans-serif;"
                    placeholder="请输入新密码"
                    required
                  />
                  <button
                    type="button"
                    class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-500 hover:text-gray-700"
                    @click="showNewPassword = !showNewPassword"
                  >
                    <i :class="showNewPassword ? 'fas fa-eye-slash' : 'fas fa-eye'" />
                  </button>
                </div>
              </div>
              
              <!-- 确认新密码 -->
              <div class="mb-4">
                <label for="confirmNewPassword" class="block text-sm font-medium text-gray-700 mb-1">确认新密码</label>
                <div class="relative">
                  <input
                    id="confirmNewPassword"
                    :type="showConfirmNewPassword ? 'text' : 'password'"
                    v-model="changePasswordForm.confirmNewPassword"
                    class="w-full px-3 py-2 bg-white border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 text-gray-900 placeholder-gray-500"
                    style="font-family: 'Arial', 'Microsoft YaHei', sans-serif;"
                    placeholder="请再次输入新密码"
                    required
                  />
                  <button
                    type="button"
                    class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-500 hover:text-gray-700"
                    @click="showConfirmNewPassword = !showConfirmNewPassword"
                  >
                    <i :class="showConfirmNewPassword ? 'fas fa-eye-slash' : 'fas fa-eye'" />
                  </button>
                </div>
              </div>
              
              <div class="flex justify-end space-x-3">
                <button
                  type="button"
                  @click="showChangePasswordModal = false"
                  class="px-4 py-2 text-sm font-medium text-gray-700 border border-gray-300 hover:bg-gray-50 transform hover:scale-105 transition-all duration-300 rounded-md"
                >
                  取消
                </button>
                <button
                  type="submit"
                  :disabled="changePasswordLoading"
                  class="px-4 py-2 text-sm font-medium text-white bg-gradient-to-r from-blue-500 to-purple-600 hover:from-blue-600 hover:to-purple-700 transform hover:scale-105 transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none rounded-md flex items-center shadow-sm"
                >
                  <i v-if="changePasswordLoading" class="fas fa-spinner fa-spin mr-2"></i>
                  {{ changePasswordLoading ? '修改中...' : '修改密码' }}
                </button>
              </div>
            </form>
          </div>
        </div>

        <!-- 创建子账户弹窗 -->
        <div v-if="showCreateSubAccountModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div class="bg-white w-full max-w-md p-6 relative mx-auto" style="border-radius: 8px; box-shadow: 0 4px 16px rgba(0,0,0,0.1);">
            <!-- 关闭按钮 -->
            <button 
              @click="showCreateSubAccountModal = false"
              class="absolute top-4 right-4 rounded-full flex items-center justify-center transition-all duration-300 hover:bg-gradient-to-r hover:from-purple-500 hover:to-indigo-600 hover:text-white"
              style="width: 24px; height: 24px; color: #999; background: transparent;"
            >
              <i class="fas fa-times text-sm"></i>
            </button>
            <h3 class="font-semibold mb-5" style="font-size: 16px; font-weight: 600; color: #333; margin-bottom: 20px;">创建子账户</h3>
            
            <!-- 表单错误信息 -->
            <div v-if="subAccountFormErrors.length > 0" class="mb-4">
              <div v-for="error in subAccountFormErrors" :key="error" class="text-red-600 text-sm mb-1 bg-red-50 px-3 py-2 rounded-md border border-red-200">
                {{ error }}
              </div>
            </div>
            
            <form @submit.prevent="handleCreateSubAccount">
              <div class="mb-4">
                <label for="username" class="block font-medium mb-2" style="font-size: 14px; color: #666; margin-bottom: 8px;">用户名</label>
                <input
                  id="username"
                  type="text"
                  v-model="subAccountForm.username"
                  class="w-full bg-white text-gray-800 transition-all duration-300 hover:shadow-sm focus:outline-none focus:ring-0"
                  style="font-family: 'Arial', 'Microsoft YaHei', sans-serif; border-radius: 4px; padding: 8px 12px; border: 1px solid #e5e7eb; color: #333;"
                  :style="{ 'border': focusedField === 'username' ? '1px solid transparent' : '1px solid #e5e7eb', 'background-image': focusedField === 'username' ? 'linear-gradient(white, white), linear-gradient(90deg, #6A5ACD, #9370DB)' : 'none', 'background-origin': 'border-box', 'background-clip': 'padding-box, border-box' }"
                  placeholder="请输入用户名"
                  @focus="focusedField = 'username'"
                  @blur="focusedField = ''"
                  required
                />
              </div>
              
              <div class="mb-4">
                <label for="email" class="block font-medium mb-2" style="font-size: 14px; color: #666; margin-bottom: 8px;">邮箱</label>
                <input
                  id="email"
                  type="email"
                  v-model="subAccountForm.email"
                  class="w-full bg-white text-gray-800 transition-all duration-300 hover:shadow-sm focus:outline-none focus:ring-0"
                  style="font-family: 'Arial', 'Microsoft YaHei', sans-serif; border-radius: 4px; padding: 8px 12px; border: 1px solid #e5e7eb; color: #333;"
                  :style="{ 'border': focusedField === 'email' ? '1px solid transparent' : '1px solid #e5e7eb', 'background-image': focusedField === 'email' ? 'linear-gradient(white, white), linear-gradient(90deg, #6A5ACD, #9370DB)' : 'none', 'background-origin': 'border-box', 'background-clip': 'padding-box, border-box' }"
                  placeholder="请输入邮箱地址"
                  @focus="focusedField = 'email'"
                  @blur="focusedField = ''"
                  required
                />
              </div>
              
              <div class="mb-6">
                <label for="role" class="block font-medium mb-2" style="font-size: 14px; color: #666; margin-bottom: 8px;">角色</label>
                <select
                  id="role"
                  v-model="subAccountForm.role"
                  class="w-full bg-white text-gray-800 transition-all duration-300 hover:shadow-sm focus:outline-none focus:ring-0"
                  style="border-radius: 4px; padding: 8px 12px; border: 1px solid #e5e7eb; color: #333;"
                  :style="{ 'border': focusedField === 'role' ? '1px solid transparent' : '1px solid #e5e7eb', 'background-image': focusedField === 'role' ? 'linear-gradient(white, white), linear-gradient(90deg, #6A5ACD, #9370DB)' : 'none', 'background-origin': 'border-box', 'background-clip': 'padding-box, border-box' }"
                  @focus="focusedField = 'role'"
                  @blur="focusedField = ''"
                  required
                >
                  <option value="admin">管理员</option>
                  <option value="editor">编辑者</option>
                  <option value="viewer">查看者</option>
                </select>
              </div>
              
              <div class="flex justify-end" style="margin-top: 20px; gap: 16px;">
                <button 
                  type="button"
                  @click="closeCreateSubAccountModal"
                  :disabled="createSubAccountLoading"
                  class="bg-white text-gray-600 hover:bg-gray-50 transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed hover:shadow-sm"
                  style="border-radius: 4px; padding: 8px 24px; border: 1px solid #e5e7eb; color: #666; background: white; margin-right: 16px;"
                >
                  取消
                </button>
                <button 
                  type="submit"
                  :disabled="createSubAccountLoading"
                  class="text-white transform hover:scale-105 transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none flex items-center shadow-sm hover:shadow-md"
                  style="border-radius: 4px; padding: 8px 24px; background: linear-gradient(90deg, #6A5ACD, #9370DB); color: white;"
                  @mouseover="$event.target.style.background = 'linear-gradient(90deg, #5D47F5, #9F7AEA)'"
                  @mouseout="$event.target.style.background = 'linear-gradient(90deg, #6A5ACD, #9370DB)'"
                >
                  <i v-if="createSubAccountLoading" class="fas fa-spinner fa-spin mr-2"></i>
                  {{ createSubAccountLoading ? '创建中...' : '创建账户' }}
                </button>
              </div>
            </form>
          </div>
        </div>

        <!-- 统计卡片 - 仅管理员可查看 -->
        <div v-if="isSuperUser" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
          <div class="bg-white p-4 rounded-lg shadow-md border border-gray-200 transform hover:-translate-y-1 hover:scale-105 transition-all duration-300 hover:shadow-lg h-32 flex flex-col justify-between cursor-pointer">
            <div class="flex items-center justify-between mb-3">
              <h3 class="text-sm font-medium text-gray-600">子账户总数</h3>
              <span class="p-2 bg-gradient-to-br from-blue-500 to-purple-600 text-white rounded-lg shadow-sm transition-all duration-300 hover:shadow-md">
                <i class="fas fa-users text-sm" />
              </span>
            </div>
            <div class="flex-1 flex flex-col justify-center">
              <p class="text-2xl font-bold text-gray-800 mb-2">{{ subAccountsCount }}</p>
            </div>
            <p class="text-xs bg-green-50 text-gray-700 px-2 py-1 rounded flex items-center">
              <i class="fas fa-arrow-up mr-1 text-green-600" /> 较上月增长12%
            </p>
          </div>
          <div class="bg-white p-4 rounded-lg shadow-md border border-gray-200 transform hover:-translate-y-1 hover:scale-105 transition-all duration-300 hover:shadow-lg h-32 flex flex-col justify-between cursor-pointer">
            <div class="flex items-center justify-between mb-3">
              <h3 class="text-sm font-medium text-gray-600">今日活跃</h3>
              <span class="p-2 bg-gradient-to-br from-blue-500 to-purple-600 text-white rounded-lg shadow-sm transition-all duration-300 hover:shadow-md">
                <i class="fas fa-user-check text-sm" />
              </span>
            </div>
            <div class="flex-1 flex flex-col justify-center">
              <p class="text-2xl font-bold text-gray-800 mb-2">{{ todayActiveSubAccountsCount }}</p>
            </div>
            <p class="text-xs text-gray-600">活跃率: <span class="text-green-600 font-semibold">{{ activePercentage }}%</span></p>
          </div>
          <div class="bg-white p-4 rounded-lg shadow-md border border-gray-200 transform hover:-translate-y-1 hover:scale-105 transition-all duration-300 hover:shadow-lg h-32 flex flex-col justify-between cursor-pointer">
            <div class="flex items-center justify-between mb-3">
              <h3 class="text-sm font-medium text-gray-600">已禁用账户</h3>
              <span class="p-2 bg-gradient-to-br from-blue-500 to-purple-600 text-white rounded-lg shadow-sm transition-all duration-300 hover:shadow-md">
                <i class="fas fa-user-times text-sm" />
              </span>
            </div>
            <div class="flex-1 flex flex-col justify-center">
              <p class="text-2xl font-bold text-gray-800 mb-2">{{ stoppedSubAccountsCount }}</p>
            </div>
            <p class="text-xs bg-red-50 text-gray-700 px-2 py-1 rounded flex items-center">
              <i class="fas fa-arrow-down mr-1 text-red-600" /> 较上月减少8%
            </p>
          </div>
          <div class="bg-white p-4 rounded-lg shadow-md border border-gray-200 transform hover:-translate-y-1 hover:scale-105 transition-all duration-300 hover:shadow-lg h-32 flex flex-col justify-between cursor-pointer">
            <div class="flex items-center justify-between mb-3">
              <h3 class="text-sm font-medium text-gray-600">待处理邀请</h3>
              <span class="p-2 bg-gradient-to-br from-blue-500 to-purple-600 text-white rounded-lg shadow-sm transition-all duration-300 hover:shadow-md">
                <i class="fas fa-user-clock text-sm" />
              </span>
            </div>
            <div class="flex-1 flex flex-col justify-center">
              <p class="text-2xl font-bold text-gray-800 mb-2">{{ invitingSubAccountsCount }}</p>
            </div>
            <p class="text-xs bg-orange-50 text-gray-700 px-2 py-1 rounded flex items-center">
              <i class="fas fa-clock mr-1" /> 等待确认
            </p>
          </div>
        </div>

        <!-- 用户列表表格 - 参考截图样式 -->
        <div class="flex-1 flex flex-col bg-white rounded-lg shadow-md border border-gray-200 overflow-hidden">
          <!-- 表格标题 -->
          <div class="px-4 py-3 border-b border-gray-200 bg-gray-50">
            <div class="flex items-center justify-between">
              <div class="flex items-center space-x-3">
                <h3 class="text-base font-semibold text-gray-800">成员</h3>
                <span class="px-2 py-1 text-xs bg-gradient-to-br from-blue-500 to-purple-600 text-white rounded-full shadow-sm">{{ filteredUsers.length }} 人</span>
              </div>
              <div class="flex items-center space-x-2">
                <!-- 添加成员按钮 - 只有管理员可见 -->
                <button v-if="isSuperUser" class="px-3 py-1.5 text-xs bg-gradient-to-br from-blue-500 to-purple-600 text-white rounded-md hover:shadow-md transform hover:scale-105 transition-all duration-300 flex items-center shadow-sm" @click="showCreateSubAccountModal = true">
                  <i class="fas fa-plus mr-1" />添加成员
                </button>
                <!-- 筛选按钮 -->
                <button class="px-3 py-1.5 text-xs border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 transform hover:scale-105 transition-all duration-300 flex items-center shadow-sm">
                  <i class="fas fa-filter mr-1" />筛选
                </button>
                <!-- 导出按钮 -->
                <button class="px-3 py-1.5 text-xs bg-green-50 text-gray-700 rounded-md hover:bg-green-100 hover:shadow-sm transform hover:scale-105 transition-all duration-300 flex items-center shadow-sm">
                  <i class="fas fa-download mr-1" />导出
                </button>
              </div>
            </div>
          </div>
          
          <div class="flex-1 overflow-y-auto">
            <table class="min-w-full">
              <thead class="bg-gray-50 border-b border-gray-200">
                <tr>
                  <th scope="col" class="px-3 py-2 text-left">
                    <input type="checkbox">
                  </th>
                  <th scope="col" class="px-3 py-2 text-left text-xs font-semibold text-gray-700">
                    姓名
                  </th>
                  <th scope="col" class="px-3 py-2 text-left text-xs font-semibold text-gray-700">
                    电话
                  </th>
                  <th scope="col" class="px-3 py-2 text-left text-xs font-semibold text-gray-700">
                    邮箱
                  </th>
                  <th scope="col" class="px-3 py-2 text-left text-xs font-semibold text-gray-700">
                    状态
                  </th>
                  <th scope="col" class="px-3 py-2 text-left text-xs font-semibold text-gray-700">
                    角色
                  </th>
                  <th scope="col" class="px-3 py-2 text-right text-xs font-semibold text-gray-700">
                    操作
                  </th>
                </tr>
              </thead>
              <tbody class="bg-white">
                <!-- 管理员可以看到所有用户，普通用户只能看到自己 -->
                <tr v-for="user in isSuperUser ? filteredUsers : filteredUsers.filter(u => u.id === currentUser?.id)" :key="user.id" class="border-b border-gray-100 hover:bg-gray-50 hover:shadow-sm transform hover:scale-[1.01] transition-all duration-300 h-12 cursor-pointer">
                  <td class="px-3 py-1.5">
                    <input type="checkbox">
                  </td>
                  <td class="px-3 py-1.5">
                    <div class="flex items-center">
                      <div class="flex-shrink-0 h-6 w-6">
                        <img class="h-6 w-6 rounded-full border border-gray-200" :src="getUserAvatar(user.email)" alt="">
                      </div>
                      <div class="ml-2">
                        <div class="text-xs font-medium text-gray-800 flex items-center">
                          {{ user.username || user.email.split('@')[0] }}
                          <span v-if="user.id === currentUser?.id" class="ml-2 px-1.5 py-0.5 text-xs bg-gradient-to-br from-blue-500 to-purple-600 text-white rounded-full font-medium shadow-sm">我</span>
                        </div>
                      </div>
                    </div>
                  </td>
                  <td class="px-3 py-1.5 text-xs text-gray-600">
                    {{ user.phone || '未设置' }}
                  </td>
                  <td class="px-3 py-1.5 text-xs text-gray-600">
                    {{ user.email }}
                  </td>
                  <td class="px-3 py-1.5">
                    <!-- 主账号（管理员）始终显示正常 -->
                    <span v-if="user.role === 0" class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-green-50 text-gray-700 shadow-sm">
                      正常
                    </span>
                    <!-- 子账号状态判断 -->
                    <span v-else-if="user.accountStatus === 'NORMAL' && user.lastLoginTime" class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-green-50 text-gray-700 shadow-sm">
                      正常
                    </span>
                    <span v-else-if="user.accountStatus === 'STOPPED'" class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-red-50 text-gray-700 shadow-sm">
                      已禁用
                    </span>
                    <span v-else-if="user.accountStatus === 'INVITING' || (user.accountStatus === 'NORMAL' && !user.lastLoginTime)" class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium shadow-sm" :class="getInviteCountdown(user) > 0 ? 'bg-orange-50 text-gray-700' : 'bg-red-50 text-gray-700'">
                      <span class="flex items-center">
                        待处理
                        <span v-if="getInviteCountdown(user) > 0" class="ml-1 text-xs">
                          ({{ getInviteCountdown(user) }}天)
                        </span>
                        <span v-else-if="getInviteCountdown(user) === 0" class="ml-1 text-xs">
                          (已过期)
                        </span>
                      </span>
                    </span>
                    <span v-else-if="user.accountStatus === 'INVALID'" class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-gray-50 text-gray-700 shadow-sm">
                      无效
                    </span>
                    <span v-else-if="user.accountStatus === 'INVITE_FAILED'" class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-red-50 text-gray-700 shadow-sm">
                      邀请失败
                    </span>
                    <span v-else class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-gray-50 text-gray-700 shadow-sm">
                      未知
                    </span>
                  </td>
                  <td class="px-3 py-1.5">
                    <span v-if="user.role === 0" class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-gradient-to-br from-blue-500 to-purple-600 text-white shadow-sm">
                      管理员
                    </span>
                    <span v-else class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-gradient-to-br from-blue-500 to-purple-600 text-white shadow-sm">
                      成员
                    </span>
                  </td>
                  <td class="px-3 py-1.5 text-right text-xs font-medium">
                    <div class="flex items-center justify-end space-x-1">
                      <!-- Edit Member Button - 管理员可以编辑所有用户的完整信息 -->
                      <button v-if="isSuperUser" @click="editMember(user)" class="text-gray-500 hover:text-blue-600 transform hover:scale-110 transition-all duration-300 p-1 rounded hover:bg-gray-100" title="编辑成员信息">
                        <i class="fas fa-user-edit" />
                      </button>
                      
                      <!-- Edit Username Button - 所有用户都可以编辑自己的用户名 -->
                      <button v-if="String(user.id) === String(currentUser?.id)" @click="editUserName(user)" class="text-gray-500 hover:text-blue-600 transform hover:scale-110 transition-all duration-300 p-1 rounded hover:bg-gray-100" title="编辑用户名">
                        <i class="fas fa-edit" />
                      </button>
                      
                      <!-- Status Action Buttons - Only visible to supervisors and cannot operate on themselves -->
                      <template v-if="isSuperUser && user.id !== currentUser?.id">
                        <!-- Edit Button -->
                        <button @click="editMember(user)" class="text-gray-500 hover:text-blue-600 transform hover:scale-110 transition-all duration-300 p-1 rounded hover:bg-gray-100" title="编辑成员">
                          <i class="fas fa-edit" />
                        </button>
                        
                        <!-- Enable/Disable Button -->
                        <button v-if="user.accountStatus === 'NORMAL'" @click="updateUserAccountStatus(user, 'STOPPED')" class="text-gray-500 hover:text-red-600 transform hover:scale-110 transition-all duration-300 p-1 rounded hover:bg-gray-100" title="禁用账户">
                          <i class="fas fa-ban" />
                        </button>
                        <button v-else-if="user.accountStatus === 'STOPPED'" @click="updateUserAccountStatus(user, 'NORMAL')" class="text-gray-500 hover:text-green-600 transform hover:scale-110 transition-all duration-300 p-1 rounded hover:bg-gray-100" title="启用账户">
                          <i class="fas fa-check-circle" />
                        </button>
                        
                        <!-- Resend Invitation Button - Only shown when invitation failed or expired -->
                        <button v-if="user.accountStatus === 'INVITE_FAILED' || (isInviteExpired(user) && !user.lastLoginTime)" @click="resendInvite(user)" class="text-gray-500 hover:text-yellow-600 transform hover:scale-110 transition-all duration-300 p-1 rounded hover:bg-gray-100" title="重新发送邀请">
                          <i class="fas fa-paper-plane" />
                        </button>
                      </template>
                      
                      <!-- More Actions Button - 所有用户都可以看到更多操作菜单 -->
                      <div class="relative user-menu-container">
                        <button @click="toggleUserMenu(user.id)" class="text-gray-500 hover:text-gray-800 transform hover:scale-110 transition-all duration-300 p-1 rounded hover:bg-gray-100" title="更多操作">
                          <i class="fas fa-ellipsis-h" />
                        </button>
                        
                        <!-- Dropdown Menu -->
                        <div v-if="showUserMenu === user.id" class="absolute right-0 mt-1 w-40 bg-white rounded-md shadow-lg border border-gray-200 z-10">
                          <div class="py-1">
                            <!-- 查看详情 - 所有用户都可以查看自己的详情，管理员可以查看所有用户 -->
                            <button v-if="user.id === currentUser?.id || isSuperUser" @click="viewUserDetails(user)" class="block w-full text-left px-3 py-2 text-xs text-gray-700 hover:bg-gray-50 hover:text-gray-900">
                              <i class="fas fa-eye mr-2" />查看详情
                            </button>
                            <!-- 修改密码 - 所有用户都可以修改自己的密码，管理员可以修改所有用户密码 -->
                            <button v-if="user.id === currentUser?.id || isSuperUser" @click="changePassword(user)" class="block w-full text-left px-3 py-2 text-xs text-gray-700 hover:bg-gray-50 hover:text-gray-900">
                              <i class="fas fa-key mr-2" />修改密码
                            </button>
                            <!-- 管理员专用操作 -->
                            <div v-if="isSuperUser && user.id !== currentUser?.id" class="border-t border-gray-200 my-1"></div>
                            <button v-if="isSuperUser && user.id !== currentUser?.id && user.accountStatus !== 'STOPPED'" @click="disableUser(user)" class="block w-full text-left px-3 py-2 text-xs text-yellow-600 hover:bg-gray-50 hover:text-yellow-700">
                              <i class="fas fa-ban mr-2" />停用账户
                            </button>
                            <button v-if="isSuperUser && user.id !== currentUser?.id && user.accountStatus === 'STOPPED'" @click="enableUser(user)" class="block w-full text-left px-3 py-2 text-xs text-green-600 hover:bg-gray-50 hover:text-green-700">
                              <i class="fas fa-check-circle mr-2" />启用账户
                            </button>
                            <button v-if="isSuperUser && user.role !== 0 && user.id !== currentUser?.id" @click="removeUser(user)" class="block w-full text-left px-3 py-2 text-xs text-red-600 hover:bg-gray-50 hover:text-red-700">
                              <i class="fas fa-trash mr-2" />删除用户
                            </button>
                          </div>
                        </div>
                      </div>
                    </div>
                  </td>
                </tr>
                <!-- If regular user doesn't find their own record, show empty state -->
                <tr v-if="!isSuperUser && filteredUsers.filter(u => u.id === currentUser?.id).length === 0">
                  <td colspan="7" class="px-3 py-16">
                    <div class="flex flex-col items-center justify-center min-h-[300px]">
                      <div class="relative mb-6">
                        <!-- 背景装饰 -->
                        <div class="absolute inset-0 flex items-center justify-center opacity-5">
                          <div class="w-32 h-32 bg-gradient-to-br from-blue-500 to-purple-600 rounded-full blur-2xl"></div>
                        </div>
                        
                        <!-- 主图标 -->
                        <div class="relative">
                          <div class="mx-auto w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center shadow-sm">
                            <i class="fas fa-user-slash text-2xl text-gray-400"></i>
                          </div>
                          <!-- 装饰点 -->
                          <div class="absolute -top-1 -right-1 w-4 h-4 bg-blue-500 rounded-full opacity-20 animate-pulse"></div>
                          <div class="absolute -bottom-0.5 -left-0.5 w-3 h-3 bg-purple-500 rounded-full opacity-30 animate-pulse delay-300"></div>
                        </div>
                      </div>
                      
                      <!-- 文本内容 -->
                      <h3 class="text-lg font-semibold text-gray-800 mb-2">未找到用户记录</h3>
                      <p class="text-gray-600 text-center max-w-sm leading-relaxed">
                        系统中找不到您的用户记录。请联系管理员寻求帮助。
                      </p>
                    </div>
                  </td>
                </tr>
                
                <!-- Empty state for supervisors when no users exist -->
                <tr v-if="isSuperUser && filteredUsers.length === 0">
                  <td colspan="7" class="px-3 py-16">
                    <div class="flex flex-col items-center justify-center min-h-[300px]">
                      <div class="relative mb-6">
                        <!-- 背景装饰 -->
                        <div class="absolute inset-0 flex items-center justify-center opacity-5">
                          <div class="w-32 h-32 bg-gradient-to-br from-blue-500 to-purple-600 rounded-full blur-2xl"></div>
                        </div>
                        
                        <!-- 主图标 -->
                        <div class="relative">
                          <div class="mx-auto w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center shadow-sm">
                            <i class="fas fa-users text-2xl text-gray-400"></i>
                          </div>
                          <!-- 装饰点 -->
                          <div class="absolute -top-1 -right-1 w-4 h-4 bg-blue-500 rounded-full opacity-20 animate-pulse"></div>
                          <div class="absolute -bottom-0.5 -left-0.5 w-3 h-3 bg-purple-500 rounded-full opacity-30 animate-pulse delay-300"></div>
                        </div>
                      </div>
                      
                      <!-- 文本内容 -->
                      <h3 class="text-lg font-semibold text-gray-800 mb-2">暂无用户</h3>
                      <p class="text-gray-600 text-center max-w-sm mb-6 leading-relaxed">
                        系统中暂无用户。创建您的第一个用户账户开始使用。
                      </p>
                      
                      <!-- 操作按钮 -->
                      <div class="flex flex-col sm:flex-row gap-3 justify-center">
                        <button 
                          @click="showCreateSubAccountModal = true"
                          class="px-6 py-2.5 bg-gradient-to-r from-blue-500 to-purple-600 text-white rounded-lg hover:from-blue-600 hover:to-purple-700 transition-all duration-200 shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 font-medium text-sm"
                        >
                          <i class="fas fa-plus mr-2"></i>
                          创建第一个用户
                        </button>
                        <button 
                          @click="loadUsers"
                          class="px-6 py-2.5 bg-white text-gray-700 border border-gray-300 rounded-lg hover:bg-gray-50 hover:border-gray-400 transition-all duration-200 font-medium text-sm"
                        >
                          <i class="fas fa-refresh mr-2"></i>
                          刷新
                        </button>
                      </div>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <!-- Pagination -->
          <div class="px-4 py-3 bg-white border-t border-gray-200 sm:px-4 flex items-center justify-between">
            <div class="hidden sm:flex sm:flex-1 sm:items-center sm:justify-between">
              <div>
                <p class="text-xs text-gray-600">
                  显示第 <span class="font-medium text-gray-800">{{ Math.min((currentPage - 1) * pageSize + 1, filteredUsers.length) }}</span> 到 <span class="font-medium text-gray-800">{{ Math.min(currentPage * pageSize, filteredUsers.length) }}</span> 条，共 <span class="font-medium text-gray-800">{{ filteredUsers.length }}</span> 条结果
                </p>
              </div>
              <div>
                <nav class="relative z-0 inline-flex rounded-md shadow-sm -space-x-px" aria-label="Pagination">
                  <button @click="prevPage" :disabled="currentPage === 1" class="relative inline-flex items-center px-2 py-1.5 rounded-l-md border border-gray-300 bg-white text-xs font-medium text-gray-500 hover:bg-gray-50 transform hover:scale-105 transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none">
                    <span class="sr-only">Previous</span>
                    <i class="fas fa-chevron-left" />
                  </button>
                  <button v-for="page in visiblePages" :key="page" @click="goToPage(page)" :class="['relative inline-flex items-center px-3 py-1.5 border text-xs font-medium transform hover:scale-105 transition-all duration-300', currentPage === page ? 'bg-gradient-to-r from-blue-500 to-purple-600 border-blue-500 text-white shadow-md' : 'bg-white border-gray-300 text-gray-700 hover:bg-gray-50 hover:shadow-sm']">
                    {{ page }}
                  </button>
                  <button @click="nextPage" :disabled="currentPage === totalPages" class="relative inline-flex items-center px-2 py-1.5 rounded-r-md border border-gray-300 bg-white text-xs font-medium text-gray-500 hover:bg-gray-50 transform hover:scale-105 transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none">
                    <span class="sr-only">Next</span>
                    <i class="fas fa-chevron-right" />
                  </button>
                </nav>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
    <!-- Custom Confirmation Modal -->
    <CustomConfirmModal v-if="showConfirmModal" :show="showConfirmModal" :title="confirmModalTitle" :message="confirmModalMessage" @confirm="handleConfirm" @cancel="handleCancel" @close="showConfirmModal = false" />
    
    <!-- User Details Modal -->
    <UserDetailsModal :show="showUserDetailsModal" :user="selectedUser" :userLogs="userLogs" :currentPage="userLogsPage" :pageSize="userLogsPageSize" :totalItems="userLogsTotal" :totalPages="userLogsTotalPages" :searchParams="userLogsSearch" @close="closeUserDetailsModal" @userUpdated="handleUserUpdated" @search="searchUserLogs" @resetSearch="resetUserLogsSearch" @pageChange="changeUserLogsPage" @pageSizeChange="changeUserLogsPageSize" />
  </div>
</template>

<script>
import { getCurrentUser, logout as logoutUser } from '../utils/auth.js';
import { userAPI } from '../utils/api.js';
import { showSuccess, showError } from '../utils/notification.js';
import CommonSidebar from './CommonSidebar.vue';
import CommonHeader from './CommonHeader.vue';
import CustomConfirmModal from './CustomConfirmModal.vue';
import UserDetailsModal from './UserDetailsModal.vue';

export default {
  name: 'UserManagement',
  components: {
    CommonSidebar,
    CommonHeader,
    CustomConfirmModal,
    UserDetailsModal
  },
  data() {
      return {
        focusedField: '',
        currentUser: null,
        users: [],
        filteredUsers: [],
        searchTerm: '',
        currentPage: 1,
        pageSize: 10,
        totalPages: 1,
        notifications: [],
        isLoading: false,
        isSuperUser: false,
        // Sub-account creation form
        showCreateSubAccountModal: false,
        // Custom confirmation modal
        showConfirmModal: false,
        confirmModalTitle: '',
        confirmModalMessage: '',
        confirmModalCallback: null,
        // Edit user related
        showUserDetailsModal: false,
        selectedUser: null,
        userLogs: [],
        // Operation log pagination and search
        userLogsPage: 1,
        userLogsPageSize: 10,
        userLogsTotal: 0,
        userLogsTotalPages: 1,
        userLogsSearch: {
          operationType: '',
          description: ''
        },
        // User menu state management
        showUserMenu: null,
        subAccountForm: {
          username: '',
          email: '',
          role: 'editor' // Default role set to editor
        },
        subAccountFormErrors: [],
        createSubAccountLoading: false,
        showPassword: false,
        showConfirmPassword: false,
        // Edit user name modal
        showEditUserNameModal: false,
        editUserNameForm: {
          userId: null,
          newUserName: ''
        },
        editUserNameErrors: [],
        editUserNameLoading: false,

        // 编辑成员相关数据
        showEditMemberModal: false,
        editMemberActiveTab: 'basic', // 'basic' 或 'logs'
        editMemberForm: {
          id: null,
          username: '',
          phone: '',
          email: '',
          account_status: 'NORMAL',
          role: 1
        },
        editMemberFormErrors: [],
        editMemberLoading: false,

        // 成员日志相关数据
        memberLogs: [],
        memberLogFilter: {
          timeRange: '',
          operationType: '',
          keyword: ''
        },
        expandedLogs: [], // 展开的日志ID列表
        // Change password modal
        showChangePasswordModal: false,
        changePasswordMethod: 'original', // 'original' or 'email'
        changePasswordForm: {
          userId: null,
          currentPassword: '',
          newPassword: '',
          confirmNewPassword: '',
          emailCode: ''
        },
        changePasswordErrors: [],
        changePasswordLoading: false,
        showCurrentPassword: false,
        showNewPassword: false,
        showConfirmNewPassword: false,
        emailCodeLoading: false,
        emailCodeCountdown: 0
      };
  },
  computed: {
    // Total sub-accounts
    subAccountsCount() {
      if (!Array.isArray(this.users)) {
        return 0;
      }
      return this.users.filter(user => user.role !== 0).length;
    },
    // Today's active sub-accounts
    todayActiveSubAccountsCount() {
      if (!Array.isArray(this.users)) {
        return 0;
      }
      const today = new Date().toDateString();
      return this.users.filter(user => 
        user.role !== 0 && 
        user.lastLoginTime && 
        new Date(user.lastLoginTime).toDateString() === today
      ).length;
    },
    // Disabled sub-accounts
    stoppedSubAccountsCount() {
      if (!Array.isArray(this.users)) {
        return 0;
      }
      return this.users.filter(user => user.role !== 0 && user.accountStatus === 'STOPPED').length;
    },
    // Inviting sub-accounts
    invitingSubAccountsCount() {
      if (!Array.isArray(this.users)) {
        return 0;
      }
      return this.users.filter(user => user.role !== 0 && user.accountStatus === 'INVITING').length;
    },
    // Active percentage
    activePercentage() {
      if (this.subAccountsCount === 0) {
        return 0;
      }
      return Math.round((this.todayActiveSubAccountsCount / this.subAccountsCount) * 100);
    },
    superUsersCount() {
      if (!Array.isArray(this.users)) {
        return 0;
      }
      return this.users.filter(user => user.role === 0).length;
    },
    superUsersPercentage() {
      if (!Array.isArray(this.users) || this.users.length === 0) {
        return 0;
      }
      return Math.round((this.superUsersCount / this.users.length) * 100);
    },
    activeUsersCount() {
      if (!Array.isArray(this.users)) {
        return 0;
      }
      return this.users.filter(user => user.status === 1).length;
    },
    disabledUsersCount() {
      if (!Array.isArray(this.users)) {
        return 0;
      }
      return this.users.filter(user => user.status === 0).length;
    },
    canRemoveLastSuperUser() {
      return this.superUsersCount > 1;
    },
    visiblePages() {
      const total = this.totalPages;
      const current = this.currentPage;
      const maxVisible = 5;
      
      if (total <= maxVisible) {
        return Array.from({ length: total }, (_, i) => i + 1);
      }
      
      let start = Math.max(1, current - Math.floor(maxVisible / 2));
      let end = Math.min(total, start + maxVisible - 1);
      
      if (end - start + 1 < maxVisible) {
        start = Math.max(1, end - maxVisible + 1);
      }
      
      return Array.from({ length: end - start + 1 }, (_, i) => i + start);
    },

    // 过滤后的成员日志
    filteredMemberLogs() {
      if (!Array.isArray(this.memberLogs)) {
        return [];
      }

      let filtered = [...this.memberLogs];

      // 时间范围筛选
      if (this.memberLogFilter.timeRange) {
        const now = new Date();
        let startDate;

        switch (this.memberLogFilter.timeRange) {
          case 'today':
            startDate = new Date(now.getFullYear(), now.getMonth(), now.getDate());
            break;
          case 'week':
            startDate = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
            break;
          case 'month':
            startDate = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000);
            break;
        }

        if (startDate) {
          filtered = filtered.filter(log => new Date(log.created_at) >= startDate);
        }
      }

      // 操作类型筛选
      if (this.memberLogFilter.operationType) {
        filtered = filtered.filter(log => log.operation_type === this.memberLogFilter.operationType);
      }

      // 关键字搜索
      if (this.memberLogFilter.keyword) {
        const keyword = this.memberLogFilter.keyword.toLowerCase();
        filtered = filtered.filter(log => 
          (log.description && log.description.toLowerCase().includes(keyword)) ||
          (log.operator_name && log.operator_name.toLowerCase().includes(keyword))
        );
      }

      // 按时间倒序排列
      return filtered.sort((a, b) => new Date(b.created_at) - new Date(a.created_at));
    }
  },
  async mounted() {
    // Initialize user information
    this.currentUser = getCurrentUser();
    this.isSuperUser = this.currentUser && this.currentUser.role === 0;
    
    console.log('当前用户信息:', this.currentUser);
    console.log('是否为超级用户:', this.isSuperUser);
    
    // Check if logged in
    if (!this.currentUser) {
      showError('请先登录', '未登录');
      window.location.hash = '#/';
      return;
    }
    
    // Load user list
    await this.loadUsers();
    
    // Add global click event listener to close user menu when clicking outside
    document.addEventListener('click', this.handleClickOutside);
  },
  
  beforeUnmount() {
    // Remove global click event listener
    document.removeEventListener('click', this.handleClickOutside);
  },
  
  methods: {
      logout() {
        logoutUser();
      },
      
      // Check if it's a recent login (within 24 hours)
      isRecentLogin(loginTime) {
        if (!loginTime) return false;
        const now = new Date();
        const loginDate = new Date(loginTime);
        const diffHours = (now - loginDate) / (1000 * 60 * 60);
        return diffHours < 24;
      },
      
      // Calculate invite countdown (14 days from creation)
      getInviteCountdown(user) {
        if (!user.createTime) return 14; // 如果没有创建时间，默认14天
        const now = new Date();
        const createDate = new Date(user.createTime);
        const diffMs = now - createDate;
        const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));
        const remainingDays = 14 - diffDays;
        return Math.max(0, remainingDays);
      },
      
      // Check if invite is expired
      isInviteExpired(user) {
        return this.getInviteCountdown(user) <= 0;
      },
      
      // Show confirmation modal
      showConfirm(title, message, callback) {
        this.confirmModalTitle = title;
        this.confirmModalMessage = message;
        this.confirmModalCallback = callback;
        this.showConfirmModal = true;
      },
    
    // Handle modal confirmation
    handleConfirm() {
      if (this.confirmModalCallback) {
        this.confirmModalCallback();
      }
      this.showConfirmModal = false;
    },
    
    // Handle modal cancellation
    handleCancel() {
      this.showConfirmModal = false;
    },
    
    async loadUsers() {
      try {
        this.isLoading = true;
        console.log('开始加载用户列表...');
        
        const response = await userAPI.getUsers();
        console.log('用户API响应:', response);
        
        // 确保正确提取用户数组
        if (response && Array.isArray(response.users)) {
          this.users = response.users;
          console.log('成功加载用户列表:', this.users.length, '个用户');
        } else if (Array.isArray(response)) {
          // 如果直接返回数组
          this.users = response;
          console.log('成功加载用户列表（直接数组）:', this.users.length, '个用户');
        } else {
          console.warn('用户API返回格式异常:', response);
          this.users = [];
        }
        
        this.filterUsers();
        
        // 检查当前用户是否在列表中
        const currentUserId = this.currentUser?.id;
        const currentUserInList = this.users.find(user => user.id === currentUserId);
        console.log('当前用户ID:', currentUserId);
        console.log('当前用户是否在列表中:', currentUserInList ? '是' : '否');
        
        if (!currentUserInList && currentUserId) {
          console.warn('当前用户不在用户列表中，可能存在数据同步问题');
        }
        
      } catch (error) {
        console.error('加载用户列表失败:', error);
        
        // 如果是认证错误，不显示错误提示（已经跳转到登录页面）
        if (!error.message.includes('认证') && !error.message.includes('登录')) {
          showError('加载用户列表失败，请稍后重试', '加载失败');
        }
        
        // 即使出错也要确保users是数组
        this.users = [];
        this.filterUsers();
      } finally {
        this.isLoading = false;
      }
    },
    
    handleSearchInput(value) {
      this.searchTerm = value;
      this.filterUsers();
    },
    
    filterUsers() {
      const searchTerm = this.searchTerm.toLowerCase();
      // Add type check to ensure this.users is an array
      if (!Array.isArray(this.users)) {
        this.filteredUsers = [];
        this.totalPages = 1;
        this.currentPage = 1;
        return;
      }
      this.filteredUsers = this.users.filter(user => 
        (user.username && user.username.toLowerCase().includes(searchTerm)) || 
        (user.email && user.email.toLowerCase().includes(searchTerm))
      );
      this.totalPages = Math.ceil(this.filteredUsers.length / this.pageSize);
      this.currentPage = 1;
    },
    
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--;
      }
    },
    
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++;
      }
    },
    
    goToPage(page) {
      this.currentPage = page;
    },
    
    formatDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      });
    },
    
    getUserAvatar(email) {
      // 使用基于邮箱的一致性头像生成
      if (!email) return 'https://ui-avatars.com/api/?name=User&background=6366f1&color=fff&size=200';
      
      // 生成基于邮箱的哈希值，确保同一邮箱总是生成相同的头像
      const hash = this.md5(email.trim().toLowerCase());
      const name = email.split('@')[0];
      
      // 使用UI Avatars服务生成一致的头像
      const colors = ['6366f1', '8b5cf6', 'ec4899', 'f59e0b', '10b981', 'ef4444', '3b82f6', '06b6d4'];
      const colorIndex = parseInt(hash.substring(0, 2), 16) % colors.length;
      const backgroundColor = colors[colorIndex];
      
      return `https://ui-avatars.com/api/?name=${encodeURIComponent(name)}&background=${backgroundColor}&color=fff&size=200&font-size=0.6`;
    },
    
    md5(str) {
      // Simple md5 implementation, for demonstration only
      // In actual projects, use dedicated libraries
      let hash = 0;
      if (str.length === 0) return hash.toString();
      for (let i = 0; i < str.length; i++) {
        const char = str.charCodeAt(i);
        hash = ((hash << 5) - hash) + char;
        hash = hash & hash; // Convert to 32-bit integer
      }
      return Math.abs(hash).toString(16);
    },
    
    async changeUserRole(user) {
      const newRole = user.role === 0 ? 1 : 0;
      const actionText = newRole === 0 ? 'upgrade to supervisor' : 'downgrade to regular user';
      
      // Prevent deleting the last supervisor
        if (newRole === 1 && this.superUsersCount <= 1) {
          showError('Cannot delete the last supervisor!', 'Operation failed');
        return;
      }
      
      // Use custom confirmation modal
      this.showConfirm(
        'Confirm role change',
        `Are you sure you want to ${actionText} for user ${user.email}?`,
        async () => {
          try {
            await userAPI.updateUserRole(user.id, newRole);
            // Update local data directly to avoid reloading
            user.role = newRole;
            showSuccess(`用户 ${user.email} ${actionText === 'enabled' ? '启用' : '禁用'}成功`, '操作成功');
          } catch (error) {
            console.error('Failed to update user role:', error);
            showError('Failed to update user role, please try again later', 'Operation failed');
          }
        }
      );
    },
    
    async toggleUserStatus(user) {
      const newStatus = user.status === 1 ? 0 : 1;
      const actionText = newStatus === 1 ? 'enable' : 'disable';
      
      // Supervisors cannot be disabled
      if (user.role === 0) {
        showError('Supervisors cannot be disabled', 'Operation failed');
        return;
      }
      
      // Use custom confirmation modal
      this.showConfirm(
        'Confirm status change',
        `Are you sure you want to ${actionText} user ${user.email}?`,
        async () => {
          try {
            await userAPI.updateUserStatus(user.id, newStatus);
            // Update local data directly to avoid reloading
            user.status = newStatus;
            showSuccess(`用户 ${user.email} ${actionText === 'enable' ? '启用' : '禁用'}成功`, '操作成功');
          } catch (error) {
            console.error('Failed to update user status:', error);
            showError('Failed to update user status, please try again later', 'Operation failed');
          }
        }
      );
    },
    
    // Edit user (view details and operation logs)
    editUser(user) {
      this.selectedUser = user;
      // Reset operation log pagination and search conditions
      this.userLogsPage = 1;
      this.userLogsPageSize = 10;
      this.userLogsSearch = {
        operationType: '',
        description: ''
      };
      // Load user operation logs
      this.loadUserLogs(user.id);
      this.showUserDetailsModal = true;
    },
    
    // Close user details modal
    closeUserDetailsModal() {
      this.showUserDetailsModal = false;
      this.selectedUser = null;
      this.userLogs = [];
    },
    
    // Load user operation logs
    async loadUserLogs(userId) {
      try {
        // Check if current logged-in user is a supervisor
        const canAccess = this.isSuperUser || (userId === this.currentUser?.id);
        
        if (canAccess) {
          // Build query parameters
          const params = {
            page: this.userLogsPage,
            pageSize: this.userLogsPageSize,
            operationType: this.userLogsSearch.operationType,
            description: this.userLogsSearch.description,
            userId: userId // Add user ID parameter to filter specific user's logs
          };
          
          const response = await userAPI.getUserLogs(params);
          this.userLogs = Array.isArray(response?.logs) ? response.logs : [];
          this.userLogsTotal = response?.total || 0;
          this.userLogsTotalPages = response?.totalPages || 1;
        } else {
          // If no permission to view, show empty list
          this.userLogs = [];
          this.userLogsTotal = 0;
          this.userLogsTotalPages = 1;
        }
      } catch (error) {
        console.error('Failed to load user operation logs:', error);
        this.userLogs = [];
        this.userLogsTotal = 0;
        this.userLogsTotalPages = 1;
      }
    },
    
    // Search operation logs
    searchUserLogs() {
      this.userLogsPage = 1;
      if (this.selectedUser) {
        this.loadUserLogs(this.selectedUser.id);
      }
    },
    
    // Reset search conditions
    resetUserLogsSearch() {
      this.userLogsSearch = {
        operationType: '',
        description: ''
      };
      this.searchUserLogs();
    },
    
    // Switch operation log page number
    changeUserLogsPage(page) {
      this.userLogsPage = page;
      if (this.selectedUser) {
        this.loadUserLogs(this.selectedUser.id);
      }
    },
    
    // Change number of items displayed per page
    changeUserLogsPageSize(pageSize) {
      this.userLogsPageSize = Math.min(pageSize, 50); // Maximum 50 items
      this.userLogsPage = 1;
      if (this.selectedUser) {
        this.loadUserLogs(this.selectedUser.id);
      }
    },
    
    // Close create sub-account modal
    closeCreateSubAccountModal() {
      this.showCreateSubAccountModal = false;
      this.resetSubAccountForm();
    },
    
    // Reset sub-account form
    resetSubAccountForm() {
      this.subAccountForm = {
        username: '',
        email: '',
        role: 'editor'
      };
      this.subAccountFormErrors = [];
      this.createSubAccountLoading = false;
    },
    
    // Validate form
    validateForm() {
      this.subAccountFormErrors = [];
      
      if (!this.subAccountForm.username) {
        this.subAccountFormErrors.push('Username cannot be empty');
      }
      
      if (!this.subAccountForm.email) {
        this.subAccountFormErrors.push('Email cannot be empty');
      } else if (!this.isValidEmail(this.subAccountForm.email)) {
        this.subAccountFormErrors.push('Please enter a valid email address');
      }
      
      if (!this.subAccountForm.role) {
        this.subAccountFormErrors.push('Please select a role');
      }
      
      return this.subAccountFormErrors.length === 0;
    },
    
    // Handle create sub-account
    handleCreateSubAccount() {
      if (this.validateForm()) {
        this.createSubAccountLoading = true;
        
        // Role mapping: convert frontend string values to backend numeric values
        const roleMapping = {
          'admin': 2,    // Administrator
          'editor': 3,   // Editor
          'viewer': 4    // Viewer
        };
        
        // Prepare data for creating sub-account (no password needed, backend will generate temp password)
        const subAccountData = {
          username: this.subAccountForm.username,
          email: this.subAccountForm.email,
          role: roleMapping[this.subAccountForm.role] || 3 // Default to editor role
        };
        
        // Call API to create sub-account
        userAPI.createSubAccount(subAccountData).then(response => {
          showSuccess('子账号创建成功', '操作成功');
          this.closeCreateSubAccountModal();
          this.resetSubAccountForm();
          this.loadUsers(); // Reload user list
        }).catch(error => {
          console.error('Failed to create sub-account:', error);
          showError('创建子账号失败，请稍后重试', '创建失败');
        }).finally(() => {
          this.createSubAccountLoading = false;
        });
      }
    },
    
    // Show confirmation modal
    showConfirm(title, message, callback) {
      this.confirmModalTitle = title;
      this.confirmModalMessage = message;
      this.confirmModalCallback = callback;
      this.showConfirmModal = true;
    },
    
    // Handle confirmation operation
    handleConfirm() {
      this.showConfirmModal = false;
      if (this.confirmModalCallback && typeof this.confirmModalCallback === 'function') {
        this.confirmModalCallback();
      }
      this.confirmModalCallback = null;
    },
    
    // Handle cancel operation
    handleCancel() {
      this.showConfirmModal = false;
      this.confirmModalCallback = null;
    },
    
    // Email validation rules
    isValidEmail(email) {
      const re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
      return re.test(email);
    },
    
    // Handle user update events
    handleUserUpdated() {
      // Reload user list
      this.loadUsers();
    },
    
    // Toggle user menu display status
    toggleUserMenu(userId) {
      this.showUserMenu = this.showUserMenu === userId ? null : userId;
    },
    
    // Update user account status
    async updateUserAccountStatus(user, newStatus) {
      const statusText = {
        'NORMAL': 'enable',
        'STOPPED': 'disable',
        'INVITING': 'inviting',
        'INVITE_FAILED': 'invite failed'
      };
      
      const actionText = statusText[newStatus] || 'update';
      
      this.showConfirm(
        `${actionText} account`,
        `Are you sure you want to ${actionText} user "${user.username}"'s account?`,
        async () => {
          try {
            await userAPI.updateUserAccountStatus(user.id, newStatus);
            showSuccess(`账户${actionText === 'disable' ? '禁用' : '启用'}成功`, '操作成功');
            this.loadUsers(); // Reload user list
            this.showUserMenu = null; // Close menu
          } catch (error) {
            console.error(`${actionText} account failed:`, error);
            showError(`${actionText} account failed, please try again later`);
          }
        }
      );
    },
    
    // Resend invitation
    async resendInvite(user) {
      const isExpired = this.isInviteExpired(user);
      const title = isExpired ? '重新邀请' : '重新发送邀请';
      const message = isExpired 
        ? `用户 "${user.username}" 的邀请已过期，确定要重新邀请吗？`
        : `确定要重新发送邀请给用户 "${user.username}" 吗？`;
        
      this.showConfirm(
        title,
        message,
        async () => {
          try {
            await userAPI.resendInvite(user.id);
            showSuccess(isExpired ? '重新邀请发送成功' : '邀请重新发送成功');
            this.loadUsers(); // Reload user list
            this.showUserMenu = null; // Close menu
          } catch (error) {
            console.error('Failed to resend invitation:', error);
            showError('发送邀请失败，请稍后重试');
          }
        }
      );
    },
    
    // View user details
    viewUserDetails(user) {
      this.selectedUser = user;
      this.showUserDetailsModal = true;
      this.loadUserLogs(user.id);
      this.showUserMenu = null; // Close menu
    },
    
    // Reset user password
    async resetUserPassword(user) {
      this.showConfirm(
        'Reset password',
        `Are you sure you want to reset password for user "${user.username}"? A new password will be sent to the user's email after reset.`,
        async () => {
          try {
            await userAPI.resetUserPassword(user.id);
            showSuccess('密码重置成功，新密码已发送到用户邮箱', '操作成功');
            this.showUserMenu = null; // Close menu
          } catch (error) {
            console.error('Failed to reset password:', error);
            showError('Failed to reset password, please try again later');
          }
        }
      );
    },
    
    // Edit user name
    editUserName(user) {
      this.editUserNameForm.userId = user.id;
      this.editUserNameForm.newUserName = user.name || user.email.split('@')[0];
      this.editUserNameErrors = [];
      this.showEditUserNameModal = true;
      this.showUserMenu = null;
    },

    // 编辑成员信息
    async editMember(user) {
      this.editMemberForm.id = user.id;
      this.editMemberForm.username = user.username || user.name || '';
      this.editMemberForm.phone = user.phone || '';
      this.editMemberForm.email = user.email || '';
      this.editMemberForm.account_status = user.account_status || 'NORMAL';
      this.editMemberForm.role = user.role || 1;
      this.editMemberFormErrors = [];
      this.editMemberActiveTab = 'basic';
      this.showEditMemberModal = true;
      this.showUserMenu = null;
      
      // 加载成员操作日志
      await this.loadMemberLogs(user.id);
    },

    // 关闭编辑成员弹窗
    closeEditMemberModal() {
      this.showEditMemberModal = false;
      this.editMemberForm = {
        id: null,
        username: '',
        phone: '',
        email: '',
        account_status: 'NORMAL',
        role: 1
      };
      this.editMemberFormErrors = [];
      this.memberLogs = [];
      this.expandedLogs = [];
    },

    // 保存编辑成员信息
    async saveEditMember() {
      this.editMemberFormErrors = [];
      
      // 表单验证
      if (!this.editMemberForm.username.trim()) {
        this.editMemberFormErrors.push('姓名不能为空');
      }
      
      if (this.editMemberForm.username.length < 2) {
        this.editMemberFormErrors.push('姓名至少需要2个字符');
      }
      
      if (this.editMemberForm.email && !this.isValidEmail(this.editMemberForm.email)) {
        this.editMemberFormErrors.push('请输入有效的邮箱地址');
      }
      
      if (this.editMemberForm.phone && !this.isValidPhone(this.editMemberForm.phone)) {
        this.editMemberFormErrors.push('请输入有效的手机号码');
      }
      
      if (this.editMemberFormErrors.length > 0) {
        return;
      }
      
      this.editMemberLoading = true;
      
      try {
        // 获取原始用户数据用于对比变更
        const originalUser = this.users.find(u => u.id === this.editMemberForm.id);
        
        const updateData = {
          username: this.editMemberForm.username.trim(),
          phone: this.editMemberForm.phone.trim(),
          email: this.editMemberForm.email.trim(),
          account_status: this.editMemberForm.account_status,
          role: this.editMemberForm.role
        };
        
        await userAPI.updateUser(this.editMemberForm.id, updateData);
        
        // 记录操作日志
        await this.recordMemberLog(originalUser, updateData);
        
        showSuccess('成员信息修改成功');
        this.closeEditMemberModal();
        this.loadUsers(); // 重新加载用户列表
      } catch (error) {
        console.error('Failed to update member:', error);
        if (error.response?.data?.message) {
          this.editMemberFormErrors.push(error.response.data.message);
        } else {
          this.editMemberFormErrors.push('修改成员信息失败，请稍后重试');
        }
      } finally {
        this.editMemberLoading = false;
      }
    },

    // 加载成员操作日志
    async loadMemberLogs(userId) {
      try {
        const response = await userAPI.getUserLogs(userId);
        this.memberLogs = response.logs || [];
      } catch (error) {
        console.error('Failed to load member logs:', error);
        this.memberLogs = [];
      }
    },

    // 记录成员操作日志
    async recordMemberLog(originalUser, newData) {
      try {
        const changes = [];
        
        // 对比字段变更
        const fieldMap = {
          username: '姓名',
          phone: '电话',
          email: '邮箱',
          account_status: '状态',
          role: '角色'
        };
        
        Object.keys(fieldMap).forEach(field => {
          const oldValue = originalUser[field];
          const newValue = newData[field];
          
          if (oldValue !== newValue) {
            changes.push({
              field: field,
              field_name: fieldMap[field],
              old_value: this.formatFieldValue(field, oldValue),
              new_value: this.formatFieldValue(field, newValue)
            });
          }
        });
        
        if (changes.length > 0) {
          const logData = {
            user_id: originalUser.id,
            operation_type: 'UPDATE',
            description: `修改了${changes.map(c => c.field_name).join('、')}`,
            changes: changes,
            ip_address: await this.getCurrentIP()
          };
          
          await userAPI.createUserLog(logData);
        }
      } catch (error) {
        console.error('Failed to record member log:', error);
      }
    },

    // 格式化字段值用于日志显示
    formatFieldValue(field, value) {
      if (field === 'role') {
        return value === 0 ? '管理员' : '成员';
      }
      if (field === 'account_status') {
        const statusMap = {
          'NORMAL': '正常',
          'STOPPED': '停用',
          'INVITING': '邀请中',
          'INVITE_FAILED': '邀请失败'
        };
        return statusMap[value] || value;
      }
      return value || '未设置';
    },

    // 获取当前IP地址
    async getCurrentIP() {
      try {
        const response = await fetch('https://api.ipify.org?format=json');
        const data = await response.json();
        return data.ip;
      } catch (error) {
        return '未知';
      }
    },

    // 切换日志详情展开状态
    toggleLogDetail(logId) {
      const index = this.expandedLogs.indexOf(logId);
      if (index > -1) {
        this.expandedLogs.splice(index, 1);
      } else {
        this.expandedLogs.push(logId);
      }
    },

    // 格式化日志时间
    formatLogTime(timestamp) {
      if (!timestamp) return '未知时间';
      const date = new Date(timestamp);
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      });
    },

    // 获取日志类型样式类
    getLogTypeClass(type) {
      const classMap = {
        'UPDATE': 'bg-blue-100 text-blue-800',
        'STATUS_CHANGE': 'bg-yellow-100 text-yellow-800',
        'ROLE_CHANGE': 'bg-purple-100 text-purple-800',
        'CREATE': 'bg-green-100 text-green-800',
        'DELETE': 'bg-red-100 text-red-800'
      };
      return classMap[type] || 'bg-gray-100 text-gray-800';
    },

    // 获取日志类型文本
    getLogTypeText(type) {
      const textMap = {
        'UPDATE': '信息修改',
        'STATUS_CHANGE': '状态变更',
        'ROLE_CHANGE': '角色变更',
        'CREATE': '创建',
        'DELETE': '删除'
      };
      return textMap[type] || '其他操作';
    },

    // 邮箱格式验证
    isValidEmail(email) {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      return emailRegex.test(email);
    },

    // 手机号格式验证
    isValidPhone(phone) {
      const phoneRegex = /^1[3-9]\d{9}$/;
      return phoneRegex.test(phone);
    },
    
    // Handle edit user name
    async handleEditUserName() {
      this.editUserNameErrors = [];
      
      // Validation
      if (!this.editUserNameForm.newUserName.trim()) {
        this.editUserNameErrors.push('用户名不能为空');
        return;
      }
      
      if (this.editUserNameForm.newUserName.length < 2) {
        this.editUserNameErrors.push('用户名至少需要2个字符');
        return;
      }
      
      this.editUserNameLoading = true;
      
      try {
        await userAPI.updateUserName(this.editUserNameForm.userId, {
          name: this.editUserNameForm.newUserName.trim()
        });
        
        showSuccess('用户名修改成功');
        this.showEditUserNameModal = false;
        this.loadUsers(); // Reload user list
      } catch (error) {
        console.error('Failed to update user name:', error);
        if (error.response?.data?.message) {
          this.editUserNameErrors.push(error.response.data.message);
        } else {
          this.editUserNameErrors.push('修改用户名失败，请稍后重试');
        }
      } finally {
        this.editUserNameLoading = false;
      }
    },
    
    // Change password
    changePassword(user) {
      this.changePasswordForm.userId = user.id;
      this.changePasswordForm.currentPassword = '';
      this.changePasswordForm.newPassword = '';
      this.changePasswordForm.confirmNewPassword = '';
      this.changePasswordForm.emailCode = '';
      this.changePasswordMethod = 'original';
      this.changePasswordErrors = [];
      this.showChangePasswordModal = true;
      this.showUserMenu = null;
    },
    
    // Send email verification code
    async sendEmailCode() {
      const user = this.users.find(u => u.id === this.changePasswordForm.userId);
      if (!user) {
        this.changePasswordErrors.push('用户信息不存在');
        return;
      }
      
      this.emailCodeLoading = true;
      
      try {
        await userAPI.sendPasswordResetCode(user.email);
        showSuccess('验证码已发送到您的邮箱');
        
        // Start countdown
        this.emailCodeCountdown = 60;
        const countdown = setInterval(() => {
          this.emailCodeCountdown--;
          if (this.emailCodeCountdown <= 0) {
            clearInterval(countdown);
          }
        }, 1000);
        
      } catch (error) {
        console.error('Failed to send email code:', error);
        if (error.response?.data?.message) {
          this.changePasswordErrors.push(error.response.data.message);
        } else {
          this.changePasswordErrors.push('发送验证码失败，请稍后重试');
        }
      } finally {
        this.emailCodeLoading = false;
      }
    },
    
    // Handle change password
    async handleChangePassword() {
      this.changePasswordErrors = [];
      
      // Validation
      if (this.changePasswordMethod === 'original' && !this.changePasswordForm.currentPassword) {
        this.changePasswordErrors.push('请输入当前密码');
        return;
      }
      
      if (this.changePasswordMethod === 'email' && !this.changePasswordForm.emailCode) {
        this.changePasswordErrors.push('请输入邮箱验证码');
        return;
      }
      
      if (!this.changePasswordForm.newPassword) {
        this.changePasswordErrors.push('请输入新密码');
        return;
      }
      
      if (this.changePasswordForm.newPassword.length < 6) {
        this.changePasswordErrors.push('新密码至少需要6个字符');
        return;
      }
      
      if (this.changePasswordForm.newPassword !== this.changePasswordForm.confirmNewPassword) {
        this.changePasswordErrors.push('两次输入的新密码不一致');
        return;
      }
      
      this.changePasswordLoading = true;
      
      try {
        const requestData = {
          newPassword: this.changePasswordForm.newPassword
        };
        
        if (this.changePasswordMethod === 'original') {
          requestData.currentPassword = this.changePasswordForm.currentPassword;
          await userAPI.changePasswordWithCurrent(this.changePasswordForm.userId, requestData);
        } else {
          requestData.emailCode = this.changePasswordForm.emailCode;
          await userAPI.changePasswordWithEmail(this.changePasswordForm.userId, requestData);
        }
        
        showSuccess('密码修改成功');
        this.showChangePasswordModal = false;
        
        // Clear form
        this.changePasswordForm.currentPassword = '';
        this.changePasswordForm.newPassword = '';
        this.changePasswordForm.confirmNewPassword = '';
        this.changePasswordForm.emailCode = '';
        
      } catch (error) {
        console.error('Failed to change password:', error);
        if (error.response?.data?.message) {
          this.changePasswordErrors.push(error.response.data.message);
        } else {
          this.changePasswordErrors.push('修改密码失败，请稍后重试');
        }
      } finally {
        this.changePasswordLoading = false;
      }
    },
    
    // Disable user
    async disableUser(user) {
      this.showConfirm(
        '停用账户',
        `确定要停用用户 "${user.name || user.email}" 的账户吗？停用后该用户将无法登录系统。`,
        async () => {
          try {
            await userAPI.updateUserStatus(user.id, 'STOPPED');
            showSuccess('账户已停用');
            this.loadUsers(); // Reload user list
            this.showUserMenu = null; // Close menu
          } catch (error) {
            console.error('Failed to disable user:', error);
            showError('停用账户失败，请稍后重试');
          }
        }
      );
    },
    
    // Enable user
    async enableUser(user) {
      this.showConfirm(
        '启用账户',
        `确定要启用用户 "${user.name || user.email}" 的账户吗？`,
        async () => {
          try {
            await userAPI.updateUserStatus(user.id, 'NORMAL');
            showSuccess('账户已启用');
            this.loadUsers(); // Reload user list
            this.showUserMenu = null; // Close menu
          } catch (error) {
            console.error('Failed to enable user:', error);
            showError('启用账户失败，请稍后重试');
          }
        }
      );
    },
    
    // Remove user
    async removeUser(user) {
      this.showConfirm(
        '删除用户',
        `确定要删除用户 "${user.name || user.email}" 吗？此操作不可撤销，用户的所有数据将被永久删除。`,
        async () => {
          try {
            await userAPI.removeUser(user.id);
            showSuccess('用户已删除');
            this.loadUsers(); // Reload user list
            this.showUserMenu = null; // Close menu
          } catch (error) {
            console.error('Failed to remove user:', error);
            showError('删除用户失败，请稍后重试');
          }
        }
      );
    },
    
    // Handle clicking outside to close menu
    handleClickOutside(event) {
      // Check if the clicked element is inside the user menu
      const userMenus = document.querySelectorAll('.user-menu-container');
      let clickedInside = false;
      
      userMenus.forEach(menu => {
        if (menu.contains(event.target)) {
          clickedInside = true;
        }
      });
      
      // If clicked outside the menu, close all menus
      if (!clickedInside) {
        this.showUserMenu = null;
      }
    }
  }
}
</script>

<style scoped>
/* User management page specific styles */
button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* Transition animations */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

/* Responsive adjustments */
@media (max-width: 640px) {
  .grid-cols-1 {
    grid-template-columns: repeat(1, minmax(0, 1fr));
  }
}

/* 移动端表格响应式优化 */
@media (max-width: 768px) {
  /* 表格在小屏幕上的优化 */
  table {
    font-size: 0.75rem;
  }
  
  /* 隐藏部分列以节省空间 */
  th:nth-child(3), td:nth-child(3), /* 电话列 */
  th:nth-child(6), td:nth-child(6) { /* 角色列 */
    display: none;
  }
  
  /* 操作列始终显示 */
  th:nth-child(7), td:nth-child(7) {
    display: table-cell !important;
    min-width: 80px;
  }
  
  /* 操作按钮在小屏幕上的优化 */
  .user-menu-container .absolute {
    right: -20px;
    left: auto;
  }
  
  /* 统计卡片在小屏幕上单列显示 */
  .grid-cols-1.md\\:grid-cols-2.lg\\:grid-cols-4 {
    grid-template-columns: 1fr;
  }
}

/* 超小屏幕优化 */
@media (max-width: 480px) {
  /* 进一步隐藏列 */
  th:nth-child(4), td:nth-child(4) { /* 邮箱列 */
    display: none;
  }
  
  /* 姓名列和状态列保持显示 */
  th:nth-child(2), td:nth-child(2), /* 姓名 */
  th:nth-child(5), td:nth-child(5), /* 状态 */
  th:nth-child(7), td:nth-child(7) { /* 操作 */
    display: table-cell !important;
  }
  
  /* 操作按钮紧凑显示 */
  .space-x-1 > * + * {
    margin-left: 0.125rem;
  }
  
  /* 弹窗在小屏幕上的优化 */
  .max-w-md {
    max-width: 95vw;
    margin: 0 auto;
  }
}
</style>
