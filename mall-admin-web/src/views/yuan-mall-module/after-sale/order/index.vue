<template>
  <div class="page-wrap">
    <div class="content-card">
      <div class="card-head">
        <div>
          <div class="card-title">售后订单</div>
          <div class="card-desc">查看并处理用户的退款、退货退款和换货申请。</div>
        </div>
        <div class="filters">
          <el-select
            v-model="typeFilter"
            clearable
            placeholder="售后类型"
            style="width: 140px"
            @change="loadAfterSalePage"
          >
            <el-option label="仅退款" value="REFUND_ONLY" />
            <el-option label="退货退款" value="RETURN_REFUND" />
            <el-option label="换货" value="EXCHANGE" />
          </el-select>
          <el-select
            v-model="statusFilter"
            clearable
            placeholder="售后状态"
            style="width: 140px; margin-left: 12px"
            @change="loadAfterSalePage"
          >
            <el-option label="待处理" value="APPLIED" />
            <el-option label="已同意" value="APPROVED" />
            <el-option label="已拒绝" value="REJECTED" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已关闭" value="CLOSED" />
          </el-select>
        </div>
      </div>
      <div class="table-wrap">
        <el-table :data="afterSaleList">
          <el-table-column prop="id" label="售后单号" width="120" />
          <el-table-column label="订单编号" min-width="200">
            <template #default="{ row }">
              {{ row.orderNo || row.orderId }}
            </template>
          </el-table-column>
          <el-table-column prop="userName" label="用户" width="120" />
          <el-table-column label="售后类型" width="110">
            <template #default="{ row }">
              {{ getTypeText(row.type) }}
            </template>
          </el-table-column>
          <el-table-column label="申请金额" width="120" align="right">
            <template #default="{ row }">
              ￥{{ Number(row.applyAmount || 0).toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column prop="applyTime" label="申请时间" width="180" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" style="margin: 0">
                {{ getStatusText(String(row.status || "").trim()) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="160" fixed="right">
            <template #default="{ row }">
              <div class="op-btns">
                <el-button link type="primary" @click="openDetailDialog(row.id)">详情</el-button>
                <el-button
                  link
                  type="danger"
                  :disabled="row.status !== 'APPLIED'"
                  @click="openHandleDialog(row)"
                >
                  审核
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @current-change="loadAfterSalePage"
          @size-change="loadAfterSalePage"
        />
      </div>
    </div>

    <el-dialog
      v-model="detailDialogVisible"
      title="售后详情"
      width="640px"
      @open="loadAfterSaleDetail"
    >
      <div v-if="detailId" class="detail-dialog-body">
        <div v-if="detailLoading" class="detail-loading">加载中...</div>
        <template v-else-if="detail">
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="售后单号">{{ detail.id }}</el-descriptions-item>
            <el-descriptions-item label="售后类型">{{ getTypeText(detail.type) }}</el-descriptions-item>
            <el-descriptions-item label="订单编号">{{ detail.orderNo || detail.orderId }}</el-descriptions-item>
            <el-descriptions-item label="用户">{{ detail.userName }}</el-descriptions-item>
            <el-descriptions-item label="申请金额">
              ￥{{ Number(detail.applyAmount || 0).toFixed(2) }}
            </el-descriptions-item>
            <el-descriptions-item label="预计退款">
              ￥{{ Number(detail.actualAmount || 0).toFixed(2) }}
            </el-descriptions-item>
            <el-descriptions-item label="当前状态" :span="2">
              <el-tag :type="getStatusType(detail.status)" size="small">
                {{ getStatusText(detail.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="申请时间" :span="2">{{ detail.applyTime }}</el-descriptions-item>
          </el-descriptions>

          <div class="detail-section-title">商品信息</div>
          <el-table :data="detail.itemList" size="small" border>
            <el-table-column prop="name" label="商品名称" min-width="200" show-overflow-tooltip />
            <el-table-column label="单价" width="90" align="right">
              <template #default="{ row }">￥{{ Number(row.price ?? 0).toFixed(2) }}</template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="80" align="center" />
          </el-table>

          <div v-if="detail.logList?.length" class="detail-section-title">处理记录</div>
          <el-timeline v-if="detail.logList?.length">
            <el-timeline-item
              v-for="(log, index) in detail.logList"
              :key="index"
              :timestamp="log.createTime"
              placement="top"
            >
              <p>{{ log.remark || log.action }}</p>
            </el-timeline-item>
          </el-timeline>
        </template>
      </div>
    </el-dialog>

    <el-dialog
      v-model="handleDialogVisible"
      title="处理售后申请"
      width="420px"
      :close-on-click-modal="false"
    >
      <el-form :model="handleForm" label-width="90px">
        <el-form-item label="处理结果">
          <el-radio-group v-model="handleForm.action">
            <el-radio label="APPROVE">同意</el-radio>
            <el-radio label="REJECT">拒绝</el-radio>
            <el-radio label="COMPLETE">标记完成</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="handleForm.action === 'APPROVE'" label="退款金额">
          <el-input-number v-model="handleForm.actualAmount" :min="0" :precision="2" :step="1" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="handleForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入处理说明，用户将在小程序售后详情中看到"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="handleSubmitting" @click="submitHandle">
          确认提交
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { ElMessage } from "element-plus";
import type { AfterSaleDetail } from "../../../../mall-miniprogram/src/types/after-sale";
import { getAfterSalePage, getAfterSaleDetail, handleAfterSale } from "../../../../api/yuan-mall-module";

const afterSaleList = ref<AfterSaleDetail[]>([]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);
const typeFilter = ref<string | undefined>(undefined);
const statusFilter = ref<string | undefined>(undefined);

const detailDialogVisible = ref(false);
const detailId = ref<number | null>(null);
const detail = ref<AfterSaleDetail | null>(null);
const detailLoading = ref(false);

const handleDialogVisible = ref(false);
const handleAfterSaleId = ref<number | null>(null);
const handleSubmitting = ref(false);
const handleForm = ref<{
  action: "APPROVE" | "REJECT" | "COMPLETE";
  actualAmount?: number;
  remark?: string;
}>({
  action: "APPROVE",
  actualAmount: undefined,
  remark: "",
});

const getTypeText = (type: string) => {
  const map: Record<string, string> = {
    REFUND_ONLY: "仅退款",
    RETURN_REFUND: "退货退款",
    EXCHANGE: "换货",
  };
  return map[type] || type;
};

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    APPLIED: "待处理",
    APPROVED: "已同意",
    REJECTED: "已拒绝",
    COMPLETED: "已完成",
    CLOSED: "已关闭",
  };
  return map[status] || status;
};

const getStatusType = (status: string) => {
  switch (status) {
    case "APPLIED":
      return "warning";
    case "APPROVED":
      return "success";
    case "COMPLETED":
      return "success";
    case "REJECTED":
      return "danger";
    case "CLOSED":
      return "info";
    default:
      return "info";
  }
};

const loadAfterSalePage = async () => {
  const res = await getAfterSalePage({
    type: typeFilter.value,
    status: statusFilter.value,
    pageNum: pageNum.value,
    pageSize: pageSize.value,
  });
  afterSaleList.value = res.list;
  total.value = res.total;
};

const openDetailDialog = (id: number) => {
  detailId.value = id;
  detailDialogVisible.value = true;
};

const loadAfterSaleDetail = async () => {
  if (!detailId.value) return;
  detailLoading.value = true;
  try {
    detail.value = await getAfterSaleDetail(detailId.value);
  } finally {
    detailLoading.value = false;
  }
};

const openHandleDialog = (row: AfterSaleDetail) => {
  handleAfterSaleId.value = row.id;
  const applyAmount = Number(row.applyAmount ?? 0);
  handleForm.value = {
    action: "APPROVE",
    actualAmount: applyAmount > 0 ? applyAmount : undefined,
    remark: "",
  };
  handleDialogVisible.value = true;
};

const submitHandle = async () => {
  if (!handleAfterSaleId.value) return;
  handleSubmitting.value = true;
  try {
    const payload: { afterSaleId: number; action: string; actualAmount?: number; remark?: string } = {
      afterSaleId: handleAfterSaleId.value,
      action: handleForm.value.action,
      remark: handleForm.value.remark ?? "",
    };
    if (handleForm.value.action === "APPROVE" && handleForm.value.actualAmount != null) {
      payload.actualAmount = handleForm.value.actualAmount;
    }
    await handleAfterSale(payload);
    ElMessage.success("处理成功");
    handleDialogVisible.value = false;
    await loadAfterSalePage();
  } catch (e: any) {
    ElMessage.error(e?.message || "处理失败");
  } finally {
    handleSubmitting.value = false;
  }
};

onMounted(() => {
  loadAfterSalePage();
});
</script>

<style lang="scss" scoped>
.content-card {
  padding: 20px 24px;
}

.card-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
}

.card-desc {
  margin-top: 4px;
  font-size: 13px;
  color: #6b7280;
}

.filters {
  display: flex;
  align-items: center;
}

.table-wrap {
  margin-top: 12px;
}

.pagination-wrap {
  margin-top: 16px;
  text-align: right;
}

.op-btns {
  display: flex;
  gap: 4px;
}

.detail-section-title {
  margin: 18px 0 10px;
  font-size: 14px;
  font-weight: 600;
  color: #111827;
}

.detail-loading {
  padding: 40px 0;
  text-align: center;
  color: #9ca3af;
}
</style>

