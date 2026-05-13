package com.yuan.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 售后明细实体
 */
@Data
@TableName("mall_after_sale_item")
public class MallAfterSaleItemEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 售后主单 ID
     */
    private Long afterSaleId;

    /**
     * 原订单明细 ID
     */
    private Long orderItemId;

    /**
     * 商品 ID
     */
    private Long goodsId;

    /**
     * 申请售后数量
     */
    private Integer applyQuantity;

    /**
     * 实际同意数量（可选）
     */
    private Integer approvedQuantity;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

