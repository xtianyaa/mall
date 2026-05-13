package com.yuan.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体
 */
@Data
@TableName("mall_order")
public class MallOrderEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long couponId;

    private String orderNo;

    private String userName;

    private BigDecimal totalAmount;

    private BigDecimal payAmount;

    private String addressText;

    private Boolean isDiscreet;

    private String status;

    private String payChannel;

    private String payTradeNo;

    private LocalDateTime payDeadline;

    private LocalDateTime payTime;

    private LocalDateTime cancelTime;

    private String expressCompany;

    private String expressNo;

    private LocalDateTime shipTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
