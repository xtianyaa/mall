<template>
  <scroll-view class="order-detail-scroll" scroll-y show-scrollbar="false">
    <view v-if="orderDetail" class="page-container">
      <view class="card detail-card">
        <view class="detail-title">订单状态：{{ statusTextMap[orderDetail.status] }}</view>
        <view class="detail-line">订单编号：{{ orderDetail.id }}</view>
        <view class="detail-line">下单时间：{{ orderDetail.createTime }}</view>
        <view class="detail-line">配送地址：{{ orderDetail.addressText }}</view>
      </view>

      <view
        v-if="hasLogistics"
        class="card detail-card logistics-card"
      >
        <view class="section-title">
          <text class="section-title-text">物流信息</text>
        </view>
        <view class="logistics-info">
          <view class="detail-line">
            <text class="label">快递公司</text>
            <text>{{ orderDetail.expressCompany || "—" }}</text>
          </view>
          <view class="detail-line">
            <text class="label">快递单号</text>
            <text class="express-no">{{ orderDetail.expressNo }}</text>
          </view>
          <view v-if="orderDetail.shipTime" class="detail-line">
            <text class="label">发货时间</text>
            <text>{{ orderDetail.shipTime }}</text>
          </view>
        </view>
        <button
          class="logistics-btn"
          :disabled="logisticsLoading"
          @click="toggleLogistics"
        >
          {{ logisticsLoading ? "加载中..." : (showLogisticsTrace ? "收起物流进度" : "查看物流进度") }}
        </button>
        <view v-if="showLogisticsTrace && logisticsList.length" class="logistics-timeline">
          <view
            v-for="(trace, index) in logisticsList"
            :key="index"
            class="logistics-trace-item"
            :class="{ first: index === 0, last: index === logisticsList.length - 1 }"
          >
            <view class="trace-dot" />
            <view class="trace-line" v-if="index < logisticsList.length - 1" />
            <view class="trace-content">
              <text class="trace-desc">{{ trace.desc }}</text>
              <text class="trace-time">{{ trace.time }}</text>
            </view>
          </view>
        </view>
        <view v-else-if="showLogisticsTrace && !logisticsLoading && logisticsList.length === 0" class="logistics-empty">
          暂无物流轨迹
        </view>
      </view>

      <view v-if="orderDetail.status === 'pending'" class="card detail-card action-card">
        <view class="section-title">
          <text class="section-title-text">支付操作（待接入支付网关）</text>
        </view>
        <view class="action-row">
          <button class="action-button primary" :disabled="submitting" @click="handlePaySuccess">
            {{ submitting ? "处理中..." : "标记支付成功（测试回调）" }}
          </button>
          <button class="action-button ghost" :disabled="submitting" @click="handlePayFail">
            标记支付失败（测试回调）
          </button>
          <button class="action-button danger" :disabled="submitting" @click="handleCancel">
            取消订单
          </button>
        </view>
        <view class="action-tip">提示：支付失败/取消会释放锁库存与锁券。</view>
      </view>

      <view class="card detail-card">
        <view class="section-title">
          <text class="section-title-text">商品清单</text>
        </view>
        <view
          v-for="item in orderDetail.itemList"
          :key="item.id"
          class="goods-line goods-line-with-action"
        >
          <view class="goods-main">
            <text class="goods-name">{{ item.name }}{{ item.variantLabel ? `（${item.variantLabel}）` : "" }}</text>
            <text>x{{ item.quantity }}</text>
            <text class="price-text">￥{{ item.price }}</text>
            <text v-if="item.afterSaleStatus" class="after-sale-status-tag">{{ afterSaleStatusText(item.afterSaleStatus) }}</text>
          </view>
          <button
            v-if="(canApplyAfterSale || canApplyPreSale) && !item.afterSaleStatus"
            class="after-sale-btn"
            @click.stop="goApplyAfterSale(item)"
          >
            {{ canApplyPreSale ? "申请售前退款" : "申请售后" }}
          </button>
        </view>
      </view>

      <view class="card detail-card">
        <view class="amount-line">
          <text>订单金额</text>
          <text>￥{{ orderDetail.totalAmount.toFixed(2) }}</text>
        </view>
        <view class="amount-line total-line">
          <text>实付金额</text>
          <text class="price-text">￥{{ orderDetail.payAmount.toFixed(2) }}</text>
        </view>
      </view>
    </view>
  </scroll-view>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { onLoad } from "@dcloudio/uni-app";
