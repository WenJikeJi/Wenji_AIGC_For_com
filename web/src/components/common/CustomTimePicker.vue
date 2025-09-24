<template>
  <div class="relative">
    <!-- 时间输入框 -->
    <div class="relative group">
      <input 
        type="text"
        :value="displayValue"
        @click="toggleTimePicker"
        @focus="showTimePicker = true"
        readonly
        :placeholder="placeholder"
        class="w-full px-5 py-4 text-sm rounded-2xl bg-white border-2 border-purple-200 focus:outline-none focus:border-purple-400 focus:ring-4 focus:ring-purple-100 transition-all duration-300 shadow-sm hover:shadow-lg group-hover:border-purple-300 cursor-pointer"
      />
      <div class="absolute right-4 top-1/2 transform -translate-y-1/2 pointer-events-none">
        <div class="w-6 h-6 bg-gradient-to-br from-purple-400 to-indigo-500 rounded-lg flex items-center justify-center shadow-sm">
          <i class="fas fa-clock text-white text-xs"></i>
        </div>
      </div>
    </div>

    <!-- 时间选择器弹出层 -->
    <transition
      enter-active-class="transition-all duration-300 ease-out"
      enter-from-class="opacity-0 scale-95 translate-y-2"
      enter-to-class="opacity-100 scale-100 translate-y-0"
      leave-active-class="transition-all duration-200 ease-in"
      leave-from-class="opacity-100 scale-100 translate-y-0"
      leave-to-class="opacity-0 scale-95 translate-y-2"
    >
      <div
        v-if="showTimePicker"
        class="absolute bottom-full left-0 mb-2 w-80 bg-white rounded-2xl shadow-2xl border border-purple-100 z-50 overflow-hidden"
      >
        <!-- 头部 -->
        <div class="bg-gradient-to-r from-purple-500 to-indigo-600 text-white p-4">
          <div class="flex items-center justify-between">
            <div class="flex items-center">
              <i class="fas fa-clock mr-2"></i>
              <span class="font-semibold">选择时间</span>
            </div>
            <button
              @click="showTimePicker = false"
              class="w-6 h-6 rounded-full bg-white/20 hover:bg-white/30 flex items-center justify-center transition-colors duration-200"
            >
              <i class="fas fa-times text-xs"></i>
            </button>
          </div>
        </div>

        <!-- 时间选择区域 -->
        <div class="p-6">
          <div class="flex justify-center space-x-4">
            <!-- 小时选择 -->
            <div class="flex flex-col items-center">
              <label class="text-sm font-medium text-gray-600 mb-3">小时</label>
              <div class="h-40 overflow-y-auto scrollbar-thin scrollbar-thumb-purple-300 scrollbar-track-gray-100">
                <div class="space-y-1">
                  <button
                    v-for="hour in hours"
                    :key="hour"
                    @click="selectHour(hour)"
                    :disabled="isHourDisabled(hour)"
                    :class="[
                      'w-12 h-10 rounded-xl text-sm font-medium transition-all duration-200 flex items-center justify-center',
                      {
                        'bg-gradient-to-br from-purple-500 to-indigo-600 text-white shadow-lg transform scale-105': tempHour === hour,
                        'text-gray-700 hover:bg-purple-50 hover:text-purple-600': tempHour !== hour && !isHourDisabled(hour),
                        'text-gray-300 cursor-not-allowed bg-gray-50': isHourDisabled(hour),
                        'hover:scale-105 hover:shadow-md': !isHourDisabled(hour)
                      }
                    ]"
                  >
                    {{ hour.toString().padStart(2, '0') }}
                  </button>
                </div>
              </div>
            </div>

            <!-- 分钟选择 -->
            <div class="flex flex-col items-center">
              <label class="text-sm font-medium text-gray-600 mb-3">分钟</label>
              <div class="h-40 overflow-y-auto scrollbar-thin scrollbar-thumb-purple-300 scrollbar-track-gray-100">
                <div class="space-y-1">
                  <button
                    v-for="minute in minutes"
                    :key="minute"
                    @click="selectMinute(minute)"
                    :disabled="isMinuteDisabled(minute)"
                    :class="[
                      'w-12 h-10 rounded-xl text-sm font-medium transition-all duration-200 flex items-center justify-center',
                      {
                        'bg-gradient-to-br from-purple-500 to-indigo-600 text-white shadow-lg transform scale-105': tempMinute === minute,
                        'text-gray-700 hover:bg-purple-50 hover:text-purple-600': tempMinute !== minute && !isMinuteDisabled(minute),
                        'text-gray-300 cursor-not-allowed bg-gray-50': isMinuteDisabled(minute),
                        'hover:scale-105 hover:shadow-md': !isMinuteDisabled(minute)
                      }
                    ]"
                  >
                    {{ minute.toString().padStart(2, '0') }}
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- 当前选择预览 -->
          <div class="mt-6 p-4 bg-purple-50 rounded-xl border border-purple-100">
            <div class="text-center">
              <div class="text-sm text-gray-600 mb-2">当前选择</div>
              <div class="text-2xl font-bold text-purple-700">
                {{ tempHour !== null && tempMinute !== null ? 
                   `${tempHour.toString().padStart(2, '0')}:${tempMinute.toString().padStart(2, '0')}` : 
                   '--:--' }}
              </div>
            </div>
          </div>
        </div>

        <!-- 底部操作按钮 -->
        <div class="p-4 bg-gray-50 flex justify-between items-center">
          <button
            @click="selectCurrentTime"
            class="px-4 py-2 text-sm text-purple-600 hover:text-purple-700 hover:bg-purple-50 rounded-lg transition-colors duration-200"
          >
            当前时间
          </button>
          <div class="flex space-x-2">
            <button
              @click="cancelSelection"
              class="px-4 py-2 text-sm text-gray-600 hover:text-gray-700 hover:bg-gray-100 rounded-lg transition-colors duration-200"
            >
              取消
            </button>
            <button
              @click="confirmSelection"
              :disabled="tempHour === null || tempMinute === null"
              class="px-4 py-2 text-sm bg-purple-500 text-white hover:bg-purple-600 disabled:bg-gray-300 disabled:cursor-not-allowed rounded-lg transition-colors duration-200"
            >
              确认
            </button>
          </div>
        </div>
      </div>
    </transition>

    <!-- 点击外部关闭时间选择器 -->
    <div 
      v-if="showTimePicker" 
      @click="showTimePicker = false"
      class="fixed inset-0 z-40"
    ></div>
  </div>
