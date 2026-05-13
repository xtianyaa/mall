<template>
  <view class="page-container category-page">
    <!-- 左侧分类：固定，不随右侧滚动 -->
    <view class="category-menu-fixed">
      <view class="category-menu">
        <view
          v-for="item in filteredCategoryList"
          :key="item.id"
          class="menu-item"
          :class="{ active: activeCategoryId === item.id }"
          @click="handleMenuClick(item.id)"
        >
          <image
            v-if="isIconImage(item.icon) && !categoryIconLoadError.has(item.id)"
            class="menu-item-icon-img"
            :src="categoryIconUrl(item.icon)"
            mode="aspectFit"
            @error="onCategoryIconError(item.id)"
          />
          <text v-else class="menu-item-icon-emoji">{{ categoryIconPlaceholder(item.icon) }}</text>
          <text class="menu-item-text">{{ item.name }}</text>
        </view>
      </view>
    </view>
    <!-- 右侧内容区：可滚动，留出左侧宽度 -->
    <view class="goods-panel-wrap">
      <view class="goods-panel">
        <view class="search-row">
          <view class="search-box">
            <text class="search-icon">🔍</text>
            <input
              v-model="searchKeyword"
              class="search-input"
              type="text"
              placeholder="搜索分类或商品"
              placeholder-class="search-placeholder"
              confirm-type="search"
              @confirm="onSearchConfirm"
            />
            <text
              v-if="searchKeyword"
              class="search-clear"
              @click.stop="searchKeyword = ''"
            >×</text>
          </view>
        </view>
        <scroll-view
          class="goods-scroll"
          scroll-y
          :scroll-top="scrollTop"
          scroll-with-animation
          lower-threshold="80"
          show-scrollbar="false"
          @scrolltolower="onScrollToLower"
        >
          <!-- 默认只显示当前分类的商品 -->
          <view v-if="currentSection" class="category-section">
            <view class="section-head">
              <text class="section-title">{{ currentSection.category.name }}</text>
              <text class="section-extra">{{ currentSection.goods.length }} 款</text>
            </view>
            <view
              v-for="item in currentSection.goods"
              :key="item.id"
              class="goods-item"
              @click="goGoodsDetail(item.id)"
            >
              <view
                class="goods-emoji"
                :style="coverStyle(item)"
              >
                <view v-if="!firstBannerUrl(item)" class="emoji-placeholder">图</view>
              </view>
              <view class="goods-content">
                <view class="goods-name line-clamp-1">{{ item.name }}</view>
                <view class="goods-desc line-clamp-2">{{ item.characteristic }}</view>
                <view class="goods-bottom">
                  <text class="price-text">￥{{ item.price }}</text>
                  <view class="add-cart-btn" @click.stop="handleAddCart(item.id)">
                    <text class="add-cart-icon">🛒</text>
                  </view>
                </view>
              </view>
            </view>
            <view v-if="currentSection.goods.length === 0" class="section-empty">
              该分类下暂无商品
            </view>
          </view>
          <!-- 当前分类浏览到底后提示上拉，上拉或点击都可切到下一分类 -->
          <view
            v-if="currentSection && hasNextCategory"
            class="load-more-hint"
            @click="goNextCategory"
          >
            <text class="load-more-text">上拉浏览更多</text>
            <text class="load-more-tap">点击也可切换下一分类</text>
          </view>
          <view v-if="!currentSection" class="panel-empty">
            <text v-if="searchKeyword">未找到「{{ searchKeyword }}」相关分类或商品</text>
            <text v-else>暂无分类数据</text>
          </view>
        </scroll-view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref, watch } from "vue";
import { onShow } from "@dcloudio/uni-app";
import { useMallStore } from "../../store/modules/mall";
import type { MallCategory, MallGoods } from "../../types/mall";
import {
  firstBannerUrlFromGoods,
  isImageUrlOrPath,
  goodsImageUrl as buildImageUrl,
  categoryIconPlaceholder,
} from "../../utils/image";
import { needsSkuPicker } from "../../utils/goodsVariant";

function isIconImage(icon: string | undefined): boolean {
  return isImageUrlOrPath(icon);
}

function categoryIconUrl(icon: string | undefined): string {
  return buildImageUrl(icon);
}

const categoryIconLoadError = ref(new Set<number>());
function onCategoryIconError(id: number) {
  categoryIconLoadError.value = new Set([...categoryIconLoadError.value, id]);
}

