<template>
  <div class="relative">
    <!-- 日期输入框 -->
    <div class="relative group">
      <input 
        type="text"
        :value="displayValue"
        @click="toggleCalendar"
        @focus="showCalendar = true"
        readonly
        :placeholder="placeholder"
        class="w-full px-5 py-4 text-sm rounded-2xl bg-white border-2 border-purple-200 focus:outline-none focus:border-purple-400 focus:ring-4 focus:ring-purple-100 transition-all duration-300 shadow-sm hover:shadow-lg group-hover:border-purple-300 cursor-pointer"
      />
      <div class="absolute right-4 top-1/2 transform -translate-y-1/2 pointer-events-none">
        <div class="w-6 h-6 bg-gradient-to-br from-purple-400 to-indigo-500 rounded-lg flex items-center justify-center shadow-sm">
          <i class="fas fa-calendar text-white text-xs"></i>
        </div>
      </div>
    </div>

    <!-- 日历弹出层 -->
    <transition
      enter-active-class="transition-all duration-300 ease-out"
      enter-from-class="opacity-0 scale-95 translate-y-2"
      enter-to-class="opacity-100 scale-100 translate-y-0"
      leave-active-class="transition-all duration-200 ease-in"
      leave-from-class="opacity-100 scale-100 translate-y-0"
      leave-to-class="opacity-0 scale-95 translate-y-2"
    >
      <div
        v-if="showCalendar"
        class="absolute bottom-full left-0 mb-2 w-80 bg-white rounded-2xl shadow-2xl border border-purple-100 z-50 overflow-hidden"
      >
        <!-- 日历头部 -->
        <div class="bg-gradient-to-r from-purple-500 to-indigo-600 text-white p-4">
          <div class="flex items-center justify-between">
            <button 
              @click="previousMonth"
              class="w-8 h-8 rounded-full hover:bg-white hover:bg-opacity-20 flex items-center justify-center transition-all duration-200"
            >
              <i class="fas fa-chevron-left text-sm"></i>
            </button>
            
            <div class="text-center">
              <div class="text-lg font-semibold">{{ currentYear }}年{{ currentMonth }}月</div>
            </div>
            
            <button 
              @click="nextMonth"
              class="w-8 h-8 rounded-full hover:bg-white hover:bg-opacity-20 flex items-center justify-center transition-all duration-200"
            >
              <i class="fas fa-chevron-right text-sm"></i>
            </button>
          </div>
        </div>

        <!-- 星期标题 -->
        <div class="grid grid-cols-7 bg-gray-50">
          <div 
            v-for="day in weekDays" 
            :key="day"
            class="p-3 text-center text-sm font-medium text-gray-600"
          >
            {{ day }}
          </div>
        </div>

        <!-- 日期网格 -->
        <div class="grid grid-cols-7 p-2">
          <button
            v-for="date in calendarDates"
            :key="`${date.year}-${date.month}-${date.day}`"
            @click="selectDate(date)"
            :disabled="date.disabled"
            :class="[
              'w-10 h-10 m-1 rounded-xl text-sm font-medium transition-all duration-200 flex items-center justify-center',
              {
                'text-gray-300 cursor-not-allowed': date.disabled,
                'text-gray-400': !date.isCurrentMonth && !date.disabled,
                'text-gray-700 hover:bg-purple-50 hover:text-purple-600': date.isCurrentMonth && !date.selected && !date.isTempSelected && !date.disabled,
                'bg-gradient-to-br from-purple-500 to-indigo-600 text-white shadow-lg transform scale-105': date.selected,
                'bg-purple-300 text-white shadow-sm': date.isTempSelected && !date.selected,
                'bg-purple-100 text-purple-700 font-semibold': date.isToday && !date.selected && !date.isTempSelected,
                'hover:scale-105 hover:shadow-md': !date.disabled
              }
            ]"
          >
            {{ date.day }}
          </button>
        </div>

        <!-- 底部操作按钮 -->
        <div class="p-4 bg-gray-50 flex justify-between items-center">
          <button
            @click="selectToday"
            class="px-4 py-2 text-sm text-purple-600 hover:text-purple-700 hover:bg-purple-50 rounded-lg transition-colors duration-200"
          >
            今天
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
              :disabled="!tempSelectedDate"
              class="px-4 py-2 text-sm bg-purple-500 text-white hover:bg-purple-600 disabled:bg-gray-300 disabled:cursor-not-allowed rounded-lg transition-colors duration-200"
            >
              确认
            </button>
          </div>
        </div>
      </div>
    </transition>

    <!-- 点击外部关闭日历 -->
    <div 
      v-if="showCalendar" 
      @click="showCalendar = false"
      class="fixed inset-0 z-40"
    ></div>
  </div>
