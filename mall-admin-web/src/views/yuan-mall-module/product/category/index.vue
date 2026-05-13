<template>
  <div class="page-wrap">
    <div class="summary-grid">
      <div class="summary-card">
        <div class="summary-label">分类总量</div>
        <div class="summary-value">{{ mallStore.categoryTotal }}</div>
      </div>
      <div class="summary-card">
        <div class="summary-label">当前页启用分类</div>
        <div class="summary-value">{{ enabledCount }}</div>
      </div>
      <div class="summary-card">
        <div class="summary-label">当前页商品归属数</div>
        <div class="summary-value">{{ currentPageGoodsCount }}</div>
      </div>
    </div>

    <div class="content-card table-card">
      <div class="filter-row">
        <div>
          <div class="card-title">商品分类</div>
          <div class="card-desc">支持按名称筛选分类，并统一维护图标、排序和启停状态。当前页共 {{ mallStore.categoryList.length }} 条。</div>
        </div>
        <div class="filter-toolbar">
          <el-input
            v-model="searchName"
            placeholder="搜索分类名称"
            clearable
            class="filter-input"
            style="width: 220px"
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleResetSearch">重置</el-button>
          <el-button
            type="danger"
            plain
            :disabled="!selectedCategories.length"
            @click="handleBatchDelete"
          >
            批量删除
          </el-button>
          <el-button type="primary" class="btn-add" @click="openAddDialog">新增分类</el-button>
        </div>
      </div>
      <div class="table-wrap">
        <el-table :data="mallStore.categoryList" @selection-change="onSelectionChange">
          <el-table-column type="selection" width="48" />
          <el-table-column label="图标" width="84">
            <template #default="{ row }">
              <div class="table-icon-cell">
                <img
                  v-if="row.icon && isImageUrl(row.icon)"
                  :src="imageFullUrl(row.icon)"
                  class="table-icon-img"
                  alt=""
                />
                <span v-else class="table-icon-emoji">{{ row.icon || "—" }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="name" label="分类名称" />
          <el-table-column prop="sort" label="排序" width="100" />
          <el-table-column prop="goodsCount" label="商品数" width="100" />
          <el-table-column label="状态" width="120">
            <template #default="{ row }">
              <el-tag :type="row.status ? 'success' : 'info'">
                {{ row.status ? "启用" : "停用" }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="240">
            <template #default="{ row }">
              <div class="op-btns">
                <el-button link type="primary" @click="openEditDialog(row)">编辑</el-button>
                <el-button link type="danger" @click="toggleStatus(row.id)">
                  {{ row.status ? "停用" : "启用" }}
                </el-button>
                <el-tooltip :disabled="!row.goodsCount" content="请先删除或迁移该分类下商品" placement="top">
                  <span>
                    <el-button link type="danger" :disabled="row.goodsCount > 0" @click="handleDeleteOne(row)">
                      删除
                    </el-button>
                  </span>
                </el-tooltip>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="categoryPage"
          v-model:page-size="categoryPageSize"
          :total="mallStore.categoryTotal"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @current-change="loadCategoryPage"
          @size-change="loadCategoryPage"
        />
      </div>
    </div>

    <el-dialog v-model="editDialogVisible" title="编辑分类" width="480px">
      <el-form label-width="90px">
        <el-form-item label="分类名称" required>
          <el-input v-model="editForm.name" placeholder="分类名称" />
        </el-form-item>
        <el-form-item label="分类图标">
          <div class="icon-upload-wrap">
            <el-upload
              class="category-icon-upload"
              :show-file-list="false"
              :http-request="handleEditIconUpload"
              accept="image/*"
            >
              <img
                v-if="editForm.icon"
                :src="imageFullUrl(editForm.icon)"
                class="icon-preview"
                alt="图标"
              />
              <el-icon v-else class="icon-placeholder"><Plus /></el-icon>
            </el-upload>
            <el-button
              v-if="editForm.icon"
              link
              type="danger"
              size="small"
              class="icon-clear"
              @click="editForm.icon = ''"
            >
              清除
            </el-button>
          </div>
        </el-form-item>
        <el-form-item label="排序" required>
          <el-input-number v-model="editForm.sort" :min="1" />
        </el-form-item>
        <el-form-item label="启用">
          <el-switch v-model="editForm.status" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="danger" :loading="editSubmitting" @click="handleSaveEdit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="addDialogVisible" title="新增分类" width="480px" @closed="resetAddForm">
      <el-form label-width="90px">
        <el-form-item label="分类名称" required>
          <el-input v-model="formData.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="分类图标">
          <div class="icon-upload-wrap">
            <el-upload
              class="category-icon-upload"
              :show-file-list="false"
              :http-request="handleAddIconUpload"
              accept="image/*"
            >
              <img
                v-if="formData.icon"
                :src="imageFullUrl(formData.icon)"
                class="icon-preview"
                alt="图标"
              />
              <el-icon v-else class="icon-placeholder"><Plus /></el-icon>
            </el-upload>
            <el-button
              v-if="formData.icon"
              link
              type="danger"
              size="small"
              class="icon-clear"
              @click="formData.icon = ''"
            >
              清除
            </el-button>
          </div>
        </el-form-item>
        <el-form-item label="排序" required>
          <el-input-number v-model="formData.sort" :min="1" />
        </el-form-item>
        <el-form-item label="启用">
          <el-switch v-model="formData.status" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="danger" :loading="addSubmitting" @click="handleAddCategory">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Search } from "@element-plus/icons-vue";
import { useMallStore } from "../../../../store/modules/mall";
import { uploadImage, deleteCategory, batchDeleteCategory } from "../../../../api/yuan-mall-module";
import type { AdminCategory } from "../../../../types/mall";

const baseURL = import.meta.env.VITE_API_BASE_URL || "http://127.0.0.1:8080";
const imageFullUrl = (path: string) =>
  path && path.startsWith("http") ? path : baseURL + path;

function isImageUrl(val: string): boolean {
  if (!val || typeof val !== "string") return false;
  return val.startsWith("http") || val.startsWith("/");
}

const mallStore = useMallStore();
const selectedCategories = ref<AdminCategory[]>([]);
const onSelectionChange = (rows: AdminCategory[]) => {
  selectedCategories.value = rows;
};

const formData = reactive<{ name: string; icon: string; sort: number; status: boolean }>({
  name: "",
  icon: "",
  sort: 1,
  status: true,
});

const addSubmitting = ref(false);
const addDialogVisible = ref(false);
const searchName = ref("");
const categoryPage = ref(1);
const categoryPageSize = ref(10);
const enabledCount = computed(() => mallStore.categoryList.filter((item) => item.status).length);
const currentPageGoodsCount = computed(() =>
  mallStore.categoryList.reduce((sum, item) => sum + item.goodsCount, 0)
);

const loadCategoryPage = () => {
  mallStore.fetchCategoryList({
    nameKeyword: searchName.value.trim() || undefined,
    pageNum: categoryPage.value,
    pageSize: categoryPageSize.value,
  });
};

const handleSearch = () => {
  categoryPage.value = 1;
  loadCategoryPage();
};
const handleResetSearch = () => {
  searchName.value = "";
  categoryPage.value = 1;
  loadCategoryPage();
};
const openAddDialog = () => {
  resetAddForm();
  addDialogVisible.value = true;
};
const resetAddForm = () => {
  formData.name = "";
  formData.icon = "";
  formData.sort = 1;
  formData.status = true;
};

const editDialogVisible = ref(false);
const editSubmitting = ref(false);
const editForm = reactive<{ id: number; name: string; icon: string; sort: number; status: boolean }>({
  id: 0,
  name: "",
  icon: "",
  sort: 1,
  status: true,
});

const handleAddIconUpload = async (options: { file: File }) => {
  try {
    const url = await uploadImage(options.file);
    formData.icon = url;
    ElMessage.success("图标上传成功");
  } catch (e) {
    ElMessage.error((e as Error).message || "图标上传失败");
  }
};

const handleEditIconUpload = async (options: { file: File }) => {
  try {
    const url = await uploadImage(options.file);
    editForm.icon = url;
    ElMessage.success("图标上传成功");
  } catch (e) {
    ElMessage.error((e as Error).message || "图标上传失败");
  }
};

const handleAddCategory = async () => {
  if (!formData.name.trim()) {
    ElMessage.warning("请输入分类名称");
    return;
  }
  addSubmitting.value = true;
  try {
    await mallStore.addCategory({
      name: formData.name.trim(),
      icon: formData.icon || undefined,
      sort: formData.sort,
      status: formData.status,
    });
    addDialogVisible.value = false;
    resetAddForm();
    ElMessage.success("分类新增成功");
  } catch (e) {
    ElMessage.error((e as Error).message || "分类新增失败");
  } finally {
    addSubmitting.value = false;
  }
};

const openEditDialog = (row: AdminCategory) => {
  editForm.id = row.id;
  editForm.name = row.name;
  editForm.icon = row.icon || "";
  editForm.sort = row.sort;
  editForm.status = row.status;
  editDialogVisible.value = true;
};

const handleSaveEdit = async () => {
  if (!editForm.name.trim()) {
    ElMessage.warning("请输入分类名称");
    return;
  }
  editSubmitting.value = true;
  try {
    await mallStore.updateCategoryItem({
      id: editForm.id,
      name: editForm.name.trim(),
      icon: editForm.icon || undefined,
      sort: editForm.sort,
      status: editForm.status,
    });
    editDialogVisible.value = false;
    ElMessage.success("分类已更新");
  } finally {
    editSubmitting.value = false;
  }
};

const toggleStatus = async (id: number) => {
  await mallStore.toggleCategoryStatus(id);
  ElMessage.success("分类状态已更新");
};

const handleDeleteOne = async (row: AdminCategory) => {
  if (row.goodsCount > 0) return;
  try {
    await ElMessageBox.confirm(`确定删除分类「${row.name}」？`, "删除确认", {
      type: "warning",
      confirmButtonText: "删除",
      cancelButtonText: "取消",
    });
  } catch {
    return;
  }
  try {
    await deleteCategory({ id: row.id });
    ElMessage.success("已删除");
    await mallStore.fetchCategoryOptions();
    await loadCategoryPage();
  } catch (e) {
    ElMessage.error((e as Error).message || "删除失败");
  }
};

const handleBatchDelete = async () => {
  const rows = selectedCategories.value.filter((r) => (r.goodsCount ?? 0) === 0);
  const blocked = selectedCategories.value.length - rows.length;
  if (!rows.length) {
    ElMessage.warning(blocked ? "所选分类下均有商品，无法删除" : "请先勾选分类");
    return;
  }
  try {
    await ElMessageBox.confirm(
      `将删除 ${rows.length} 个分类${blocked ? `（${blocked} 个因含商品已跳过）` : ""}，是否继续？`,
      "批量删除",
      { type: "warning", confirmButtonText: "删除", cancelButtonText: "取消" }
    );
  } catch {
    return;
  }
  try {
    await batchDeleteCategory({ ids: rows.map((r) => r.id) });
    ElMessage.success("已删除");
    selectedCategories.value = [];
    await mallStore.fetchCategoryOptions();
    await loadCategoryPage();
  } catch (e) {
    ElMessage.error((e as Error).message || "删除失败");
  }
};

onMounted(() => {
  loadCategoryPage();
});
</script>

<style lang="scss" scoped>
.page-wrap {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.card-desc {
  color: var(--ya-text-muted);
  font-size: 12px;
}

.card-title {
  margin-bottom: 8px;
  color: #111827;
  font-size: 18px;
  font-weight: 700;
}

.btn-add {
  margin-left: 4px;
}

.icon-upload-wrap {
  display: flex;
  align-items: center;
  gap: 8px;
}

.category-icon-upload {
  width: 64px;
  height: 64px;
  border: 1px dashed var(--el-border-color);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  overflow: hidden;
  background: var(--el-fill-color-light);

  &:hover {
    border-color: var(--el-color-danger);
  }
}

.icon-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.icon-placeholder {
  font-size: 24px;
  color: var(--el-text-color-placeholder);
}

.icon-clear {
  flex-shrink: 0;
}

.table-icon-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
}

.table-icon-img {
  max-width: 36px;
  max-height: 36px;
  object-fit: contain;
  border-radius: 4px;
}

.table-icon-emoji {
  font-size: 20px;
  line-height: 1;
}

.table-wrap {
  overflow-x: auto;
}

.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
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
}
</style>
