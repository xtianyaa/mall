<template>
  <scroll-view class="coupon-scroll" scroll-y show-scrollbar="false">
    <view class="page-container">
      <view
        v-for="item in mallStore.couponList"
        :key="item.id"
        class="card coupon-item"
        :class="item.status"
      >
        <view class="coupon-amount">￥{{ item.amount }}</view>
        <view class="coupon-content">
          <view class="coupon-name">{{ item.name }}</view>
          <view class="coupon-rule">满 {{ item.minAmount }} 元可用</view>
          <view class="coupon-expire">有效期至 {{ item.expireTime }}</view>
        </view>
        <view class="coupon-status">
          {{
            item.status === "unused"
              ? "可使用"
              : item.status === "locked"
                ? "已锁定"
                : item.status === "used"
                  ? "已使用"
                  : "已过期"
          }}
        </view>
      </view>
    </view>
  </scroll-view>
</template>

<script setup lang="ts">
import { onShow } from "@dcloudio/uni-app";
import { useMallStore } from "../../store/modules/mall";

const mallStore = useMallStore();

onShow(() => {
  mallStore.syncUserData();
});
</script>

<style lang="scss" scoped>
.coupon-scroll {
  height: 100vh;
}

.coupon-item {
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 24rpx;
  margin-bottom: 18rpx;
}

.coupon-item.used,
.coupon-item.locked,
.coupon-item.expired {
  opacity: 0.6;
}

.coupon-amount {
  width: 140rpx;
  font-size: 42rpx;
  font-weight: 700;
  color: #ef4444;
  text-align: center;
}

.coupon-content {
  flex: 1;
}

.coupon-name {
  font-size: 30rpx;
  font-weight: 600;
}

.coupon-rule,
.coupon-expire {
  margin-top: 8rpx;
  color: #6b7280;
  font-size: 22rpx;
}

.coupon-status {
  color: #ef4444;
  font-size: 24rpx;
}
</style>
