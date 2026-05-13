<template>
  <scroll-view class="search-scroll" scroll-y show-scrollbar="false">
    <view class="page-container">
      <view class="card search-card">
        <input
          v-model="keyword"
          class="search-input"
          placeholder="输入商品名称或卖点"
          confirm-type="search"
          @confirm="handleInputConfirm"
        />
        <button class="search-button" @click="handleInputConfirm">搜索</button>
      </view>

      <view class="section-title">
        <text class="section-title-text">热门搜索</text>
      </view>
      <view class="hot-list">
        <text
          v-for="item in hotKeywordList"
          :key="item"
          class="hot-item"
          @click="handleQuickSearch(item)"
        >
          {{ item }}
        </text>
      </view>

      <view class="section-title result-title">
        <text class="section-title-text">搜索预览</text>
        <text class="section-title-extra">{{ resultList.length }} 条结果</text>
      </view>

      <view v-if="resultList.length" class="result-list">
        <view
          v-for="item in resultList"
          :key="item.id"
          class="card result-item"
          @click="goGoodsDetail(item.id)"
        >
          <view
            class="result-cover"
            :style="coverStyle(item)"
          >
            <view v-if="!firstBannerUrl(item)" class="cover-placeholder">暂无图</view>
          </view>
          <view class="result-content">
            <view class="line-clamp-1 result-name">{{ item.name }}</view>
            <view class="line-clamp-2 result-desc">{{ item.characteristic }}</view>
            <view class="result-footer">
              <text class="price-text">￥{{ item.price }}</text>
              <text class="result-sales">月销 {{ item.monthlySales }}</text>
            </view>
          </view>
        </view>
      </view>

      <view v-else class="card empty-card">
        <text>输入关键词后可预览真实商品结果。</text>
      </view>
    </view>
  </scroll-view>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { getGoodsList } from "../../api/yuan-mall-module";
import type { MallGoods } from "../../types/mall";
import { firstBannerUrlFromGoods } from "../../utils/image";

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
const hotKeywordList = ["蓝莓", "脐橙", "礼盒", "牛奶", "紫薯脆片"];
const resultList = ref<MallGoods[]>([]);

const goSearchResult = () => {
  if (!keyword.value.trim()) {
    uni.showToast({
      title: "请输入搜索关键词",
      icon: "none",
    });
    return;
  }
  uni.navigateTo({
    url: `/pages/goods/list?keyword=${encodeURIComponent(keyword.value.trim())}`,
  });
};

const loadSearchPreview = async (value: string) => {
  if (!value.trim()) {
    resultList.value = [];
    return;
  }
  resultList.value = await getGoodsList({ keyword: value.trim() });
};

const handleQuickSearch = async (value: string) => {
  keyword.value = value;
  await loadSearchPreview(value);
  goSearchResult();
};

const handleInputConfirm = async () => {
  await loadSearchPreview(keyword.value);
  if (!resultList.value.length) {
    uni.showToast({
      title: "未搜索到相关商品",
      icon: "none",
    });
    return;
  }
  goSearchResult();
};

const goGoodsDetail = (id: number) => {
  uni.navigateTo({ url: `/pages/goods/detail?id=${id}` });
};
</script>

<style lang="scss" scoped>
.search-scroll {
  height: 100vh;
}

.search-card {
  display: flex;
  gap: 16rpx;
  padding: 24rpx;
}

.search-input {
  flex: 1;
  height: 84rpx;
  padding: 0 24rpx;
  border-radius: 18rpx;
  background: #f3f4f6;
}

.search-button {
  margin: 0;
  width: 160rpx;
  height: 84rpx;
  line-height: 84rpx;
  border-radius: 18rpx;
  border: none;
  background: #ef4444;
  color: #ffffff;
}

.hot-list {
  display: flex;
  flex-wrap: wrap;
  gap: 18rpx;
}

.result-title {
  margin-top: 32rpx;
}

.result-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.result-item {
  display: flex;
  gap: 18rpx;
  padding: 20rpx;
}

.result-cover {
  width: 120rpx;
  height: 120rpx;
  border-radius: 20rpx;
  background: #f3f4f6;
  overflow: hidden;
  flex-shrink: 0;
}

.result-cover .cover-img {
  width: 100%;
  height: 100%;
}

.result-cover .cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #9ca3af;
  font-size: 22rpx;
}

.result-content {
  flex: 1;
}

.result-name {
  font-size: 28rpx;
  font-weight: 600;
}

.result-desc {
  margin-top: 10rpx;
  color: #6b7280;
  font-size: 24rpx;
}

.result-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 14rpx;
}

.result-sales {
  color: #9ca3af;
  font-size: 22rpx;
}

.hot-item {
  padding: 14rpx 20rpx;
  border-radius: 999rpx;
  background: #ffffff;
  color: #4b5563;
}

.empty-card {
  margin-top: 12rpx;
  padding: 48rpx 24rpx;
  text-align: center;
  color: #6b7280;
}
</style>
