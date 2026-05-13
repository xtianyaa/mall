package com.yuan.mall.vo;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 小程序购物车条目
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MiniCartItemVO {

    private Long goodsId;

    private Long variantId;

    private String variantLabel;

    private BigDecimal variantPrice;

    private Integer quantity;
    private Boolean checked;
}

