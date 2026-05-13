package com.yuan.mall.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 系统设置更新参数
 */
@Data
public class SystemSettingUpdateDTO {

    @NotBlank(message = "商城名称不能为空")
    private String mallName;

    @NotBlank(message = "客服电话不能为空")
    private String serviceMobile;

    @NotBlank(message = "自动取消时长不能为空")
    private String cancelMinutes;

    @NotBlank(message = "包邮门槛不能为空")
    private String freeShippingAmount;
}
