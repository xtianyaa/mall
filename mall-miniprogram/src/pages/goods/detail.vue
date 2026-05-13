<template>
  <view v-if="goodsDetail" class="goods-detail-page">
    <!-- 顶部导航 + 分段标签 -->
    <view class="page-header">
      <view class="page-header-left" @click="handleBack">
        <text class="iconfont">&#xe600;</text>
      </view>
      <view class="page-header-center">
        <view class="header-tabs">
          <view
            v-for="tab in tabs"
            :key="tab.key"
            class="header-tab-item"
            :class="{ 'is-active': activeTab === tab.key }"
            @click="handleTabClick(tab.key)"
          >
            <text class="header-tab-text">{{ tab.label }}</text>
          </view>
        </view>
      </view>
      <view class="page-header-right">
        <text class="icon-dot"></text>
      </view>
    </view>

    <scroll-view
      class="detail-scroll"
      scroll-y
      show-scrollbar="false"
      scroll-with-animation
      :scroll-into-view="scrollIntoViewId"
      @scroll="handleDetailScroll"
    >
      <view class="detail-scroll-content">
        <view class="hero">
          <swiper
            v-if="bannerImageList.length"
            class="hero-swiper"
            indicator-dots
            indicator-color="rgba(255,255,255,0.4)"
            indicator-active-color="#fff"
            autoplay
            circular
          >
            <swiper-item v-for="(url, idx) in bannerImageList" :key="idx">
              <!-- 真机 scroll-view 内 swiper+image 常空白，与详情图一致用背景图 -->
              <view class="hero-slide" :style="heroBannerStyle(url)" />
            </swiper-item>
          </swiper>
          <view v-else class="hero-placeholder">暂无商品图</view>
        </view>

        <view class="page-container detail-body">
          <view id="section-goods" class="detail-section">
            <view class="card summary-card">
              <view class="price-row">
                <view>
                  <text class="price-text price-large">￥{{ selectedPrice }}</text>
                  <text class="origin-price">￥{{ selectedOriginalPrice }}</text>
                </view>
                <text class="sold-text">月销 {{ goodsDetail.monthlySales }}</text>
              </view>

              <view v-if="showSkuSummaryRow" class="sku-summary-row" @click="openSkuSheet">
                <view class="sku-summary-left">
                  <text class="sku-summary-label">已选</text>
                  <text class="sku-summary-val">{{ selectedSummaryText }}</text>
                </view>
                <text class="sku-summary-arrow">›</text>
              </view>

              <view class="goods-title">{{ goodsDetail.name }}</view>
              <view class="goods-desc">{{ goodsDetail.characteristic }}</view>
              <view v-if="goodsDetail.tags?.length" class="tag-list">
                <text v-for="tag in goodsDetail.tags" :key="tag" class="tag-item">{{ tag }}</text>
              </view>
            </view>

            <view class="card meta-card">
              <view class="section-title">
                <text class="section-title-text">商品信息</text>
              </view>
              <view class="meta-row">
                <text class="meta-label">商品分类</text>
                <text class="meta-value">{{ goodsDetail.categoryName || "应季鲜果" }}</text>
              </view>
              <view class="meta-row">
                <text class="meta-label">计量单位</text>
                <text class="meta-value">{{ goodsDetail.unit }}</text>
              </view>
              <view class="meta-row">
                <text class="meta-label">发货时效</text>
                <text class="meta-value">当日/次日发货（节假日顺延）</text>
              </view>
              <view class="meta-row">
                <text class="meta-label">库存情况</text>
                <text class="meta-value">
                  {{ selectedStock > 50 ? "库存充足" : selectedStock > 0 ? "库存紧张" : "暂时缺货" }}
                </text>
              </view>
            </view>
          </view>

          <view id="section-detail" class="detail-section">
            <view class="card info-card">
              <view class="section-title">
                <text class="section-title-text">图文详情</text>
              </view>
              <view v-if="detailBlocks.length" class="detail-rich">
                <view v-for="(block, idx) in detailBlocks" :key="idx" class="detail-block">
                  <!--
                    真机 scroll-view 内嵌套 image（含绝对定位）常不绘制，仅见灰底；
                    与「为你推荐」一致：view + background-image，真机可正常显示。
                  -->
                  <view
                    v-if="block.type === 'image'"
                    class="detail-image-cover"
                    :style="detailImageCoverStyle(block.value)"
                  />
                  <view v-else class="detail-text">{{ block.value }}</view>
                </view>
              </view>
              <view v-else class="recommend-empty">暂无图文详情</view>
            </view>
          </view>

          <view id="section-recommend" class="detail-section">
            <view class="card info-card">
              <view class="section-title">
                <text class="section-title-text">为你推荐</text>
              </view>
              <view
                v-if="recommendGoodsList.length"
                class="recommend-list"
              >
                <view
                  v-for="item in recommendGoodsList"
                  :key="item.id"
                  class="recommend-item"
                  @click="goGoodsDetail(item.id)"
                >
                  <view
                    class="recommend-thumb"
                    :style="recommendThumbStyle(item)"
                  />
                  <view class="recommend-info">
                    <text class="recommend-name">{{ item.name }}</text>
                    <text class="recommend-desc">{{ item.characteristic }}</text>
                    <view class="recommend-meta">
                      <text class="recommend-price">￥{{ item.price }}</text>
                      <text class="recommend-sales">月销 {{ item.monthlySales }}</text>
                    </view>
                  </view>
                </view>
              </view>
              <view v-else class="recommend-empty">暂无更多推荐商品</view>
            </view>
          </view>

          <view class="bottom-safe-spacer" />
        </view>
      </view>
    </scroll-view>

    <view class="bottom-bar">
      <view class="bottom-left">
        <view class="bottom-nav-item" @click="goMy">
          <text class="bottom-nav-text">我的</text>
        </view>
        <view class="bottom-nav-item" @click="goCart">
          <text class="bottom-nav-text">购物车</text>
        </view>
        <view class="bottom-nav-item" @click="goHome">
          <text class="bottom-nav-text">首页</text>
        </view>
      </view>
      <view class="bottom-right">
        <button class="bottom-btn bottom-cart-btn" :disabled="selectedStock <= 0" @click="openSkuSheet">
          加入购物车
        </button>
        <button class="bottom-btn bottom-buy-btn" :disabled="selectedStock <= 0" @click="openSkuSheet">
          立即购买
        </button>
      </view>
    </view>

    <GoodsSkuSheet
      v-model:show="skuSheetVisible"
      :goods="goodsDetail"
      :initial-variant-id="selectedVariantId"
      @add-cart="onSkuAddCart"
      @buy="onSkuBuy"
    />
  </view>
