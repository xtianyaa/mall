<template>
  <div class="page-wrap">
    <div class="summary-grid">
      <div class="summary-card">
        <div class="summary-label">发放记录</div>
        <div class="summary-value">{{ mallStore.userCouponTotal }}</div>
      </div>
      <div class="summary-card">
        <div class="summary-label">当前页待使用</div>
        <div class="summary-value">{{ unusedCount }}</div>
      </div>
      <div class="summary-card">
        <div class="summary-label">当前页已使用</div>
        <div class="summary-value">{{ usedCount }}</div>
      </div>
    </div>

    <div class="content-card">
      <div class="card-head">
        <div>
          <div class="card-title">用户优惠券台账</div>
          <div class="card-desc">快速查看优惠券发放状态、使用进度和有效期分布。</div>
        </div>
      </div>
      <div class="table-wrap">
        <el-table :data="mallStore.userCouponList">
          <el-table-column prop="userName" label="用户昵称" min-width="140" />
          <el-table-column prop="couponName" label="券名称" min-width="160" />
          <el-table-column label="面额 / 门槛" min-width="200">
            <template #default="{ row }">
              <span class="amount-line">
                ￥{{ row.couponAmount }}（满 {{ row.useThreshold }} 元可用）
              </span>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="120">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="expireTime" label="到期时间" width="180" />
        </el-table>
      </div>
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="userCouponPage"
          v-model:page-size="userCouponPageSize"
          :total="mallStore.userCouponTotal"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @current-change="loadUserCouponPage"
          @size-change="loadUserCouponPage"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useMallStore } from "../../../../store/modules/mall";

const mallStore = useMallStore();
const userCouponPage = ref(1);
const userCouponPageSize = ref(10);

const unusedCount = computed(
  () => mallStore.userCouponList.filter((item) => item.status === "unused").length
);
const usedCount = computed(
  () => mallStore.userCouponList.filter((item) => item.status === "used").length
);

const getStatusText = (status: string) => {
  if (status === "unused") {
    return "待使用";
  }
  if (status === "used") {
    return "已使用";
  }
  return "已过期";
};

const getStatusType = (status: string) => {
  if (status === "unused") {
    return "success";
  }
  if (status === "used") {
    return "info";
  }
  return "warning";
};

const loadUserCouponPage = async () => {
  await mallStore.fetchUserCouponList({
    pageNum: userCouponPage.value,
    pageSize: userCouponPageSize.value,
  });
};

onMounted(loadUserCouponPage);
</script>

<style lang="scss" scoped>
.page-wrap {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.summary-card {
  padding: 16px 18px;
  border-radius: 16px;
  background: #ffffff;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.05);
}

.content-card {
  padding: 20px 24px;
  border-radius: 18px;
  background: #ffffff;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.05);
}

.summary-label,
.card-desc {
  color: #6b7280;
  font-size: 12px;
}

.summary-value {
  margin-top: 8px;
  color: #111827;
  font-size: 24px;
  font-weight: 700;
}

.card-title {
  margin-bottom: 8px;
  color: #111827;
  font-size: 18px;
  font-weight: 700;
}

.amount-line {
  color: #111827;
  font-size: 13px;
}

.card-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
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
