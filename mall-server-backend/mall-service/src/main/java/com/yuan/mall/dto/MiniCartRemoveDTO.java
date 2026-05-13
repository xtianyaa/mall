package com.yuan.mall.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 购物车删除商品
 */
@Data
public class MiniCartRemoveDTO {

    @NotNull(message = "用户 ID 不能为空")
    private Long userId;

    @NotNull(message = "商品 ID 不能为空")
    private Long goodsId;

    /**
     * 规格 SKU ID（可空：兼容旧前端只按 goodsId 删除）
     */
    private Long variantId;
}

