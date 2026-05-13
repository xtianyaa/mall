package com.yuan.mall.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券创建参数
 */
@Data
public class CouponCreateDTO {

    @NotBlank(message = "优惠券名称不能为空")
    private String name;

    @NotNull(message = "优惠券面额不能为空")
    private BigDecimal amount;

    @NotNull(message = "使用门槛不能为空")
    private BigDecimal minAmount;

    @NotNull(message = "状态不能为空")
    private Boolean status;

    /** 可用开始时间，null 表示立即生效 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;

    /** 可用结束时间（过期时间），不填则默认 30 天后 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime expireTime;
}
