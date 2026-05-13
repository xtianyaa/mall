<template>
  <div class="page-wrap">
    <div class="summary-grid">
      <div class="summary-card">
        <div class="summary-label">Banner 数量</div>
        <div class="summary-value">{{ mallStore.bannerList.length }}</div>
      </div>
      <div class="summary-card">
        <div class="summary-label">首屏主推</div>
        <div class="summary-value">{{ heroBannerTitle }}</div>
      </div>
      <div class="summary-card">
        <div class="summary-label">发布提示</div>
        <div class="summary-value">已接真实后台</div>
      </div>
    </div>

    <div class="content-card">
      <div class="card-head">
        <div class="card-title">首屏文案配置</div>
        <el-button type="primary" :loading="decoSaving" @click="saveHomeDeco">保存文案</el-button>
      </div>
      <el-form :model="homeDecoForm" label-width="120px" class="deco-form">
        <el-form-item label="搜索框占位文案">
          <el-input v-model="homeDecoForm.searchPlaceholder" placeholder="如：搜索蓝莓、牛奶、礼盒" clearable />
        </el-form-item>
        <el-form-item label="服务承诺 1">
          <el-input v-model="homeDecoForm.stat1Value" placeholder="数值，如 30 分钟" style="width: 140px" />
          <el-input v-model="homeDecoForm.stat1Label" placeholder="描述，如 同城闪送" style="width: 140px; margin-left: 8px" />
        </el-form-item>
        <el-form-item label="服务承诺 2">
          <el-input v-model="homeDecoForm.stat2Value" placeholder="数值，如 99 元" style="width: 140px" />
          <el-input v-model="homeDecoForm.stat2Label" placeholder="描述，如 满额包邮" style="width: 140px; margin-left: 8px" />
        </el-form-item>
        <el-form-item label="服务承诺 3">
          <el-input v-model="homeDecoForm.stat3Value" placeholder="数值，如 48 小时" style="width: 140px" />
          <el-input v-model="homeDecoForm.stat3Label" placeholder="描述，如 售后保障" style="width: 140px; margin-left: 8px" />
        </el-form-item>
      </el-form>
    </div>

    <div class="content-card">
      <div class="card-head">
        <div class="card-title">首页 Banner 配置</div>
        <el-button type="primary" @click="openCreate">新增 Banner</el-button>
      </div>
      <el-table :data="mallStore.bannerList">
        <el-table-column label="图片" width="120">
          <template #default="{ row }">
            <el-image
              v-if="row.imageUrl"
              :src="imageFullUrl(row.imageUrl)"
              style="width: 88px; height: 48px; border-radius: 10px"
              fit="cover"
              preview-teleported
              :preview-src-list="[imageFullUrl(row.imageUrl)]"
            />
            <span v-else class="muted">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="linkType" label="跳转类型" width="140" />
        <el-table-column prop="linkUrl" label="跳转参数/链接" min-width="180" />
        <el-table-column label="启用" width="90">
          <template #default="{ row }">
            <el-switch v-model="row.status" @change="() => handleQuickEdit(row)" />
          </template>
        </el-table-column>
        <el-table-column label="排序" width="180">
          <template #default="{ row }">
            <el-input-number
              :model-value="row.sort"
              :min="1"
              @change="(value: number | undefined) => updateSort(row.id, Number(value || 1))"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <div class="op-btns">
              <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
              <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="标题" required>
          <el-input v-model="formData.title" placeholder="如：春日鲜果节" />
        </el-form-item>
        <el-form-item label="图片" required>
          <el-upload
            class="upload-box"
            :http-request="handleImageUpload"
            :show-file-list="false"
            accept="image/*"
          >
            <div class="upload-preview">
              <img v-if="formData.imageUrl" :src="imageFullUrl(formData.imageUrl)" class="preview-img" />
              <div v-else class="preview-placeholder">点击上传</div>
            </div>
          </el-upload>
          <div class="muted mt8">建议：750×320，大小 ≤ 10MB</div>
        </el-form-item>
        <el-form-item label="跳转类型" required>
          <el-select v-model="formData.linkType" placeholder="选择跳转类型" style="width: 100%">
            <el-option label="商品专题" value="商品专题" />
            <el-option label="优惠券" value="优惠券" />
            <el-option label="自定义链接" value="自定义链接" />
          </el-select>
        </el-form-item>
        <el-form-item label="跳转参数">
          <el-input v-model="formData.linkUrl" placeholder="如：goodsId=1001 / https://..." />
        </el-form-item>
        <el-form-item label="启用" required>
          <el-switch v-model="formData.status" />
        </el-form-item>
        <el-form-item label="排序" required>
          <el-input-number v-model="formData.sort" :min="1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="danger" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import type { UploadRequestOptions } from "element-plus";
import { useMallStore } from "../../../../store/modules/mall";
import { getHomeDeco, updateHomeDeco, uploadImage } from "../../../../api/yuan-mall-module";
import type { AdminBanner } from "../../../../types/mall";

const mallStore = useMallStore();
const heroBannerTitle = computed(() => mallStore.bannerList[0]?.title || "待配置");

const baseURL = import.meta.env.VITE_API_BASE_URL || "http://127.0.0.1:8080";
const imageFullUrl = (path: string) => (path && path.startsWith("http") ? path : baseURL + path);

const dialogVisible = ref(false);
const dialogTitle = ref("新增 Banner");
const saving = ref(false);
const editingId = ref<number | null>(null);

const createDefaultForm = () => ({
  title: "",
  imageUrl: "",
  linkType: "商品专题",
  linkUrl: "",
  status: true,
  sort: 1,
});
const formData = reactive(createDefaultForm());

