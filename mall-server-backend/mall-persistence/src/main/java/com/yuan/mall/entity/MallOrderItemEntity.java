package com.yuan.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单明细实体
 */
@Data
@TableName("mall_order_item")
public class MallOrderItemEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private Long goodsId;

    /**
     * 规格 SKU ID（旧订单可能为空）
     */
    private Long variantId;

    private String goodsName;

    private BigDecimal goodsPrice;

    private Integer quantity;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
