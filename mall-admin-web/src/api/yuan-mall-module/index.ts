import { request } from "../common";
import type { ApiResponse, AdminLoginResult } from "../../types/mall";
import type {
  AdminBanner,
  AdminCategory,
  AdminCoupon,
  AdminDashboard,
  AdminGoods,
  HomeDeco,
  AdminOrder,
  AdminSystemSetting,
  AdminUserAddress,
  AdminUserCoupon,
  AdminUser,
} from "../../types/mall";
import type { AfterSaleDetail } from "../../../../mall-miniprogram/src/types/after-sale";

export const getDashboard = async () =>
  (await request.get<ApiResponse<AdminDashboard>>("/mall/admin/dashboard")).data.data;

export const adminLogin = async (data: { username: string; password: string }) =>
  (await request.post<ApiResponse<AdminLoginResult>>("/mall/admin/login", data)).data.data;

export const adminRefresh = async (refreshToken: string) =>
  (await request.post<ApiResponse<AdminLoginResult>>("/mall/admin/refresh", { refreshToken })).data.data;

export const adminLogout = async () =>
  (await request.post<ApiResponse<boolean>>("/mall/admin/logout")).data.data;

/** 公开接口，无需登录，用于登录页展示商城名称（与小程序端同源配置） */
export const getPublicMallConfig = async (): Promise<{ mallName: string }> =>
  (await request.get<ApiResponse<{ mallName: string }>>("/mall/mini/system/config")).data.data;

export const changeAdminPassword = async (data: { oldPassword: string; newPassword: string }) =>
  (await request.post<ApiResponse<boolean>>("/mall/admin/password/change", data)).data.data;

export interface PageResult<T> {
  list: T[];
  total: number;
}

export const getCategoryList = async (params?: {
  nameKeyword?: string;
  pageNum?: number;
  pageSize?: number;
}) => {
  const res = (await request.get<ApiResponse<PageResult<AdminCategory>>>("/mall/admin/category/list", { params })).data.data;
  return res;
};

export const createCategory = async (data: {
  name: string;
  icon?: string;
  sort: number;
  status: boolean;
}) =>
  (await request.post<ApiResponse<AdminCategory>>("/mall/admin/category/create", data)).data.data;

export const updateCategoryStatus = async (data: { id: number; status: boolean }) =>
  (await request.post<ApiResponse<boolean>>("/mall/admin/category/status", data)).data.data;

export const updateCategory = async (data: {
  id: number;
  name: string;
  icon?: string;
  sort: number;
  status: boolean;
}) =>
  (await request.post<ApiResponse<AdminCategory>>("/mall/admin/category/update", data)).data.data;

export const deleteCategory = async (data: { id: number }) =>
  (await request.post<ApiResponse<boolean>>("/mall/admin/category/delete", data)).data.data;

export const batchDeleteCategory = async (data: { ids: number[] }) =>
  (await request.post<ApiResponse<boolean>>("/mall/admin/category/batch-delete", data)).data.data;

export const getGoodsList = async (params?: {
  keyword?: string;
  categoryId?: number;
  pageNum?: number;
  pageSize?: number;
}) => {
  const res = (await request.get<ApiResponse<PageResult<AdminGoods>>>("/mall/admin/goods/list", { params })).data.data;
  return res;
};

export const createGoods = async (data: {
  name: string;
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
  variants?: AdminGoods["variants"];
}) => (await request.post<ApiResponse<AdminGoods>>("/mall/admin/goods/create", data)).data.data;

export const updateGoods = async (data: {
  id: number;
  name: string;
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
  variants?: AdminGoods["variants"];
}) => (await request.post<ApiResponse<AdminGoods>>("/mall/admin/goods/update", data)).data.data;

export const updateGoodsStatus = async (data: { id: number; status: boolean }) =>
  (await request.post<ApiResponse<boolean>>("/mall/admin/goods/status", data)).data.data;

export const deleteGoods = async (data: { id: number }) =>
  (await request.post<ApiResponse<boolean>>("/mall/admin/goods/delete", data)).data.data;

export const batchDeleteGoods = async (data: { ids: number[] }) =>
  (await request.post<ApiResponse<boolean>>("/mall/admin/goods/batch-delete", data)).data.data;

export const getOrderList = async (params?: {
  status?: string;
  userId?: number;
  pageNum?: number;
  pageSize?: number;
}) => {
  const res = (await request.get<ApiResponse<PageResult<AdminOrder>>>("/mall/admin/order/list", { params })).data.data;
  return res;
};

/** 订单详情（与小程序订单详情结构一致，status 为 pending/paid/shipping/finished/cancelled） */
export interface AdminOrderDetail {
  id: string;
  status: string;
  createTime: string;
  totalAmount: number;
  payAmount: number;
  addressText: string;
  isDiscreet: boolean;
  itemList: { goodsId: number; name: string; price: number; quantity: number }[];
  expressCompany?: string;
  expressNo?: string;
  shipTime?: string;
}

export const getOrderDetail = async (orderId: string) =>
  (await request.get<ApiResponse<AdminOrderDetail>>("/mall/admin/order/detail", {
    params: { orderId },
  })).data.data;

