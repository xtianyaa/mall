package com.yuan.mall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 支付预下单参数（后续对接微信支付 JSAPI/小程序支付）
 */
@Data
public class MiniPayPrepayDTO {

    @NotNull(message = "用户 ID 不能为空")
    private Long userId;

    @NotBlank(message = "订单号不能为空")
    private String orderId;

    /**
     * 支付渠道（预留）：wx_jsapi / wx_mp 等
     */
    private String payChannel;
}

