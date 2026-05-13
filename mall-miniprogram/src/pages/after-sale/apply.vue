<template>
  <scroll-view class="after-sale-scroll" scroll-y show-scrollbar="false">
    <view v-if="orderItem && orderDetail" class="page-container">
      <view class="card goods-card">
        <view class="section-title">
          <text class="section-title-text">申请售后商品</text>
        </view>
        <view class="goods-line">
          <text class="goods-name">{{ orderItem.name }}</text>
          <text>x{{ orderItem.quantity }}</text>
          <text class="price-text">￥{{ orderItem.price }}</text>
        </view>
      </view>

      <view class="card form-card">
        <view class="section-title">
          <text class="section-title-text">售后类型</text>
        </view>
        <radio-group class="type-group" @change="onTypeChange">
          <label class="type-item" v-for="item in typeOptions" :key="item.value">
            <radio :value="item.value" :checked="afterSaleType === item.value" color="#ef4444" />
            <view class="type-texts">
              <text class="type-label">{{ item.label }}</text>
              <text class="type-desc">{{ item.desc }}</text>
            </view>
          </label>
        </radio-group>
      </view>

      <view class="card form-card">
        <view class="section-title">
          <text class="section-title-text">申请数量</text>
        </view>
        <view class="quantity-row">
          <text class="quantity-label">本次申请数量</text>
          <view class="quantity-stepper">
            <button class="qty-btn" :disabled="applyQuantity <= 1" @click="changeQuantity(-1)">-</button>
            <text class="qty-value">{{ applyQuantity }}</text>
            <button class="qty-btn" :disabled="applyQuantity >= maxQuantity" @click="changeQuantity(1)">+</button>
          </view>
        </view>
        <view class="quantity-tip">最多可申请 {{ maxQuantity }} 件</view>
      </view>

      <view class="card form-card">
        <view class="section-title">
          <text class="section-title-text">问题描述</text>
        </view>
        <picker :range="reasonOptions" @change="onReasonChange">
          <view class="picker-value">{{ currentReasonLabel }}</view>
        </picker>
        <textarea
          v-model="description"
          class="reason-textarea"
          placeholder="请补充说明具体问题，方便商家快速处理"
          :maxlength="200"
          auto-height
        />
      </view>

      <view class="card amount-card">
        <view class="amount-row">
          <text>预计退款金额</text>
          <text class="price-text">￥{{ expectAmount.toFixed(2) }}</text>
        </view>
      </view>

      <button class="submit-button" :disabled="submitting" @click="submitApply">
        {{ submitting ? "提交中..." : "提交申请" }}
      </button>
    </view>
  </scroll-view>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { onLoad } from "@dcloudio/uni-app";
import { applyAfterSale, getOrderDetail } from "../../api/yuan-mall-module/index";
import { useMallStore } from "../../store/modules/mall";
import type { MallOrder, MallOrderItem } from "../../types/mall";

const mallStore = useMallStore();

const orderDetail = ref<MallOrder | null>(null);
const orderItem = ref<MallOrderItem | null>(null);
const afterSaleType = ref<"REFUND_ONLY" | "RETURN_REFUND" | "EXCHANGE">("REFUND_ONLY");
const reason = ref("商品质量问题");
const description = ref("");
const applyQuantity = ref(1);
const submitting = ref(false);

const typeOptions = [
  { value: "REFUND_ONLY", label: "仅退款", desc: "未收到货或无需退货，仅申请退款" },
  { value: "RETURN_REFUND", label: "退货退款", desc: "已收到货，退回商品后退款" },
  { value: "EXCHANGE", label: "换货", desc: "收到商品有问题，申请换同款" },
];

const reasonOptions = ["商品质量问题", "少发/漏发", "与描述不符", "未按约定时间送达", "其他"];

const currentReasonLabel = computed(() => reason.value || "请选择原因");

const maxQuantity = computed(() => orderItem.value?.quantity || 1);

const expectAmount = computed(() => {
  if (!orderItem.value) {
    return 0;
  }
  return Number(orderItem.value.price) * applyQuantity.value;
});

const onTypeChange = (e: any) => {
  afterSaleType.value = e.detail.value;
};

