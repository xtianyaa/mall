package com.yuan.mall.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Banner 删除参数
 */
@Data
public class BannerDeleteDTO {

    @NotNull(message = "Banner ID 不能为空")
    private Long id;
}