const mallStore = useMallStore();
const searchKeyword = ref("");
const activeCategoryId = ref<number | null>(null);
/** 用于切换分类时把滚动条拉回顶部 */
const scrollTop = ref(0);

/** 搜索关键词归一化（去首尾空格） */
const searchTrim = computed(() => searchKeyword.value.trim().toLowerCase());

/** 左侧展示的分类：有搜索时 = 名称命中 或 该分类下存在名称/卖点命中的商品 */
const filteredCategoryList = computed(() => {
  const list = mallStore.categoryList;
  if (!searchTrim.value) return list;
  return list.filter((c) => {
    if (c.name.toLowerCase().includes(searchTrim.value)) return true;
    return mallStore.goodsList.some((g) => g.categoryId === c.id && matchGoodsKeyword(g));
  });
});

/** 是否匹配商品（名称或卖点包含关键词） */
function matchGoodsKeyword(goods: MallGoods): boolean {
  if (!searchTrim.value) return true;
  const k = searchTrim.value;
  const nameMatch = goods.name ? goods.name.toLowerCase().includes(k) : false;
  const characteristicMatch = goods.characteristic ? String(goods.characteristic).toLowerCase().includes(k) : false;
  return nameMatch || characteristicMatch;
}

/** 分类名命中关键词时展示该分类下全部商品；否则只展示名称/卖点命中的商品 */
function categoryNameMatchesSearch(category: MallCategory): boolean {
  if (!searchTrim.value) return false;
  return category.name.toLowerCase().includes(searchTrim.value);
}

/** 按分类分组的区块，顺序与左侧一致 */
const categorySections = computed(() => {
  const categories = filteredCategoryList.value;
  const goodsList = mallStore.goodsList;
  return categories.map((category) => {
    const goods = goodsList
      .filter((g) => {
        if (g.categoryId !== category.id) return false;
        if (!searchTrim.value) return true;
        if (categoryNameMatchesSearch(category)) return true;
        return matchGoodsKeyword(g);
      })
      .slice();
    return { category, goods };
  });
});

/** 当前选中的分类对应的一个区块（默认只显示这一块） */
const currentSection = computed(() => {
  if (activeCategoryId.value == null) return null;
  return categorySections.value.find((s) => s.category.id === activeCategoryId.value) ?? null;
});

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

/** 是否有下一分类（用于显示「上拉浏览更多」） */
const hasNextCategory = computed(() => {
  const list = filteredCategoryList.value;
  if (list.length === 0 || activeCategoryId.value == null) return false;
  const idx = list.findIndex((c) => c.id === activeCategoryId.value);
  return idx >= 0 && idx < list.length - 1;
});

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

const goGoodsDetail = (goodsId: number) => {
  uni.navigateTo({ url: `/pages/goods/detail?id=${goodsId}` });
};

/** 左侧点击：切换到该分类，右侧只显示该分类商品并滚回顶部 */
function handleMenuClick(categoryId: number) {
  activeCategoryId.value = categoryId;
  resetScrollToTop();
}

function onSearchConfirm() {
  // 仅确认搜索，列表已由 searchKeyword 响应式更新
}

/** 重置滚动条到顶部（先设非 0 再设 0，确保小程序端生效） */
function resetScrollToTop() {
  scrollTop.value = 0.1;
  setTimeout(() => {
    scrollTop.value = 0;
  }, 50);
}

/** 切到下一分类（上拉触发或点击「上拉浏览更多」时调用） */
function goNextCategory() {
  if (!hasNextCategory.value) return;
  const list = filteredCategoryList.value;
  const idx = list.findIndex((c) => c.id === activeCategoryId.value);
  if (idx < 0 || idx >= list.length - 1) return;
  activeCategoryId.value = list[idx + 1].id;
  resetScrollToTop();
}

/** 滚动到底（看到「上拉浏览更多」后继续上拉）：切到下一分类 */
function onScrollToLower() {
  goNextCategory();
}

/** 初始化或筛选变化时，若当前选中不在左侧列表中则选中第一个 */
watch(
  filteredCategoryList,
  (list) => {
    if (list.length === 0) {
      activeCategoryId.value = null;
      return;
    }
    const hasActive = list.some((c) => c.id === activeCategoryId.value);
    if (!hasActive) activeCategoryId.value = list[0].id;
  },
  { immediate: true }
);

