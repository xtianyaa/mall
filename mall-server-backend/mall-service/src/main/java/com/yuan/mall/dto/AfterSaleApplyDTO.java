package com.yuan.mall.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 小程序端提交售后申请参数
 */
@Data
public class AfterSaleApplyDTO {

    /**
     * 订单 ID（字符串形式，保持与现有 MiniOrderVO 一致）
     */
    @NotBlank
    private String orderId;

    /**
     * 订单明细 ID
     */
    @NotNull
    private Long orderItemId;

    /**
     * 用户 ID
     */
    @NotNull
    private Long userId;

    /**
     * 售后类型：REFUND_ONLY / RETURN_REFUND / EXCHANGE
     */
    @NotBlank
    private String type;

    /**
     * 申请数量
     */
    @NotNull
    @Min(1)
    private Integer quantity;

    /**
     * 申请原因
     */
    @NotBlank
    private String reason;

    /**
     * 补充说明（可选）
     */
    private String description;

    /**
     * 凭证图片地址列表（可选）
     */
    private List<String> evidenceImageList;
}

