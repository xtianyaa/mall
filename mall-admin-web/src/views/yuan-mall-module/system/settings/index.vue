<template>
  <div class="page-wrap">
    <div class="content-card">
      <div class="card-title">系统设置</div>
      <el-form label-width="140px" style="max-width: 720px">
        <el-form-item label="商城名称">
          <el-input v-model="mallStore.systemSetting.mallName" />
        </el-form-item>
        <el-form-item label="客服手机号">
          <el-input v-model="mallStore.systemSetting.serviceMobile" />
        </el-form-item>
        <el-form-item label="订单自动取消">
          <el-input v-model="mallStore.systemSetting.cancelMinutes" />
        </el-form-item>
        <el-form-item label="包邮门槛">
          <el-input v-model="mallStore.systemSetting.freeShippingAmount" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSave">保存设置</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from "vue";
import { ElMessage } from "element-plus";
import { useMallStore } from "../../../../store/modules/mall";

const mallStore = useMallStore();

const handleSave = async () => {
  await mallStore.saveSystemSetting({
    mallName: mallStore.systemSetting.mallName,
    serviceMobile: mallStore.systemSetting.serviceMobile,
    cancelMinutes: mallStore.systemSetting.cancelMinutes,
    freeShippingAmount: mallStore.systemSetting.freeShippingAmount,
  });
  ElMessage.success("系统设置已保存");
};

onMounted(async () => {
  await mallStore.fetchSystemSetting();
});
</script>