const decoSaving = ref(false);
const homeDecoForm = reactive({
  searchPlaceholder: "搜索蓝莓、牛奶、礼盒",
  stat1Value: "30 分钟",
  stat1Label: "同城闪送",
  stat2Value: "99 元",
  stat2Label: "满额包邮",
  stat3Value: "48 小时",
  stat3Label: "售后保障",
});

const loadHomeDeco = async () => {
  const deco = await getHomeDeco();
  homeDecoForm.searchPlaceholder = deco.searchPlaceholder ?? homeDecoForm.searchPlaceholder;
  if (deco.stats?.length >= 3) {
    homeDecoForm.stat1Value = deco.stats[0].value ?? homeDecoForm.stat1Value;
    homeDecoForm.stat1Label = deco.stats[0].label ?? homeDecoForm.stat1Label;
    homeDecoForm.stat2Value = deco.stats[1].value ?? homeDecoForm.stat2Value;
    homeDecoForm.stat2Label = deco.stats[1].label ?? homeDecoForm.stat2Label;
    homeDecoForm.stat3Value = deco.stats[2].value ?? homeDecoForm.stat3Value;
    homeDecoForm.stat3Label = deco.stats[2].label ?? homeDecoForm.stat3Label;
  }
};

const saveHomeDeco = async () => {
  decoSaving.value = true;
  try {
    await updateHomeDeco({
      searchPlaceholder: homeDecoForm.searchPlaceholder.trim() || "搜索蓝莓、牛奶、礼盒",
      stat1Value: homeDecoForm.stat1Value.trim() || "30 分钟",
      stat1Label: homeDecoForm.stat1Label.trim() || "同城闪送",
      stat2Value: homeDecoForm.stat2Value.trim() || "99 元",
      stat2Label: homeDecoForm.stat2Label.trim() || "满额包邮",
      stat3Value: homeDecoForm.stat3Value.trim() || "48 小时",
      stat3Label: homeDecoForm.stat3Label.trim() || "售后保障",
    });
    ElMessage.success("首屏文案已保存，小程序将同步展示");
  } finally {
    decoSaving.value = false;
  }
};

const updateSort = async (id: number, sort: number) => {
  await mallStore.updateBannerSort(id, sort);
  ElMessage.success("Banner 排序已更新");
};

const handleQuickEdit = async (row: AdminBanner) => {
  await mallStore.editBanner({
    id: row.id,
    title: row.title,
    imageUrl: row.imageUrl,
    linkType: row.linkType,
    linkUrl: row.linkUrl,
    status: row.status,
    sort: row.sort,
  });
  ElMessage.success("Banner 已更新");
};

const openCreate = () => {
  dialogTitle.value = "新增 Banner";
  editingId.value = null;
  Object.assign(formData, createDefaultForm());
  dialogVisible.value = true;
};

const openEdit = (row: AdminBanner) => {
  dialogTitle.value = "编辑 Banner";
  editingId.value = row.id;
  Object.assign(formData, {
    title: row.title,
    imageUrl: row.imageUrl,
    linkType: row.linkType,
    linkUrl: row.linkUrl,
    status: row.status,
    sort: row.sort,
  });
  dialogVisible.value = true;
};

const handleDelete = async (id: number) => {
  await mallStore.removeBanner(id);
  ElMessage.success("Banner 已删除");
};

const handleImageUpload = async (options: UploadRequestOptions) => {
  try {
    const url = await uploadImage(options.file);
    formData.imageUrl = url;
    options.onSuccess?.(url);
  } catch (e: unknown) {
    ElMessage.error((e as Error)?.message || "上传失败");
    options.onError?.(e as any);
  }
};

const handleSave = async () => {
  if (!formData.title.trim()) {
    ElMessage.warning("请填写标题");
    return;
  }
  if (!formData.imageUrl.trim()) {
    ElMessage.warning("请上传图片");
    return;
  }
  saving.value = true;
  try {
    if (editingId.value == null) {
      await mallStore.addBanner({
        title: formData.title.trim(),
        imageUrl: formData.imageUrl.trim(),
        linkType: formData.linkType,
        linkUrl: formData.linkUrl.trim(),
        status: formData.status,
        sort: formData.sort,
      });
      ElMessage.success("Banner 已新增");
    } else {
      await mallStore.editBanner({
        id: editingId.value,
        title: formData.title.trim(),
        imageUrl: formData.imageUrl.trim(),
        linkType: formData.linkType,
        linkUrl: formData.linkUrl.trim(),
        status: formData.status,
        sort: formData.sort,
      });
      ElMessage.success("Banner 已保存");
    }
    dialogVisible.value = false;
  } finally {
    saving.value = false;
  }
};

onMounted(async () => {
  await Promise.all([mallStore.fetchBannerList(), loadHomeDeco()]);
});
</script>

<style lang="scss" scoped>
.page-wrap {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.content-card {
  padding: 20px 24px;
  border-radius: 18px;
  background: #ffffff;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.05);
}

.card-title {
  color: #111827;
  font-size: 18px;
  font-weight: 700;
}

.card-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.deco-form {
  max-width: 560px;
}

.muted {
  color: #6b7280;
}

.mt8 {
  margin-top: 8px;
}

.upload-preview {
  width: 220px;
  height: 110px;
  border-radius: 14px;
  border: 1px dashed #e5e7eb;
  background: #f9fafb;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.preview-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview-placeholder {
  color: #9ca3af;
  font-size: 13px;
}
</style>
