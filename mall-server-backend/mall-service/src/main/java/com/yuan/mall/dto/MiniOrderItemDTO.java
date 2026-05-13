package com.yuan.mall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 小程序订单商品参数
 */
@Data
public class MiniOrderItemDTO {

    @NotNull(message = "商品 ID 不能为空")
    private Long goodsId;

    /**
     * 规格 SKU ID（可空：兼容旧订单逻辑/未传规格场景）
     */
    private Long variantId;

    @NotBlank(message = "商品名称不能为空")
    private String name;

    @NotNull(message = "商品价格不能为空")
    private BigDecimal price;

    @NotNull(message = "商品数量不能为空")
    private Integer quantity;
}
