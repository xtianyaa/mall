<template>
  <view v-if="show" class="sku-mask" @click="handleMask">
    <view class="sku-panel" @click.stop>
      <view class="sku-close" @click="close">
        <text class="sku-close-x">×</text>
      </view>

      <view class="sku-head">
        <view class="sku-thumb" :style="thumbStyle" />
        <view class="sku-head-info">
          <view class="sku-price-row">
            <text class="sku-price-label">销售价</text>
            <text class="sku-price-num">￥{{ displayPrice }}</text>
          </view>
          <text class="sku-stock">库存 {{ displayStock }} 件</text>
          <text v-if="hintText" class="sku-hint">{{ hintText }}</text>
        </view>
      </view>

      <scroll-view class="sku-body" scroll-y :show-scrollbar="false">

        <!-- 单 SKU 模式：直接展示规格文案 -->
        <view v-if="plan.mode === 'single'" class="sku-block">
          <text class="sku-dim-name">{{ plan.specNames[0] || '规格' }}</text>
          <text class="sku-single-line">{{ singleVariantLine }}</text>
        </view>

        <!-- 2维矩阵模式：先选 dim1，再在 dim1 选中后展示 dim2 -->
        <template v-if="plan.mode === 'matrix'">
          <view
            v-for="(dim, dimIdx) in filledDims"
            :key="dim.name"
            class="sku-block"
          >
            <text class="sku-dim-name">{{ dim.name }}</text>
            <view v-if="dimIdx === 1 && !selectedSpecs[plan.specNames[0]]" class="sku-size-tip">
              请先选择{{ plan.specNames[0] }}
            </view>
            <view v-else class="sku-chips">
              <view
                v-for="chip in dim.chips"
                :key="chip.value"
                class="sku-chip"
                :class="{
                  'is-on': selectedSpecs[dim.name] === chip.value,
                  'is-disabled': chip.disabled,
                }"
                @click="onPickChip(dim.name, chip.value)"
              >
                <text class="sku-chip-text">{{ chip.display }}</text>
              </view>
            </view>
          </view>
        </template>

        <!-- 平铺模式（1维或3+维）：每维度一行 chip -->
        <view
          v-if="plan.mode === 'flat'"
          v-for="dim in filledDims"
          :key="dim.name"
          class="sku-block"
        >
          <text class="sku-dim-name">{{ dim.name }}</text>
          <view class="sku-chips">
            <view
              v-for="chip in dim.chips"
              :key="chip.value"
              class="sku-chip"
              :class="{
                'is-on': selectedSpecs[dim.name] === chip.value,
                'is-disabled': chip.disabled,
              }"
              @click="onPickChip(dim.name, chip.value)"
            >
              <text class="sku-chip-text">{{ chip.display }}</text>
            </view>
          </view>
        </view>

        <view class="sku-qty-row">
          <text class="sku-dim-name">购买数量</text>
          <view class="sku-stepper">
            <view class="sku-step-btn" @click="changeQty(-1)">－</view>
            <input
              class="sku-step-input"
              type="number"
              :value="quantity"
              @input="onQtyInput"
            />
            <view class="sku-step-btn" @click="changeQty(1)">＋</view>
          </view>
        </view>
      </scroll-view>

      <view class="sku-footer safe-bottom">
        <button class="sku-btn sku-btn-cart" @click="emitAddCart">加入购物车</button>
        <button class="sku-btn sku-btn-buy" @click="emitBuy">立即购买</button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref, watch } from "vue";
import type { MallGoods, MallGoodsVariant } from "../../types/mall";
import { goodsImageUrl, firstBannerUrlFromGoods } from "../../utils/image";
import {
  buildSelectHint,
  buildVariantUiPlan,
  fillSpecChips,
  findVariantBySpecs,
  getRealVariants,
  selectionFromVariant,
  type SpecDim,
  type VariantUiPlan,
} from "../../utils/goodsVariant";

const props = defineProps<{
  show: boolean;
  goods: MallGoods | null;
  /** 打开弹层时的预选 SKU ID */
  initialVariantId?: number | null;
}>();

const emit = defineEmits<{
  (e: "update:show", v: boolean): void;
  (e: "add-cart", payload: { variantId: number | null; quantity: number }): void;
  (e: "buy", payload: { variantId: number | null; quantity: number }): void;
}>();

const quantity = ref(1);
/** 已选规格：key=维度名，value=选中值 */
const selectedSpecs = ref<Record<string, string>>({});

const realVariants = computed(() => getRealVariants(props.goods?.variants));

/** UI 展示方案（维度结构） */
const plan = computed<VariantUiPlan>(() =>
  buildVariantUiPlan(props.goods?.variants || [], props.goods?.specNames || [])
);

