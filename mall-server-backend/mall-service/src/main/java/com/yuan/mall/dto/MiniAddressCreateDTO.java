package com.yuan.mall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 小程序地址创建参数
 */
@Data
public class MiniAddressCreateDTO {

    @NotNull(message = "用户 ID 不能为空")
    private Long userId;

    @NotBlank(message = "收货人不能为空")
    private String name;

    @NotBlank(message = "手机号不能为空")
    private String mobile;

    @NotBlank(message = "省份不能为空")
    private String province;

    @NotBlank(message = "城市不能为空")
    private String city;

    @NotBlank(message = "区县不能为空")
    private String district;

    @NotBlank(message = "详细地址不能为空")
    private String detail;

    @NotNull(message = "默认标记不能为空")
    private Boolean isDefault;
}
