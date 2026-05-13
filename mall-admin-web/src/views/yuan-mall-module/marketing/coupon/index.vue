<template>
  <div class="page-wrap">
    <div class="summary-grid">
      <div class="summary-card">
        <div class="summary-label">优惠券总量</div>
        <div class="summary-value">{{ mallStore.couponTotal }}</div>
      </div>
      <div class="summary-card">
        <div class="summary-label">当前页投放中</div>
        <div class="summary-value">{{ activeCouponCount }}</div>
      </div>
      <div class="summary-card">
        <div class="summary-label">当前页累计领取</div>
        <div class="summary-value">{{ currentPageReceiveCount }}</div>
      </div>
    </div>

    <div class="content-card table-card">
      <div class="filter-row">
        <div>
          <div class="card-title">优惠券管理</div>
          <div class="card-desc">配置面额、使用门槛和投放状态，按分页浏览活动券池。当前页共 {{ mallStore.couponList.length }} 条。</div>
        </div>
        <div class="filter-toolbar">
          <el-button type="primary" class="btn-add" @click="openAddDialog">新增优惠券</el-button>
        </div>
      </div>
      <div class="table-wrap">
        <el-table :data="mallStore.couponList">
          <el-table-column prop="name" label="优惠券名称" min-width="120" />
          <el-table-column prop="amount" label="面额" width="100" />
          <el-table-column prop="minAmount" label="门槛" width="100" />
          <el-table-column label="可用时间" min-width="280">
            <template #default="{ row }">
              {{ row.startTime ? formatDateTime(row.startTime) : "立即" }} ～ {{ row.expireTime ? formatDateTime(row.expireTime) : "—" }}
            </template>
          </el-table-column>
          <el-table-column prop="receiveCount" label="领取量" width="100" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status ? 'success' : 'info'">
                {{ row.status ? "投放中" : "已停用" }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="couponPage"
          v-model:page-size="couponPageSize"
          :total="mallStore.couponTotal"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @current-change="loadCouponPage"
          @size-change="loadCouponPage"
        />
      </div>
    </div>

    <el-dialog v-model="addDialogVisible" title="新增优惠券" width="520px" @closed="resetAddForm">
      <el-form :model="formData" label-width="100px" class="add-form">
        <el-form-item label="优惠券名称" required>
          <el-input v-model="formData.name" placeholder="请输入优惠券名称" clearable />
        </el-form-item>
        <el-form-item label="面额" required>
          <el-input-number v-model="formData.amount" :min="1" :max="9999" style="width: 100%" />
        </el-form-item>
        <el-form-item label="使用门槛" required>
          <el-input-number v-model="formData.minAmount" :min="0" :max="99999" style="width: 100%" />
        </el-form-item>
        <el-form-item label="可用开始时间">
          <el-date-picker
            v-model="formData.startTime"
            type="datetime"
            placeholder="不填则立即生效"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
            clearable
          />
        </el-form-item>
        <el-form-item label="可用结束时间">
          <el-date-picker
            v-model="formData.expireTime"
            type="datetime"
            placeholder="不填则默认 30 天后"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
            clearable
          />
        </el-form-item>
        <el-form-item label="投放状态">
          <el-switch v-model="formData.status" active-text="投放中" inactive-text="已停用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="addSubmitting" @click="handleAddCoupon">创建优惠券</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { useMallStore } from "../../../../store/modules/mall";

const mallStore = useMallStore();
const couponPage = ref(1);
const couponPageSize = ref(10);
const addDialogVisible = ref(false);
const addSubmitting = ref(false);
const activeCouponCount = computed(() => mallStore.couponList.filter((item) => item.status).length);
const currentPageReceiveCount = computed(() =>
  mallStore.couponList.reduce((sum, item) => sum + item.receiveCount, 0)
);
const formData = reactive<{
  name: string;
  amount: number;
  minAmount: number;
  status: boolean;
  startTime?: string | null;
  expireTime?: string | null;
}>({
  name: "",
  amount: 10,
  minAmount: 59,
  status: true,
  startTime: null,
  expireTime: null,
});

const formatDateTime = (val: string | null | undefined) => {
  if (!val) return "";
  const d = new Date(val);
  return isNaN(d.getTime()) ? val : d.toLocaleString("zh-CN", { year: "numeric", month: "2-digit", day: "2-digit", hour: "2-digit", minute: "2-digit" });
};

const loadCouponPage = async () => {
  await mallStore.fetchCouponList({
    pageNum: couponPage.value,
    pageSize: couponPageSize.value,
  });
};

const openAddDialog = () => {
  resetAddForm();
  addDialogVisible.value = true;
};

const resetAddForm = () => {
  formData.name = "";
  formData.amount = 10;
  formData.minAmount = 59;
  formData.status = true;
  formData.startTime = null;
  formData.expireTime = null;
};

const handleAddCoupon = async () => {
  if (!formData.name?.trim()) {
    ElMessage.warning("请输入优惠券名称");
    return;
  }
  addSubmitting.value = true;
  try {
    await mallStore.addCoupon({
      name: formData.name,
      amount: formData.amount,
      minAmount: formData.minAmount,
      status: formData.status,
      startTime: formData.startTime || undefined,
      expireTime: formData.expireTime || undefined,
    });
    addDialogVisible.value = false;
    couponPage.value = 1;
    await loadCouponPage();
    ElMessage.success("优惠券创建成功");
  } finally {
    addSubmitting.value = false;
  }
};

onMounted(loadCouponPage);
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
  margin-bottom: 8px;
  color: #111827;
  font-size: 18px;
  font-weight: 700;
}

.btn-add {
  margin-left: 4px;
}

.add-form :deep(.el-form-item) {
  margin-bottom: 18px;
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
}
</style>