const onReasonChange = (e: any) => {
  const index = Number(e.detail.value);
  reason.value = reasonOptions[index] || reasonOptions[0];
};

const changeQuantity = (delta: number) => {
  const next = applyQuantity.value + delta;
  if (next < 1) return;
  if (next > maxQuantity.value) return;
  applyQuantity.value = next;
};

const submitApply = async () => {
  if (!orderDetail.value || !orderItem.value) {
    return;
  }
  if (!mallStore.userInfo?.id) {
    uni.showToast({ title: "请先登录", icon: "none" });
    return;
  }
  if (!reason.value) {
    uni.showToast({ title: "请选择或填写原因", icon: "none" });
    return;
  }
  submitting.value = true;
  try {
    const res = await applyAfterSale({
      orderId: orderDetail.value.id,
      orderItemId: orderItem.value.id,
      userId: mallStore.userInfo.id,
      type: afterSaleType.value,
      quantity: applyQuantity.value,
      reason: reason.value,
      description: description.value || undefined,
    });
    uni.showToast({ title: "申请已提交，等待商家处理", icon: "success" });
    setTimeout(() => {
      uni.redirectTo({ url: `/pages/after-sale/detail?id=${res.id}` });
    }, 500);
  } catch (error: any) {
    uni.showToast({ title: error?.message || "提交失败", icon: "none" });
  } finally {
    submitting.value = false;
  }
};

onLoad(async (options) => {
  const orderId = String(options?.orderId || "");
  const orderItemId = Number(options?.orderItemId || 0);
  if (!orderId || !orderItemId) {
    uni.showToast({ title: "参数错误", icon: "none" });
    return;
  }
  const detail = await getOrderDetail(orderId);
  orderDetail.value = detail;
  const target = detail.itemList.find((item) => Number(item.id) === orderItemId || item.goodsId === orderItemId);
  if (!target) {
    uni.showToast({ title: "未找到该商品行", icon: "none" });
    return;
  }
  orderItem.value = target;
  applyQuantity.value = 1;
});
</script>

<style lang="scss" scoped>
.after-sale-scroll {
  height: 100vh;
}

.goods-card,
.form-card,
.amount-card {
  padding: 24rpx;
  margin-bottom: 20rpx;
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

.type-group {
  margin-top: 12rpx;
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.type-item {
  display: flex;
  align-items: flex-start;
  gap: 12rpx;
}

.type-texts {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.type-label {
  font-size: 28rpx;
  color: #111827;
}

.type-desc {
  font-size: 24rpx;
  color: #6b7280;
}

.quantity-row {
  margin-top: 12rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.quantity-label {
  color: #374151;
}

.quantity-stepper {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.qty-btn {
  width: 64rpx;
  height: 64rpx;
  line-height: 64rpx;
  padding: 0;
  font-size: 36rpx;
  font-weight: 500;
  color: #ef4444;
  text-align: center;
  border-radius: 50%;
  border: 2rpx solid #ef4444;
  background: #fff5f5;
}

.qty-btn[disabled] {
  color: #d1d5db;
  border-color: #e5e7eb;
  background: #f9fafb;
}

.qty-value {
  min-width: 56rpx;
  font-size: 32rpx;
  font-weight: 600;
  color: #111827;
  text-align: center;
}

.quantity-tip {
  margin-top: 8rpx;
  font-size: 22rpx;
  color: #9ca3af;
}

.picker-value {
  margin-top: 12rpx;
  padding: 16rpx 18rpx;
  border-radius: 12rpx;
  border: 1rpx solid #e5e7eb;
  color: #374151;
}

.reason-textarea {
  margin-top: 16rpx;
  padding: 16rpx 18rpx;
  min-height: 160rpx;
  border-radius: 12rpx;
  border: 1rpx solid #e5e7eb;
  font-size: 26rpx;
}

.amount-card .amount-row {
  display: flex;
  justify-content: space-between;
  gap: 16rpx;
  color: #374151;
}

.submit-button {
  margin: 40rpx 24rpx 40rpx;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 999rpx;
  border: none;
  background: #ef4444;
  color: #ffffff;
}
</style>

