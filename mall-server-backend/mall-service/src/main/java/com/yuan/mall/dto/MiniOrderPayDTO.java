package com.yuan.mall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 小程序订单支付（模拟）参数
 */
@Data
public class MiniOrderPayDTO {

    @NotNull(message = "用户 ID 不能为空")
    private Long userId;

    @NotBlank(message = "订单号不能为空")
    private String orderId;
}

