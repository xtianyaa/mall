package com.yuan.mall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 小程序优惠券视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MiniCouponVO {

    private Long id;
    private String name;
    private BigDecimal amount;
    private BigDecimal minAmount;
    /** 可用开始时间，空表示立即生效 */
    private String startTime;
    private String expireTime;
    private String status;
}
