<template>
  <div class="page-wrap">
    <div class="stat-grid">
      <div class="stat-card">
        <div class="stat-label">今日支付订单</div>
        <div class="stat-value">{{ mallStore.dashboardData.todayOrderCount }}</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">今日成交额</div>
        <div class="stat-value">￥{{ mallStore.dashboardData.todayAmount }}</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">待发货订单</div>
        <div class="stat-value">{{ mallStore.dashboardData.waitShipCount }}</div>
      </div>
    </div>

    <el-row :gutter="12">
      <el-col :span="12">
        <div class="content-card">
          <div class="card-title">热销商品</div>
          <div class="table-wrap">
            <el-table :data="hotGoodsList">
              <el-table-column prop="name" label="商品名称" />
              <el-table-column prop="monthlySales" label="月销" width="100" />
              <el-table-column prop="stock" label="库存" width="100" />
            </el-table>
          </div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="content-card">
          <div class="card-title">订单动态</div>
          <div class="timeline-wrap">
            <el-timeline>
              <el-timeline-item
                v-for="item in recentOrderTimeline"
                :key="item.orderId"
                :timestamp="item.time"
              >
                {{ item.desc }}
              </el-timeline-item>
              <el-timeline-item
                v-if="!recentOrderTimeline.length"
                timestamp="-"
              >
                暂无最新订单
              </el-timeline-item>
            </el-timeline>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from "vue";
import { useMallStore } from "../../store/modules/mall";

const mallStore = useMallStore();

// 热销商品：基于实际商品列表，按月销倒序取前 10 条
const hotGoodsList = computed(() =>
  [...mallStore.goodsList].sort((a, b) => b.monthlySales - a.monthlySales).slice(0, 10)
);

// 订单动态：基于最近创建的订单列表构造时间线
const recentOrderTimeline = computed(() =>
  [...mallStore.orderList]
    .sort((a, b) => (a.createTime < b.createTime ? 1 : -1))
    .slice(0, 5)
    .map((item) => ({
      orderId: item.orderId,
      time: item.createTime.slice(11, 16),
      desc: `订单 ${item.orderId} ${item.status}，金额 ￥${item.payAmount.toFixed(2)}`,
    }))
);

onMounted(async () => {
  await Promise.all([
    mallStore.fetchDashboard(),
    mallStore.fetchGoodsList({ pageNum: 1, pageSize: 100 }),
    mallStore.fetchOrderList({ pageNum: 1, pageSize: 20 }),
  ]);
});
</script>

<style scoped>
.page-wrap {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(210px, 1fr));
  gap: 12px;
  margin-bottom: 0;
}

.stat-card {
  padding: 14px 16px;
  border-radius: 10px;
  background: #fff;
  box-shadow: 0 1px 3px rgba(15, 23, 42, 0.06);
}

.stat-label {
  font-size: 13px;
  color: #6b7280;
}

.stat-value {
  margin-top: 8px;
  font-size: 22px;
  font-weight: 600;
  color: #111827;
}

.content-card {
  height: 100%;
  padding: 12px 16px 10px;
  border-radius: 10px;
  background: #fff;
  box-shadow: 0 1px 3px rgba(15, 23, 42, 0.06);
  display: flex;
  flex-direction: column;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 8px;
}

.table-wrap {
  flex: 1;
  min-height: 0;
}

.table-wrap,
.timeline-wrap {
  max-height: 260px;
  overflow-y: auto;
}

.content-card :deep(.el-timeline-item__timestamp) {
  font-size: 12px;
  color: #6b7280;
}

.content-card :deep(.el-timeline-item__content) {
  font-size: 13px;
  color: #374151;
}
</style>
