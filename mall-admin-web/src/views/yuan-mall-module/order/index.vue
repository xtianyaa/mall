<template>
  <div class="page-wrap">
    <div class="summary-grid">
      <div class="summary-card">
        <div class="summary-label">订单总量</div>
        <div class="summary-value">{{ mallStore.orderTotal }}</div>
      </div>
      <div class="summary-card">
        <div class="summary-label">当前页待发货</div>
        <div class="summary-value">{{ waitShipCount }}</div>
      </div>
      <div class="summary-card">
        <div class="summary-label">当前页配送中</div>
        <div class="summary-value">{{ shippingCount }}</div>
      </div>
    </div>

    <div class="content-card">
      <div class="card-head">
        <div>
          <div class="card-title">订单列表</div>
          <div class="card-desc">支持按订单状态筛选，并完成发货、关单、签收和物流查看等操作。</div>
        </div>
        <div class="filters">
          <el-select
            v-model="statusFilter"
            clearable
            placeholder="全部状态"
            style="width: 160px"
            @change="onOrderFilterChange"
          >
            <el-option label="待支付" value="待支付" />
            <el-option label="待发货" value="待发货" />
            <el-option label="配送中" value="配送中" />
            <el-option label="已完成" value="已完成" />
            <el-option label="已取消" value="已取消" />
          </el-select>
        </div>
      </div>
      <div class="table-wrap">
        <el-table :data="mallStore.orderList">
          <el-table-column prop="orderId" label="订单编号" min-width="220" />
          <el-table-column prop="userName" label="用户" width="120" />
          <el-table-column prop="payAmount" label="实付金额" width="120" />
          <el-table-column prop="createTime" label="下单时间" width="180" />
          <el-table-column label="状态" min-width="160">
            <template #default="{ row }">
              <span class="status-cell">
                <el-tag :type="getStatusType(row.status)" size="small">
                  {{ getStatusText(row.status) }}
                </el-tag>
                <template v-if="(row.afterSaleCount ?? 0) > 0 || row.isDiscreet">
                  <span class="status-extra">
                    <span v-if="(row.afterSaleCount ?? 0) > 0" class="status-dot status-after-sale">{{ row.afterSaleCount }}笔售后</span>
                    <span v-if="row.isDiscreet" class="status-dot status-discreet">隐秘</span>
                  </span>
                </template>
              </span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="160" fixed="right">
            <template #default="{ row }">
              <div class="op-btns">
                <el-button link type="primary" @click="openDetailDialog(row)">
                  详情
                </el-button>
                <el-dropdown trigger="click" @command="(cmd) => handleOrderCommand(cmd, row)">
                  <el-button link type="info">
                    更多 <el-icon class="el-icon--right"><ArrowDown /></el-icon>
                  </el-button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item
                        command="ship"
                        :disabled="row.status !== '待发货'"
                      >
                        发货
                      </el-dropdown-item>
                      <el-dropdown-item
                        command="cancel"
                        :disabled="row.status !== '待支付'"
                      >
                        关闭订单
                      </el-dropdown-item>
                      <el-dropdown-item
                        command="finish"
                        :disabled="row.status !== '配送中'"
                      >
                        完成
                      </el-dropdown-item>
                      <el-dropdown-item divided command="logistics" :disabled="row.status !== '配送中' && row.status !== '已完成'">
                        查看物流
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="orderPage"
          v-model:page-size="orderPageSize"
          :total="mallStore.orderTotal"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @current-change="loadOrderPage"
          @size-change="loadOrderPage"
        />
      </div>
    </div>

    <el-dialog
      v-model="detailDialogVisible"
      title="订单详情"
      width="560px"
      @open="loadOrderDetail"
    >
      <div v-if="detailOrderId" class="detail-dialog-body">
        <div v-if="detailLoading" class="detail-loading">加载中...</div>
        <template v-else-if="orderDetail">
          <el-descriptions :column="1" border size="small">
            <el-descriptions-item label="订单编号">{{ orderDetail.id }}</el-descriptions-item>
            <el-descriptions-item label="订单状态">
              <el-tag :type="getDetailStatusType(orderDetail.status)" size="small">
                {{ getDetailStatusText(orderDetail.status) }}
              </el-tag>
              <el-tag v-if="orderDetail.isDiscreet" type="danger" size="small" effect="dark" style="margin-left: 8px">
                隐秘发货
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="下单时间">{{ orderDetail.createTime }}</el-descriptions-item>
            <el-descriptions-item label="配送地址">{{ orderDetail.addressText }}</el-descriptions-item>
            <el-descriptions-item v-if="orderDetail.expressCompany" label="快递公司">
              {{ orderDetail.expressCompany }}
            </el-descriptions-item>
            <el-descriptions-item v-if="orderDetail.expressNo" label="快递单号">
              {{ orderDetail.expressNo }}
            </el-descriptions-item>
            <el-descriptions-item v-if="orderDetail.shipTime" label="发货时间">
              {{ orderDetail.shipTime }}
            </el-descriptions-item>
          </el-descriptions>
          <div class="detail-section-title">商品清单</div>
          <el-table :data="orderDetail.itemList" size="small" border max-height="240">
            <el-table-column prop="name" label="商品名称" min-width="180" show-overflow-tooltip />
            <el-table-column prop="price" label="单价" width="90" align="right">
              <template #default="{ row }">￥{{ Number(row.price).toFixed(2) }}</template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="70" align="center" />
            <el-table-column label="小计" width="90" align="right">
              <template #default="{ row }">
                ￥{{ (Number(row.price) * row.quantity).toFixed(2) }}
              </template>
            </el-table-column>
          </el-table>
          <div class="detail-amount">
            <span>订单金额</span>
            <span>￥{{ orderDetail.totalAmount.toFixed(2) }}</span>
          </div>
          <div class="detail-amount total">
            <span>实付金额</span>
            <span>￥{{ orderDetail.payAmount.toFixed(2) }}</span>
          </div>
        </template>
      </div>
    </el-dialog>

    <el-dialog
      v-model="logisticsDialogVisible"
      title="物流进度"
      width="480px"
      @open="loadLogistics"
    >
      <div v-if="logisticsOrderId" class="logistics-dialog-body">
        <div v-if="logisticsLoading" class="logistics-loading">加载中...</div>
        <template v-else>
          <div v-if="logisticsList.length === 0" class="logistics-empty">暂无物流信息（未发货或单号未录入）</div>
          <div v-else class="logistics-timeline">
            <div
              v-for="(trace, index) in logisticsList"
              :key="index"
              class="logistics-trace-item"
              :class="{ first: index === 0 }"
            >
              <div class="trace-dot" />
              <div v-if="index < logisticsList.length - 1" class="trace-line" />
              <div class="trace-content">
                <div class="trace-desc">{{ trace.desc }}</div>
                <div class="trace-time">{{ trace.time }}</div>
              </div>
            </div>
          </div>
        </template>
      </div>
    </el-dialog>

    <el-dialog
      v-model="shipDialogVisible"
      title="填写物流信息"
      width="420px"
      :close-on-click-modal="false"
      @closed="resetShipForm"
    >
      <el-form ref="shipFormRef" :model="shipForm" :rules="shipRules" label-width="90px">
        <el-form-item label="订单编号">
          <span class="ship-order-no">{{ shipForm.orderId }}</span>
        </el-form-item>
        <el-form-item label="快递公司" prop="expressCompany">
          <el-select
            v-model="shipForm.expressCompany"
            placeholder="请选择快递公司"
            filterable
            style="width: 100%"
          >
            <el-option label="顺丰速运" value="顺丰速运" />
            <el-option label="中通快递" value="中通快递" />
            <el-option label="圆通速递" value="圆通速递" />
            <el-option label="韵达快递" value="韵达快递" />
            <el-option label="申通快递" value="申通快递" />
            <el-option label="邮政EMS" value="邮政EMS" />
            <el-option label="极兔速递" value="极兔速递" />
            <el-option label="德邦快递" value="德邦快递" />
          </el-select>
        </el-form-item>
        <el-form-item label="快递单号" prop="expressNo">
          <el-input v-model="shipForm.expressNo" placeholder="请输入快递单号" clearable />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="shipSubmitting" @click="submitShip">
          确认发货
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { ElMessage } from "element-plus";
import { ArrowDown } from "@element-plus/icons-vue";
import type { FormInstance, FormRules } from "element-plus";
import {
  getOrderDetail,
  getOrderLogistics,
  type AdminOrderDetail,
  type LogisticsTrace,
} from "../../../api/yuan-mall-module";
import { useMallStore } from "../../../store/modules/mall";