</template>

<script setup lang="ts">
import { computed, nextTick, ref } from "vue";
import { onLoad } from "@dcloudio/uni-app";
import GoodsSkuSheet from "../../components/goods-sku-sheet/goods-sku-sheet.vue";
import { useMallStore } from "../../store/modules/mall";
import type { MallGoods } from "../../types/mall";
import { getRealVariants } from "../../utils/goodsVariant";
import { goodsImageUrl, isImageUrlOrPath } from "../../utils/image";

const mallStore = useMallStore();
const goodsDetail = ref<MallGoods | null>(null);

const selectedVariantId = ref<number | null>(null);

const skuSheetVisible = ref(false);

const realVariantList = computed(() => getRealVariants(goodsDetail.value?.variants));

const selectedVariant = computed(() => {
  const g = goodsDetail.value;
  const list = realVariantList.value;
  if (!g) return null;
  if (list.length === 0) return null;
  if (selectedVariantId.value != null) {
    return list.find((v) => v.id === selectedVariantId.value) || list[0];
  }
  const defId = g.defaultVariantId;
  if (defId != null) return list.find((v) => v.id === defId) || list[0];
  return list[0];
});

const showSkuSummaryRow = computed(() => !!goodsDetail.value);

const selectedSummaryText = computed(() => {
  const g = goodsDetail.value;
  if (!g) return "";
  const list = realVariantList.value;
  if (list.length === 0) return "默认";
  const v = selectedVariant.value;
  if (!v) return "请选择规格";
  return Object.values(v.specs || {}).filter(Boolean).join(" · ") || "规格";
});

