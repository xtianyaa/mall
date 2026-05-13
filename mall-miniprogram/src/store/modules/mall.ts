import { computed, ref } from "vue";
import { defineStore } from "pinia";
import {
  addCart as addCartApi,
  createAddress,
  createOrder,
  createOrderFromCart,
  cancelOrder,
  deleteAddress,
  getBannerList,
  getHomeDeco,
  getMallConfig,
  getAddressList,
  getCategoryList,
  getCartList,
  getCouponList,
  getGoodsDetail,
  getGoodsList,
  getOrderDetail,
  getOrderList,
  getUserProfile,
  login,
  payOrderFail,
  payOrderSuccess,
  removeCart as removeCartApi,
  updateCart as updateCartApi,
  updateAddress,
} from "../../api/yuan-mall-module/index";
import type {
  CreateOrderPayload,
  HomeDeco,
  MallCategory,
  MallAddress,
  MallBanner,
  MallCartItem,
  MallCoupon,
  MallGoods,
  MallOrder,
  MallOrderItem,
  MallUserProfile,
} from "../../types/mall";
import { getRealVariants } from "../../utils/goodsVariant";

const STORAGE_KEY = "YA_MALL_PHASE1_STORE";
const CART_TAB_INDEX = 2; // pages.json 中 tabBar 第 3 个是“购物车”

interface MallLocalState {
  cartList: MallCartItem[];
  userInfo: MallUserProfile | null;
  hasLogin: boolean;
}

