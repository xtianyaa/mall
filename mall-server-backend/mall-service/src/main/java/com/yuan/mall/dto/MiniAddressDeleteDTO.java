package com.yuan.mall.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 小程序地址删除参数
 */
@Data
public class MiniAddressDeleteDTO {

    @NotNull(message = "用户 ID 不能为空")
    private Long userId;

    @NotNull(message = "地址 ID 不能为空")
    private Long addressId;
}
