package com.yuan.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 购物车实体
 */
@Data
@TableName("mall_cart")
public class MallCartEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long goodsId;

    /**
     * 规格 SKU ID（兼容旧购物车：可能为空）
     */
    private Long variantId;

    private Integer quantity;

    private Boolean checked;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

