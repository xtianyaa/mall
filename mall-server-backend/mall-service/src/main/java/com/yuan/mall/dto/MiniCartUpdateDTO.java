package com.yuan.mall.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 购物车更新数量/勾选
 */
@Data
public class MiniCartUpdateDTO {

    @NotNull(message = "用户 ID 不能为空")
    private Long userId;

    @NotNull(message = "商品 ID 不能为空")
    private Long goodsId;

    /**
     * 规格 SKU ID（可空：兼容旧前端只按 goodsId 操作购物车）
     */
    private Long variantId;

    @Min(value = 1, message = "数量必须大于 0")
    private Integer quantity;

    private Boolean checked;
}