export const useMallStore = defineStore("mall-store", () => {
  const mallName = ref("");
  const bannerList = ref<MallBanner[]>([]);
  const homeDeco = ref<HomeDeco | null>(null);
  const categoryList = ref<MallCategory[]>([]);
  const goodsList = ref<MallGoods[]>([]);
  const cartList = ref<MallCartItem[]>([]);
  const addressList = ref<MallAddress[]>([]);
  const couponList = ref<MallCoupon[]>([]);
  const orderList = ref<MallOrder[]>([]);
  const userInfo = ref<MallUserProfile | null>(null);
  const hasLogin = ref(false);

  const cartGoodsList = computed(() =>
    cartList.value
      .map((item) => {
        const goods = goodsList.value.find((goodsItem) => goodsItem.id === item.goodsId);
        if (!goods) {
          return null;
        }
        return {
          ...item,
          goods,
          totalAmount: Number((Number(item.variantPrice ?? goods.price) * item.quantity).toFixed(2)),
        };
      })
      .filter((item): item is NonNullable<typeof item> => Boolean(item))
  );

  const checkedCartList = computed(() => cartGoodsList.value.filter((item) => item.checked));

  const cartAmount = computed(() =>
    checkedCartList.value.reduce((amount, item) => amount + item.totalAmount, 0)
  );

  const cartCount = computed(() =>
    cartList.value.reduce((amount, item) => amount + item.quantity, 0)
  );

  const defaultAddress = computed(
    () => addressList.value.find((item) => item.isDefault) || addressList.value[0]
  );

  const unusedCouponList = computed(() =>
    couponList.value.filter((item) => item.status === "unused")
  );

  const lockedCouponList = computed(() =>
    couponList.value.filter((item) => item.status === "locked")
  );

  const updateCartBadge = () => {
    const count = cartCount.value;
    if (count > 0) {
      uni.setTabBarBadge({
        index: CART_TAB_INDEX,
        text: String(count),
      });
    } else {
      uni.removeTabBarBadge({ index: CART_TAB_INDEX });
    }
  };

  const saveLocalData = () => {
    uni.setStorageSync(STORAGE_KEY, {
      cartList: cartList.value,
      userInfo: userInfo.value,
      hasLogin: hasLogin.value,
    } satisfies MallLocalState);
    updateCartBadge();
  };

  const syncCartFromServer = async () => {
    if (!userInfo.value?.id) {
      return;
    }
    const serverCart = await getCartList(userInfo.value.id);
    cartList.value = serverCart.map((item) => ({
      goodsId: item.goodsId,
      variantId: item.variantId ?? null,
      quantity: item.quantity,
      variantLabel: item.variantLabel ?? null,
      variantPrice: item.variantPrice ?? 0,
      checked: item.checked,
    }));
    saveLocalData();
  };

  const mergeLocalCartToServer = async () => {
    if (!userInfo.value?.id) {
      return;
    }
    if (!cartList.value.length) {
      return;
    }
    // 将本地购物车逐条合并到服务端（服务端 add 会做累加）
    for (const item of cartList.value) {
      await addCartApi({
        userId: userInfo.value.id,
        goodsId: item.goodsId,
        variantId: item.variantId,
        quantity: item.quantity,
      });
    }
    await syncCartFromServer();
  };

  const syncUserData = async () => {
    if (!userInfo.value?.id) {
      return;
    }
    const [profile, address, coupon, order] = await Promise.all([
      getUserProfile(userInfo.value.id),
      getAddressList(userInfo.value.id),
      getCouponList(userInfo.value.id),
      getOrderList(userInfo.value.id),
    ]);

    userInfo.value = profile;
    addressList.value = address;
    couponList.value = coupon;
    orderList.value = order;
    await syncCartFromServer();
    saveLocalData();
  };

  const fetchGoodsListData = async (params?: { categoryId?: number; keyword?: string }) => {
    goodsList.value = await getGoodsList(params);
  };

  const fetchGoodsDetail = async (goodsId: number) => getGoodsDetail(goodsId);

  const fetchOrderDetail = async (orderId: string) => getOrderDetail(orderId);

  const initMallData = async () => {
    const cacheData = uni.getStorageSync(STORAGE_KEY) as Partial<MallLocalState> | undefined;
    cartList.value = (cacheData?.cartList || []).map((item) => ({
      goodsId: item.goodsId,
      variantId: item.variantId ?? null,
      variantLabel: item.variantLabel ?? null,
      variantPrice: item.variantPrice ?? 0,
      quantity: item.quantity,
      checked: item.checked,
    }));
    userInfo.value = cacheData?.userInfo || null;
    hasLogin.value = cacheData?.hasLogin ?? false;

    const [config, banner, deco, category, goods] = await Promise.all([
      getMallConfig().catch(() => ({ mallName: "元选商城" })),
      getBannerList(),
      getHomeDeco().catch(() => null),
      getCategoryList(),
      getGoodsList(),
    ]);
    mallName.value = config.mallName || "元选商城";
    bannerList.value = banner;
    homeDeco.value = deco ?? null;
    categoryList.value = category;
    goodsList.value = goods;

    if (hasLogin.value && userInfo.value?.id) {
      await syncUserData();
    }
    saveLocalData();
  };

  const addCart = async (goodsId: number, quantity = 1, variantId?: number | null) => {
    const goods = goodsList.value.find((item) => item.id === goodsId);
    if (!goods) {
      return { success: false, message: "商品不存在" };
    }

    const realV = getRealVariants(goods.variants || []);
    let targetVariantId: number | null;
    if (variantId !== undefined) {
      targetVariantId = variantId;
    } else if (realV.length === 0) {
      targetVariantId = null;
    } else {
      targetVariantId = goods.defaultVariantId ?? realV[0]?.id ?? null;
    }
    const selectedVariant =
      targetVariantId != null ? realV.find((v) => v.id === targetVariantId) : undefined;
    const targetVariantLabel = selectedVariant
      ? Object.values(selectedVariant.specs || {}).filter(Boolean).join(" / ") || null
      : null;
    const targetVariantPrice = selectedVariant?.price ?? goods.price;

    if (hasLogin.value && userInfo.value?.id) {
      const serverCart = await addCartApi({
        userId: userInfo.value.id,
        goodsId,
        variantId: targetVariantId,
        quantity,
      });
      cartList.value = serverCart;
      saveLocalData();
      return {
        success: true,
        message: targetVariantLabel ? `${goods.name}（${targetVariantLabel}）已加入购物车` : `${goods.name} 已加入购物车`,
      };
    }

    const currentItem = cartList.value.find(
      (item) => item.goodsId === goodsId && (item.variantId ?? null) === (targetVariantId ?? null)
    );
    if (currentItem) {
      currentItem.quantity += quantity;
    } else {
      cartList.value.push({
        goodsId,
        variantId: targetVariantId ?? null,
        variantPrice: targetVariantPrice,
        variantLabel: targetVariantLabel,
        quantity,
        checked: true,
      });
    }
    saveLocalData();
    return {
      success: true,
      message: targetVariantLabel ? `${goods.name}（${targetVariantLabel}）已加入购物车` : `${goods.name} 已加入购物车`,
    };
  };

  const toggleCartChecked = async (goodsId: number, variantId?: number | null) => {
    const currentItem =
      variantId !== undefined
        ? cartList.value.find((item) => item.goodsId === goodsId && (item.variantId ?? null) === (variantId ?? null))
        : cartList.value.find((item) => item.goodsId === goodsId);
    if (currentItem) {
      if (hasLogin.value && userInfo.value?.id) {
        const serverCart = await updateCartApi({
          userId: userInfo.value.id,
          goodsId,
          variantId: currentItem.variantId,
          checked: !currentItem.checked,
        });
        cartList.value = serverCart;
      } else {
        currentItem.checked = !currentItem.checked;
      }
      saveLocalData();
    }
  };

  const updateCartQuantity = async (goodsId: number, quantity: number, variantId?: number | null) => {
    const currentItem =
      variantId !== undefined
        ? cartList.value.find((item) => item.goodsId === goodsId && (item.variantId ?? null) === (variantId ?? null))
        : cartList.value.find((item) => item.goodsId === goodsId);
    if (!currentItem) {
      return;
    }
    const nextQuantity = Math.max(1, quantity);
    if (hasLogin.value && userInfo.value?.id) {
      const serverCart = await updateCartApi({
        userId: userInfo.value.id,
        goodsId,
        variantId: currentItem.variantId,
        quantity: nextQuantity,
      });
      cartList.value = serverCart;
    } else {
      currentItem.quantity = nextQuantity;
    }
    saveLocalData();
  };

  const removeCartItem = async (goodsId: number, variantId?: number | null) => {
    const currentItem =
      variantId !== undefined
        ? cartList.value.find((item) => item.goodsId === goodsId && (item.variantId ?? null) === (variantId ?? null))
        : cartList.value.find((item) => item.goodsId === goodsId);

    if (hasLogin.value && userInfo.value?.id) {
      const serverCart = await removeCartApi({
        userId: userInfo.value.id,
        goodsId,
        variantId: currentItem?.variantId ?? null,
      });
      cartList.value = serverCart;
      saveLocalData();
      return;
    }
    cartList.value =
      variantId !== undefined
        ? cartList.value.filter((item) => !(item.goodsId === goodsId && (item.variantId ?? null) === (variantId ?? null)))
        : cartList.value.filter((item) => item.goodsId !== goodsId);
    saveLocalData();
  };

  /**
   * 微信授权登录：内部会先调 wx.login 取 code，再请求后端 code2Session 绑定/创建用户
   * @param nickName 可选，用于新用户默认昵称或更新昵称
   */
  const loginAction = async (nickName?: string) => {
    const wasLoggedIn = hasLogin.value && !!userInfo.value?.id;
    const loginRes = await new Promise<UniApp.LoginRes>((resolve, reject) => {
      uni.login({
        provider: "weixin",
        success: resolve,
        fail: reject,
      });
    });
    if (!loginRes.code) {
      throw new Error("获取微信登录 code 失败，请重试");
    }
    const profile = await login(loginRes.code, nickName?.trim() || undefined);
    userInfo.value = profile;
    hasLogin.value = true;
    // 先持久化登录态到本地存储，保证后续请求能携带 X-User-Id / X-Token
    saveLocalData();
    // 只有“首次登录/之前未登录”才需要将本地购物车合并到服务端，
    // 已登录用户只是修改昵称时，不再重复合并，避免数量翻倍
    if (!wasLoggedIn) {
      await mergeLocalCartToServer();
    }
    await syncUserData();
  };

  const addAddress = async (payload: Omit<MallAddress, "id">) => {
    if (!userInfo.value?.id) {
      throw new Error("请先登录");
    }
    await createAddress({
      userId: userInfo.value.id,
      ...payload,
    });
    await syncUserData();
  };

  const updateAddressAction = async (payload: MallAddress) => {
    if (!userInfo.value?.id) {
      throw new Error("请先登录");
    }
    await updateAddress({
      userId: userInfo.value.id,
      ...payload,
    });
    await syncUserData();
  };

  const deleteAddressAction = async (addressId: number) => {
    if (!userInfo.value?.id) {
      throw new Error("请先登录");
    }
    await deleteAddress(userInfo.value.id, addressId);
    await syncUserData();
  };

  const createOrderAction = async (couponId?: number, isDiscreet?: boolean) => {
    if (!checkedCartList.value.length) {
      return { success: false, message: "请先勾选商品" };
    }
    if (!defaultAddress.value) {
      return { success: false, message: "请先新增收货地址" };
    }
    if (!userInfo.value?.id) {
      return { success: false, message: "请先登录" };
    }

    // 真实项目：下单金额由后端重算，这里直接调用“从购物车勾选项创建订单”
    const order = await createOrderFromCart({
      userId: userInfo.value.id,
      addressId: defaultAddress.value.id,
      couponId,
      isDiscreet,
    });
    // 后端已清理勾选项，这里同步一次购物车
    await syncCartFromServer();
    await syncUserData();
    saveLocalData();
    return { success: true, message: `订单 ${order.id} 提交成功`, orderId: order.id };
  };

  const payOrderSuccessAction = async (orderId: string) => {
    if (!userInfo.value?.id) {
      return { success: false, message: "请先登录" };
    }
    const order = await payOrderSuccess(userInfo.value.id, orderId);
    await syncUserData();
    return { success: true, message: "支付成功", order };
  };

  const payOrderFailAction = async (orderId: string) => {
    if (!userInfo.value?.id) {
      return { success: false, message: "请先登录" };
    }
    await payOrderFail(userInfo.value.id, orderId);
    await syncUserData();
    return { success: true, message: "支付失败，订单已关闭" };
  };

  const cancelOrderAction = async (orderId: string) => {
    if (!userInfo.value?.id) {
      return { success: false, message: "请先登录" };
    }
    await cancelOrder(userInfo.value.id, orderId);
    await syncUserData();
    return { success: true, message: "订单已取消" };
  };

  const logoutAction = () => {
    userInfo.value = null;
    hasLogin.value = false;
    uni.removeStorageSync(STORAGE_KEY);
    // 重新初始化商城基础数据（不含用户私有数据）
    initMallData();
  };

  const getGoodsById = (goodsId: number) =>
    goodsList.value.find((item) => item.id === goodsId) || null;

  const getOrderById = (orderId: string) =>
    orderList.value.find((item) => item.id === orderId) || null;

  return {
    mallName,
    bannerList,
    homeDeco,
    categoryList,
    goodsList,
    cartList,
    addressList,
    couponList,
    orderList,
    userInfo,
    hasLogin,
    cartGoodsList,
    checkedCartList,
    cartAmount,
    cartCount,
    defaultAddress,
    unusedCouponList,
    lockedCouponList,
    initMallData,
    fetchGoodsListData,
    fetchGoodsDetail,
    fetchOrderDetail,
    syncUserData,
    syncCartFromServer,
    addCart,
    toggleCartChecked,
    updateCartQuantity,
    removeCartItem,
    loginAction,
    addAddress,
    updateAddressAction,
    deleteAddressAction,
    createOrderAction,
    payOrderSuccessAction,
    payOrderFailAction,
    cancelOrderAction,
    logoutAction,
    getGoodsById,
    getOrderById,
  };
});
