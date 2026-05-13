package com.yuan.mall.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GoodsDeleteDTO {

    @NotNull(message = "商品 ID 不能为空")
    private Long id;
}
