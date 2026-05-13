<template>
  <view class="page-container">
    <view class="card profile-card">
      <view class="profile-head">
        <view>
          <view class="profile-name">{{ mallStore.userInfo?.nickName || "未登录用户" }}</view>
          <view class="profile-mobile">{{ mallStore.userInfo?.mobile || "请先登录" }}</view>
        </view>
        <text class="profile-level">{{ mallStore.userInfo?.levelName || "普通访客" }}</text>
      </view>
      <view class="profile-stat">
        <view class="stat-item">
          <text class="stat-value">{{ mallStore.userInfo?.points || 0 }}</text>
          <text class="stat-label">积分</text>
        </view>
        <view class="stat-item">
          <text class="stat-value">{{ mallStore.couponList.filter(item => item.status === 'unused').length }}</text>
          <text class="stat-label">可用券</text>
        </view>
        <view class="stat-item">
          <text class="stat-value">{{ mallStore.orderList.length }}</text>
          <text class="stat-label">订单数</text>
        </view>
      </view>
    </view>

    <view class="card menu-card">
      <view class="menu-item" @click="goOrderList">我的订单</view>
      <view class="menu-item" @click="goCoupon">我的优惠券</view>
      <view class="menu-item" @click="goAddress">收货地址</view>
      <view class="menu-item" @click="goLogin">修改昵称</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onShow } from "@dcloudio/uni-app";
import { useMallStore } from "../../store/modules/mall";

const mallStore = useMallStore();

const goOrderList = () => {
  uni.navigateTo({ url: "/pages/order/list" });
};

const goCoupon = () => {
  uni.navigateTo({ url: "/pages/coupon/index" });
};

const goAddress = () => {
  uni.navigateTo({ url: "/pages/address/index" });
};

const goLogin = () => {
  uni.navigateTo({ url: "/pages/login/index" });
};

onShow(() => {
  if (mallStore.mallName) {
    uni.setNavigationBarTitle({ title: mallStore.mallName });
  }
  if (mallStore.hasLogin) {
    mallStore.syncUserData();
  }
});
</script>

<style lang="scss" scoped>
.profile-card,
.menu-card {
  padding: 28rpx;
  margin-bottom: 20rpx;
}

.profile-card {
  background: linear-gradient(135deg, #111827, #374151);
  color: #ffffff;
}

.profile-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.profile-name {
  font-size: 38rpx;
  font-weight: 700;
}

.profile-mobile {
  margin-top: 10rpx;
  color: rgba(255, 255, 255, 0.72);
  font-size: 24rpx;
}

.profile-level {
  padding: 8rpx 18rpx;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.12);
  font-size: 22rpx;
}

.profile-stat {
  display: flex;
  justify-content: space-between;
  margin-top: 28rpx;
}

.stat-item {
  text-align: center;
  flex: 1;
}

.stat-value {
  display: block;
  font-size: 34rpx;
  font-weight: 700;
}

.stat-label {
  display: block;
  margin-top: 10rpx;
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.7);
}

.menu-item {
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f3f4f6;
  font-size: 28rpx;
  color: #374151;
}

.danger-item {
  color: #ef4444;
}

.menu-item:last-child {
  border-bottom: none;
}
</style>