const mallStore = useMallStore();
const statusFilter = ref<string | undefined>(undefined);
const orderPage = ref(1);
const orderPageSize = ref(10);
const detailDialogVisible = ref(false);
const detailOrderId = ref("");
const orderDetail = ref<AdminOrderDetail | null>(null);
const detailLoading = ref(false);
const shipDialogVisible = ref(false);
const logisticsDialogVisible = ref(false);
const logisticsOrderId = ref("");
const logisticsList = ref<LogisticsTrace[]>([]);
const logisticsLoading = ref(false);
const shipFormRef = ref<FormInstance>();
const shipSubmitting = ref(false);
const shipForm = ref({
  orderId: "",
  expressCompany: "",
  expressNo: "",
});
const shipRules: FormRules = {
  expressCompany: [{ required: true, message: "请选择快递公司", trigger: "change" }],
  expressNo: [{ required: true, message: "请输入快递单号", trigger: "blur" }],
};
const waitShipCount = computed(() => mallStore.orderList.filter((item) => item.status === "待发货").length);
const shippingCount = computed(() => mallStore.orderList.filter((item) => item.status === "配送中").length);
const statusTextMap = {
  待支付: "待支付",
  待发货: "待发货",
  配送中: "配送中",
  已完成: "已完成",
  已取消: "已取消",
};
const statusTypeMap = {
  待支付: "info",
  待发货: "warning",
  配送中: "primary",
  已完成: "success",
  已取消: "danger",
} as const;

