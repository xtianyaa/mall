package com.yuan.mall.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 小程序订单创建参数
 */
@Data
public class MiniOrderCreateDTO {

    @NotNull(message = "用户 ID 不能为空")
    private Long userId;

    @NotNull(message = "地址 ID 不能为空")
    private Long addressId;

    private Long couponId;

    @NotNull(message = "订单总金额不能为空")
    private BigDecimal totalAmount;

    @NotNull(message = "订单实付金额不能为空")
    private BigDecimal payAmount;

    @Valid
    @NotEmpty(message = "订单商品不能为空")
    private List<MiniOrderItemDTO> itemList;
}

