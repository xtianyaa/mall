<template>
  <scroll-view
    class="home-scroll"
    scroll-y
    show-scrollbar="false"
    refresher-enabled
    :refresher-triggered="isRefreshing"
    @refresherrefresh="handleRefresh"
  >
    <view class="page-container home-page">
    <view class="banner card">
      <view class="banner-head">
        <view>
          <view class="banner-title">{{ currentBanner?.title || mallStore.mallName || "元选商城" }}</view>
          <view class="banner-subtitle">
            {{ currentBanner ? `${currentBanner.linkType} · 推荐活动进行中` : "微信商城" }}
          </view>
        </view>
        <view class="banner-tag">{{ currentBanner ? `NO.${currentBanner.sort}` : "精选好物" }}</view>
      </view>
      <view class="search-box" @click="goSearch">
        <text class="search-placeholder">{{ searchPlaceholder }}</text>
      </view>
      <view class="banner-stat">
        <view v-for="(stat, i) in statsList" :key="i" class="stat-item">
          <text class="stat-value">{{ stat.value }}</text>
          <text class="stat-label">{{ stat.label }}</text>
        </view>
      </view>
    </view>

    <view class="section-title">
      <text class="section-title-text">热门分类</text>
      <text class="section-title-extra" @click="goCategory">查看全部</text>
    </view>
    <view class="category-grid card">
      <view
        v-for="item in mallStore.categoryList"
        :key="item.id"
        class="category-item"
        @click="goGoodsList(item.id)"
      >
        <image
          v-if="isCategoryIconImage(item.icon) && !categoryIconLoadError.has(item.id)"
          class="category-icon-img"
          :src="categoryIconUrl(item.icon)"
          mode="aspectFit"
          @error="onCategoryIconError(item.id)"
        />
        <text v-else class="category-icon">{{ categoryIconPlaceholder(item.icon) }}</text>
        <text class="category-name">{{ item.name }}</text>
      </view>
    </view>

    <view class="section-title">
      <text class="section-title-text">今日推荐</text>
      <text class="section-title-extra" @click="goCoupon">领券下单</text>
    </view>
    <view class="goods-list">
      <view
        v-for="item in recommendGoodsList"
        :key="item.id"
        class="goods-card card"
        @click="goGoodsDetail(item.id)"
      >
        <view
          class="goods-cover"
          :style="coverStyle(item)"
        >
          <view v-if="!firstBannerUrl(item)" class="cover-placeholder">暂无图</view>
        </view>
        <view class="goods-info">
          <view class="goods-name line-clamp-1">{{ item.name }}</view>
          <view class="goods-desc line-clamp-2">{{ item.characteristic }}</view>
          <view class="goods-tags">
            <text v-for="tag in item.tags" :key="tag" class="goods-tag">{{ tag }}</text>
          </view>
          <view class="goods-footer">
            <view>
              <text class="price-text">￥{{ item.price }}</text>
              <text class="goods-original">￥{{ item.originalPrice }}</text>
            </view>
            <button class="add-button" size="mini" @click.stop="handleAddCart(item.id)">
              加购
            </button>
          </view>
        </view>
      </view>
    </view>
    </view>
  </scroll-view>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { onPullDownRefresh, onShow } from "@dcloudio/uni-app";
import { useMallStore } from "../../store/modules/mall";
import {
  firstBannerUrlFromGoods,
  isImageUrlOrPath,
  goodsImageUrl,
  categoryIconPlaceholder,
} from "../../utils/image";
import { needsSkuPicker } from "../../utils/goodsVariant";

const mallStore = useMallStore();
const isRefreshing = ref(false);

const categoryIconLoadError = ref(new Set<number>());

function isCategoryIconImage(icon: string | undefined): boolean {
  return isImageUrlOrPath(icon);
}

function categoryIconUrl(icon: string | undefined): string {
  return goodsImageUrl(icon);
}

function onCategoryIconError(id: number) {
  categoryIconLoadError.value = new Set([...categoryIconLoadError.value, id]);
}

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

const currentBanner = computed(() => mallStore.bannerList[0] || null);
const recommendGoodsList = computed(() => mallStore.goodsList.slice(0, 4));

