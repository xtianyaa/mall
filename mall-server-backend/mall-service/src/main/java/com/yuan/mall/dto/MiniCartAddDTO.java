package com.yuan.mall.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 购物车添加/累加
 */
@Data
public class MiniCartAddDTO {

    @NotNull(message = "用户 ID 不能为空")
    private Long userId;

    @NotNull(message = "商品 ID 不能为空")
    private Long goodsId;

    /**
     * 规格 SKU ID（可空：兼容旧前端只按 goodsId 加购的场景）
     */
    private Long variantId;

    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于 0")
    private Integer quantity;
}

