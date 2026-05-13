import { reactive, ref } from "vue";
import { defineStore } from "pinia";
import {
  createCategory as createCategoryApi,
  createCoupon as createCouponApi,
  createGoods as createGoodsApi,
  createBanner as createBannerApi,
  deleteBanner as deleteBannerApi,
  getBannerList,
  getCategoryList,
  getCouponList,
  getDashboard,
  getGoodsList,
  getOrderList,
  getSystemSetting,
  getUserAddressList,
  getUserCouponList,
  getUserList,
  shipOrder as shipOrderApi,
  updateOrderStatus as updateOrderStatusApi,
  updateBanner as updateBannerApi,
  updateBannerSort as updateBannerSortApi,
  updateCategory as updateCategoryApi,
  updateCategoryStatus as updateCategoryStatusApi,
  updateGoods as updateGoodsApi,
  updateGoodsStatus as updateGoodsStatusApi,
  updateSystemSetting as updateSystemSettingApi,
} from "../../api/yuan-mall-module";
import type {
  AdminBanner,
  AdminCategory,
  AdminCoupon,
  AdminDashboard,
  AdminGoods,
  AdminOrder,
  AdminSystemSetting,
  AdminUserAddress,
  AdminUserCoupon,
  AdminUser,
} from "../../types/mall";

