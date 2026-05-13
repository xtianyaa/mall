export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
  timestamp: number;
}

export interface AdminDashboard {
  todayOrderCount: number;
  waitShipCount: number;
  userCount: number;
  todayAmount: string;
}

export interface AdminCategory {
  id: number;
  name: string;
  icon?: string;
  sort: number;
  status: boolean;
  goodsCount: number;
}

export interface AdminGoods {
  id: number;
  name: string;
  discreetName?: string;
  categoryName: string;
  characteristic: string;
  price: number;
  originalPrice: number;
  unit: string;
  stock: number;
  monthlySales: number;
  tags: string;
  bannerList: string;
  detailList: string;
  status: boolean;

  defaultVariantId?: number | null;
  /** 规格 SKU 列表（用于新增/编辑） */
  variants?: AdminGoodsVariant[];
}

export interface AdminGoodsVariant {
  id?: number;
  /** 规格维度键值对，如 { "颜色": "红", "尺码": "S" } */
  specs: Record<string, string>;
  /** 维度名列表（按顺序），如 ["颜色", "尺码", "套餐"] */
  specNames: string[];
  price: number;
  originalPrice: number;
  unit: string;
  stock: number;
}

export interface AdminOrder {
  userId: number;
  orderId: string;
  userName: string;
  payAmount: number;
  isDiscreet: boolean;
  status: "待支付" | "待发货" | "配送中" | "已完成" | "已取消";
  createTime: string;
  /** 该订单关联的售后单数量 */
  afterSaleCount?: number;
}

export interface AdminUser {
  id: number;
  nickName: string;
  mobile: string;
  levelName: string;
  orderCount: number;
  consumeAmount: number;
}

export interface AdminUserAddress {
  id: number;
  userId: number;
  userName: string;
  mobile: string;
  consignee: string;
  addressText: string;
  isDefault: boolean;
}

export interface AdminUserCoupon {
  id: number;
  userId: number;
  userName: string;
  couponName: string;
  couponAmount: string;
  useThreshold: string;
  status: string;
  expireTime: string;
}

export interface AdminCoupon {
  id: number;
  name: string;
  amount: number;
  minAmount: number;
  receiveCount: number;
  status: boolean;
  /** 可用开始时间，null 表示立即生效 */
  startTime?: string | null;
  /** 可用结束时间（过期时间） */
  expireTime?: string | null;
}

export interface AdminBanner {
  id: number;
  title: string;
  imageUrl: string;
  linkType: string;
  linkUrl: string;
  status: boolean;
  sort: number;
}

export interface AdminSystemSetting {
  id: number;
  mallName: string;
  serviceMobile: string;
  cancelMinutes: string;
  freeShippingAmount: string;
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

export interface AdminLoginResult {
  token: string;
  refreshToken: string;
  username: string;
  expireTime: number;
  refreshExpireTime: number;
}
