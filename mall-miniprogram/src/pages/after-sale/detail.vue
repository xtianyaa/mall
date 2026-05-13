<template>
  <scroll-view class="after-sale-scroll" scroll-y show-scrollbar="false">
    <view v-if="detail" class="page-container">
      <view class="card status-card">
        <view class="detail-title">售后状态：{{ statusText }}</view>
        <view class="detail-line">售后单号：{{ detail.id }}</view>
        <view class="detail-line">关联订单：{{ detail.orderId }}</view>
        <view class="detail-line">申请时间：{{ detail.applyTime }}</view>
      </view>

      <view class="card goods-card">
        <view class="section-title">
          <text class="section-title-text">商品信息</text>
        </view>
        <view v-for="item in detail.itemList" :key="item.id" class="goods-line">
          <text class="goods-name">{{ item.goodsName }}</text>
          <text>x{{ item.quantity }}</text>
          <text class="price-text">￥{{ item.goodsPrice }}</text>
        </view>
      </view>

      <view class="card info-card">
        <view class="section-title">
          <text class="section-title-text">申请信息</text>
        </view>
        <view class="detail-line">售后类型：{{ typeText }}</view>
        <view class="detail-line">申请原因：{{ detail.reason }}</view>
        <view v-if="detail.description" class="detail-line">补充说明：{{ detail.description }}</view>
        <view class="detail-line amount-line">
          <text>申请金额</text>
          <text class="price-text">￥{{ detail.applyAmount.toFixed(2) }}</text>
        </view>
        <view class="detail-line amount-line">
          <text>预计退款</text>
          <text class="price-text">￥{{ detail.actualAmount.toFixed(2) }}</text>
        </view>
      </view>

      <view v-if="detail.logList?.length" class="card log-card">
        <view class="section-title">
          <text class="section-title-text">处理进度</text>
        </view>
        <view class="log-timeline">
          <view
            v-for="(log, index) in detail.logList"
            :key="index"
            class="log-item"
            :class="{ first: index === 0, last: index === detail.logList.length - 1 }"
          >
            <view class="log-dot" />
            <view v-if="index < detail.logList.length - 1" class="log-line" />
            <view class="log-content">
              <text class="log-text">{{ log.remark || log.action }}</text>
              <text class="log-time">{{ log.createTime }}</text>
            </view>
          </view>
        </view>
      </view>

      <button class="submit-button" @click="goOrderDetail">返回订单详情</button>
    </view>
  </scroll-view>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { onLoad } from "@dcloudio/uni-app";
import { getAfterSaleDetail } from "../../api/yuan-mall-module/index";
import type { AfterSaleDetail } from "../../types/after-sale";

const detail = ref<AfterSaleDetail | null>(null);

const statusText = computed(() => {
  if (!detail.value) return "";
  const map: Record<string, string> = {
    APPLIED: "待商家处理",
    APPROVED: "商家已同意",
    REJECTED: "商家已拒绝",
    COMPLETED: "已完成",
    CLOSED: "已关闭",
  };
  return map[detail.value.status] || detail.value.status;
});

const typeText = computed(() => {
  if (!detail.value) return "";
  const map: Record<string, string> = {
    REFUND_ONLY: "仅退款",
    RETURN_REFUND: "退货退款",
    EXCHANGE: "换货",
  };
  return map[detail.value.type] || detail.value.type;
});

const goOrderDetail = () => {
  if (!detail.value) return;
  uni.redirectTo({ url: `/pages/order/detail?id=${detail.value.orderId}` });
};

onLoad(async (options) => {
  const id = Number(options?.id || 0);
  if (!id) {
    uni.showToast({ title: "参数错误", icon: "none" });
    return;
  }
  try {
    const res = await getAfterSaleDetail(id);
    detail.value = res;
  } catch (error: any) {
    uni.showToast({ title: error?.message || "加载失败", icon: "none" });
  }
});
</script>

<style lang="scss" scoped>
.after-sale-scroll {
  height: 100vh;
}

.status-card,
.goods-card,
.info-card,
.log-card {
  padding: 24rpx;
  margin-bottom: 20rpx;
}

.detail-title {
  font-size: 32rpx;
  font-weight: 700;
  color: #ef4444;
}

.detail-line {
  margin-top: 14rpx;
  color: #374151;
  line-height: 1.6;
}

.goods-line {
  margin-top: 14rpx;
  display: flex;
  justify-content: space-between;
  gap: 16rpx;
  color: #374151;
}

.goods-name {
  flex: 1;
}

.amount-line {
  display: flex;
  justify-content: space-between;
  gap: 16rpx;
}

.log-timeline {
  margin-top: 20rpx;
  padding-left: 24rpx;
  position: relative;
}

.log-item {
  position: relative;
  padding-bottom: 20rpx;
}

.log-item.last {
  padding-bottom: 0;
}

.log-dot {
  position: absolute;
  left: -24rpx;
  top: 8rpx;
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;
  background: #ef4444;
  transform: translateX(-50%);
}

.log-line {
  position: absolute;
  left: -24rpx;
  top: 26rpx;
  bottom: 0;
  width: 2rpx;
  background: #e5e7eb;
  transform: translateX(-50%);
}

.log-content {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.log-text {
  color: #374151;
  font-size: 28rpx;
}

.log-time {
  color: #9ca3af;
  font-size: 24rpx;
}

.submit-button {
  margin: 40rpx 24rpx 40rpx;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 999rpx;
  border: none;
  background: #111827;
  color: #ffffff;
}
</style>