/** 填充 disabled 状态后的维度列表 */
const filledDims = computed<SpecDim[]>(() =>
  fillSpecChips(props.goods?.variants || [], plan.value, selectedSpecs.value)
);

/** 当前选中的 SKU */
const resolvedVariant = computed<MallGoodsVariant | null>(() => {
  const g = props.goods;
  if (!g) return null;
  const list = realVariants.value;
  if (list.length === 0) return null;
  if (list.length === 1) return list[0];
  if (Object.keys(selectedSpecs.value).length === 0) return null;
  return findVariantBySpecs(g.variants || [], selectedSpecs.value);
});

const displayPrice = computed(() => {
  const v = resolvedVariant.value;
  if (v) return v.price;
  return props.goods?.price ?? 0;
});

const displayStock = computed(() => {
  const v = resolvedVariant.value;
  if (v) return v.stock ?? 0;
  return props.goods?.stock ?? 0;
});

/** 已选规格行数（用于 hint 计算） */
const filledGroupsCount = computed(() =>
  Object.values(selectedSpecs.value).filter(Boolean).length
);

const hintText = computed(() =>
  buildSelectHint(plan.value, selectedSpecs.value, filledGroupsCount.value)
);

const singleVariantLine = computed(() => {
  const v = realVariants.value[0];
  if (!v) return "";
  const specs = v.specs || {};
  const parts = Object.entries(specs)
    .filter(([, v]) => v && !["默认规格", "默认"].includes(v))
    .map(([, v]) => v);
  return parts.length > 0 ? parts.join(" / ") : "默认规格";
});

const thumbStyle = computed(() => {
  const g = props.goods;
  const url = g ? firstBannerUrlFromGoods(g) : "";
  const full = url ? goodsImageUrl(url) : "";
  return {
    backgroundColor: "#f3f4f6",
    backgroundImage: full ? `url("${full.replace(/"/g, "%22")}")` : "none",
    backgroundSize: "cover",
    backgroundPosition: "center",
  };
});

function resetSelectionFromGoods() {
  const g = props.goods;
  if (!g) {
    selectedSpecs.value = {};
    return;
  }
  const list = realVariants.value;
  const p = plan.value;
  if (list.length === 0) {
    selectedSpecs.value = {};
    return;
  }
  // 优先使用传入的 initialVariantId
  let pick = props.initialVariantId != null
    ? list.find((v) => v.id === props.initialVariantId)
    : undefined;
  // 其次取有库存的
  pick = pick || list.find((v) => (v.stock ?? 0) > 0);
  // 再取第一个
  pick = pick || list[0];
  selectedSpecs.value = selectionFromVariant(pick);
}

watch(
  () => props.show,
  (v) => {
    if (v) {
      quantity.value = 1;
      resetSelectionFromGoods();
    }
  }
);

watch(
  () => props.goods?.id,
  () => {
    if (props.show) resetSelectionFromGoods();
  }
);

/** 点击某个 chip */
function onPickChip(dimName: string, value: string) {
  // matrix 模式：切换 dim1 时清除 dim2
  if (plan.value.mode === "matrix" && plan.value.specNames[0] === dimName) {
    selectedSpecs.value = { [dimName]: value, [plan.value.specNames[1]]: "" };
  } else {
    selectedSpecs.value = { ...selectedSpecs.value, [dimName]: value };
  }
}

function changeQty(delta: number) {
  const max = Math.max(1, displayStock.value || 1);
  quantity.value = Math.min(max, Math.max(1, quantity.value + delta));
}

function onQtyInput(e: Event) {
  const ev = e as unknown as { detail?: { value?: string }; target?: { value?: string } };
  const raw = ev.detail?.value ?? ev.target?.value ?? "";
  let n = parseInt(String(raw), 10);
  if (Number.isNaN(n) || n < 1) n = 1;
  const max = Math.max(1, displayStock.value || 1);
  quantity.value = Math.min(max, n);
}

function resolveVariantIdForSubmit(): number | null {
  const v = resolvedVariant.value;
  return v?.id ?? null;
}

function emitAddCart() {
  if (displayStock.value <= 0) {
    uni.showToast({ title: "库存不足", icon: "none" });
    return;
  }
  if (plan.value.mode !== "none" && plan.value.mode !== "single" && !resolvedVariant.value) {
    uni.showToast({ title: hintText.value || "请选择规格", icon: "none" });
    return;
  }
  emit("add-cart", { variantId: resolveVariantIdForSubmit(), quantity: quantity.value });
}

