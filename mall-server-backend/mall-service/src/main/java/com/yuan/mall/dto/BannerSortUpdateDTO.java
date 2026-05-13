package com.yuan.mall.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Banner 排序更新参数
 */
@Data
public class BannerSortUpdateDTO {

    @NotNull(message = "Banner ID 不能为空")
    private Long id;

    @NotNull(message = "排序不能为空")
    private Integer sort;
}
