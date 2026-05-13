export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
  timestamp: number;
}

export interface MallCategory {
  id: number;
  name: string;
  icon: string;
}

export interface MallBanner {
  id: number;
  title: string;
  linkType: string;
  sort: number;
}

/** 首页首屏文案配置（装修） */
export interface HomeDecoStatItem {
  value: string;
  label: string;
}

export interface HomeDeco {
  searchPlaceholder: string;
  stats: HomeDecoStatItem[];
}

/** 小程序端系统配置（如商城名称） */
export interface MallSystemConfig {
  mallName: string;
}

export interface MallGoods {
  id: number;
  categoryId: number;
  categoryName: string;
  name: string;
  characteristic: string;
  price: number;
  originalPrice: number;
  unit: string;
  stock: number;
  monthlySales: number;
  tags: string[];
  bannerList: string[];
  detailList: string[];
  detailRichList?: { type: "text" | "image"; value: string }[];

  /** 默认规格 SKU ID（可能为空：老商品兼容） */
  defaultVariantId: number | null;
  /** 规格 SKU 列表 */
  variants: MallGoodsVariant[];

  /** 规格维度名列表（按顺序），如 ["颜色","尺码","套餐"] */
  specNames?: string[];
}

export interface MallGoodsVariant {
  id: number | null;
  /** 规格维度键值对，如 { "颜色": "红", "尺码": "S" } */
  specs: Record<string, string>;
  /** 维度名列表（按顺序），如 ["颜色", "尺码"] */
  specNames: string[];
  price: number;
  originalPrice: number;
  unit: string;
  /** 可用库存（stock - lockedStock） */
  stock: number;
}

export interface MallCartItem {
  goodsId: number;
  variantId: number | null;
  quantity: number;
  variantLabel: string | null;
  variantPrice: number;
  checked: boolean;
}

export interface MallAddress {
  id: number;
  name: string;
  mobile: string;
  province: string;
  city: string;
  district: string;
  detail: string;
  isDefault: boolean;
}

export interface MallCoupon {
  id: number;
  name: string;
  amount: number;
  minAmount: number;
  expireTime: string;
  status: "unused" | "locked" | "used" | "expired";
}

export interface MallOrderItem {
  id: number;
  goodsId: number;
  variantId?: number | null;
  variantLabel?: string | null;
  name: string;
  price: number;
  quantity: number;
  /** 该订单项是否已有售后（APPLIED/APPROVED/REJECTED/COMPLETED/CLOSED），有则不再展示申请售后按钮 */
  afterSaleStatus?: string;
}

export interface MallOrder {
  id: string;
  status: "pending" | "paid" | "shipping" | "finished" | "cancelled";
  createTime: string;
  totalAmount: number;
  payAmount: number;
  addressText: string;
  itemList: MallOrderItem[];
  /** 快递公司（已发货时有值） */
  expressCompany?: string;
  /** 快递单号（已发货时有值） */
  expressNo?: string;
  /** 发货时间（已发货时有值） */
  shipTime?: string;
}

export interface LogisticsTrace {
  time: string;
  desc: string;
}

export interface MallUserProfile {
  id: number;
  nickName: string;
  mobile: string;
  levelName: string;
  points: number;
  couponCount: number;
}

export interface CreateOrderPayload {
  userId: number;
  addressId: number;
  couponId?: number;
  totalAmount: number;
  payAmount: number;
  isDiscreet?: boolean;
  itemList: MallOrderItem[];
}

export interface CreateOrderFromCartPayload {
  userId: number;
  addressId: number;
  couponId?: number;
  isDiscreet?: boolean;
}

export interface UpdateAddressPayload extends Omit<MallAddress, "id"> {
  id: number;
  userId: number;
}
