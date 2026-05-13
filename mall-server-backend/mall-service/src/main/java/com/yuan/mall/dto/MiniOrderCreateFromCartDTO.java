package com.yuan.mall.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 从购物车勾选项创建订单
 */
@Data
public class MiniOrderCreateFromCartDTO {

    @NotNull(message = "用户 ID 不能为空")
    private Long userId;

    @NotNull(message = "地址 ID 不能为空")
    private Long addressId;

    private Long couponId;
}

