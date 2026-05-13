<template>
  <view class="goods-list-page">
    <scroll-view class="goods-list-scroll" scroll-y show-scrollbar="false">
      <view class="page-container">
        <view class="section-title">
          <text class="section-title-text">{{ pageTitle }}</text>
          <text class="section-title-extra">{{ filterGoodsList.length }} 款商品</text>
        </view>

        <view v-if="filterGoodsList.length" class="goods-list">
          <view
            v-for="item in filterGoodsList"
            :key="item.id"
            class="card goods-card"
            @click="goGoodsDetail(item.id)"
          >
            <view
              class="goods-cover"
              :style="coverStyle(item)"
            >
              <view v-if="!firstBannerUrl(item)" class="cover-placeholder">暂无图</view>
            </view>
            <view class="goods-content">
              <view class="goods-name line-clamp-1">{{ item.name }}</view>
              <view class="goods-desc line-clamp-2">{{ item.characteristic }}</view>
              <view class="goods-meta">月销 {{ item.monthlySales }} · 库存 {{ item.stock }}</view>
              <view class="goods-footer">
                <view>
                  <text class="price-text">￥{{ item.price }}</text>
                  <text class="goods-original">￥{{ item.originalPrice }}</text>
                </view>
                <button class="goods-button" size="mini" @click.stop="handleAddCart(item.id)">
                  加入购物车
                </button>
              </view>
            </view>
          </view>
        </view>
        <view v-else class="empty-tip card">
          <text>该分类暂无商品，去首页或分类看看吧</text>
        </view>
      </view>
    </scroll-view>

    <!-- 底部导航（本页非 tabBar 页，高度与系统 tabBar 一致：内容区 100rpx + 安全区） -->
    <view class="bottom-tab">
      <view class="bottom-tab-inner">
        <view class="tab-item" @click="goTab('index')">
          <text class="tab-text">首页</text>
        </view>
        <view class="tab-item" @click="goTab('category')">
          <text class="tab-text">分类</text>
        </view>
        <view class="tab-item" @click="goTab('cart')">
          <text class="tab-text">购物车</text>
        </view>
        <view class="tab-item" @click="goTab('my')">
          <text class="tab-text">我的</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { onLoad } from "@dcloudio/uni-app";
import { useMallStore } from "../../store/modules/mall";
import { firstBannerUrlFromGoods } from "../../utils/image";
import { needsSkuPicker } from "../../utils/goodsVariant";

const mallStore = useMallStore();
const categoryId = ref<number | null>(null);
const keyword = ref("");

function firstBannerUrl(item: { bannerList?: string[] | string }): string {
  return firstBannerUrlFromGoods(item);
}
function coverStyle(item: { bannerList?: string[] | string }) {
  const url = firstBannerUrl(item);
  return {
    backgroundImage: url ? `url(${url})` : "none",
    backgroundSize: "cover",
    backgroundPosition: "center",
  };
}

const filterGoodsList = computed(() =>
  mallStore.goodsList.filter((item) => {
    const matchCategory = !categoryId.value || item.categoryId === categoryId.value;
    const matchKeyword =
      !keyword.value ||
      item.name.includes(keyword.value) ||
      item.characteristic.includes(keyword.value);
    return matchCategory && matchKeyword;
  })
);

const pageTitle = computed(() => {
  if (keyword.value) {
    return `搜索“${keyword.value}”`;
  }
  const category = mallStore.categoryList.find((item) => item.id === categoryId.value);
  return category?.name || "商品列表";
});

const goGoodsDetail = (goodsId: number) => {
  uni.navigateTo({ url: `/pages/goods/detail?id=${goodsId}` });
};

const handleAddCart = async (goodsId: number) => {
  const goods = mallStore.getGoodsById(goodsId);
  if (!goods) {
    uni.showToast({ title: "商品不存在", icon: "none" });
    return;
  }
  if (needsSkuPicker(goods)) {
    uni.navigateTo({ url: `/pages/goods/detail?id=${goodsId}&openSku=1` });
    return;
  }
  const result = await mallStore.addCart(goodsId, 1);
  uni.showToast({
    title: result.message,
    icon: result.success ? "success" : "none",
  });
};

/** 底部导航：跳转到对应 tabBar 页 */
const goTab = (tab: "index" | "category" | "cart" | "my") => {
  const path =
    tab === "index"
      ? "/pages/index/index"
      : tab === "category"
        ? "/pages/category/index"
        : tab === "cart"
          ? "/pages/cart/index"
          : "/pages/my/index";
  uni.switchTab({ url: path });
};

onLoad(async (options) => {
  if (options?.categoryId) {
    categoryId.value = Number(options.categoryId);
  }
  if (options?.keyword) {
    keyword.value = decodeURIComponent(String(options.keyword));
  }
  await mallStore.fetchGoodsListData({
    categoryId: categoryId.value || undefined,
    keyword: keyword.value || undefined,
  });
  // 若带分类或关键词请求后仍无数据，拉取全量再在前端筛选（兜底）
  const hasFilter = categoryId.value != null || (keyword.value && keyword.value.trim() !== "");
  if (hasFilter && mallStore.goodsList.length === 0) {
    await mallStore.fetchGoodsListData({});
  }
});
</script>

<style lang="scss" scoped>
.goods-list-page {
  height: 100vh;
  overflow: hidden;
}

.goods-list-scroll {
  height: 100vh;
}

.page-container {
  padding-bottom: 120rpx;
}

.empty-tip {
  padding: 48rpx 24rpx;
  text-align: center;
  color: #6b7280;
  font-size: 28rpx;
}

/* 与系统 tabBar 一致：内容区固定高度，安全区仅作底部留白 */
.bottom-tab {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #ffffff;
  border-top: 1rpx solid #e5e7eb;
  padding-bottom: constant(safe-area-inset-bottom);
  padding-bottom: env(safe-area-inset-bottom);
}

.bottom-tab-inner {
  height: 100rpx;
  display: flex;
  align-items: center;
  justify-content: space-around;
}

.tab-item {
  flex: 1;
  text-align: center;
}

.tab-text {
  font-size: 24rpx;
  color: #6b7280;
}

.goods-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.goods-card {
  display: flex;
  gap: 20rpx;
  padding: 24rpx;
}

.goods-cover {
  width: 160rpx;
  height: 160rpx;
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
  font-size: 24rpx;
}

.goods-content {
  flex: 1;
}

.goods-name {
  font-size: 30rpx;
  font-weight: 600;
}

.goods-desc,
.goods-meta {
  margin-top: 12rpx;
  font-size: 24rpx;
  color: #6b7280;
}

.goods-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 18rpx;
}

.goods-original {
  margin-left: 12rpx;
  color: #9ca3af;
  font-size: 22rpx;
  text-decoration: line-through;
}

.goods-button {
  margin: 0;
  border: none;
  border-radius: 999rpx;
  background: #111827;
  color: #ffffff;
}
</style>