onShow(() => {
  categoryIconLoadError.value = new Set();
  mallStore.initMallData();
  if (mallStore.categoryList.length && activeCategoryId.value === null) {
    activeCategoryId.value = mallStore.categoryList[0].id;
  }
});
</script>

<style lang="scss" scoped>
.category-page {
  min-height: 100vh;
  padding-bottom: 48rpx;
}

/* 左侧分类固定，不随页面/右侧滚动 */
.category-menu-fixed {
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  width: 200rpx;
  z-index: 10;
  background: #f9fafb;
}

.category-menu {
  height: 100%;
  overflow-y: auto;
}

.menu-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10rpx;
  padding: 28rpx 12rpx;
  color: #6b7280;
}

.menu-item-icon-img {
  width: 84rpx;
  height: 84rpx;
  border-radius: 16rpx;
  background: #f3f4f6;
}

.menu-item-icon-emoji {
  font-size: 48rpx;
  line-height: 1;
}

.menu-item-text {
  font-size: 24rpx;
  text-align: center;
  word-break: break-all;
}

.menu-item.active {
  background: #ffffff;
  color: #ef4444;
  font-weight: 600;
}

/* 右侧区域留出左侧 200rpx，仅此区域可滚动 */
.goods-panel-wrap {
  margin-left: 200rpx;
  height: calc(100vh - 48rpx);
  display: flex;
  flex-direction: column;
}

.goods-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  min-height: 0;
  padding: 0 0 24rpx 0;
}

.search-row {
  padding: 20rpx 24rpx 16rpx;
  flex-shrink: 0;
}

.search-box {
  display: flex;
  align-items: center;
  height: 64rpx;
  padding: 0 20rpx;
  background: #f3f4f6;
  border-radius: 999rpx;
}

.search-icon {
  font-family: iconfont;
  font-size: 28rpx;
  color: #9ca3af;
  margin-right: 12rpx;
}

.search-input {
  flex: 1;
  font-size: 26rpx;
  color: #111827;
}

.search-placeholder {
  color: #9ca3af;
}

.search-clear {
  padding: 0 8rpx;
  font-size: 36rpx;
  color: #9ca3af;
  line-height: 1;
}

.goods-scroll {
  flex: 1;
  height: 0;
}

.category-section {
  padding: 0 24rpx 32rpx;
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16rpx;
  padding: 16rpx 0 8rpx;
  border-bottom: 2rpx solid #f3f4f6;
}

.section-title {
  font-size: 30rpx;
  font-weight: 700;
  color: #111827;
}

.section-extra {
  font-size: 22rpx;
  color: #9ca3af;
}

.goods-item {
  display: flex;
  gap: 16rpx;
  padding: 18rpx 0;
  border-bottom: 1rpx solid #f3f4f6;
}

.goods-emoji {
  width: 96rpx;
  height: 96rpx;
  border-radius: 24rpx;
  background: #f3f4f6;
  overflow: hidden;
  flex-shrink: 0;
}

.emoji-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #9ca3af;
  font-size: 28rpx;
}

.goods-content {
  flex: 1;
  min-width: 0;
}

.goods-name {
  font-size: 28rpx;
  font-weight: 600;
}

.goods-desc {
  margin-top: 10rpx;
  color: #6b7280;
  font-size: 23rpx;
  line-height: 1.5;
}

.goods-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 12rpx;
}

.go-link {
  color: #ef4444;
  font-size: 22rpx;
}

.add-cart-btn {
  width: 52rpx;
  height: 52rpx;
  border-radius: 26rpx;
  background: #111827;
  display: flex;
  align-items: center;
  justify-content: center;
}

.add-cart-icon {
  font-size: 30rpx;
  color: #ffffff;
}

.section-empty {
  padding: 24rpx 0;
  text-align: center;
  color: #9ca3af;
  font-size: 24rpx;
}

/* 当前分类商品结束后、下一分类前的提示 */
.load-more-hint {
  padding: 32rpx 24rpx;
  text-align: center;
  background: #f9fafb;
}

.load-more-text {
  display: block;
  font-size: 24rpx;
  color: #9ca3af;
}

.load-more-tap {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  color: #d1d5db;
}

.panel-empty {
  padding: 80rpx 24rpx;
  text-align: center;
  color: #9ca3af;
  font-size: 26rpx;
}
</style>