const searchPlaceholder = computed(
  () => mallStore.homeDeco?.searchPlaceholder || "搜索蓝莓、牛奶、礼盒"
);

const defaultStats = [
  { value: "30 分钟", label: "同城闪送" },
  { value: "99 元", label: "满额包邮" },
  { value: "48 小时", label: "售后保障" },
];
const statsList = computed(
  () => mallStore.homeDeco?.stats?.length === 3
    ? mallStore.homeDeco!.stats
    : defaultStats
);

const goSearch = () => {
  uni.navigateTo({ url: "/pages/search/index" });
};

const goCategory = () => {
  uni.switchTab({ url: "/pages/category/index" });
};

const goCoupon = () => {
  uni.navigateTo({ url: "/pages/coupon/index" });
};

const goGoodsList = (categoryId: number) => {
  uni.navigateTo({ url: `/pages/goods/list?categoryId=${categoryId}` });
};

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

const refreshHomeData = async () => {
  await mallStore.initMallData();
  uni.setNavigationBarTitle({ title: mallStore.mallName || "元选商城" });
};

const handleRefresh = async () => {
  if (isRefreshing.value) {
    return;
  }
  isRefreshing.value = true;
  try {
    await refreshHomeData();
  } finally {
    isRefreshing.value = false;
  }
};

onPullDownRefresh(() => {
  refreshHomeData().finally(() => {
    uni.stopPullDownRefresh();
  });
});

onShow(() => {
  categoryIconLoadError.value = new Set();
  refreshHomeData();
});
</script>

<style lang="scss" scoped>
.home-scroll {
  height: 100vh;
}

.home-page {
  padding-bottom: 48rpx;
}

.banner {
  padding: 32rpx;
  margin-bottom: 28rpx;
  background: linear-gradient(135deg, #111827, #ef4444);
  color: #ffffff;
}

.banner-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.banner-title {
  font-size: 40rpx;
  font-weight: 700;
}

.banner-subtitle {
  margin-top: 12rpx;
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.82);
}

.banner-tag {
  padding: 8rpx 18rpx;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.16);
  font-size: 22rpx;
}

.search-box {
  margin-top: 28rpx;
  padding: 22rpx 24rpx;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.18);
}

.search-placeholder {
  color: rgba(255, 255, 255, 0.88);
}

.banner-stat {
  display: flex;
  margin-top: 28rpx;
  gap: 16rpx;
}

.stat-item {
  flex: 1;
  padding: 18rpx;
  border-radius: 18rpx;
  background: rgba(255, 255, 255, 0.14);
}

.stat-value {
  display: block;
  font-size: 30rpx;
  font-weight: 700;
}

.stat-label {
  margin-top: 10rpx;
  display: block;
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.82);
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16rpx;
  padding: 24rpx;
  margin-bottom: 28rpx;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10rpx;
  padding: 12rpx 0;
}

.category-icon-img {
  width: 84rpx;
  height: 84rpx;
  border-radius: 24rpx;
  background: #fff1f2;
}

.category-icon {
  width: 84rpx;
  height: 84rpx;
  line-height: 84rpx;
  text-align: center;
  border-radius: 24rpx;
  background: #fff1f2;
  font-size: 40rpx;
}

.category-name {
  font-size: 24rpx;
  color: #4b5563;
}

.goods-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.goods-card {
  display: flex;
  padding: 24rpx;
  gap: 20rpx;
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

.goods-info {
  flex: 1;
}

.goods-name {
  font-size: 30rpx;
  font-weight: 600;
}

.goods-desc {
  margin-top: 12rpx;
  color: #6b7280;
  font-size: 24rpx;
  line-height: 1.5;
}

.goods-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10rpx;
  margin-top: 14rpx;
}

.goods-tag {
  padding: 6rpx 14rpx;
  border-radius: 999rpx;
  background: #fef2f2;
  color: #ef4444;
  font-size: 20rpx;
}

.goods-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 18rpx;
}

.goods-original {
  margin-left: 12rpx;
  color: #9ca3af;
  font-size: 22rpx;
  text-decoration: line-through;
}

.add-button {
  margin: 0;
  border: none;
  border-radius: 999rpx;
  background: #ef4444;
  color: #ffffff;
}
</style>
