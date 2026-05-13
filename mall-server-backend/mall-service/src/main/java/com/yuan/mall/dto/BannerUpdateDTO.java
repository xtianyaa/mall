package com.yuan.mall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Banner 更新参数
 */
@Data
public class BannerUpdateDTO {

    @NotNull(message = "Banner ID 不能为空")
    private Long id;

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "图片不能为空")
    private String imageUrl;

    @NotBlank(message = "跳转类型不能为空")
    private String linkType;

    private String linkUrl;

    @NotNull(message = "状态不能为空")
    private Boolean status;

    @NotNull(message = "排序不能为空")
    private Integer sort;
}

