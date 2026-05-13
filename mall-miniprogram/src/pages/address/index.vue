<template>
  <scroll-view class="address-scroll" scroll-y show-scrollbar="false">
    <view class="page-container">
      <view v-for="item in mallStore.addressList" :key="item.id" class="card address-item">
        <view class="address-head">
          <text class="address-name">{{ item.name }} {{ item.mobile }}</text>
          <text v-if="item.isDefault" class="default-tag">默认</text>
        </view>
        <view class="address-detail">
          {{ item.province }}{{ item.city }}{{ item.district }}{{ item.detail }}
        </view>
        <view class="address-action">
          <text class="action-link" @click="handleEdit(item.id)">编辑</text>
          <text class="action-link danger-link" @click="handleDelete(item.id)">删除</text>
        </view>
      </view>

      <view class="card form-card">
        <view class="section-title">
          <text class="section-title-text">{{ editingId ? "编辑地址" : "新增地址" }}</text>
          <text v-if="editingId" class="section-title-extra" @click="resetForm">取消编辑</text>
        </view>
        <input v-model="formData.name" class="form-input" placeholder="收货人" />
        <input v-model="formData.mobile" class="form-input" placeholder="手机号" />
        <input v-model="formData.province" class="form-input" placeholder="省份" />
        <input v-model="formData.city" class="form-input" placeholder="城市" />
        <input v-model="formData.district" class="form-input" placeholder="区县" />
        <input v-model="formData.detail" class="form-input" placeholder="详细地址" />
        <label class="default-line">
          <checkbox :checked="formData.isDefault" @click="formData.isDefault = !formData.isDefault" />
          <text>设为默认地址</text>
        </label>
        <button class="save-button" @click="saveAddress">
          {{ editingId ? "更新地址" : "保存地址" }}
        </button>
      </view>
    </view>
  </scroll-view>
</template>

<script setup lang="ts">
import { onShow } from "@dcloudio/uni-app";
import { reactive, ref } from "vue";
import { useMallStore } from "../../store/modules/mall";

const mallStore = useMallStore();

const createDefaultForm = () => ({
  name: "",
  mobile: "",
  province: "浙江省",
  city: "杭州市",
  district: "西湖区",
  detail: "",
  isDefault: false,
});

const formData = reactive(createDefaultForm());
const editingId = ref<number | null>(null);

const resetForm = () => {
  editingId.value = null;
  Object.assign(formData, createDefaultForm());
};

const ensureLogin = () => {
  if (mallStore.hasLogin && mallStore.userInfo?.id) {
    return true;
  }
  uni.showToast({
    title: "请先登录",
    icon: "none",
  });
  setTimeout(() => {
    uni.navigateTo({ url: "/pages/login/index" });
  }, 500);
  return false;
};

const saveAddress = async () => {
  if (!ensureLogin()) {
    return;
  }
  if (!formData.name || !formData.mobile || !formData.detail) {
    uni.showToast({
      title: "请补全地址信息",
      icon: "none",
    });
    return;
  }

  const isEdit = Boolean(editingId.value);

  if (editingId.value) {
    await mallStore.updateAddressAction({
      id: editingId.value,
      ...formData,
    });
  } else {
    await mallStore.addAddress({ ...formData });
  }
  resetForm();
  uni.showToast({
    title: isEdit ? "地址更新成功" : "地址保存成功",
    icon: "success",
  });
};

const handleEdit = (id: number) => {
  if (!ensureLogin()) {
    return;
  }
  const currentAddress = mallStore.addressList.find((item) => item.id === id);
  if (!currentAddress) {
    return;
  }
  editingId.value = id;
  Object.assign(formData, currentAddress);
};

const handleDelete = (id: number) => {
  if (!ensureLogin()) {
    return;
  }
  uni.showModal({
    title: "删除确认",
    content: "删除后将无法恢复，是否继续？",
    success: async (res) => {
      if (!res.confirm) {
        return;
      }
      await mallStore.deleteAddressAction(id);
      if (editingId.value === id) {
        resetForm();
      }
      uni.showToast({
        title: "地址删除成功",
        icon: "success",
      });
    },
  });
};

onShow(() => {
  if (!ensureLogin()) {
    return;
  }
  mallStore.syncUserData();
});
</script>

<style lang="scss" scoped>
.address-scroll {
  height: 100vh;
}

.address-item,
.form-card {
  padding: 24rpx;
  margin-bottom: 20rpx;
}

.address-head {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.address-name {
  font-size: 30rpx;
  font-weight: 600;
}

.default-tag {
  padding: 6rpx 14rpx;
  border-radius: 999rpx;
  background: #fef2f2;
  color: #ef4444;
  font-size: 22rpx;
}

.address-detail {
  margin-top: 14rpx;
  color: #6b7280;
  line-height: 1.6;
}

.address-action {
  display: flex;
  gap: 24rpx;
  margin-top: 16rpx;
}

.action-link {
  color: #111827;
  font-size: 24rpx;
}

.danger-link {
  color: #ef4444;
}

.form-input {
  height: 82rpx;
  margin-bottom: 16rpx;
  padding: 0 24rpx;
  border-radius: 18rpx;
  background: #f3f4f6;
}

.default-line {
  display: flex;
  align-items: center;
  gap: 10rpx;
  margin: 10rpx 0 26rpx;
}

.save-button {
  height: 84rpx;
  line-height: 84rpx;
  border: none;
  border-radius: 999rpx;
  background: #111827;
  color: #ffffff;
}
</style>
