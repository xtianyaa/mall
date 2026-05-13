<template>
  <div class="layout-page">
    <!-- 菜单图标 symbol 定义 -->
    <svg class="sr-only" aria-hidden="true">
      <symbol id="icon-dashboard" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="7" height="9" rx="1" /><rect x="14" y="3" width="7" height="5" rx="1" /><rect x="14" y="12" width="7" height="9" rx="1" /><rect x="3" y="16" width="7" height="5" rx="1" /></symbol>
      <symbol id="icon-category" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 4h6v6H4V4zm10 0h6v6h-6V4zM4 14h6v6H4v-6zm10 0h6v6h-6v-6z" /></symbol>
      <symbol id="icon-goods" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z" /><line x1="3" y1="6" x2="21" y2="6" /><path d="M16 10a4 4 0 0 1-8 0" /></symbol>
      <symbol id="icon-order" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" /><polyline points="14 2 14 8 20 8" /><line x1="16" y1="13" x2="8" y2="13" /><line x1="16" y1="17" x2="8" y2="17" /><polyline points="10 9 9 9 8 9" /></symbol>
      <symbol id="icon-after-sale" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 12a9 9 0 1 0 9-9 9.75 9.75 0 0 0-6.74 2.74L3 8" /><path d="M3 3v5h5" /></symbol>
      <symbol id="icon-user" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" /><circle cx="12" cy="7" r="4" /></symbol>
      <symbol id="icon-address" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" /><circle cx="12" cy="10" r="3" /></symbol>
      <symbol id="icon-coupon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 4h16a2 2 0 0 1 2 2v2H2V6a2 2 0 0 1 2-2z" /><path d="M2 12h20v6a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2v-6z" /><line x1="8" y1="12" x2="8.01" y2="12" /></symbol>
      <symbol id="icon-marketing" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z" /></symbol>
      <symbol id="icon-home" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" /><polyline points="9 22 9 12 15 12 15 22" /></symbol>
      <symbol id="icon-settings" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3" /><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z" /></symbol>
    </svg>
    <aside class="layout-aside">
      <div class="brand-block">
        <span class="brand-title">{{ brandTitle }}</span>
      </div>
      <nav class="menu-list" aria-label="主导航">
        <router-link
          v-for="item in menuList"
          :key="item.path"
          :to="item.path"
          class="menu-item"
          :class="{ 'menu-item--active': route.path === item.path }"
        >
          <svg class="menu-icon" aria-hidden="true"><use :href="`#icon-${item.icon}`" /></svg>
          <span class="menu-text">{{ item.title }}</span>
        </router-link>
      </nav>
    </aside>

    <div class="layout-main">
      <header class="layout-header">
        <div>
          <div class="header-title">{{ currentTitle }}</div>
        </div>
        <div class="header-actions">
          <el-button type="default" @click="showChangePwd = true">修改密码</el-button>
          <el-button type="default" @click="handleLogout">退出登录</el-button>
        </div>
      </header>

      <main class="layout-content">
        <router-view />
      </main>
    </div>

    <el-dialog v-model="showChangePwd" title="修改密码" width="420px">
      <el-form label-width="80px">
        <el-form-item label="原密码">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="pwdForm.newPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showChangePwd = false">取 消</el-button>
        <el-button type="primary" :loading="pwdLoading" @click="handleSubmitChangePwd">
          确认修改
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from "vue";
import { ElMessage } from "element-plus";
import { useRoute, useRouter } from "vue-router";
import { adminLogout, changeAdminPassword } from "../api/yuan-mall-module";
import { useMallStore } from "../store/modules/mall";

const route = useRoute();
const router = useRouter();
const mallStore = useMallStore();

const brandTitle = computed(() =>
  mallStore.systemSetting.mallName
    ? `${mallStore.systemSetting.mallName}`
    : "元选商城后台"
);

watch(brandTitle, (title) => {
  document.title = title;
}, { immediate: true });

onMounted(() => {
  mallStore.fetchSystemSetting().catch(() => {});
});

