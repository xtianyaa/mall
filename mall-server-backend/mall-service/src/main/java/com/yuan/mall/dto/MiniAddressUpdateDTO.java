package com.yuan.mall.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 小程序地址更新参数
 */
@Data
public class MiniAddressUpdateDTO extends MiniAddressCreateDTO {

    @NotNull(message = "地址 ID 不能为空")
    private Long id;
}