function emitBuy() {
  if (displayStock.value <= 0) {
    uni.showToast({ title: "库存不足", icon: "none" });
    return;
  }
  if (plan.value.mode !== "none" && plan.value.mode !== "single" && !resolvedVariant.value) {
    uni.showToast({ title: hintText.value || "请选择规格", icon: "none" });
    return;
  }
  emit("buy", { variantId: resolveVariantIdForSubmit(), quantity: quantity.value });
}

function close() {
  emit("update:show", false);
}

function handleMask() {
  close();
}
</script>

<style lang="scss" scoped>
.sku-mask {
  position: fixed;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  background: rgba(15, 23, 42, 0.45);
  z-index: 500;
  display: flex;
  align-items: flex-end;
  justify-content: center;
}

.sku-panel {
  width: 100%;
  max-height: 78vh;
  background: #fff;
  border-radius: 24rpx 24rpx 0 0;
  padding: 24rpx 24rpx 20rpx;
  box-sizing: border-box;
  position: relative;
}

.sku-close {
  position: absolute;
  right: 20rpx;
  top: 16rpx;
  width: 56rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2;
}

.sku-close-x {
  font-size: 44rpx;
  color: #9ca3af;
  line-height: 1;
}

.sku-head {
  display: flex;
  gap: 20rpx;
  padding-right: 56rpx;
  margin-bottom: 20rpx;
}

.sku-thumb {
  width: 160rpx;
  height: 160rpx;
  border-radius: 16rpx;
  flex-shrink: 0;
}

.sku-head-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  justify-content: flex-end;
}

.sku-price-row {
  display: flex;
  align-items: baseline;
  gap: 10rpx;
}

.sku-price-label {
  font-size: 24rpx;
  color: #6b7280;
}

.sku-price-num {
  font-size: 40rpx;
  font-weight: 700;
  color: #ef4444;
}

.sku-stock {
  font-size: 24rpx;
  color: #6b7280;
}

.sku-hint {
  font-size: 24rpx;
  color: #f97316;
}

.sku-body {
  max-height: 46vh;
  padding-bottom: 8rpx;
}

.sku-block {
  margin-bottom: 28rpx;
}

.sku-dim-name {
  display: block;
  font-size: 26rpx;
  color: #374151;
  font-weight: 600;
  margin-bottom: 16rpx;
}

.sku-single-line {
  font-size: 26rpx;
  color: #111827;
  line-height: 1.5;
}

.sku-size-tip {
  font-size: 24rpx;
  color: #9ca3af;
  padding: 8rpx 0;
}

.sku-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.sku-chip {
  max-width: 100%;
  padding: 14rpx 22rpx;
  border-radius: 12rpx;
  background: #f3f4f6;
  border: 2rpx solid transparent;
  box-sizing: border-box;
}

.sku-chip.is-on {
  background: rgba(239, 68, 68, 0.08);
  border-color: #ef4444;
}

.sku-chip.is-disabled {
  opacity: 0.4;
}

.sku-chip-text {
  font-size: 24rpx;
  color: #111827;
  line-height: 1.45;
  word-break: break-all;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
  overflow: hidden;
}

.sku-chip.is-on .sku-chip-text {
  color: #b91c1c;
  font-weight: 600;
}

.sku-qty-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 8rpx;
  margin-bottom: 8rpx;
}

.sku-stepper {
  display: flex;
  align-items: center;
  gap: 0;
  border-radius: 12rpx;
  overflow: hidden;
  border: 1rpx solid #e5e7eb;
}

.sku-step-btn {
  width: 72rpx;
  height: 64rpx;
  line-height: 64rpx;
  text-align: center;
  font-size: 32rpx;
  color: #374151;
  background: #f9fafb;
}

.sku-step-input {
  width: 88rpx;
  height: 64rpx;
  line-height: 64rpx;
  text-align: center;
  font-size: 28rpx;
  border-left: 1rpx solid #e5e7eb;
  border-right: 1rpx solid #e5e7eb;
}

.sku-footer {
  display: flex;
  gap: 20rpx;
  padding-top: 16rpx;
  margin-top: 8rpx;
  border-top: 1rpx solid #f3f4f6;
}

.safe-bottom {
  padding-bottom: constant(safe-area-inset-bottom);
  padding-bottom: env(safe-area-inset-bottom);
}

.sku-btn {
  flex: 1;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 999rpx;
  font-size: 28rpx;
  font-weight: 600;
  border: none;
  margin: 0;
}

.sku-btn-cart {
  background: #fff;
  color: #ea580c;
  border: 2rpx solid #fb923c;
}

.sku-btn-buy {
  background: linear-gradient(90deg, #ef4444, #dc2626);
  color: #fff;
}
</style>