const selectedPrice = computed(() => selectedVariant.value?.price ?? goodsDetail.value?.price ?? 0);
const selectedOriginalPrice = computed(
  () => selectedVariant.value?.originalPrice ?? goodsDetail.value?.originalPrice ?? 0
);
const selectedStock = computed(() => selectedVariant.value?.stock ?? goodsDetail.value?.stock ?? 0);

const tabs = [
  { key: "goods", label: "商品" },
  { key: "detail", label: "详情" },
  { key: "recommend", label: "推荐" },
] as const;
type TabKey = (typeof tabs)[number]["key"];
const activeTab = ref<TabKey>("goods");
const scrollIntoViewId = ref("");

const sectionTops = ref<number[]>([0, 0, 0]);

const handleTabClick = (tabKey: TabKey) => {
  activeTab.value = tabKey;
  scrollIntoViewId.value = "";
  nextTick(() => {
    scrollIntoViewId.value = `section-${tabKey}`;
  });
};

const getHeaderHeightPx = () => {
  try {
    const win = uni.getWindowInfo();
    const windowWidth = win?.windowWidth ?? 375;
    return Math.ceil((120 * windowWidth) / 750);
  } catch {
    return 60;
  }
};

const updateActiveTabByScroll = (scrollTop: number) => {
  const tops = sectionTops.value;
  if (tops[1] <= 0 && tops[2] <= 0) return;
  const headerHeight = getHeaderHeightPx();
  const threshold = scrollTop + headerHeight;
  if (threshold < tops[1]) {
    activeTab.value = "goods";
  } else if (threshold < tops[2]) {
    activeTab.value = "detail";
  } else {
    activeTab.value = "recommend";
  }
};

const refreshSectionTops = () => {
  const query = uni.createSelectorQuery();
  query
    .select(".detail-scroll")
    .boundingClientRect()
    .select("#section-goods")
    .boundingClientRect()
    .select("#section-detail")
    .boundingClientRect()
    .select("#section-recommend")
    .boundingClientRect()
    .exec((res) => {
      if (!res || res.length < 4) return;
      const scrollRect = res[0] as { top: number } | null;
      const rects = [res[1], res[2], res[3]] as Array<{ top: number } | null>;
      if (!scrollRect) return;
      sectionTops.value = rects.map((r) => (r ? Math.max(r.top - scrollRect.top, 0) : 0));
    });
};

const bannerImageList = computed(() => {
  const list = goodsDetail.value?.bannerList || [];
  return list.map((url) => goodsImageUrl(url)).filter(Boolean);
});

