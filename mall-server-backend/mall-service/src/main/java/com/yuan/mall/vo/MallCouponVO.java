package com.yuan.mall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MallCouponVO {

    private Long id;
    private String name;
    private BigDecimal amount;
    private BigDecimal minAmount;
    private Integer receiveCount;
    private Boolean status;
    /** 可用开始时间，null 表示立即生效 */
    private LocalDateTime startTime;
    /** 可用结束时间（过期时间） */
    private LocalDateTime expireTime;
}
