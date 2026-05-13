package com.yuan.mall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 小程序订单视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MiniOrderVO {

    private String id;
    private String status;
    private String createTime;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private String addressText;
    private Boolean isDiscreet;
    private List<MiniOrderItemVO> itemList;
    /** 快递公司（已发货时有值） */
    private String expressCompany;
    /** 快递单号（已发货时有值） */
    private String expressNo;
    /** 发货时间（已发货时有值，格式 yyyy-MM-dd HH:mm:ss） */
    private String shipTime;
}