const getStatusText = (status: keyof typeof statusTextMap) => statusTextMap[status];

const getStatusType = (status: keyof typeof statusTypeMap) => statusTypeMap[status];

const detailStatusTextMap: Record<string, string> = {
  pending: "待支付",
  paid: "待发货",
  shipping: "配送中",
  finished: "已完成",
  cancelled: "已取消",
};
const detailStatusTypeMap: Record<string, "info" | "warning" | "primary" | "success" | "danger"> = {
  pending: "info",
  paid: "warning",
  shipping: "primary",
  finished: "success",
  cancelled: "danger",
};
const getDetailStatusText = (status: string) => detailStatusTextMap[status] ?? status;
const getDetailStatusType = (status: string) => detailStatusTypeMap[status] ?? "info";

const loadOrderPage = () => {
  mallStore.fetchOrderList({
    status: statusFilter.value || undefined,
    pageNum: orderPage.value,
    pageSize: orderPageSize.value,
  });
};

const onOrderFilterChange = () => {
  orderPage.value = 1;
  loadOrderPage();
};

const handleOrderCommand = (command: string, row: { orderId: string }) => {
  switch (command) {
    case "ship":
      openShipDialog(row);
      break;
    case "cancel":
      handleCancel(row.orderId);
      break;
    case "finish":
      handleFinish(row.orderId);
      break;
    case "logistics":
      openLogisticsDialog(row);
      break;
  }
};

const openDetailDialog = (row: { orderId: string }) => {
  detailOrderId.value = row.orderId;
  orderDetail.value = null;
  detailDialogVisible.value = true;
};

const loadOrderDetail = async () => {
  if (!detailOrderId.value) return;
  detailLoading.value = true;
  try {
    orderDetail.value = await getOrderDetail(detailOrderId.value);
  } catch (e: any) {
    ElMessage.error(e?.message || "获取订单详情失败");
    orderDetail.value = null;
  } finally {
    detailLoading.value = false;
  }
};

const openShipDialog = (row: { orderId: string }) => {
  shipForm.value.orderId = row.orderId;
  shipForm.value.expressCompany = "";
  shipForm.value.expressNo = "";
  shipDialogVisible.value = true;
};

