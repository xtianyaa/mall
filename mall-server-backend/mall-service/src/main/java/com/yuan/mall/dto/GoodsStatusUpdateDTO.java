package com.yuan.mall.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 商品状态更新参数
 */
@Data
public class GoodsStatusUpdateDTO {

    @NotNull(message = "商品 ID 不能为空")
    private Long id;

    @NotNull(message = "商品状态不能为空")
    private Boolean status;
}
