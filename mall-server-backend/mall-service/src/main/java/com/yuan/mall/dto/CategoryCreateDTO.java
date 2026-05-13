package com.yuan.mall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 分类创建参数
 */
@Data
public class CategoryCreateDTO {

    @NotBlank(message = "分类名称不能为空")
    private String name;

    /** 分类图标 URL，可选 */
    private String icon;

    @NotNull(message = "排序不能为空")
    private Integer sort;

    @NotNull(message = "状态不能为空")
    private Boolean status;
}
