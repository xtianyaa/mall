export interface AfterSaleLog {
  action: string;
  remark?: string | null;
  operatorRole: "USER" | "ADMIN" | string;
  createTime: string;
}

export interface AfterSaleDetail {
  id: number;
  /** 订单编号（如 MO1773409046226000），管理端展示用 */
  orderNo?: string;
  orderId: string;
  userId: number;
  userName?: string | null;
  type: "REFUND_ONLY" | "RETURN_REFUND" | "EXCHANGE" | string;
  status: string;
  applyAmount: number;
  actualAmount: number;
  reason: string;
  description?: string | null;
  applyTime: string;
  updateTime: string;
  itemList: {
    id: string;
    goodsId: number;
    goodsName: string;
    goodsPrice: number;
    quantity: number;
  }[];
  logList: AfterSaleLog[];
}

