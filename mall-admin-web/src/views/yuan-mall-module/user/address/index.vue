<template>
  <div class="page-wrap">
    <div class="summary-grid">
      <div class="summary-card">
        <div class="summary-label">地址总量</div>
        <div class="summary-value">{{ mallStore.userAddressTotal }}</div>
      </div>
      <div class="summary-card">
        <div class="summary-label">当前页默认地址</div>
        <div class="summary-value">{{ defaultCount }}</div>
      </div>
      <div class="summary-card">
        <div class="summary-label">当前页涉及用户</div>
        <div class="summary-value">{{ userCount }}</div>
      </div>
    </div>

    <div class="content-card">
      <div class="card-head">
        <div>
          <div class="card-title">收货地址列表</div>
          <div class="card-desc">统一查看用户收件信息，便于核对默认地址和用户地址覆盖情况。</div>
        </div>
      </div>
      <div class="table-wrap">
        <el-table :data="mallStore.userAddressList">
          <el-table-column prop="userName" label="用户昵称" min-width="140" />
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
      </div>
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="addressPage"
          v-model:page-size="addressPageSize"
          :total="mallStore.userAddressTotal"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @current-change="loadAddressPage"
          @size-change="loadAddressPage"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useMallStore } from "../../../../store/modules/mall";

const mallStore = useMallStore();
const addressPage = ref(1);
const addressPageSize = ref(10);

const defaultCount = computed(
  () => mallStore.userAddressList.filter((item) => item.isDefault).length
);
const userCount = computed(() => new Set(mallStore.userAddressList.map((item) => item.userId)).size);

const loadAddressPage = async () => {
  await mallStore.fetchUserAddressList({
    pageNum: addressPage.value,
    pageSize: addressPageSize.value,
  });
};

onMounted(loadAddressPage);
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
