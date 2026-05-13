package com.yuan.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券实体
 */
@Data
@TableName("mall_coupon")
public class MallCouponEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private BigDecimal amount;

    private BigDecimal minAmount;

    private Integer receiveCount;

    private Boolean status;

    /** 可用开始时间，null 表示立即生效 */
    private LocalDateTime startTime;

    private LocalDateTime expireTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
