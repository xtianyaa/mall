package com.yuan.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 售后主单实体
 */
@Data
@TableName("mall_after_sale_order")
public class MallAfterSaleOrderEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 原订单 ID
     */
    private Long orderId;

    /**
     * 申请用户 ID
     */
    private Long userId;

    /**
     * 售后类型：REFUND_ONLY / RETURN_REFUND / EXCHANGE
     */
    private String type;

    /**
     * 售后状态：APPLIED / APPROVED / REJECTED / COMPLETED / CLOSED 等
     */
    private String status;

    /**
     * 申请原因
     */
    private String reason;

    /**
     * 申请金额
     */
    private BigDecimal applyAmount;

    /**
     * 实际退款金额（可选）
     */
    private BigDecimal actualAmount;

    /**
     * 商家备注
     */
    private String remark;

    /**
     * 申请时间
     */
    private LocalDateTime applyTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 允许申请截止时间（用于超期校验，可选）
     */
    private LocalDateTime deadline;
}

