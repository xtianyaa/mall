package com.yuan.mall.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品更新参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GoodsUpdateDTO extends GoodsCreateDTO {

    @NotNull(message = "商品 ID 不能为空")
    private Long id;
}
