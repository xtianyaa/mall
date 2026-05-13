<script setup lang="ts">
import { onLaunch } from "@dcloudio/uni-app";
import { useMallStore } from "./store/modules/mall";

onLaunch(() => {
  const mallStore = useMallStore();
  mallStore.initMallData().catch((error) => {
    console.error("商城初始化失败", error);
  });
});
</script>

<style lang="scss">
page {
  background: #f6f7fb;
  color: #1f2937;
  font-size: 28rpx;
  -ms-overflow-style: none;
  scrollbar-width: none;
}

uni-page-body {
  -ms-overflow-style: none;
  scrollbar-width: none;
}

::-webkit-scrollbar {
  display: none !important;
  width: 0 !important;
  height: 0 !important;
  color: transparent !important;
}

page::-webkit-scrollbar,
uni-page-body::-webkit-scrollbar,
scroll-view::-webkit-scrollbar {
  display: none !important;
  width: 0 !important;
  height: 0 !important;
  background: transparent !important;
}

view,
text,
button,
input,
scroll-view,
swiper,
swiper-item {
  box-sizing: border-box;
}

.page-container {
  min-height: 100vh;
  padding: 24rpx;
}

.card {
  background: #ffffff;
  border-radius: 24rpx;
  box-shadow: 0 12rpx 40rpx rgba(15, 23, 42, 0.05);
}

.section-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24rpx;
}

.section-title-text {
  font-size: 34rpx;
  font-weight: 700;
  color: #111827;
}

.section-title-extra {
  color: #fb7185;
  font-size: 24rpx;
}

.price-text {
  color: #ef4444;
  font-weight: 700;
}

.line-clamp-1 {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.line-clamp-2 {
  display: -webkit-box;
  overflow: hidden;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}
</style>
