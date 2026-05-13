package com.yuan.mall.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class GoodsBatchDeleteDTO {

    @NotEmpty(message = "请选择要删除的商品")
    private List<Long> ids;
}
