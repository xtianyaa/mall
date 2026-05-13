package com.yuan.mall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 售后详情视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AfterSaleVO {

    private Long id;

    /** 订单编号（如 MO1773409046226000），管理端展示用 */
    private String orderNo;

    private String orderId;

    private Long userId;

    private String userName;

    private String type;

    private String status;

    private BigDecimal applyAmount;

    private BigDecimal actualAmount;

    private String reason;

    private String description;

    private String applyTime;

    private String updateTime;

    private List<MiniOrderItemVO> itemList;

    private List<AfterSaleLogVO> logList;
}