import { getOrderLogistics } from "../../api/yuan-mall-module";
import { useMallStore } from "../../store/modules/mall";
import type { LogisticsTrace, MallOrder, MallOrderItem } from "../../types/mall";

const mallStore = useMallStore();
const orderDetail = ref<MallOrder | null>(null);
const submitting = ref(false);
const showLogisticsTrace = ref(false);
const logisticsList = ref<LogisticsTrace[]>([]);
const logisticsLoading = ref(false);

const hasLogistics = computed(() => {
  const o = orderDetail.value;
  return o && (o.status === "shipping" || o.status === "finished") && !!o.expressNo;
});

const statusTextMap = {
  pending: "待支付",
  paid: "待发货",
  shipping: "配送中",
  finished: "已完成",
  cancelled: "已取消",
};

const canApplyAfterSale = computed(() => {
  const o = orderDetail.value;
  if (!o) return false;
  return o.status === "shipping" || o.status === "finished";
});

// 售前：未支付/待发货阶段支持直接发起退款申请
const canApplyPreSale = computed(() => {
  const o = orderDetail.value;
  if (!o) return false;
  return o.status === "pending" || o.status === "paid";
});

const refresh = async (orderId: string) => {
  orderDetail.value = await mallStore.fetchOrderDetail(orderId);
};

const afterSaleStatusText = (status: string) => {
  const map: Record<string, string> = {
    APPLIED: "售后中",
    APPROVED: "已同意",
    REJECTED: "已拒绝",
    COMPLETED: "已完成",
    CLOSED: "已关闭",
  };
  return map[status] || "售后";
};

const goApplyAfterSale = (item: MallOrderItem) => {
  if (!orderDetail.value) return;
  uni.navigateTo({
    url: `/pages/after-sale/apply?orderId=${orderDetail.value.id}&orderItemId=${item.id}`,
  });
};

const toggleLogistics = async () => {
  if (showLogisticsTrace.value) {
    showLogisticsTrace.value = false;
    return;
  }
  if (!orderDetail.value?.id) return;
  logisticsLoading.value = true;
  try {
    logisticsList.value = await getOrderLogistics(orderDetail.value.id);
    showLogisticsTrace.value = true;
  } catch (e: any) {
    uni.showToast({ title: e?.message || "获取物流失败", icon: "none" });
  } finally {
    logisticsLoading.value = false;
  }
};

const handlePaySuccess = async () => {
  if (!orderDetail.value) {
    return;
  }
  submitting.value = true;
  try {
    const result = await mallStore.payOrderSuccessAction(orderDetail.value.id);
    uni.showToast({ title: result.message, icon: result.success ? "success" : "none" });
    await refresh(orderDetail.value.id);
  } catch (error: any) {
    uni.showToast({ title: error?.message || "操作失败", icon: "none" });
  } finally {
    submitting.value = false;
  }
};

const handlePayFail = async () => {
  if (!orderDetail.value) {
    return;
  }
  submitting.value = true;
  try {
    const result = await mallStore.payOrderFailAction(orderDetail.value.id);
    uni.showToast({ title: result.message, icon: result.success ? "success" : "none" });
    await refresh(orderDetail.value.id);
  } catch (error: any) {
    uni.showToast({ title: error?.message || "操作失败", icon: "none" });
  } finally {
    submitting.value = false;
  }
};

const handleCancel = async () => {
  if (!orderDetail.value) {
    return;
  }
  submitting.value = true;
  try {
    const result = await mallStore.cancelOrderAction(orderDetail.value.id);
    uni.showToast({ title: result.message, icon: result.success ? "success" : "none" });
    await refresh(orderDetail.value.id);
  } catch (error: any) {
    uni.showToast({ title: error?.message || "操作失败", icon: "none" });
  } finally {
    submitting.value = false;
  }
};

