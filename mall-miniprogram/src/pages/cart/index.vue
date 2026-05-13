<template>
  <view class="cart-page">
    <scroll-view class="cart-scroll" scroll-y show-scrollbar="false">
      <view class="page-container">
        <view v-if="mallStore.cartGoodsList.length" class="cart-list">
          <view v-for="item in mallStore.cartGoodsList" :key="item.goods.id + '-' + (item.variantId ?? 'null')" class="card cart-item">
            <view class="check-box" :class="{ checked: item.checked }" @click="mallStore.toggleCartChecked(item.goods.id, item.variantId)">
              {{ item.checked ? "✓" : "" }}
            </view>
            <view
              class="goods-cover"
              :style="coverStyle(item.goods)"
            >
              <view v-if="!firstBannerUrl(item.goods)" class="cover-placeholder">暂无图</view>
            </view>
            <view class="item-content">
              <view class="line-clamp-1 item-name">{{ item.goods.name }}</view>
              <view v-if="item.variantLabel" class="item-variant-label">{{ item.variantLabel }}</view>
              <view class="item-price price-text">￥{{ item.variantPrice }}</view>
              <view class="item-action">
                <view class="stepper">
                  <text class="stepper-btn" @click="changeQuantity(item.goods.id, item.quantity - 1, item.variantId)">-</text>
                  <text class="stepper-value">{{ item.quantity }}</text>
                  <text class="stepper-btn" @click="changeQuantity(item.goods.id, item.quantity + 1, item.variantId)">+</text>
                </view>
                <text class="remove-link" @click="removeItem(item.goods.id, item.variantId)">删除</text>
              </view>
            </view>
          </view>
        </view>

        <view v-else class="card empty-card">
          <text>购物车还是空的，去首页挑点好货吧。</text>
        </view>
      </view>
    </scroll-view>

    <view class="settlement-bar card">
      <view>
        <view class="settlement-label">已选 {{ mallStore.checkedCartList.length }} 件</view>
        <view class="price-text">合计 ￥{{ mallStore.cartAmount.toFixed(2) }}</view>
      </view>
      <button class="settlement-button" @click="goCheckout">去结算</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onShow } from "@dcloudio/uni-app";
import { useMallStore } from "../../store/modules/mall";
import { firstBannerUrlFromGoods } from "../../utils/image";

const mallStore = useMallStore();

function firstBannerUrl(goods: { bannerList?: string[] | string }): string {
  return firstBannerUrlFromGoods(goods);
}
function coverStyle(goods: { bannerList?: string[] | string }) {
  const url = firstBannerUrl(goods);
  return {
    backgroundImage: url ? `url(${url})` : "none",
    backgroundSize: "cover",
    backgroundPosition: "center",
  };
}

const changeQuantity = async (goodsId: number, quantity: number, variantId?: number | null) => {
  await mallStore.updateCartQuantity(goodsId, quantity, variantId);
};

const removeItem = async (goodsId: number, variantId?: number | null) => {
  await mallStore.removeCartItem(goodsId, variantId);
  uni.showToast({
    title: "商品已移出购物车",
    icon: "success",
  });
};

const goCheckout = () => {
  if (!mallStore.checkedCartList.length) {
    uni.showToast({
      title: "请先勾选商品",
      icon: "none",
    });
    return;
  }
  uni.navigateTo({ url: "/pages/checkout/index" });
};

onShow(() => {
  mallStore.initMallData();
});
</script>

<style lang="scss" scoped>
.cart-page {
  height: 100vh;
  overflow: hidden;
}

.cart-scroll {
  height: 100vh;
}

.page-container {
  padding-bottom: 180rpx;
}

.cart-list {
  display: flex;
  flex-direction: column;
  gap: 18rpx;
  padding-bottom: 150rpx;
}

.cart-item {
  display: flex;
  align-items: center;
  gap: 18rpx;
  padding: 24rpx;
}

.check-box {
  width: 40rpx;
  height: 40rpx;
  line-height: 40rpx;
  text-align: center;
  border-radius: 50%;
  border: 2rpx solid #d1d5db;
  color: #ffffff;
}

.check-box.checked {
  background: #ef4444;
  border-color: #ef4444;
}

.goods-cover {
  width: 120rpx;
  height: 120rpx;
  border-radius: 24rpx;
  background: #f3f4f6;
  overflow: hidden;
  flex-shrink: 0;
}

.cover-img {
  width: 100%;
  height: 100%;
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #9ca3af;
  font-size: 22rpx;
}

.item-content {
  flex: 1;
}

.item-name {
  font-size: 28rpx;
  font-weight: 600;
}

.item-variant-label {
  margin-top: 6rpx;
  font-size: 22rpx;
  color: #6b7280;
}

.item-price {
  margin-top: 12rpx;
}

.item-action {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 18rpx;
}

.stepper {
  display: flex;
  gap: 14rpx;
  align-items: center;
}

.stepper-btn,
.stepper-value {
  width: 48rpx;
  height: 48rpx;
  line-height: 48rpx;
  text-align: center;
  border-radius: 12rpx;
  background: #f3f4f6;
}

.remove-link {
  color: #ef4444;
  font-size: 24rpx;
}

.empty-card {
  padding: 80rpx 24rpx;
  text-align: center;
  color: #6b7280;
}

.settlement-bar {
  position: fixed;
  left: 24rpx;
  right: 24rpx;
  bottom: 24rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 24rpx;
}

.settlement-label {
  color: #6b7280;
  font-size: 22rpx;
}

.settlement-button {
  margin: 0;
  width: 220rpx;
  height: 80rpx;
  line-height: 80rpx;
  border-radius: 999rpx;
  border: none;
  background: #ef4444;
  color: #ffffff;
}
</style>
