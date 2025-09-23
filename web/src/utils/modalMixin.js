/**
 * 弹窗管理混入
 * 提供统一的弹窗状态管理、响应式适配和交互逻辑
 */

export const modalMixin = {
  data() {
    return {
      // 弹窗状态管理
      modalStates: {},
      
      // 响应式断点
      breakpoints: {
        sm: 640,
        md: 768,
        lg: 1024,
        xl: 1280,
        '2xl': 1536
      },
      
      // 当前屏幕尺寸
      screenSize: 'lg'
    }
  },
  
  computed: {
    // 是否为移动设备
    isMobile() {
      return this.screenSize === 'sm' || this.screenSize === 'xs'
    },
    
    // 是否为平板设备
    isTablet() {
      return this.screenSize === 'md'
    },
    
    // 是否为桌面设备
    isDesktop() {
      return ['lg', 'xl', '2xl'].includes(this.screenSize)
    },
    
    // 响应式弹窗尺寸
    responsiveModalSize() {
      if (this.isMobile) return 'full'
      if (this.isTablet) return 'lg'
      return 'xl'
    },
    
    // 响应式内容类名
    responsiveContentClass() {
      if (this.isMobile) return 'p-4'
      if (this.isTablet) return 'p-5'
      return 'p-6'
    }
  },
  
  mounted() {
    this.initResponsiveDetection()
  },
  
  beforeUnmount() {
    this.cleanupResponsiveDetection()
  },
  
  methods: {
    /**
     * 初始化响应式检测
     */
    initResponsiveDetection() {
      this.updateScreenSize()
      window.addEventListener('resize', this.handleResize)
    },
    
    /**
     * 清理响应式检测
     */
    cleanupResponsiveDetection() {
      window.removeEventListener('resize', this.handleResize)
    },
    
    /**
     * 处理窗口大小变化
     */
    handleResize() {
      this.updateScreenSize()
    },
    
    /**
     * 更新屏幕尺寸
     */
    updateScreenSize() {
      const width = window.innerWidth
      
      if (width < this.breakpoints.sm) {
        this.screenSize = 'xs'
      } else if (width < this.breakpoints.md) {
        this.screenSize = 'sm'
      } else if (width < this.breakpoints.lg) {
        this.screenSize = 'md'
      } else if (width < this.breakpoints.xl) {
        this.screenSize = 'lg'
      } else if (width < this.breakpoints['2xl']) {
        this.screenSize = 'xl'
      } else {
        this.screenSize = '2xl'
      }
    },
    
    /**
     * 显示弹窗
     * @param {string} modalName - 弹窗名称
     * @param {Object} options - 弹窗选项
     */
    showModal(modalName, options = {}) {
      this.$set(this.modalStates, modalName, {
        show: true,
        loading: false,
        data: options.data || null,
        ...options
      })
      
      // 触发显示事件
      this.$emit('modal-show', modalName, options)
    },
    
    /**
     * 隐藏弹窗
     * @param {string} modalName - 弹窗名称
     */
    hideModal(modalName) {
      if (this.modalStates[modalName]) {
        this.$set(this.modalStates, modalName, {
          ...this.modalStates[modalName],
          show: false
        })
      }
      
      // 触发隐藏事件
      this.$emit('modal-hide', modalName)
    },
    
    /**
     * 切换弹窗状态
     * @param {string} modalName - 弹窗名称
     * @param {Object} options - 弹窗选项
     */
    toggleModal(modalName, options = {}) {
      const currentState = this.modalStates[modalName]
      if (currentState && currentState.show) {
        this.hideModal(modalName)
      } else {
        this.showModal(modalName, options)
      }
    },
    
    /**
     * 设置弹窗加载状态
     * @param {string} modalName - 弹窗名称
     * @param {boolean} loading - 加载状态
     */
    setModalLoading(modalName, loading) {
      if (this.modalStates[modalName]) {
        this.$set(this.modalStates, modalName, {
          ...this.modalStates[modalName],
          loading
        })
      }
    },
    
    /**
     * 获取弹窗状态
     * @param {string} modalName - 弹窗名称
     * @returns {Object} 弹窗状态
     */
    getModalState(modalName) {
      return this.modalStates[modalName] || { show: false, loading: false, data: null }
    },
    
    /**
     * 是否显示弹窗
     * @param {string} modalName - 弹窗名称
     * @returns {boolean} 是否显示
     */
    isModalVisible(modalName) {
      const state = this.getModalState(modalName)
      return state.show
    },
    
    /**
     * 弹窗是否加载中
     * @param {string} modalName - 弹窗名称
     * @returns {boolean} 是否加载中
     */
    isModalLoading(modalName) {
      const state = this.getModalState(modalName)
      return state.loading
    },
    
    /**
     * 获取弹窗数据
     * @param {string} modalName - 弹窗名称
     * @returns {*} 弹窗数据
     */
    getModalData(modalName) {
      const state = this.getModalState(modalName)
      return state.data
    },
    
    /**
     * 设置弹窗数据
     * @param {string} modalName - 弹窗名称
     * @param {*} data - 弹窗数据
     */
    setModalData(modalName, data) {
      if (this.modalStates[modalName]) {
        this.$set(this.modalStates, modalName, {
          ...this.modalStates[modalName],
          data
        })
      }
    },
    
    /**
     * 关闭所有弹窗
     */
    closeAllModals() {
      Object.keys(this.modalStates).forEach(modalName => {
        this.hideModal(modalName)
      })
    },
    
    /**
     * 获取响应式弹窗配置
     * @param {Object} baseConfig - 基础配置
     * @returns {Object} 响应式配置
     */
    getResponsiveModalConfig(baseConfig = {}) {
      return {
        size: this.responsiveModalSize,
        contentClass: this.responsiveContentClass,
        closeOnBackdrop: !this.isMobile, // 移动端不允许点击背景关闭
        ...baseConfig,
        // 移动端特殊配置
        ...(this.isMobile && {
          customClass: 'mx-2 my-4',
          customStyle: {
            maxHeight: 'calc(100vh - 2rem)'
          }
        })
      }
    },
    
    /**
     * 处理弹窗确认
     * @param {string} modalName - 弹窗名称
     * @param {*} data - 确认数据
     */
    handleModalConfirm(modalName, data) {
      this.$emit('modal-confirm', modalName, data)
    },
    
    /**
     * 处理弹窗取消
     * @param {string} modalName - 弹窗名称
     */
    handleModalCancel(modalName) {
      this.$emit('modal-cancel', modalName)
      this.hideModal(modalName)
    },
    
    /**
     * 处理弹窗关闭
     * @param {string} modalName - 弹窗名称
     */
    handleModalClose(modalName) {
      this.$emit('modal-close', modalName)
      this.hideModal(modalName)
    }
  }
}

