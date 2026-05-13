<template>
  <view class="page-container login-page">
    <view class="card login-card">
      <view class="login-logo">🛍️</view>
      <view class="login-title">{{ loginTitle }}</view>
      <view class="login-desc">使用微信授权登录，可同步购物车、地址和订单</view>
      <input
        v-model="nickName"
        class="login-input"
        placeholder="选填：设置昵称（不填将显示为微信用户）"
      />
      <button class="login-button" :loading="logging" @click="submitLogin">
        {{ logging ? "登录中…" : "微信授权登录" }}
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { onShow } from "@dcloudio/uni-app";
import { useMallStore } from "../../store/modules/mall";
import { getMallConfig } from "../../api/yuan-mall-module";

const mallStore = useMallStore();
const nickName = ref(mallStore.userInfo?.nickName || "");
const logging = ref(false);

const loginTitle = computed(() => {
  const name = mallStore.mallName?.trim();
  return name ? `欢迎登录${name}` : "欢迎登录";
});

onShow(async () => {
  try {
    const config = await getMallConfig();
    mallStore.$patch({ mallName: config.mallName?.trim() ?? "" });
  } catch {
    mallStore.$patch({ mallName: "" });
  }
});

const submitLogin = async () => {
  if (logging.value) return;
  logging.value = true;
  try {
    await mallStore.loginAction(nickName.value.trim() || undefined);
    uni.showToast({ title: "登录成功", icon: "success" });
    setTimeout(() => {
      uni.switchTab({ url: "/pages/my/index" });
    }, 500);
  } catch (e: any) {
    const msg = e?.message || e?.data?.message || "登录失败，请重试";
    uni.showToast({ title: msg, icon: "none", duration: 2500 });
  } finally {
    logging.value = false;
  }
};
</script>

<style lang="scss" scoped>
.login-page {
  display: flex;
  align-items: center;
  min-height: 100vh;
}

.login-card {
  width: 100%;
  padding: 48rpx 32rpx;
  text-align: center;
}

.login-logo {
  font-size: 100rpx;
}

.login-title {
  margin-top: 16rpx;
  font-size: 38rpx;
  font-weight: 700;
}

.login-desc {
  margin-top: 12rpx;
  color: #6b7280;
  line-height: 1.6;
}

.login-input {
  height: 86rpx;
  margin-top: 32rpx;
  padding: 0 24rpx;
  text-align: left;
  border-radius: 18rpx;
  background: #f3f4f6;
}

.login-button {
  margin-top: 24rpx;
  height: 86rpx;
  line-height: 86rpx;
  border: none;
  border-radius: 999rpx;
  background: #ef4444;
  color: #ffffff;
}
</style>
