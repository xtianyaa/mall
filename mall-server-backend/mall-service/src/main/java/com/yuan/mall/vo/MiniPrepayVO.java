package com.yuan.mall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 预支付返回（占位：后续替换为微信支付所需参数）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MiniPrepayVO {

    private String orderId;
    private String payChannel;
    private String payDeadline;

    /**
     * 预留字段：真实接入后返回小程序支付参数（timeStamp/nonceStr/package/signType/paySign 等）
     */
    private String raw;
}