const resetShipForm = () => {
  shipForm.value = { orderId: "", expressCompany: "", expressNo: "" };
  shipFormRef.value?.resetFields();
};

const submitShip = async () => {
  if (!shipFormRef.value) return;
  await shipFormRef.value.validate(async (valid) => {
    if (!valid) return;
    shipSubmitting.value = true;
    try {
      await mallStore.shipOrder({
        orderId: shipForm.value.orderId,
        expressCompany: shipForm.value.expressCompany,
        expressNo: shipForm.value.expressNo,
      });
      ElMessage.success("发货成功，用户可查看物流进度");
      shipDialogVisible.value = false;
    } catch (e: any) {
      ElMessage.error(e?.message || "发货失败");
    } finally {
      shipSubmitting.value = false;
    }
  });
};

const handleCancel = async (id: string) => {
  await mallStore.updateOrderStatus(id, "已取消");
  ElMessage.success("订单已关闭");
};

const handleFinish = async (id: string) => {
  await mallStore.updateOrderStatus(id, "已完成");
  ElMessage.success("订单已完成");
};

const openLogisticsDialog = (row: { orderId: string }) => {
  logisticsOrderId.value = row.orderId;
  logisticsList.value = [];
  logisticsDialogVisible.value = true;
};

const loadLogistics = async () => {
  if (!logisticsOrderId.value) return;
  logisticsLoading.value = true;
  try {
    logisticsList.value = await getOrderLogistics(logisticsOrderId.value);
  } catch (e: any) {
    ElMessage.error(e?.message || "获取物流失败");
    logisticsList.value = [];
  } finally {
    logisticsLoading.value = false;
  }
};

onMounted(() => {
  loadOrderPage();
});
</script>

<style lang="scss" scoped>
/* 汇总与卡片使用全局 style.css；此处仅保留页面独有样式 */
.card-head {
  margin-bottom: 16px;
}

.filters {
  flex-shrink: 0;
}

.ship-order-no {
  color: var(--el-text-color-regular);
  font-size: 13px;
}

.detail-dialog-body {
  min-height: 120px;
}

.detail-loading {
  text-align: center;
  color: var(--el-text-color-secondary);
  padding: 24px 0;
}

.detail-section-title {
  margin: 16px 0 8px;
  font-size: 13px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.detail-amount {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
  font-size: 13px;
  color: var(--el-text-color-regular);
}

.detail-amount.total {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid var(--el-border-color-lighter);
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.logistics-dialog-body {
  min-height: 120px;
}

.logistics-loading,
.logistics-empty {
  text-align: center;
  color: var(--el-text-color-secondary);
  padding: 24px 0;
}

.logistics-timeline {
  padding-left: 12px;
  position: relative;
}

.logistics-trace-item {
  position: relative;
  padding-bottom: 20px;
}

.logistics-trace-item:last-child {
  padding-bottom: 0;
}

.trace-dot {
  position: absolute;
  left: -12px;
  top: 6px;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--el-color-primary);
  transform: translateX(-50%);
}

.logistics-trace-item.first .trace-dot {
  width: 10px;
  height: 10px;
  top: 5px;
  background: var(--el-color-primary);
}

.trace-line {
  position: absolute;
  left: -12px;
  top: 18px;
  bottom: 0;
  width: 1px;
  background: var(--el-border-color-lighter);
  transform: translateX(-50%);
}

.trace-content {
  padding-left: 4px;
}

.trace-desc {
  color: var(--el-text-color-primary);
  font-size: 13px;
  line-height: 1.5;
}

.trace-time {
  color: var(--el-text-color-secondary);
  font-size: 12px;
  margin-top: 2px;
}

.status-cell {
  display: inline-flex;
  align-items: center;
  flex-wrap: nowrap;
  gap: 6px;
}

.status-extra {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  flex-shrink: 0;
}

.status-dot {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.status-dot.status-after-sale {
  color: var(--el-color-warning);
}

.status-dot.status-discreet {
  color: var(--el-color-danger);
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

  .card-head {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
