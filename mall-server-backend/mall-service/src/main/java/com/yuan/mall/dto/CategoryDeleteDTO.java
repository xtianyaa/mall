package com.yuan.mall.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryDeleteDTO {

    @NotNull(message = "分类 ID 不能为空")
    private Long id;
}