onLoad(async (options) => {
  const id = String(options?.id || "");
  await refresh(id);
});
</script>

<style lang="scss" scoped>
.order-detail-scroll {
  height: 100vh;
}

.detail-card {
  padding: 24rpx;
  margin-bottom: 20rpx;
}

.action-card .action-row {
  margin-top: 18rpx;
  display: flex;
  gap: 16rpx;
}

.action-button {
  flex: 1;
  height: 72rpx;
  line-height: 72rpx;
  border-radius: 999rpx;
  font-size: 26rpx;
}

.action-button.primary {
  background: #ef4444;
  color: #ffffff;
  border: none;
}

.action-button.ghost {
  background: #ffffff;
  color: #ef4444;
  border: 1rpx solid #ef4444;
}

.action-button.danger {
  background: #111827;
  color: #ffffff;
  border: none;
}

.action-tip {
  margin-top: 14rpx;
  color: #9ca3af;
  font-size: 22rpx;
  line-height: 1.6;
}

.detail-title {
  font-size: 32rpx;
  font-weight: 700;
  color: #ef4444;
}

.detail-line,
.goods-line,
.amount-line {
  margin-top: 14rpx;
  color: #374151;
  line-height: 1.6;
}

.goods-line-with-action {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.goods-main {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12rpx;
  min-width: 0;
}

.goods-name {
  flex: 1;
  min-width: 0;
}

.after-sale-btn {
  flex-shrink: 0;
  padding: 0 24rpx;
  height: 56rpx;
  line-height: 56rpx;
  font-size: 24rpx;
  color: #ef4444;
  background: #fef2f2;
  border: 1rpx solid #fecaca;
  border-radius: 999rpx;
}

.after-sale-btn::after {
  border: none;
}

.after-sale-status-tag {
  margin-left: 12rpx;
  padding: 4rpx 12rpx;
  font-size: 22rpx;
  color: #ef4444;
  background: #fef2f2;
  border-radius: 8rpx;
}

.goods-line,
.amount-line {
  display: flex;
  justify-content: space-between;
  gap: 16rpx;
}

.total-line {
  margin-top: 20rpx;
  padding-top: 20rpx;
  border-top: 1rpx solid #f3f4f6;
  font-weight: 600;
}

.logistics-card {
  margin-bottom: 20rpx;
}

.logistics-info {
  margin-top: 12rpx;
}

.logistics-info .label {
  display: inline-block;
  width: 140rpx;
  color: #6b7280;
}

.express-no {
  font-family: ui-monospace, monospace;
}

.logistics-btn {
  margin-top: 24rpx;
  height: 72rpx;
  line-height: 72rpx;
  font-size: 28rpx;
  color: #ef4444;
  background: #fef2f2;
  border: 1rpx solid #fecaca;
  border-radius: 999rpx;
}

.logistics-timeline {
  margin-top: 28rpx;
  padding-left: 24rpx;
  position: relative;
}

.logistics-trace-item {
  position: relative;
  padding-bottom: 28rpx;
}

.logistics-trace-item.last {
  padding-bottom: 0;
}

.trace-dot {
  position: absolute;
  left: -24rpx;
  top: 10rpx;
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;
  background: #ef4444;
  transform: translateX(-50%);
}

.logistics-trace-item.first .trace-dot {
  width: 20rpx;
  height: 20rpx;
  top: 8rpx;
  background: #dc2626;
}

.trace-line {
  position: absolute;
  left: -24rpx;
  top: 32rpx;
  bottom: 0;
  width: 2rpx;
  background: #e5e7eb;
  transform: translateX(-50%);
}

.trace-content {
  display: flex;
  flex-direction: column;
  gap: 6rpx;
}

.trace-desc {
  color: #374151;
  font-size: 28rpx;
  line-height: 1.5;
}

.trace-time {
  color: #9ca3af;
  font-size: 24rpx;
}

.logistics-empty {
  margin-top: 24rpx;
  text-align: center;
  color: #9ca3af;
  font-size: 26rpx;
}
</style>