</template>

<script>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'

export default {
  name: 'CustomTimePicker',
  props: {
    modelValue: {
      type: String,
      default: ''
    },
    placeholder: {
      type: String,
      default: '请选择时间'
    },
    selectedDate: {
      type: String,
      default: ''
    },
    disablePastTime: {
      type: Boolean,
      default: true
    }
  },
  emits: ['update:modelValue'],
  setup(props, { emit }) {
    const showTimePicker = ref(false)
    const tempHour = ref(null)
    const tempMinute = ref(null)
    const selectedTime = ref(props.modelValue)

    // 生成小时和分钟选项
    const hours = computed(() => {
      return Array.from({ length: 24 }, (_, i) => i)
    })

    const minutes = computed(() => {
      return Array.from({ length: 60 }, (_, i) => i)
    })

    // 显示值
    const displayValue = computed(() => {
      return selectedTime.value || ''
    })

    // 检查是否是今天
    const isToday = computed(() => {
      if (!props.selectedDate) return false
      const today = new Date()
      const selectedDate = new Date(props.selectedDate)
      return selectedDate.toDateString() === today.toDateString()
    })

    // 获取当前时间
    const getCurrentTime = () => {
      const now = new Date()
      return {
        hour: now.getHours(),
        minute: now.getMinutes()
      }
    }

    // 检查小时是否被禁用
    const isHourDisabled = (hour) => {
      if (!props.disablePastTime || !isToday.value) return false
      
      const currentTime = getCurrentTime()
      return hour < currentTime.hour
    }

    // 检查分钟是否被禁用
    const isMinuteDisabled = (minute) => {
      if (!props.disablePastTime || !isToday.value) return false
      if (tempHour.value === null) return false
      
      const currentTime = getCurrentTime()
      if (tempHour.value > currentTime.hour) return false
      if (tempHour.value < currentTime.hour) return true
      
      return minute < currentTime.minute
    }

    const toggleTimePicker = () => {
      showTimePicker.value = !showTimePicker.value
      if (showTimePicker.value && selectedTime.value) {
        const [hour, minute] = selectedTime.value.split(':').map(Number)
        tempHour.value = hour
        tempMinute.value = minute
      }
    }

    const selectHour = (hour) => {
      if (isHourDisabled(hour)) return
      tempHour.value = hour
      
      // 如果当前选择的分钟被禁用，清除分钟选择
      if (tempMinute.value !== null && isMinuteDisabled(tempMinute.value)) {
        tempMinute.value = null
      }
    }

    const selectMinute = (minute) => {
      if (isMinuteDisabled(minute)) return
      tempMinute.value = minute
    }

    const selectCurrentTime = () => {
      const currentTime = getCurrentTime()
      tempHour.value = currentTime.hour
      tempMinute.value = currentTime.minute
    }

    const confirmSelection = () => {
      if (tempHour.value !== null && tempMinute.value !== null) {
        const timeString = `${tempHour.value.toString().padStart(2, '0')}:${tempMinute.value.toString().padStart(2, '0')}`
        selectedTime.value = timeString
        emit('update:modelValue', timeString)
        showTimePicker.value = false
        tempHour.value = null
        tempMinute.value = null
      }
    }

    const cancelSelection = () => {
      tempHour.value = null
      tempMinute.value = null
      showTimePicker.value = false
    }

    // 监听外部值变化
    watch(() => props.modelValue, (newValue) => {
      selectedTime.value = newValue
    })

    // 键盘事件处理
    const handleKeydown = (event) => {
      if (event.key === 'Escape') {
        showTimePicker.value = false
      }
    }

    onMounted(() => {
      document.addEventListener('keydown', handleKeydown)
    })

    onUnmounted(() => {
      document.removeEventListener('keydown', handleKeydown)
    })

    return {
      showTimePicker,
      tempHour,
      tempMinute,
      hours,
      minutes,
      displayValue,
      isHourDisabled,
      isMinuteDisabled,
      toggleTimePicker,
      selectHour,
      selectMinute,
      selectCurrentTime,
      confirmSelection,
      cancelSelection
    }
  }
}
</script>

<style scoped>
/* 自定义滚动条样式 */
.scrollbar-thin {
  scrollbar-width: thin;
}

.scrollbar-thumb-purple-300::-webkit-scrollbar-thumb {
  background-color: #d8b4fe;
  border-radius: 6px;
}

.scrollbar-track-gray-100::-webkit-scrollbar-track {
  background-color: #f3f4f6;
  border-radius: 6px;
}

::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #d8b4fe;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #c084fc;
}
</style>