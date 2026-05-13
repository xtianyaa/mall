package com.yuan.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 支付回调日志
 */
@Data
@TableName("mall_pay_log")
public class MallPayLogEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private String tradeNo;

    private String payChannel;

    private String result;

    private String raw;

    private LocalDateTime createTime;
}