/**
 * 弹窗管理器类
 * 提供全局弹窗状态管理
 */
export class ModalManager {
  constructor() {
    this.modals = new Map()
    this.zIndexBase = 1000
  }
  
  /**
   * 注册弹窗
   * @param {string} name - 弹窗名称
   * @param {Object} config - 弹窗配置
   */
  register(name, config = {}) {
    this.modals.set(name, {
      zIndex: this.zIndexBase + this.modals.size,
      ...config
    })
  }
  
  /**
   * 获取弹窗配置
   * @param {string} name - 弹窗名称
   * @returns {Object} 弹窗配置
   */
  get(name) {
    return this.modals.get(name) || {}
  }
  
  /**
   * 获取弹窗层级
   * @param {string} name - 弹窗名称
   * @returns {number} 层级值
   */
  getZIndex(name) {
    const config = this.get(name)
    return config.zIndex || this.zIndexBase
  }
  
  /**
   * 更新弹窗层级
   * @param {string} name - 弹窗名称
   */
  bringToFront(name) {
    const config = this.get(name)
    if (config) {
      config.zIndex = this.zIndexBase + this.modals.size + 1
    }
  }
}

// 全局弹窗管理器实例
export const globalModalManager = new ModalManager()

// 常用弹窗配置预设
export const modalPresets = {
  // 确认对话框
  confirm: {
    size: 'sm',
    showHeader: true,
    showFooter: true,
    showCancelButton: true,
    showConfirmButton: true,
    icon: 'fas fa-question-circle',
    iconType: 'warning'
  },
  
  // 信息对话框
  info: {
    size: 'md',
    showHeader: true,
    showFooter: true,
    showConfirmButton: true,
    icon: 'fas fa-info-circle',
    iconType: 'info',
    confirmText: '知道了'
  },
  
  // 成功对话框
  success: {
    size: 'md',
    showHeader: true,
    showFooter: true,
    showConfirmButton: true,
    icon: 'fas fa-check-circle',
    iconType: 'success',
    confirmText: '好的'
  },
  
  // 错误对话框
  error: {
    size: 'md',
    showHeader: true,
    showFooter: true,
    showConfirmButton: true,
    icon: 'fas fa-exclamation-circle',
    iconType: 'error',
    confirmText: '知道了'
  },
  
  // 表单对话框
  form: {
    size: 'lg',
    showHeader: true,
    showFooter: true,
    showCancelButton: true,
    showConfirmButton: true,
    confirmText: '保存'
  },
  
  // 详情对话框
  detail: {
    size: 'xl',
    showHeader: true,
    showFooter: true,
    showConfirmButton: true,
    confirmText: '关闭'
  },
  
  // 全屏对话框
  fullscreen: {
    size: 'full',
    showHeader: true,
    customClass: 'h-screen rounded-none'
  }
}