const menuList = [
  { path: "/dashboard", title: "工作台", icon: "dashboard" },
  { path: "/yuan-mall-module/product/category", title: "商品分类", icon: "category" },
  { path: "/yuan-mall-module/product/goods", title: "商品管理", icon: "goods" },
  { path: "/yuan-mall-module/order", title: "订单管理", icon: "order" },
  { path: "/yuan-mall-module/after-sale/order", title: "售后订单", icon: "after-sale" },
  { path: "/yuan-mall-module/user", title: "用户管理", icon: "user" },
  { path: "/yuan-mall-module/user/address", title: "收货地址", icon: "address" },
  { path: "/yuan-mall-module/user/coupon", title: "用户优惠券", icon: "coupon" },
  { path: "/yuan-mall-module/marketing/coupon", title: "优惠券管理", icon: "marketing" },
  { path: "/yuan-mall-module/content/home", title: "首页装修", icon: "home" },
  { path: "/yuan-mall-module/system/settings", title: "系统设置", icon: "settings" },
];

const currentTitle = computed(() => String(route.meta.title || "工作台"));

const showChangePwd = ref(false);
const pwdLoading = ref(false);
const pwdForm = reactive({
  oldPassword: "",
  newPassword: "",
});

const handleLogout = async () => {
  try {
    await adminLogout();
  } finally {
    window.localStorage.removeItem("YA_ADMIN_TOKEN");
    window.localStorage.removeItem("YA_ADMIN_REFRESH_TOKEN");
    router.replace("/login");
  }
};

const handleSubmitChangePwd = async () => {
  if (!pwdForm.oldPassword || !pwdForm.newPassword) {
    ElMessage.warning("请填写完整密码信息");
    return;
  }
  pwdLoading.value = true;
  try {
    await changeAdminPassword({
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword,
    });
    ElMessage.success("密码修改成功，请重新登录");
    showChangePwd.value = false;
    pwdForm.oldPassword = "";
    pwdForm.newPassword = "";
    await handleLogout();
  } finally {
    pwdLoading.value = false;
  }
};
</script>

<style lang="scss" scoped>
.layout-page {
  display: flex;
  min-height: 100vh;
  background: var(--ya-bg-page, #f0f2f6);
}

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}

.layout-aside {
  --menu-bg: #0c1222;
  --menu-border: rgba(255, 255, 255, 0.06);
  --menu-text: rgba(255, 255, 255, 0.78);
  --menu-text-active: #fff;
  --menu-accent: #0d9488;
  --menu-accent-dim: rgba(13, 148, 136, 0.2);

  flex-shrink: 0;
  width: 248px;
  display: flex;
  flex-direction: column;
  background: var(--menu-bg);
  border-right: 1px solid var(--menu-border);
}

.brand-block {
  padding: 22px 22px 26px;
  border-bottom: 1px solid var(--menu-border);
}

.brand-title {
  font-family: "Syne", "DM Sans", "PingFang SC", sans-serif;
  font-size: 1.25rem;
  font-weight: 700;
  letter-spacing: -0.02em;
  color: var(--menu-text-active);
}

.menu-list {
  flex: 1;
  padding: 18px 14px 24px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  overflow-y: auto;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 12px 16px 12px 14px;
  border-radius: 10px;
  color: var(--menu-text);
  font-size: 0.9375rem;
  font-weight: 500;
  text-decoration: none;
  transition: color 0.2s ease, background 0.2s ease;
  position: relative;
}

.menu-item:hover {
  color: rgba(255, 255, 255, 0.95);
  background: var(--menu-accent-dim);
}

.menu-item--active {
  color: var(--menu-text-active);
  background: var(--menu-accent-dim);
}

.menu-item--active::before {
  content: "";
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 22px;
  border-radius: 0 4px 4px 0;
  background: var(--menu-accent);
}

.menu-icon {
  flex-shrink: 0;
  width: 22px;
  height: 22px;
  opacity: 0.85;
}

.menu-item--active .menu-icon {
  opacity: 1;
}

.menu-text {
  flex: 1;
  min-width: 0;
}

.layout-main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.layout-main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 28px;
  background: #ffffff;
  border-bottom: 1px solid #ebeef5;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-title {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
}

.header-desc {
  margin-top: 8px;
  color: #6b7280;
  font-size: 13px;
}

.layout-content {
  flex: 1;
  min-width: 0;
  padding: 24px 28px;
}
</style>