export const shipOrder = async (data: {
  orderId: string;
  expressCompany: string;
  expressNo: string;
}) =>
  (await request.post<ApiResponse<boolean>>("/mall/admin/order/ship", data)).data.data;

export const updateOrderStatus = async (data: { orderId: string; targetStatus: string }) =>
  (await request.post<ApiResponse<boolean>>("/mall/admin/order/status", data)).data.data;

export interface LogisticsTrace {
  time: string;
  desc: string;
}

export const getOrderLogistics = async (orderId: string) =>
  (await request.get<ApiResponse<LogisticsTrace[]>>("/mall/admin/order/logistics", {
    params: { orderId },
  })).data.data;

export const getUserList = async (params?: { pageNum?: number; pageSize?: number }) => {
  const res = (await request.get<ApiResponse<PageResult<AdminUser>>>("/mall/admin/user/list", { params })).data.data;
  return res;
};

export const getUserAddressList = async (params?: {
  userId?: number;
  pageNum?: number;
  pageSize?: number;
}) => {
  const res = (await request.get<ApiResponse<PageResult<AdminUserAddress>>>("/mall/admin/user/address/list", { params }))
    .data.data;
  return res;
};

export const getUserCouponList = async (params?: {
  userId?: number;
  pageNum?: number;
  pageSize?: number;
}) => {
  const res = (await request.get<ApiResponse<PageResult<AdminUserCoupon>>>("/mall/admin/user/coupon/list", { params }))
    .data.data;
  return res;
};

export const getCouponList = async (params?: {
  pageNum?: number;
  pageSize?: number;
}) => {
  const res = (await request.get<ApiResponse<PageResult<AdminCoupon>>>("/mall/admin/coupon/list", { params })).data.data;
  return res;
};

export const createCoupon = async (data: {
  name: string;
  amount: number;
  minAmount: number;
  status: boolean;
  startTime?: string | null;
  expireTime?: string | null;
}) => (await request.post<ApiResponse<AdminCoupon>>("/mall/admin/coupon/create", data)).data.data;

export const getBannerList = async () =>
  (await request.get<ApiResponse<AdminBanner[]>>("/mall/admin/banner/list")).data.data;

export const createBanner = async (data: {
  title: string;
  imageUrl: string;
  linkType: string;
  linkUrl: string;
  status: boolean;
  sort: number;
}) => (await request.post<ApiResponse<AdminBanner>>("/mall/admin/banner/create", data)).data.data;

export const updateBanner = async (data: {
  id: number;
  title: string;
  imageUrl: string;
  linkType: string;
  linkUrl: string;
  status: boolean;
  sort: number;
}) => (await request.post<ApiResponse<AdminBanner>>("/mall/admin/banner/update", data)).data.data;

export const deleteBanner = async (data: { id: number }) =>
  (await request.post<ApiResponse<boolean>>("/mall/admin/banner/delete", data)).data.data;

export const updateBannerSort = async (data: { id: number; sort: number }) =>
  (await request.post<ApiResponse<boolean>>("/mall/admin/banner/sort", data)).data.data;

export const getHomeDeco = async () =>
  (await request.get<ApiResponse<HomeDeco>>("/mall/admin/home/deco")).data.data;

export const updateHomeDeco = async (data: {
  searchPlaceholder: string;
  stat1Value: string;
  stat1Label: string;
  stat2Value: string;
  stat2Label: string;
  stat3Value: string;
  stat3Label: string;
}) => (await request.post<ApiResponse<HomeDeco>>("/mall/admin/home/deco", data)).data.data;

export const getSystemSetting = async () =>
  (await request.get<ApiResponse<AdminSystemSetting>>("/mall/admin/system/setting")).data.data;

export const updateSystemSetting = async (data: Omit<AdminSystemSetting, "id">) =>
  (await request.post<ApiResponse<AdminSystemSetting>>("/mall/admin/system/setting", data)).data.data;

export const getAfterSalePage = async (data: {
  type?: string;
  status?: string;
  pageNum?: number;
  pageSize?: number;
}) => {
  const res = (await request.post<ApiResponse<{ list: AfterSaleDetail[]; total: number }>>(
    "/mall/admin/afterSale/page",
    data,
  )).data.data;
  return res;
};

export const getAfterSaleDetail = async (id: number) =>
  (await request.get<ApiResponse<AfterSaleDetail>>("/mall/admin/afterSale/detail", {
    params: { id },
  })).data.data;

export const handleAfterSale = async (data: {
  afterSaleId: number;
  action: string;
  actualAmount?: number;
  remark?: string;
}) =>
  (await request.post<ApiResponse<boolean>>("/mall/admin/afterSale/handle", data)).data.data;

/** 上传图片，返回可访问的 URL（如 /upload/xxx.jpg） */
export const uploadImage = async (file: File): Promise<string> => {
  const form = new FormData();
  form.append("file", file);
  const res = await request.post<ApiResponse<string>>("/mall/admin/upload/image", form, {
    headers: { "Content-Type": "multipart/form-data" },
  });
  if (res.data.code !== 200 || res.data.data == null) {
    throw new Error(res.data.message || "上传失败");
  }
  return res.data.data;
};
