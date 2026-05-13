import { request } from "../common";
import type {
  CreateOrderPayload,
  CreateOrderFromCartPayload,
  HomeDeco,
  LogisticsTrace,
  MallAddress,
  MallBanner,
  MallCategory,
  MallCoupon,
  MallGoods,
  MallCartItem,
  MallOrder,
  MallSystemConfig,
  MallUserProfile,
  UpdateAddressPayload,
} from "../../types/mall";
import type { AfterSaleDetail } from "../../types/after-sale";

export const getBannerList = () =>
  request<MallBanner[]>({
    url: "/mall/mini/banner/list",
  });

export const getHomeDeco = () =>
  request<HomeDeco>({
    url: "/mall/mini/home/deco",
  });

export const getMallConfig = () =>
  request<MallSystemConfig>({
    url: "/mall/mini/system/config",
  });

export const getCategoryList = () =>
  request<MallCategory[]>({
    url: "/mall/mini/category/list",
  });

export const getGoodsList = (params?: { categoryId?: number; keyword?: string }) =>
  request<MallGoods[]>({
    url: "/mall/mini/goods/list",
    data: params,
  });

export const getGoodsDetail = (id: number) =>
  request<MallGoods>({
    url: "/mall/mini/goods/detail",
    data: { id },
  });

/**
 * 微信授权登录：先在小程序内调用 wx.login 获取 code，再传到此接口
 * @param code 微信 wx.login 返回的临时 code
 * @param nickName 可选，新用户或更新昵称时传入
 */
export const login = (code: string, nickName?: string) =>
  request<MallUserProfile>({
    url: "/mall/mini/login",
    method: "POST",
    data: { code, nickName: nickName || undefined },
  });

export const getUserProfile = (userId: number) =>
  request<MallUserProfile>({
    url: "/mall/mini/user/profile",
    data: { userId },
  });

export const getAddressList = (userId: number) =>
  request<MallAddress[]>({
    url: "/mall/mini/address/list",
    data: { userId },
  });

export const createAddress = (data: Omit<MallAddress, "id"> & { userId: number }) =>
  request<MallAddress>({
    url: "/mall/mini/address/create",
    method: "POST",
    data,
  });

export const updateAddress = (data: UpdateAddressPayload) =>
  request<MallAddress>({
    url: "/mall/mini/address/update",
    method: "POST",
    data,
  });

export const deleteAddress = (userId: number, addressId: number) =>
  request<boolean>({
    url: "/mall/mini/address/delete",
    method: "POST",
    data: { userId, addressId },
  });

export const getCouponList = (userId: number) =>
  request<MallCoupon[]>({
    url: "/mall/mini/coupon/list",
    data: { userId },
  });

export const getCartList = (userId: number) =>
  request<MallCartItem[]>({
    url: "/mall/mini/cart/list",
    data: { userId },
  });

export const addCart = (data: { userId: number; goodsId: number; variantId?: number | null; quantity: number }) =>
  request<MallCartItem[]>({
    url: "/mall/mini/cart/add",
    method: "POST",
    data,
  });

export const updateCart = (data: {
  userId: number;
  goodsId: number;
  variantId?: number | null;
  quantity?: number;
  checked?: boolean;
}) =>
  request<MallCartItem[]>({
    url: "/mall/mini/cart/update",
    method: "POST",
    data,
  });

export const removeCart = (data: { userId: number; goodsId: number; variantId?: number | null }) =>
  request<MallCartItem[]>({
    url: "/mall/mini/cart/remove",
    method: "POST",
    data,
  });

export const getOrderList = (userId: number) =>
  request<MallOrder[]>({
    url: "/mall/mini/order/list",
    data: { userId },
  });

export const getOrderDetail = (orderId: string) =>
  request<MallOrder>({
    url: "/mall/mini/order/detail",
    data: { orderId },
  });

export const getOrderLogistics = (orderId: string) =>
  request<LogisticsTrace[]>({
    url: "/mall/mini/order/logistics",
    data: { orderId },
  });

export const createOrder = (data: CreateOrderPayload) =>
  request<MallOrder>({
    url: "/mall/mini/order/create",
    method: "POST",
    data: data as Record<string, any>,
  });

export const createOrderFromCart = (data: CreateOrderFromCartPayload) =>
  request<MallOrder>({
    url: "/mall/mini/order/createFromCart",
    method: "POST",
    data: data as Record<string, any>,
  });

export const payOrderSuccess = (userId: number, orderId: string) =>
  request<MallOrder>({
    url: "/mall/mini/order/pay/success",
    method: "POST",
    data: { userId, orderId },
  });

export const payOrderFail = (userId: number, orderId: string) =>
  request<boolean>({
    url: "/mall/mini/order/pay/fail",
    method: "POST",
    data: { userId, orderId },
  });

export const cancelOrder = (userId: number, orderId: string) =>
  request<boolean>({
    url: "/mall/mini/order/cancel",
    method: "POST",
    data: { userId, orderId },
  });

export const applyAfterSale = (data: {
  orderId: string;
  orderItemId: number;
  userId: number;
  type: string;
  quantity: number;
  reason: string;
  description?: string;
  evidenceImageList?: string[];
}) =>
  request<AfterSaleDetail>({
    url: "/mall/mini/afterSale/apply",
    method: "POST",
    data,
  });

export const getAfterSaleDetail = (id: number) =>
  request<AfterSaleDetail>({
    url: "/mall/mini/afterSale/detail",
    data: { id },
  });
