<template>
  <div class="page-wrap">
    <div class="summary-grid">
      <div class="summary-card">
        <div class="summary-label">用户总量</div>
        <div class="summary-value">{{ mallStore.userTotal }}</div>
      </div>
      <div class="summary-card">
        <div class="summary-label">当前页订单数</div>
        <div class="summary-value">{{ currentPageOrderCount }}</div>
      </div>
      <div class="summary-card">
        <div class="summary-label">当前页累计消费</div>
        <div class="summary-value">￥{{ currentPageConsumeAmount }}</div>
      </div>
    </div>

    <div class="content-card">
      <div class="card-head">
        <div>
          <div class="card-title">用户列表</div>
          <div class="card-desc">展示会员等级、订单活跃度和累计消费，并支持查看完整用户明细。</div>
        </div>
      </div>
      <div class="table-wrap">
        <el-table :data="mallStore.userList">
          <el-table-column prop="nickName" label="昵称" />
          <el-table-column prop="mobile" label="手机号" />
          <el-table-column prop="levelName" label="会员等级" width="120" />
          <el-table-column prop="orderCount" label="订单数" width="100" />
          <el-table-column prop="consumeAmount" label="累计消费" width="120" />
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="openUserDetail(row.id)">查看详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="userPage"
          v-model:page-size="userPageSize"
          :total="mallStore.userTotal"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @current-change="loadUserPage"
          @size-change="loadUserPage"
        />
      </div>
    </div>

    <el-drawer v-model="drawerVisible" size="760px" :title="drawerTitle">
      <template v-if="currentUser">
        <div class="detail-summary">
          <div class="summary-card">
            <div class="summary-label">会员等级</div>
            <div class="summary-value">{{ currentUser.levelName }}</div>
          </div>
          <div class="summary-card">
            <div class="summary-label">订单总数</div>
            <div class="summary-value">{{ userOrderList.length }}</div>
          </div>
          <div class="summary-card">
            <div class="summary-label">可用优惠券</div>
            <div class="summary-value">{{ unusedCouponCount }}</div>
          </div>
        </div>

        <el-tabs>
          <el-tab-pane :label="`收货地址（${userAddressList.length}）`">
            <el-table :data="userAddressList">
              <el-table-column prop="consignee" label="收货人" width="120" />
              <el-table-column prop="mobile" label="手机号" width="140" />
              <el-table-column prop="addressText" label="收货地址" min-width="320" />
              <el-table-column label="默认地址" width="120">
                <template #default="{ row }">
                  <el-tag :type="row.isDefault ? 'danger' : 'info'">
                    {{ row.isDefault ? "默认" : "普通" }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>

          <el-tab-pane :label="`订单记录（${userOrderList.length}）`">
            <el-table :data="userOrderList">
              <el-table-column prop="orderId" label="订单号" min-width="180" />
              <el-table-column prop="payAmount" label="实付金额" width="120" />
              <el-table-column label="订单状态" width="120">
                <template #default="{ row }">
                  <el-tag :type="getOrderStatusType(row.status)">
                    {{ row.status }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="下单时间" width="180" />
            </el-table>
          </el-tab-pane>

          <el-tab-pane :label="`优惠券（${userCouponList.length}）`">
            <el-table :data="userCouponList">
              <el-table-column prop="couponName" label="券名称" min-width="180" />
              <el-table-column label="面额 / 门槛" width="160">
                <template #default="{ row }"> ￥{{ row.couponAmount }} / ￥{{ row.useThreshold }} </template>
              </el-table-column>
              <el-table-column label="使用状态" width="120">
                <template #default="{ row }">
                  <el-tag :type="getCouponStatusType(row.status)">
                    {{ getCouponStatusText(row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="expireTime" label="到期时间" width="180" />
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useMallStore } from "../../../store/modules/mall";
import type { AdminUser } from "../../../types/mall";

const mallStore = useMallStore();
const drawerVisible = ref(false);
const userPage = ref(1);
const userPageSize = ref(10);
const currentUserId = ref<number | null>(null);

const currentUser = computed<AdminUser | null>(() => {
  const list = mallStore.userList;
  return list.find((item) => item.id === currentUserId.value) || null;
});
const drawerTitle = computed(() =>
  currentUser.value ? `${currentUser.value.nickName} 的用户详情` : "用户详情"
);
const userAddressList = computed(() => mallStore.userDetailAddressList);
const userOrderList = computed(() =>
  mallStore.userDetailOrderList.filter((item) => item.userId === currentUserId.value)
);
const userCouponList = computed(() => mallStore.userDetailCouponList);
const unusedCouponCount = computed(
  () => userCouponList.value.filter((item) => item.status === "unused").length
);
const currentPageOrderCount = computed(() =>
  mallStore.userList.reduce((sum, item) => sum + item.orderCount, 0)
);
const currentPageConsumeAmount = computed(() =>
  mallStore.userList.reduce((sum, item) => sum + Number(item.consumeAmount || 0), 0).toFixed(2)
);

const loadUserPage = () => {
  mallStore.fetchUserList({ pageNum: userPage.value, pageSize: userPageSize.value });
};

const getOrderStatusType = (status: "待发货" | "配送中" | "已完成") => {
  if (status === "待发货") {
    return "warning";
  }
  if (status === "配送中") {
    return "primary";
  }
  return "success";
};

const getCouponStatusText = (status: string) => {
  if (status === "unused") {
    return "待使用";
  }
  if (status === "used") {
    return "已使用";
  }
  return "已过期";
};

const getCouponStatusType = (status: string) => {
  if (status === "unused") {
    return "success";
  }
  if (status === "used") {
    return "info";
  }
  return "warning";
};

const openUserDetail = async (userId: number) => {
  currentUserId.value = userId;
  drawerVisible.value = true;

  await Promise.all([
    mallStore.fetchUserDetailOrders(userId),
    mallStore.fetchUserDetailAddresses(userId),
    mallStore.fetchUserDetailCoupons(userId),
  ]);
};

onMounted(() => {
  loadUserPage();
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

.card-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}

.card-title {
  margin-bottom: 8px;
  color: #111827;
  font-size: 18px;
  font-weight: 700;
}

.card-desc {
  color: var(--ya-text-muted);
  font-size: 13px;
}

.detail-summary {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.detail-summary .summary-card {
  padding: 18px 20px;
  border-radius: 14px;
  background: var(--ya-bg-elevated);
}

.detail-summary .summary-card::before {
  display: none;
}

.detail-summary .summary-value {
  font-size: 22px;
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
  .summary-grid,
  .detail-summary {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media screen and (max-width: 768px) {
  .summary-grid,
  .detail-summary {
    grid-template-columns: 1fr;
  }
}
</style>
