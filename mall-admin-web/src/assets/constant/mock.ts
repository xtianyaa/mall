import type {
  AdminBanner,
  AdminCategory,
  AdminCoupon,
  AdminGoods,
  AdminOrder,
  AdminUser,
} from "../../types/mall";

export const adminCategoryList: AdminCategory[] = [
  { id: 1, name: "应季鲜果", sort: 1, status: true, goodsCount: 18 },
  { id: 2, name: "轻食乳品", sort: 2, status: true, goodsCount: 12 },
  { id: 3, name: "休闲零食", sort: 3, status: true, goodsCount: 23 },
  { id: 4, name: "精选礼盒", sort: 4, status: false, goodsCount: 9 },
];

export const adminGoodsList: AdminGoods[] = [
  { id: 1001, name: "云南高山蓝莓 125g", categoryName: "应季鲜果", characteristic: "高山冷链直发，入口爆汁", price: 19.9, originalPrice: 29.9, unit: "125g/盒", stock: 168, monthlySales: 392, tags: "新品,鲜果直发", bannerList: "蓝莓主图,果园实拍,开箱图", detailList: "高山产区,冷链到仓,坏果包赔", status: true },
  { id: 1002, name: "赣南脐橙 优选装 5kg", categoryName: "应季鲜果", characteristic: "果径均匀，酸甜平衡", price: 49.9, originalPrice: 59.9, unit: "5kg/箱", stock: 96, monthlySales: 248, tags: "家庭装,当季热销", bannerList: "脐橙主图,切面图", detailList: "赣南核心产区,手工分拣,足斤足两", status: true },
  { id: 1003, name: "A2 高钙纯牛奶 250ml*10", categoryName: "轻食乳品", characteristic: "早餐囤货，口感香浓", price: 36.8, originalPrice: 45.9, unit: "250ml*10", stock: 125, monthlySales: 185, tags: "早餐必备,整箱优惠", bannerList: "牛奶主图,早餐场景", detailList: "高钙配方,独立小包装,冷藏口感更佳", status: true },
  { id: 1004, name: "轻烘焙海盐开心果 500g", categoryName: "休闲零食", characteristic: "低温轻烘焙，颗颗开口", price: 58, originalPrice: 69, unit: "500g/袋", stock: 76, monthlySales: 133, tags: "办公零食,坚果热卖", bannerList: "开心果主图,坚果特写", detailList: "低温烘焙,颗粒饱满,独立密封", status: false },
];

export const adminOrderList: AdminOrder[] = [
  { userId: 20260309, orderId: "MO20260308001", userName: "陈晓雯", payAmount: 76.7, status: "配送中", createTime: "2026-03-08 18:40" },
  { userId: 20260310, orderId: "MO20260309002", userName: "张一鸣", payAmount: 49.9, status: "待发货", createTime: "2026-03-09 10:25" },
  { userId: 20260311, orderId: "MO20260307012", userName: "李清欢", payAmount: 89, status: "已完成", createTime: "2026-03-07 12:18" },
];

export const adminUserList: AdminUser[] = [
  { id: 20260309, nickName: "陈晓雯", mobile: "13800138000", levelName: "银卡会员", orderCount: 12, consumeAmount: 1260 },
  { id: 20260310, nickName: "张一鸣", mobile: "13900139000", levelName: "普通会员", orderCount: 4, consumeAmount: 238 },
  { id: 20260311, nickName: "李清欢", mobile: "13700137000", levelName: "金卡会员", orderCount: 19, consumeAmount: 2680 },
];

export const adminCouponList: AdminCoupon[] = [
  { id: 1, name: "新人立减券", amount: 10, minAmount: 59, receiveCount: 842, status: true },
  { id: 2, name: "鲜果专区券", amount: 15, minAmount: 99, receiveCount: 316, status: true },
  { id: 3, name: "节日礼盒券", amount: 20, minAmount: 129, receiveCount: 102, status: false },
];

export const adminBannerList: AdminBanner[] = [
  { id: 1, title: "春日鲜果节", imageUrl: "/upload/fa9ffe23c802439287adc8d49e439135.png", linkType: "商品专题", linkUrl: "topic=spring", status: true, sort: 1 },
  { id: 2, title: "新人专享首单礼", imageUrl: "/upload/fa9ffe23c802439287adc8d49e439135.png", linkType: "优惠券", linkUrl: "couponId=1", status: true, sort: 2 },
  { id: 3, title: "礼盒满 199 包邮", imageUrl: "/upload/fa9ffe23c802439287adc8d49e439135.png", linkType: "自定义链接", linkUrl: "https://example.com/promo", status: false, sort: 3 },
];
