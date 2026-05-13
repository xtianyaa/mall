package com.yuan.mall.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 分类状态更新参数
 */
@Data
public class CategoryStatusUpdateDTO {

    @NotNull(message = "分类 ID 不能为空")
    private Long id;

    @NotNull(message = "分类状态不能为空")
    private Boolean status;
}