</template>

<script>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'

export default {
  name: 'CustomDatePicker',
  props: {
    modelValue: {
      type: String,
      default: ''
    },
    placeholder: {
      type: String,
      default: '请选择日期'
    },
    minDate: {
      type: String,
      default: ''
    }
  },
  emits: ['update:modelValue'],
  setup(props, { emit }) {
    const showCalendar = ref(false)
    const currentDate = ref(new Date())
    const selectedDate = ref(props.modelValue ? new Date(props.modelValue) : null)
    const tempSelectedDate = ref('')

    const weekDays = ['日', '一', '二', '三', '四', '五', '六']

    const currentYear = computed(() => currentDate.value.getFullYear())
    const currentMonth = computed(() => currentDate.value.getMonth() + 1)

    const displayValue = computed(() => {
      if (!selectedDate.value) return ''
      const year = selectedDate.value.getFullYear()
      const month = String(selectedDate.value.getMonth() + 1).padStart(2, '0')
      const day = String(selectedDate.value.getDate()).padStart(2, '0')
      return `${year}/${month}/${day}`
    })

    const calendarDates = computed(() => {
      const year = currentDate.value.getFullYear()
      const month = currentDate.value.getMonth()
      
      const firstDay = new Date(year, month, 1)
      const lastDay = new Date(year, month + 1, 0)
      const startDate = new Date(firstDay)
      startDate.setDate(startDate.getDate() - firstDay.getDay())
      const endDate = new Date(lastDay)
      endDate.setDate(endDate.getDate() + (6 - lastDay.getDay()))
      
      const dates = []
      const current = new Date(startDate)
      const today = new Date()
      const minDateObj = props.minDate ? new Date(props.minDate) : null
      
      while (current <= endDate) {
        const dateObj = {
          year: current.getFullYear(),
          month: current.getMonth() + 1,
          day: current.getDate(),
          date: new Date(current),
          isCurrentMonth: current.getMonth() === month,
          isToday: current.toDateString() === today.toDateString(),
          selected: selectedDate.value && current.toDateString() === selectedDate.value.toDateString(),
          isTempSelected: tempSelectedDate.value && current.toDateString() === new Date(tempSelectedDate.value).toDateString(),
          disabled: minDateObj && current < minDateObj
        }
        dates.push(dateObj)
        current.setDate(current.getDate() + 1)
      }
      
      return dates
    })

    const toggleCalendar = () => {
      showCalendar.value = !showCalendar.value
    }

    const previousMonth = () => {
      currentDate.value = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth() - 1, 1)
    }

    const nextMonth = () => {
      currentDate.value = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth() + 1, 1)
    }

    const selectDate = (date) => {
      if (date.disabled) return
      
      const selected = new Date(date.date)
      const year = selected.getFullYear()
      const month = String(selected.getMonth() + 1).padStart(2, '0')
      const day = String(selected.getDate()).padStart(2, '0')
      
      tempSelectedDate.value = `${year}-${month}-${day}`
    }

    const confirmSelection = () => {
      if (tempSelectedDate.value) {
        selectedDate.value = new Date(tempSelectedDate.value)
        emit('update:modelValue', tempSelectedDate.value)
        showCalendar.value = false
        tempSelectedDate.value = ''
      }
    }

    const cancelSelection = () => {
      tempSelectedDate.value = ''
      showCalendar.value = false
    }

    const selectToday = () => {
      const today = new Date()
      const year = today.getFullYear()
      const month = String(today.getMonth() + 1).padStart(2, '0')
      const day = String(today.getDate()).padStart(2, '0')
      
      tempSelectedDate.value = `${year}-${month}-${day}`
      currentDate.value = new Date(today.getFullYear(), today.getMonth(), 1)
    }

    watch(() => props.modelValue, (newValue) => {
      if (newValue) {
        selectedDate.value = new Date(newValue)
        currentDate.value = new Date(selectedDate.value.getFullYear(), selectedDate.value.getMonth(), 1)
      } else {
        selectedDate.value = null
      }
    })

    const handleKeydown = (event) => {
      if (event.key === 'Escape') {
        showCalendar.value = false
      }
    }

    onMounted(() => {
      document.addEventListener('keydown', handleKeydown)
    })

    onUnmounted(() => {
      document.removeEventListener('keydown', handleKeydown)
    })

    return {
      showCalendar,
      currentDate,
      selectedDate,
      tempSelectedDate,
      weekDays,
      currentYear,
      currentMonth,
      displayValue,
      calendarDates,
      toggleCalendar,
      previousMonth,
      nextMonth,
      selectDate,
      selectToday,
      confirmSelection,
      cancelSelection
    }
  }
}
</script>

<style scoped>
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}
</style>