const heroBannerStyle = (fullUrl: string) => {
  const safe = fullUrl ? fullUrl.replace(/\\/g, "/").replace(/"/g, "%22") : "";
  return {
    width: "100%",
    height: "420rpx",
    backgroundColor: "#f3f4f6",
    backgroundImage: safe ? `url("${safe}")` : "none",
    backgroundSize: "cover",
    backgroundRepeat: "no-repeat",
    backgroundPosition: "center center",
  };
};

const detailBlocks = computed(() => {
  const goods = goodsDetail.value;
  if (!goods) return [];
  const rich = goods.detailRichList || [];
  if (rich.length) return rich;
  const raw = goods.detailList || [];
  return raw
    .map((value) => String(value || "").trim())
    .filter(Boolean)
    .map((value) => ({
      type: isImageUrlOrPath(value) ? ("image" as const) : ("text" as const),
      value,
    }));
});

/** 详情图宽高比 (高/宽)*100，用于 padding-top 占位，避免 scroll-view+widthFix 真机高度塌陷 */
const detailAspectMap = ref<Record<string, number>>({});

const detailImgAspectPercent = (path: string) => detailAspectMap.value[path] ?? 56.25;

/** 详情图：背景图展示（避免真机 image 在 scroll-view 内不渲染） */
const detailImageCoverStyle = (path: string) => {
  const full = goodsImageUrl(path);
  const pct = detailImgAspectPercent(path);
  const safe = full ? full.replace(/\\/g, "/").replace(/"/g, "%22") : "";
  return {
    width: "100%",
    height: 0,
    paddingTop: `${pct}%`,
    backgroundColor: "#f3f4f6",
    backgroundImage: safe ? `url("${safe}")` : "none",
    backgroundSize: "100% 100%",
    backgroundRepeat: "no-repeat",
    backgroundPosition: "center center",
    borderRadius: "18rpx",
    overflow: "hidden",
  };
};

const prefetchDetailImageAspects = () => {
  const next: Record<string, number> = { ...detailAspectMap.value };
  for (const block of detailBlocks.value) {
    if (block.type !== "image") continue;
    const src = goodsImageUrl(block.value);
    if (!src) continue;
    const key = block.value;
    if (next[key]) continue;
    uni.getImageInfo({
      src,
      success: (res) => {
        const w = res.width || 1;
        const h = res.height || 1;
        detailAspectMap.value = { ...detailAspectMap.value, [key]: (h / w) * 100 };
      },
      fail: () => {
        detailAspectMap.value = { ...detailAspectMap.value, [key]: 56.25 };
      },
    });
  }
};

const noticeTextList = computed(() =>
  detailBlocks.value
    .filter((item) => item.type === "text")
    .map((item) => item.value)
    .slice(0, 3)
);

const recommendGoodsList = computed<MallGoods[]>(() => {
  if (!goodsDetail.value) return [];
  return mallStore.goodsList
    .filter(
      (item) =>
        item.categoryId === goodsDetail.value!.categoryId &&
        item.id !== goodsDetail.value!.id
    )
    .slice(0, 6);
});

const handleBack = () => {
  const pages = getCurrentPages();
  if (pages.length > 1) {
    uni.navigateBack();
  } else {
    uni.switchTab({ url: "/pages/index/index" });
  }
};

const goHome = () => {
  uni.switchTab({ url: "/pages/index/index" });
};

const goCart = () => {
  uni.switchTab({ url: "/pages/cart/index" });
};

const goMy = () => {
  uni.switchTab({ url: "/pages/my/index" });
};

const goGoodsDetail = (id: number) => {
  uni.navigateTo({ url: `/pages/goods/detail?id=${id}` });
};

const recommendThumbStyle = (item: MallGoods) => {
  const url = (item.bannerList && item.bannerList[0]) || "";
  const fullUrl = url ? goodsImageUrl(url) : "";
  return {
    backgroundImage: fullUrl ? `url(${fullUrl})` : "none",
    backgroundSize: "cover",
    backgroundPosition: "center",
  };
};

const openSkuSheet = () => {
  if (!goodsDetail.value || selectedStock.value <= 0) {
    uni.showToast({ title: "暂时缺货", icon: "none" });
    return;
  }
  skuSheetVisible.value = true;
};

const onSkuAddCart = async (payload: { variantId: number | null; quantity: number }) => {
  if (!goodsDetail.value) return;
  selectedVariantId.value = payload.variantId;
  const result = await mallStore.addCart(goodsDetail.value.id, payload.quantity, payload.variantId);
  skuSheetVisible.value = false;
  uni.showToast({
    title: result.message,
    icon: result.success ? "success" : "none",
  });
};

const onSkuBuy = async (payload: { variantId: number | null; quantity: number }) => {
  if (!goodsDetail.value) return;
  selectedVariantId.value = payload.variantId;
  const result = await mallStore.addCart(goodsDetail.value.id, payload.quantity, payload.variantId);
  skuSheetVisible.value = false;
  if (!result.success) {
    uni.showToast({ title: result.message, icon: "none" });
    return;
  }
  uni.navigateTo({ url: "/pages/checkout/index" });
};

const handleDetailScroll = (event: { detail: { scrollTop: number } }) => {
  updateActiveTabByScroll(event.detail.scrollTop);
};

onLoad(async (options) => {
  const id = Number(options?.id || 0);
  goodsDetail.value = await mallStore.fetchGoodsDetail(id);
  const g = goodsDetail.value;
  const rv = getRealVariants(g?.variants);
  if (rv.length === 0) {
    selectedVariantId.value = null;
  } else {
    selectedVariantId.value = g?.defaultVariantId ?? rv[0]?.id ?? null;
  }
  if (options?.openSku === "1") {
    await nextTick();
    skuSheetVisible.value = true;
  }
  if (!mallStore.goodsList.length && goodsDetail.value?.categoryId) {
    await mallStore.fetchGoodsListData({ categoryId: goodsDetail.value.categoryId });
  }
  await nextTick();
  prefetchDetailImageAspects();
  setTimeout(refreshSectionTops, 100);
  setTimeout(refreshSectionTops, 500);
});
</script>

<style lang="scss" scoped>
.goods-detail-page {
  height: 100vh;
  background: #f6f7fb;
  overflow: hidden;
}

.detail-scroll {
  height: 100vh;
}

.detail-scroll-content {
  min-height: 100%;
  padding-top: 120rpx;
  padding-bottom: calc(180rpx + constant(safe-area-inset-bottom));
  padding-bottom: calc(180rpx + env(safe-area-inset-bottom));
}

.page-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 120rpx;
  padding: 0 24rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #ffffff;
  z-index: 100;
}

.page-header-left,
.page-header-right {
  width: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.page-header-center {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.header-tabs {
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-tab-item {
  padding: 0 26rpx;
  height: 56rpx;
  line-height: 56rpx;
  position: relative;
}

.header-tab-text {
  font-size: 26rpx;
  color: #6b7280;
}

.header-tab-item.is-active .header-tab-text {
  color: #111827;
  font-weight: 600;
}

.header-tab-item.is-active::after {
  content: "";
  position: absolute;
  left: 50%;
  bottom: 4rpx;
  width: 36rpx;
  height: 4rpx;
  border-radius: 999rpx;
  background: #fbbf24;
  transform: translateX(-50%);
}

.icon-dot {
  width: 10rpx;
  height: 10rpx;
  border-radius: 50%;
  background: #111827;
}

.hero {
  height: 420rpx;
  background: #f3f4f6;
  overflow: hidden;
}

.hero-swiper {
  width: 100%;
  height: 100%;
}

.hero-slide {
  display: block;
}

.hero-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #9ca3af;
  font-size: 28rpx;
}

.detail-body {
  margin-top: -40rpx;
  position: relative;
  z-index: 2;
}

.detail-section {
  min-height: 1px;
}

.bottom-safe-spacer {
  height: calc(130rpx + constant(safe-area-inset-bottom));
  height: calc(130rpx + env(safe-area-inset-bottom));
}

.summary-card,
.info-card,
.meta-card,
.action-card {
  padding: 28rpx;
  margin-bottom: 22rpx;
}

.price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sku-summary-row {
  margin-top: 20rpx;
  padding: 20rpx 18rpx;
  border-radius: 16rpx;
  background: #f9fafb;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.sku-summary-left {
  flex: 1;
  min-width: 0;
  display: flex;
  align-items: flex-start;
  gap: 12rpx;
}

.sku-summary-label {
  font-size: 24rpx;
  color: #6b7280;
  flex-shrink: 0;
}

.sku-summary-val {
  font-size: 26rpx;
  color: #111827;
  font-weight: 600;
  line-height: 1.45;
  word-break: break-all;
}

.sku-summary-arrow {
  font-size: 36rpx;
  color: #d1d5db;
  line-height: 1;
  flex-shrink: 0;
}

.price-large {
  font-size: 46rpx;
}

.origin-price {
  margin-left: 12rpx;
  color: #9ca3af;
  text-decoration: line-through;
}

.sold-text,
.goods-desc,
.info-item {
  color: #6b7280;
}

.goods-title {
  margin-top: 20rpx;
  font-size: 36rpx;
  font-weight: 700;
}

.goods-desc {
  margin-top: 14rpx;
  line-height: 1.6;
}

.tag-list {
  display: flex;
  gap: 12rpx;
  flex-wrap: wrap;
  margin-top: 18rpx;
}

.tag-item {
  padding: 8rpx 16rpx;
  border-radius: 999rpx;
  background: #fef2f2;
  color: #ef4444;
  font-size: 22rpx;
}

.meta-card {
  font-size: 26rpx;
}

.meta-row {
  display: flex;
  justify-content: space-between;
  margin-top: 12rpx;
}

.meta-label {
  color: #6b7280;
}

.meta-value {
  color: #111827;
}

.section-title {
  margin-bottom: 12rpx;
}

.section-title-text {
  font-size: 30rpx;
  font-weight: 600;
}

.detail-rich {
  display: flex;
  flex-direction: column;
  gap: 18rpx;
}

.detail-block {
  width: 100%;
}

.detail-image-cover {
  box-sizing: border-box;
}

.detail-text {
  font-size: 26rpx;
  line-height: 1.7;
  color: #374151;
}

.recommend-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.recommend-item {
  display: flex;
  padding: 16rpx 0;
}

.recommend-thumb {
  width: 168rpx;
  height: 168rpx;
  border-radius: 24rpx;
  background: #f3f4f6;
  margin-right: 24rpx;
}

.recommend-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.recommend-name {
  font-size: 28rpx;
  font-weight: 600;
}

.recommend-desc {
  margin-top: 8rpx;
  font-size: 24rpx;
  color: #6b7280;
}

.recommend-meta {
  margin-top: 12rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.recommend-price {
  color: #ef4444;
  font-weight: 600;
}

.recommend-sales {
  font-size: 24rpx;
  color: #9ca3af;
}

.recommend-empty {
  padding: 28rpx 0;
  text-align: center;
  color: #9ca3af;
  font-size: 26rpx;
}

.bottom-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 200;
  padding: 10rpx 20rpx 26rpx;
  padding-bottom: calc(26rpx + constant(safe-area-inset-bottom));
  padding-bottom: calc(26rpx + env(safe-area-inset-bottom));
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #ffffff;
  box-shadow: 0 -6rpx 18rpx rgba(15, 23, 42, 0.06);
}

.bottom-left {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.bottom-nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  font-size: 22rpx;
  color: #6b7280;
}

.bottom-nav-text {
  margin-top: 4rpx;
}

.bottom-right {
  flex: 1;
  display: flex;
  gap: 14rpx;
  margin-left: 24rpx;
}

.bottom-btn {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  border-radius: 999rpx;
  border: none;
  font-size: 26rpx;
}

.bottom-cart-btn {
  background: #111827;
  color: #ffffff;
}

.bottom-buy-btn {
  background: #f97316;
  color: #ffffff;
}

.quantity-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stepper {
  display: flex;
  align-items: center;
  gap: 18rpx;
}

.stepper-btn,
.stepper-value {
  width: 52rpx;
  height: 52rpx;
  line-height: 52rpx;
  text-align: center;
  border-radius: 14rpx;
  background: #f3f4f6;
}

.action-buttons {
  display: flex;
  gap: 16rpx;
  margin-top: 24rpx;
}

.cart-button,
.buy-button {
  flex: 1;
  height: 84rpx;
  line-height: 84rpx;
  border-radius: 999rpx;
  border: none;
}

.cart-button {
  background: #111827;
  color: #ffffff;
}

.buy-button {
  background: #ef4444;
  color: #ffffff;
}
</style>