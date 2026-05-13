<template>
  <scroll-view class="order-list-scroll" scroll-y show-scrollbar="false">
    <view class="page-container">
      <view v-for="item in mallStore.orderList" :key="item.id" class="card order-item" @click="goOrderDetail(item.id)">
        <view class="order-head">
          <text>{{ item.id }}</text>
          <text class="order-status">{{ statusTextMap[item.status] }}</text>
        </view>
        <view class="order-time">{{ item.createTime }}</view>
        <view class="order-goods">
          <view v-for="goods in item.itemList" :key="goods.goodsId" class="goods-line">
            <text class="goods-name">{{ goods.name }}</text>
            <text>x{{ goods.quantity }}</text>
          </view>
        </view>
        <view class="order-foot">
          <text>实付 ￥{{ item.payAmount.toFixed(2) }}</text>
          <text class="order-link">查看详情</text>
        </view>
      </view>
    </view>
  </scroll-view>
</template>

<script setup lang="ts">
import { onShow } from "@dcloudio/uni-app";
import { useMallStore } from "../../store/modules/mall";

const mallStore = useMallStore();
const statusTextMap = {
  pending: "待支付",
  paid: "待发货",
  shipping: "配送中",
  finished: "已完成",
  cancelled: "已取消",
};

const goOrderDetail = (orderId: string) => {
  uni.navigateTo({ url: `/pages/order/detail?id=${orderId}` });
};

onShow(() => {
  mallStore.syncUserData();
});
</script>

<style lang="scss" scoped>
.order-list-scroll {
  height: 100vh;
}

.order-item {
  padding: 24rpx;
  margin-bottom: 20rpx;
}

.order-head,
.order-foot,
.goods-line {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-status,
.order-link {
  color: #ef4444;
}

.order-time {
  margin-top: 10rpx;
  color: #9ca3af;
  font-size: 22rpx;
}

.order-goods {
  margin-top: 18rpx;
  padding: 18rpx 0;
  border-top: 1rpx solid #f3f4f6;
  border-bottom: 1rpx solid #f3f4f6;
}

.goods-line + .goods-line {
  margin-top: 10rpx;
}

.goods-name {
  flex: 1;
  margin-right: 12rpx;
}

.order-foot {
  margin-top: 18rpx;
}
</style>
