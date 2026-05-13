package com.yuan.mall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 订单状态更新（管理端）
 */
@Data
public class OrderStatusUpdateDTO {

    @NotBlank(message = "订单编号不能为空")
    private String orderId;

    @NotNull(message = "目标状态不能为空")
    private String targetStatus;
}

