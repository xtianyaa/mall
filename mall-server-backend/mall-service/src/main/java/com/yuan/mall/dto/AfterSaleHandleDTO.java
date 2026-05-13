package com.yuan.mall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 管理端处理售后单参数
 */
@Data
public class AfterSaleHandleDTO {

    /**
     * 售后主单 ID
     */
    @NotNull
    private Long afterSaleId;

    /**
     * 操作类型：APPROVE / REJECT / COMPLETE
     */
    @NotBlank
    private String action;

    /**
     * 操作人 ID（管理员，可选；未传时日志中为空）
     */
    private Long operatorId;

    /**
     * 实际退款金额（仅在同意时可选）
     */
    private BigDecimal actualAmount;

    /**
     * 备注
     */
    private String remark;
}

