package com.yuan.mall.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CategoryBatchDeleteDTO {

    @NotEmpty(message = "请选择要删除的分类")
    private List<Long> ids;
}
