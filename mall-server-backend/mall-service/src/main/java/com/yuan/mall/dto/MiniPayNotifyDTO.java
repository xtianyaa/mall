package com.yuan.mall.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 支付回调参数（占位：后续对接支付网关回调）
 */
@Data
public class MiniPayNotifyDTO {

    @NotBlank(message = "订单号不能为空")
    private String orderId;

    /**
     * 回调结果：SUCCESS / FAIL
     */
    @NotBlank(message = "支付结果不能为空")
    private String result;

    /**
     * 第三方交易号/支付流水号（可选）
     */
    private String tradeNo;
}

