package com.yuan.mall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 分类更新参数（含图标）
 */
@Data
public class CategoryUpdateDTO {

    @NotNull(message = "分类 ID 不能为空")
    private Long id;

    @NotBlank(message = "分类名称不能为空")
    private String name;

    /** 分类图标 URL，可选 */
    private String icon;

    @NotNull(message = "排序不能为空")
    private Integer sort;

    @NotNull(message = "状态不能为空")
    private Boolean status;
}
