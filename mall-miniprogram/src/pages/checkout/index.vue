<template>
  <scroll-view class="checkout-scroll" scroll-y show-scrollbar="false">
    <view class="page-container">
      <view class="card address-card" @click="goAddress">
        <view class="section-title">
          <text class="section-title-text">收货地址</text>
          <text class="section-title-extra">管理地址</text>
        </view>
        <view v-if="mallStore.defaultAddress" class="address-text">
          {{ fullAddressText }}
        </view>
        <view v-else class="address-text">请先新增收货地址</view>
      </view>

      <view class="card order-card">
        <view class="section-title">
          <text class="section-title-text">确认商品</text>
        </view>
        <view
          v-for="item in mallStore.checkedCartList"
          :key="item.goods.id + '-' + (item.variantId ?? 'null')"
          class="goods-row"
        >
          <text class="goods-name">
            {{ item.goods.name }}{{ item.variantLabel ? `（${item.variantLabel}）` : "" }}
          </text>
          <text>x{{ item.quantity }}</text>
          <text class="price-text">￥{{ item.totalAmount.toFixed(2) }}</text>
        </view>
      </view>

      <view class="card coupon-card">
        <view class="section-title">
          <text class="section-title-text">优惠券</text>
        </view>
        <picker :range="couponOptionList" range-key="label" @change="handleCouponChange">
          <view class="picker-value">{{ currentCouponLabel }}</view>
        </picker>
      </view>

      <view class="card amount-card">
        <view class="amount-row">
          <text>商品金额</text>
          <text>￥{{ mallStore.cartAmount.toFixed(2) }}</text>
        </view>
        <view class="amount-row">
          <text>优惠金额</text>
          <text class="price-text">-￥{{ discountAmount.toFixed(2) }}</text>
        </view>
        <view class="amount-row total-row">
          <text>实付金额</text>
          <text class="price-text">￥{{ payAmount.toFixed(2) }}</text>
        </view>
      </view>

      <button class="submit-button" @click="submitOrder">提交订单</button>
    </view>
  </scroll-view>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { onShow } from "@dcloudio/uni-app";
import { useMallStore } from "../../store/modules/mall";

const mallStore = useMallStore();
const selectedCouponIndex = ref(0);

interface CouponItem {
  id: number;
  name: string;
  amount: number;
  minAmount: number;
}

const couponOptionList = computed(() => [
  { id: 0, label: "不使用优惠券", amount: 0, minAmount: 0 },
  ...mallStore.unusedCouponList.map((item: CouponItem) => ({
    id: item.id,
    label: `${item.name} - 满${item.minAmount}减${item.amount}`,
    amount: item.amount,
    minAmount: item.minAmount,
  })),
]);

const currentCoupon = computed(() => couponOptionList.value[selectedCouponIndex.value]);
const currentCouponLabel = computed(() => currentCoupon.value?.label || "不使用优惠券");

const discountAmount = computed(() => {
  const coupon = currentCoupon.value;
  if (!coupon) {
    return 0;
  }
  return mallStore.cartAmount >= coupon.minAmount ? coupon.amount : 0;
});

const payAmount = computed(() => Math.max(mallStore.cartAmount - discountAmount.value, 0));

const fullAddressText = computed(() => {
  const address = mallStore.defaultAddress;
  if (!address) {
    return "";
  }
  return `${address.name} ${address.mobile} ${address.province}${address.city}${address.district}${address.detail}`;
});

const handleCouponChange = (event: { detail: { value: string } }) => {
  selectedCouponIndex.value = Number(event.detail.value);
};

const goAddress = () => {
  uni.navigateTo({ url: "/pages/address/index" });
};

const submitOrder = async () => {
  const couponId = currentCoupon.value?.id || undefined;
  // 小程序端去掉隐私设置，默认传入 false
  const result = await mallStore.createOrderAction(couponId, false);
  uni.showToast({
    title: result.message,
    icon: result.success ? "success" : "none",
  });
  if (result.success && result.orderId) {
    setTimeout(() => {
      uni.redirectTo({ url: `/pages/order/detail?id=${result.orderId}` });
    }, 500);
  }
};

onShow(async () => {
  if (mallStore.hasLogin) {
    await mallStore.syncUserData();
  }

  // 进入结算页时自动为当前勾选商品选择“最优可用优惠券”
  const amount = mallStore.cartAmount;
  const available = mallStore.unusedCouponList.filter(
    (item: CouponItem) => amount >= item.minAmount
  );
  if (!available.length) {
    selectedCouponIndex.value = 0;
    return;
  }
  const best = available.reduce((prev, curr) =>
    curr.amount > prev.amount ? curr : prev
  );
  const bestIndex = couponOptionList.value.findIndex((item) => item.id === best.id);
  selectedCouponIndex.value = bestIndex > 0 ? bestIndex : 0;
});
</script>

<style lang="scss" scoped>
.checkout-scroll {
  height: 100vh;
}

.address-card,
.order-card,
.coupon-card,
.privacy-card,
.amount-card {
  padding: 24rpx;
  margin-bottom: 20rpx;
}

.privacy-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.privacy-info {
  display: flex;
  flex-direction: column;
}

.privacy-label {
  font-size: 28rpx;
  color: #374151;
  font-weight: 500;
}

.privacy-tip {
  font-size: 24rpx;
  color: #9ca3af;
  margin-top: 4rpx;
}

.address-text,
.picker-value,
.goods-row,
.amount-row {
  color: #374151;
  line-height: 1.7;
}

.goods-row,
.amount-row {
  display: flex;
  justify-content: space-between;
  gap: 16rpx;
}

.goods-row + .goods-row,
.amount-row + .amount-row {
  margin-top: 14rpx;
}

.goods-name {
  flex: 1;
}

.total-row {
  margin-top: 18rpx !important;
  padding-top: 18rpx;
  border-top: 1rpx solid #f3f4f6;
  font-weight: 600;
}

.submit-button {
  margin-top: 40rpx;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 999rpx;
  border: none;
  background: #ef4444;
  color: #ffffff;
}
</style>
