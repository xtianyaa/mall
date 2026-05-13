package com.yuan.mall.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 订单发货参数
 */
@Data
public class OrderShipDTO {

    @NotBlank(message = "订单编号不能为空")
    private String orderId;

    @NotBlank(message = "快递公司不能为空")
    private String expressCompany;

    @NotBlank(message = "快递单号不能为空")
    private String expressNo;
}
