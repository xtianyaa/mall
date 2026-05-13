<template>
  <div class="page-wrap">
    <div class="summary-grid">
      <div class="summary-card">
        <div class="summary-label">商品总量</div>
        <div class="summary-value">{{ mallStore.goodsTotal }}</div>
      </div>
      <div class="summary-card">
        <div class="summary-label">在售商品</div>
        <div class="summary-value">{{ onlineGoodsCount }}</div>
      </div>
      <div class="summary-card">
        <div class="summary-label">低库存预警</div>
        <div class="summary-value">{{ lowStockCount }}</div>
      </div>
    </div>

    <div class="content-card table-card">
      <div class="filter-row">
        <div>
          <div class="card-title">商品管理</div>
          <div class="card-desc">当前页共 {{ mallStore.goodsList.length }} 条。</div>
        </div>
        <div class="filter-toolbar">
          <el-input
            v-model="listKeyword"
            placeholder="搜索商品名称、卖点"
            clearable
            class="filter-input"
            style="width: 220px"
            @clear="applyListFilter"
            @keyup.enter="applyListFilter"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-select
            v-model="listCategoryId"
            placeholder="全部分类"
            clearable
            filterable
            class="filter-select"
            style="width: 180px"
            @change="applyListFilter"
          >
            <el-option
              v-for="item in mallStore.categoryOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
          <el-button type="primary" @click="applyListFilter">查询</el-button>
          <el-button type="danger" plain :disabled="!selectedGoods.length" @click="handleBatchDeleteGoods">
            批量删除
          </el-button>
          <el-button type="primary" class="btn-add" @click="openCreateDialog">新增商品</el-button>
        </div>
      </div>
      <div class="table-wrap">
        <el-table :data="mallStore.goodsList" @selection-change="onGoodsSelectionChange">
          <el-table-column type="selection" width="48" />
          <el-table-column prop="name" label="商品名称" min-width="220" />
          <el-table-column prop="characteristic" label="商品卖点" min-width="220" />
          <el-table-column prop="categoryName" label="分类" width="120" />
          <el-table-column label="价格体系" width="160">
            <template #default="{ row }">
              <div class="price-line">售价：￥{{ row.price }}</div>
              <div class="sub-line">划线价：￥{{ row.originalPrice }}</div>
            </template>
          </el-table-column>
          <el-table-column label="规格/库存" width="140">
            <template #default="{ row }">
              <div>{{ row.unit || '件' }}</div>
              <div class="sub-line">库存 {{ row.stock }}</div>
            </template>
          </el-table-column>
          <el-table-column label="标签" min-width="220">
            <template #default="{ row }">
              <div class="tag-list">
                <el-tag
                  v-for="tag in splitText(row.tags || '')"
                  :key="`${row.id}-${tag}`"
                  type="danger"
                  effect="plain"
                >
                  {{ tag }}
                </el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="monthlySales" label="月销" width="100" />
          <el-table-column label="上架状态" width="120">
            <template #default="{ row }">
              <el-tag :type="row.status ? 'success' : 'info'">
                {{ row.status ? "上架中" : "已下架" }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="260" fixed="right">
            <template #default="{ row }">
              <div class="op-btns">
                <el-button link type="primary" @click="openEditDialog(row.id)">编辑</el-button>
                <el-button link type="danger" @click="toggleStatus(row.id)">
                  {{ row.status ? "下架" : "上架" }}
                </el-button>
                <el-button link type="danger" @click="handleDeleteOneGoods(row)">删除</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="goodsPage"
          v-model:page-size="goodsPageSize"
          :total="mallStore.goodsTotal"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @current-change="loadGoodsPage"
          @size-change="loadGoodsPage"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="760px">
      <el-form label-width="110px" class="goods-form">
        <el-form-item label="商品名称" required>
          <el-input v-model="formData.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="隐秘名称">
          <el-input v-model="formData.discreetName" placeholder="隐秘名称用于面单打印及隐私发货（可选）" />
          <div class="form-tip">若开启隐私发货，订单详情及面单将优先展示此名称，避免暴露真实品名。</div>
        </el-form-item>
        <el-form-item label="商品分类" required>
          <el-select
            v-model="formData.categoryName"
            placeholder="请选择或搜索商品分类"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="item in mallStore.categoryOptions"
              :key="item.id"
              :label="item.name"
              :value="item.name"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="商品卖点" required>
          <el-input
            v-model="formData.characteristic"
            type="textarea"
            :rows="2"
            placeholder="例如：24 小时冷链直达、果径 26mm+"
          />
        </el-form-item>
        <div class="form-grid">
          <el-form-item label="销售价" required>
            <el-input-number v-model="formData.price" :min="0" :precision="2" :step="1" />
          </el-form-item>
          <el-form-item label="划线价" required>
            <el-input-number v-model="formData.originalPrice" :min="0" :precision="2" :step="1" />
          </el-form-item>
          <el-form-item label="规格单位" required>
            <el-input v-model="formData.unit" placeholder="例如：125g/盒" />
          </el-form-item>
          <el-form-item label="库存" required>
            <el-input-number v-model="formData.stock" :min="0" :step="10" />
          </el-form-item>
        </div>

        <div class="sku-section">
          <div class="sku-title">规格 SKU（最多 5 个维度，动态增删）</div>

          <!-- 维度名输入行 -->
          <div class="spec-dim-names-row">
            <div
              v-for="(dimName, i) in specNames"
              :key="i"
              class="spec-dim-input-wrap"
            >
              <el-input
                v-model="specNames[i]"
                :placeholder="`维度${i + 1}名，如：颜色`"
                @blur="onSpecDimNameBlur(i)"
              />
              <el-button
                v-if="specNames.length > 1"
                link
                type="danger"
                @click="removeSpecDim(i)"
              >
                ×
              </el-button>
            </div>
            <el-button
              plain
              type="primary"
              size="small"
              @click="addSpecDim"
              :disabled="specNames.length >= 5"
            >
              + 添加维度
            </el-button>
          </div>

          <el-table :data="formData.variants" border style="width: 100%;" size="small">
            <el-table-column
              v-for="(dimName, i) in specNames"
              :key="i"
              :label="dimName || `维度${i + 1}`"
              min-width="140"
            >
              <template #default="{ row }">
                <el-input
                  v-model="row.specs[dimName]"
                  :placeholder="`如：${dimName || '值'}${i}`"
                />
              </template>
            </el-table-column>
            <el-table-column label="销售价" width="130">
              <template #default="{ row }">
                <el-input-number v-model="row.price" :min="0" :precision="2" :step="0.1" />
              </template>
            </el-table-column>
            <el-table-column label="划线价" width="130">
              <template #default="{ row }">
                <el-input-number v-model="row.originalPrice" :min="0" :precision="2" :step="0.1" />
              </template>
            </el-table-column>
            <el-table-column label="库存" width="120">
              <template #default="{ row }">
                <el-input-number v-model="row.stock" :min="0" :step="10" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" align="center">
              <template #default="{ $index }">
                <el-button
                  type="danger"
                  link
                  @click="removeVariantRow($index)"
                  :disabled="formData.variants.length <= 1"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="sku-actions">
            <el-button type="primary" :icon="Plus" plain @click="addVariantRow">
              添加规格行
            </el-button>
            <div class="form-tip">
              至少填写一条 SKU；每行划线价不得低于销售价。提交时会自动根据 SKU 汇总更新商品售价与库存。
            </div>
          </div>
        </div>

        <el-form-item label="商品标签">
          <el-input
            v-model="formData.tags"
            placeholder="多个标签使用英文逗号分隔，例如：新品,产地直发,次日达"
          />
        </el-form-item>
        <el-form-item label="主图" required>
          <el-upload
            class="goods-upload"
            list-type="picture-card"
            :file-list="mainImageFileList"
            :limit="1"
            :http-request="handleMainUpload"
            :on-remove="() => (mainImageUrl = '')"
            accept="image/jpeg,image/png,image/gif,image/webp"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="form-tip">建议尺寸 750×750，仅支持 jpg/png/gif/webp，单张不超过 10MB</div>
        </el-form-item>
        <el-form-item label="轮播图">
          <el-upload
            class="goods-upload"
            list-type="picture-card"
            :file-list="carouselFileList"
            :http-request="handleCarouselUpload"
            :on-remove="handleCarouselRemove"
            accept="image/jpeg,image/png,image/gif,image/webp"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="form-tip">可上传多张，展示在商品详情轮播中；主图可作为首图，此处为补充轮播</div>
        </el-form-item>
        <el-form-item label="详情内容">
          <el-input
            v-model="formData.detailList"
            type="textarea"
            :rows="3"
            placeholder="多个详情卖点使用英文逗号分隔，例如：高山基地直采,48 小时冷链,坏果包赔"
          />
          <div class="form-tip">文本卖点将与小程序的「图文详情」一起展示，可与下方详情介绍图片搭配使用</div>
        </el-form-item>
        <el-form-item label="详情介绍图片">
          <el-upload
            class="goods-upload detail-images-upload"
            list-type="picture-card"
            :file-list="detailImageFileList"
            :http-request="handleDetailImageUpload"
            :on-remove="handleDetailImageRemove"
            accept="image/jpeg,image/png,image/gif,image/webp"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="form-tip">可上传多张，展示在商品详情页「图文详情」中，支持拖拽排序；建议宽度 750px，格式 jpg/png/gif/webp</div>
        </el-form-item>
        <el-form-item label="上架状态">
          <el-switch v-model="formData.status" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="danger" :loading="submitLoading" @click="submitGoodsForm">
          {{ formData.id ? "保存修改" : "创建商品" }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Search } from "@element-plus/icons-vue";
import type { UploadRequestOptions } from "element-plus";
import { useMallStore } from "../../../../store/modules/mall";
import { uploadImage, deleteGoods, batchDeleteGoods } from "../../../../api/yuan-mall-module";
import type { AdminGoods } from "../../../../types/mall";

const mallStore = useMallStore();
const goodsPage = ref(1);
const goodsPageSize = ref(10);
const dialogVisible = ref(false);
const selectedGoods = ref<AdminGoods[]>([]);
const onGoodsSelectionChange = (rows: AdminGoods[]) => {
  selectedGoods.value = rows;
};

/** 列表筛选：关键词、分类 */
const listKeyword = ref("");
const listCategoryId = ref<number | undefined>(undefined);

const loadGoodsPage = async () => {
  await mallStore.fetchGoodsList({
    keyword: listKeyword.value.trim() || undefined,
    categoryId: listCategoryId.value,
    pageNum: goodsPage.value,
    pageSize: goodsPageSize.value,
  });
};

const applyListFilter = async () => {
  goodsPage.value = 1;
  await loadGoodsPage();
};

const baseURL = import.meta.env.VITE_API_BASE_URL || "http://127.0.0.1:8080";
const imageFullUrl = (path: string) => (path && path.startsWith("http") ? path : baseURL + path);

const mainImageUrl = ref("");
const carouselUrlList = ref<string[]>([]);
const detailImageUrlList = ref<string[]>([]);

/** 判断是否为详情图 URL（与后端/小程序识别规则一致，用于回显时拆分 detailList） */
const isDetailImageUrl = (value: string): boolean => {
  const s = (value || "").trim();
  if (!s) return false;
  if (s.startsWith("http://") || s.startsWith("https://")) return true;
  if (s.startsWith("/") || s.startsWith("upload") || s.includes("/upload")) return true;
  if (/\.(png|jpe?g|gif|webp|bmp|svg)(\?.*)?$/i.test(s)) return true;
  if (s.length >= 10 && /^[a-zA-Z0-9_.-]+$/.test(s)) return true;
  return false;
};

const mainImageFileList = computed(() =>
  mainImageUrl.value ? [{ url: imageFullUrl(mainImageUrl.value), name: "主图" }] : []
);
const carouselFileList = computed(() =>
  carouselUrlList.value.map((url, i) => ({ url: imageFullUrl(url), name: `轮播${i + 1}` }))
);
const detailImageFileList = computed(() =>
  detailImageUrlList.value.map((url, i) => ({ url: imageFullUrl(url), name: `详情图${i + 1}` }))
);

const handleMainUpload = async (options: UploadRequestOptions) => {
  try {
    const url = await uploadImage(options.file);
    mainImageUrl.value = url;
    options.onSuccess?.(url);
  } catch (e: unknown) {
    ElMessage.error((e as Error)?.message || "主图上传失败");
    options.onError?.(e as any);
  }
};

const handleCarouselUpload = async (options: UploadRequestOptions) => {
  try {
    const url = await uploadImage(options.file);
    carouselUrlList.value = [...carouselUrlList.value, url];
    options.onSuccess?.(url);
  } catch (e: unknown) {
    ElMessage.error((e as Error)?.message || "轮播图上传失败");
    options.onError?.(e as any);
  }
};

const handleCarouselRemove = (_uploadFile: { url?: string }, _uploadFiles: unknown[]) => {
  const full = _uploadFile.url || "";
  const idx = carouselUrlList.value.findIndex((u) => imageFullUrl(u) === full);
  if (idx !== -1) carouselUrlList.value = carouselUrlList.value.filter((_, i) => i !== idx);
};

const handleDetailImageUpload = async (options: UploadRequestOptions) => {
  try {
    const url = await uploadImage(options.file);
    detailImageUrlList.value = [...detailImageUrlList.value, url];
    options.onSuccess?.(url);
  } catch (e: unknown) {
    ElMessage.error((e as Error)?.message || "详情图上传失败");
    options.onError?.(e as any);
  }
};

const handleDetailImageRemove = (_uploadFile: { url?: string }) => {
  const full = _uploadFile.url || "";
  const idx = detailImageUrlList.value.findIndex((u) => imageFullUrl(u) === full);
  if (idx !== -1) detailImageUrlList.value = detailImageUrlList.value.filter((_, i) => i !== idx);
};

/** 当前维度名列表（最多 5 个），与 formData.variants[].specs 的键对应 */
const specNames = ref<string[]>(["颜色", "尺码"]);

const createDefaultForm = () => ({
  id: 0,
  name: "",
  discreetName: "",
  categoryName: "应季鲜果",
  characteristic: "",
  price: 19.9,
  originalPrice: 29.9,
  unit: "件",
  stock: 100,
  tags: "新品",
  bannerList: "",
  detailList: "商品详情待补充",
  status: true,
  variants: [
    {
      specs: Object.fromEntries(["颜色", "尺码"].map((n) => [n, ""])),
      price: 19.9,
      originalPrice: 29.9,
      unit: "件",
      stock: 100,
    },
  ],
});

const formData = reactive(createDefaultForm());

const onlineGoodsCount = computed(() => mallStore.goodsList.filter((item) => item.status).length);
const lowStockCount = computed(() => mallStore.goodsList.filter((item) => item.stock <= 20).length);
const dialogTitle = computed(() => (formData.id ? "编辑商品" : "新增商品"));

const splitText = (value: string) =>
  value
    .split(",")
    .map((item) => item.trim())
    .filter(Boolean);

const resetForm = () => {
  Object.assign(formData, createDefaultForm());
  mainImageUrl.value = "";
  carouselUrlList.value = [];
  detailImageUrlList.value = [];
};

const addVariantRow = () => {
  if (!Array.isArray(formData.variants)) {
    formData.variants = [];
  }
  formData.variants.push({
    specs: Object.fromEntries(specNames.value.map((n) => [n, ""])),
    price: formData.price,
    originalPrice: formData.originalPrice,
    unit: formData.unit,
    stock: 0,
  });
};

const removeVariantRow = (index: number) => {
  if (!Array.isArray(formData.variants)) return;
  formData.variants.splice(index, 1);
};

/** 新增一个维度（末尾添加） */
const addSpecDim = () => {
  if (specNames.value.length >= 5) {
    ElMessage.warning("最多支持 5 个维度");
    return;
  }
  specNames.value.push("");
};

/** 删除第 i 个维度（同时清除所有行 specs 中对应的键） */
const removeSpecDim = (i: number) => {
  const removedName = specNames.value[i];
  specNames.value.splice(i, 1);
  // 清除各行 specs 中残留的该维度键
  for (const row of formData.variants) {
    if (removedName && row.specs) {
      delete row.specs[removedName];
    }
  }
};

/** 维度名变更后，同步更新所有行的 specs 键（旧键→新键） */
const onSpecDimNameBlur = (i: number) => {
  const newName = specNames.value[i]?.trim();
  if (!newName) return;
  for (const row of formData.variants) {
    if (row.specs) {
      // 已有同名列则跳过；没有对应键的行自动为空字符串（已由 v-model 填充）
    }
  }
};

const validateForm = () => {
  if (!formData.name.trim()) {
    ElMessage.warning("请输入商品名称");
    return false;
  }
  if (!formData.categoryName) {
    ElMessage.warning("请选择商品分类");
    return false;
  }
  if (!formData.characteristic.trim()) {
    ElMessage.warning("请输入商品卖点");
    return false;
  }
  if (!formData.unit.trim()) {
    ElMessage.warning("请输入规格单位");
    return false;
  }

  // SKU 校验（以逐行为准；提交时会自动汇总更新商品售价/库存）
  const variants = formData.variants || [];
  if (!variants.length) {
    ElMessage.warning("请至少添加一条规格SKU");
    return false;
  }
  for (let i = 0; i < variants.length; i++) {
    const v = variants[i];
    const hasAnySpecValue = Object.values(v.specs || {}).some(
      (val) => String(val || "").trim().length > 0
    );
    if (!hasAnySpecValue) {
      ElMessage.warning(`第 ${i + 1} 行：请至少填写一个规格值`);
      return false;
    }
    if (v.price == null || v.originalPrice == null || !Number.isFinite(Number(v.price)) || !Number.isFinite(Number(v.originalPrice))) {
      ElMessage.warning(`第 ${i + 1} 行：请填写销售价与划线价`);
      return false;
    }
    if (Number(v.originalPrice) < Number(v.price)) {
      ElMessage.warning(`第 ${i + 1} 行：划线价不能低于销售价`);
      return false;
    }
    if (v.stock == null || !Number.isFinite(Number(v.stock)) || Number(v.stock) < 0) {
      ElMessage.warning(`第 ${i + 1} 行：库存必须大于等于 0`);
      return false;
    }
  }

  if (formData.originalPrice < formData.price) {
    ElMessage.warning("划线价不能低于销售价");
    return false;
  }
  if (!mainImageUrl.value) {
    ElMessage.warning("请上传主图");
    return false;
  }
  const hasDetailText = formData.detailList.trim().length > 0;
  const hasDetailImages = detailImageUrlList.value.length > 0;
  if (!hasDetailText && !hasDetailImages) {
    ElMessage.warning("请至少填写一条详情内容或上传一张详情介绍图片");
    return false;
  }
  return true;
};

const openCreateDialog = () => {
  resetForm();
  dialogVisible.value = true;
};

const openEditDialog = (id: number) => {
  const currentGoods = mallStore.goodsList.find((item) => item.id === id);
  if (!currentGoods) {
    return;
  }
  Object.assign(formData, currentGoods);
  const firstVariant = (currentGoods.variants && currentGoods.variants[0]) || null;
  // 恢复维度名列表（specNames）
  if (firstVariant?.specNames && firstVariant.specNames.length > 0) {
    specNames.value = [...firstVariant.specNames];
  } else {
    specNames.value = ["颜色", "尺码"];
  }
  const urls = splitText(formData.bannerList || "");
  mainImageUrl.value = urls[0] || "";
  carouselUrlList.value = urls.slice(1);
  // 从 detailList 中拆出「详情介绍图片」与「文本卖点」分别回显
  const detailParts = splitText(formData.detailList || "");
  const textParts: string[] = [];
  const imageUrls: string[] = [];
  for (const part of detailParts) {
    if (isDetailImageUrl(part)) {
      imageUrls.push(part);
    } else {
      textParts.push(part);
    }
  }
  formData.detailList = textParts.join(", ");
  detailImageUrlList.value = imageUrls;
  dialogVisible.value = true;
};

const submitLoading = ref(false);

const submitGoodsForm = async () => {
  formData.bannerList = [mainImageUrl.value, ...carouselUrlList.value].filter(Boolean).join(",");
  // 详情：文本卖点 + 详情介绍图片 URL，合并后与后端/小程序约定一致（后端按项识别 image/text）
  const detailTexts = splitText(formData.detailList || "");
  formData.detailList = [...detailTexts, ...detailImageUrlList.value].filter(Boolean).join(",");

  // SKU 参数归一化：使用动态维度结构提交
  const rawVariants = Array.isArray(formData.variants) ? formData.variants : [];
  const normalizedVariants = rawVariants.map((v: any) => ({
    specs: { ...v.specs }, // 透传动态维度键值对
    specNames: [...specNames.value], // 当前维度名列表
    price: Number(v.price),
    originalPrice: Number(v.originalPrice),
    unit: String(formData.unit ?? v.unit ?? "").trim(),
    stock: Number(v.stock),
  }));

  formData.variants = normalizedVariants;
  if (normalizedVariants.length) {
    const minPrice = Math.min(...normalizedVariants.map((v) => v.price));
    const minOriginalPrice = Math.min(...normalizedVariants.map((v) => v.originalPrice));
    const sumStock = normalizedVariants.reduce((acc, v) => acc + (v.stock || 0), 0);
    formData.price = minPrice;
    formData.originalPrice = minOriginalPrice;
    formData.stock = sumStock;
  }

  if (!validateForm()) {
    return;
  }

  submitLoading.value = true;
  try {
    if (formData.id) {
      await mallStore.editGoods({ ...formData });
      ElMessage.success("商品编辑成功");
    } else {
      await mallStore.addGoods({ ...formData });
      ElMessage.success("商品新增成功");
    }
    dialogVisible.value = false;
    resetForm();
    await applyListFilter();
  } catch (e: unknown) {
    const msg = (e as Error)?.message || "保存失败，请重试";
    ElMessage.error(msg);
  } finally {
    submitLoading.value = false;
  }
};

const toggleStatus = async (id: number) => {
  await mallStore.toggleGoodsStatus(id);
  ElMessage.success("商品状态已更新");
  await applyListFilter();
};

const handleDeleteOneGoods = async (row: AdminGoods) => {
  try {
    await ElMessageBox.confirm(
      `确定删除商品「${row.name}」？将同步删除规格与购物车中该商品，订单历史仍保留。`,
      "删除确认",
      { type: "warning", confirmButtonText: "删除", cancelButtonText: "取消" }
    );
  } catch {
    return;
  }
  try {
    await deleteGoods({ id: row.id });
    ElMessage.success("已删除");
    selectedGoods.value = [];
    await applyListFilter();
    await mallStore.fetchCategoryOptions();
  } catch (e: unknown) {
    ElMessage.error((e as Error)?.message || "删除失败");
  }
};

const handleBatchDeleteGoods = async () => {
  if (!selectedGoods.value.length) return;
  try {
    await ElMessageBox.confirm(
      `确定删除选中的 ${selectedGoods.value.length} 个商品？将同步删除规格与购物车记录。`,
      "批量删除",
      { type: "warning", confirmButtonText: "删除", cancelButtonText: "取消" }
    );
  } catch {
    return;
  }
  try {
    await batchDeleteGoods({ ids: selectedGoods.value.map((g) => g.id) });
    ElMessage.success("已删除");
    selectedGoods.value = [];
    await applyListFilter();
    await mallStore.fetchCategoryOptions();
  } catch (e: unknown) {
    ElMessage.error((e as Error)?.message || "删除失败");
  }
};

onMounted(async () => {
  await mallStore.fetchCategoryOptions();
  await loadGoodsPage();
});
</script>

<style lang="scss" scoped>
.page-wrap {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.content-card {
  padding: 24px;
  background: #ffffff;
  border-radius: 18px;
  box-shadow: 0 8px 30px rgba(15, 23, 42, 0.05);
}

.card-desc,
.sub-line {
  color: #6b7280;
  font-size: 13px;
}

.price-line {
  color: #111827;
  font-weight: 600;
}

.tag-list {
  display: flex;
  flex-wrap: nowrap;
  gap: 8px;
  overflow: hidden;
}

.goods-form {
  padding-right: 12px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 24px;
}

.form-grid :deep(.el-form-item) {
  margin-bottom: 18px;
}

.filter-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 16px;
}

.filter-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  padding: 12px 14px;
  border-radius: 14px;
  background: linear-gradient(180deg, #f8fafc 0%, #fff 100%);
}

.filter-input,
.filter-select {
  flex-shrink: 0;
}

.btn-add {
  margin-left: 4px;
}

.table-card .card-title {
  margin-bottom: 0;
}

.table-wrap {
  overflow-x: auto;
  margin: 0 -4px;
}

.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.form-tip {
  margin-top: 8px;
  color: #6b7280;
  font-size: 12px;
  line-height: 1.4;
}

.sku-section {
  margin-top: 18px;
  padding: 14px 14px;
  border-radius: 14px;
  background: rgba(17, 24, 39, 0.03);
}

.sku-title {
  font-weight: 700;
  margin-bottom: 12px;
  color: #111827;
}

.spec-dim-names-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}

.spec-dim-input-wrap {
  display: flex;
  align-items: center;
  gap: 4px;
}

.sku-actions {
  margin-top: 12px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.goods-upload :deep(.el-upload-list--picture-card) {
  --el-upload-list-picture-card-size: 100px;
}

.detail-images-upload :deep(.el-upload-list--picture-card) {
  --el-upload-list-picture-card-size: 100px;
}

@media screen and (max-width: 1200px) {
  .summary-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media screen and (max-width: 768px) {
  .summary-grid {
    grid-template-columns: 1fr;
  }

  .filter-row {
    flex-direction: column;
    align-items: stretch;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