export const useMallStore = defineStore("admin-mall-store", () => {
  const dashboardData = ref<AdminDashboard>({
    todayOrderCount: 0,
    waitShipCount: 0,
    userCount: 0,
    todayAmount: "0.00",
  });
  const categoryList = ref<AdminCategory[]>([]);
  const categoryTotal = ref(0);
  /** 全部分类（用于商品页等下拉），需调用 fetchCategoryOptions 加载 */
  const categoryOptions = ref<AdminCategory[]>([]);
  const goodsList = ref<AdminGoods[]>([]);
  const goodsTotal = ref(0);
  const orderList = ref<AdminOrder[]>([]);
  const orderTotal = ref(0);
  /** 用户详情抽屉内展示的订单（按 userId 筛选），由 fetchUserDetailOrders 加载 */
  const userDetailOrderList = ref<AdminOrder[]>([]);
  const userList = ref<AdminUser[]>([]);
  const userTotal = ref(0);
  const userAddressList = ref<AdminUserAddress[]>([]);
  const userAddressTotal = ref(0);
  const userDetailAddressList = ref<AdminUserAddress[]>([]);
  const userCouponList = ref<AdminUserCoupon[]>([]);
  const userCouponTotal = ref(0);
  const userDetailCouponList = ref<AdminUserCoupon[]>([]);
  const couponList = ref<AdminCoupon[]>([]);
  const couponTotal = ref(0);
  const bannerList = ref<AdminBanner[]>([]);
  const systemSetting = reactive<AdminSystemSetting>({
    id: 0,
    mallName: "",
    serviceMobile: "",
    cancelMinutes: "",
    freeShippingAmount: "",
  });

  const fetchDashboard = async () => {
    dashboardData.value = await getDashboard();
  };

  const fetchCategoryList = async (params?: {
    nameKeyword?: string;
    pageNum?: number;
    pageSize?: number;
  }) => {
    const pageNum = params?.pageNum ?? 1;
    const pageSize = params?.pageSize ?? 10;
    const res = await getCategoryList({
      nameKeyword: params?.nameKeyword,
      pageNum,
      pageSize,
    });
    categoryList.value = res?.list ?? [];
    categoryTotal.value = res?.total ?? 0;
  };

  /** 加载全部分类（用于下拉选择），大 pageSize 拉取 */
  const fetchCategoryOptions = async () => {
    const res = await getCategoryList({ pageNum: 1, pageSize: 500 });
    categoryOptions.value = res?.list ?? [];
  };

  const addCategory = async (payload: {
    name: string;
    icon?: string;
    sort: number;
    status: boolean;
  }) => {
    const category = await createCategoryApi(payload);
    categoryList.value.unshift(category);
    categoryTotal.value += 1;
  };

  const updateCategoryItem = async (payload: {
    id: number;
    name: string;
    icon?: string;
    sort: number;
    status: boolean;
  }) => {
    const category = await updateCategoryApi(payload);
    const idx = categoryList.value.findIndex((item) => item.id === payload.id);
    if (idx >= 0) categoryList.value.splice(idx, 1, category);
  };

  const toggleCategoryStatus = async (id: number) => {
    const currentCategory = categoryList.value.find((item) => item.id === id);
    if (!currentCategory) {
      return;
    }
    await updateCategoryStatusApi({
      id,
      status: !currentCategory.status,
    });
    currentCategory.status = !currentCategory.status;
  };

  const fetchGoodsList = async (params?: {
    keyword?: string;
    categoryId?: number;
    pageNum?: number;
    pageSize?: number;
  }) => {
    const res = await getGoodsList(params);
    goodsList.value = res?.list ?? [];
    goodsTotal.value = res?.total ?? 0;
  };

  const addGoods = async (payload: {
    name: string;
    discreetName?: string;
    categoryName: string;
    characteristic: string;
    price: number;
    originalPrice: number;
    unit: string;
    stock: number;
    tags: string;
    bannerList: string;
    detailList: string;
    status: boolean;
    variants?: any[];
  }) => {
    const goods = await createGoodsApi(payload);
    goodsList.value.unshift(goods);
    goodsTotal.value += 1;
  };

  const editGoods = async (payload: {
    id: number;
    name: string;
    discreetName?: string;
    categoryName: string;
    characteristic: string;
    price: number;
    originalPrice: number;
    unit: string;
    stock: number;
    tags: string;
    bannerList: string;
    detailList: string;
    status: boolean;
    variants?: any[];
  }) => {
    const goods = await updateGoodsApi(payload);
    const currentIndex = goodsList.value.findIndex((item) => item.id === payload.id);
    if (currentIndex >= 0) {
      goodsList.value.splice(currentIndex, 1, goods);
    }
  };

  const toggleGoodsStatus = async (id: number) => {
    const currentGoods = goodsList.value.find((item) => item.id === id);
    if (!currentGoods) {
      return;
    }
    await updateGoodsStatusApi({
      id,
      status: !currentGoods.status,
    });
    currentGoods.status = !currentGoods.status;
  };

  const fetchOrderList = async (params?: {
    status?: string;
    userId?: number;
    pageNum?: number;
    pageSize?: number;
  }) => {
    const res = await getOrderList(params);
    orderList.value = res?.list ?? [];
    orderTotal.value = res?.total ?? 0;
  };

  /** 加载某用户的订单（用于用户详情抽屉），不覆盖 orderList */
  const fetchUserDetailOrders = async (userId: number) => {
    const res = await getOrderList({ userId, pageNum: 1, pageSize: 500 });
    userDetailOrderList.value = res?.list ?? [];
  };

  const shipOrder = async (payload: {
    orderId: string;
    expressCompany: string;
    expressNo: string;
  }) => {
    await shipOrderApi(payload);
    const currentOrder = orderList.value.find((item) => item.orderId === payload.orderId);
    if (currentOrder) {
      currentOrder.status = "配送中";
    }
    await fetchDashboard();
  };

  const updateOrderStatus = async (orderId: string, targetStatus: string) => {
    await updateOrderStatusApi({ orderId, targetStatus });
    const currentOrder = orderList.value.find((item) => item.orderId === orderId);
    if (currentOrder) {
      currentOrder.status = targetStatus as any;
    }
    await fetchDashboard();
  };

  const fetchUserList = async (params?: { pageNum?: number; pageSize?: number }) => {
    const res = await getUserList(params);
    userList.value = res?.list ?? [];
    userTotal.value = res?.total ?? 0;
  };

  const fetchUserAddressList = async (params?: { pageNum?: number; pageSize?: number }) => {
    const res = await getUserAddressList(params);
    userAddressList.value = res?.list ?? [];
    userAddressTotal.value = res?.total ?? 0;
  };

  const fetchUserDetailAddresses = async (userId: number) => {
    const res = await getUserAddressList({ userId, pageNum: 1, pageSize: 500 });
    userDetailAddressList.value = res?.list ?? [];
  };

  const fetchUserCouponList = async (params?: { pageNum?: number; pageSize?: number }) => {
    const res = await getUserCouponList(params);
    userCouponList.value = res?.list ?? [];
    userCouponTotal.value = res?.total ?? 0;
  };

  const fetchUserDetailCoupons = async (userId: number) => {
    const res = await getUserCouponList({ userId, pageNum: 1, pageSize: 500 });
    userDetailCouponList.value = res?.list ?? [];
  };

  const fetchCouponList = async (params?: { pageNum?: number; pageSize?: number }) => {
    const res = await getCouponList(params);
    couponList.value = res?.list ?? [];
    couponTotal.value = res?.total ?? 0;
  };

  const addCoupon = async (payload: {
    name: string;
    amount: number;
    minAmount: number;
    status: boolean;
  }) => {
    return await createCouponApi(payload);
  };

  const fetchBannerList = async () => {
    bannerList.value = await getBannerList();
  };

  const addBanner = async (payload: {
    title: string;
    imageUrl: string;
    linkType: string;
    linkUrl: string;
    status: boolean;
    sort: number;
  }) => {
    const banner = await createBannerApi(payload);
    bannerList.value = [...bannerList.value, banner].sort((a, b) => a.sort - b.sort);
  };

  const editBanner = async (payload: {
    id: number;
    title: string;
    imageUrl: string;
    linkType: string;
    linkUrl: string;
    status: boolean;
    sort: number;
  }) => {
    const banner = await updateBannerApi(payload);
    const idx = bannerList.value.findIndex((item) => item.id === payload.id);
    if (idx >= 0) bannerList.value.splice(idx, 1, banner);
    bannerList.value = [...bannerList.value].sort((a, b) => a.sort - b.sort);
  };

  const removeBanner = async (id: number) => {
    await deleteBannerApi({ id });
    bannerList.value = bannerList.value.filter((item) => item.id !== id);
  };

  const updateBannerSort = async (id: number, sort: number) => {
    await updateBannerSortApi({ id, sort });
    const currentBanner = bannerList.value.find((item) => item.id === id);
    if (currentBanner) {
      currentBanner.sort = sort;
    }
    bannerList.value = [...bannerList.value].sort((a, b) => a.sort - b.sort);
  };

  const fetchSystemSetting = async () => {
    const result = await getSystemSetting();
    Object.assign(systemSetting, result);
  };

  const saveSystemSetting = async (payload: Omit<AdminSystemSetting, "id">) => {
    const result = await updateSystemSettingApi(payload);
    Object.assign(systemSetting, result);
  };

  return {
    dashboardData,
    categoryList,
    categoryTotal,
    categoryOptions,
    goodsList,
    goodsTotal,
    orderList,
    orderTotal,
    userDetailOrderList,
    userList,
    userTotal,
    userAddressList,
    userAddressTotal,
    userDetailAddressList,
    userCouponList,
    userCouponTotal,
    userDetailCouponList,
    couponList,
    couponTotal,
    bannerList,
    systemSetting,
    fetchDashboard,
    fetchCategoryList,
    fetchCategoryOptions,
    addCategory,
    updateCategoryItem,
    toggleCategoryStatus,
    fetchGoodsList,
    addGoods,
    editGoods,
    toggleGoodsStatus,
    fetchOrderList,
    fetchUserDetailOrders,
    shipOrder,
    updateOrderStatus,
    fetchUserList,
    fetchUserAddressList,
    fetchUserDetailAddresses,
    fetchUserCouponList,
    fetchUserDetailCoupons,
    fetchCouponList,
    addCoupon,
    fetchBannerList,
    addBanner,
    editBanner,
    removeBanner,
    updateBannerSort,
    fetchSystemSetting,
    saveSystemSetting,
  };
});
