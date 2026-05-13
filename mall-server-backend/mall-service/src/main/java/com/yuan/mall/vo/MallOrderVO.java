package com.yuan.mall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 订单视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MallOrderVO {

    private Long userId;
    private String orderId;
    private String userName;
    private BigDecimal payAmount;
    private Boolean isDiscreet;
    private String status;
    private String createTime;

    /** 该订单关联的售后单数量（0 表示无售后） */
    private Integer afterSaleCount;
}
