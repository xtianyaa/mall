package com.yuan.mall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 小程序订单商品视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MiniOrderItemVO {

    /** 订单明细 ID */
    private Long id;

    /** 商品 ID */
    private Long goodsId;
    private String name;
    private BigDecimal price;
    private Integer quantity;

    /** 该订单项是否已有售后申请（APPLIED/APPROVED/REJECTED/COMPLETED/CLOSED），有则不再展示「申请售后」入口 */
    private String afterSaleStatus;

    private Long variantId;

    private String variantLabel;
}
