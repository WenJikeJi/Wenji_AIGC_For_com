<template>
  <div class="sidebar-container">
    <div 
      v-for="item in menuItems" 
      :key="item.path"
      class="sidebar-item"
      :class="{ active: isActive(item) }"
      @click="navigateTo(item)"
    >
      <span class="icon" v-if="item.icon">
        <component :is="item.icon" />
      </span>
      <span class="title">{{ item.title }}</span>
    </div>
  </div>
</template>

<script>
import { ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import HomeIcon from "@/assets/icons/HomeIcon.vue";
import UserIcon from "@/assets/icons/UserIcon.vue";
// 导入其他需要的图标组件...

export default {
  setup() {
    const route = useRoute();
    const router = useRouter();

    const menuItems = ref([
      {
        title: "首页",
        path: "/",
        icon: HomeIcon,
      },
      {
        title: "用户管理",
        path: "/users",
        icon: UserIcon,
      },
      // 添加更多菜单项...
    ]);

    const isActive = (item) => {
      return route.path === item.path;
    };

    const navigateTo = (item) => {
      router.push(item.path);
    };

    return {
      menuItems,
      isActive,
      navigateTo,
    };
  },
};
</script>

<style scoped>
.sidebar-container {
  width: 240px;
  height: 100vh;
  background: #f8fafc;
  box-shadow: 2px 0 12px rgba(0, 0, 0, 0.08);
  padding: 20px 0;
}

.sidebar-item {
  display: flex;
  align-items: center;
  padding: 12px 24px;
  margin: 4px 0;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  color: #4b5563;
}

.sidebar-item:hover {
  background: #f1f5f9;
}

.sidebar-item.active {
  background: #e0f2fe;
  border-left: 3px solid #38bdf8;
  color: #0369a1;
}

/* 深色模式支持 */
.dark .sidebar-container {
  background: #1e293b;
}

.dark .sidebar-item {
  color: #e2e8f0;
}

.dark .sidebar-item.active {
  background: #0c4a6e;
  border-left-color: #7dd3fc;
}

.sidebar-item .icon {
  margin-right: 12px;
  display: flex;
  align-items: center;
}

.sidebar-item .title {
  font-size: 14px;
  font-weight: 500;
}
</style>