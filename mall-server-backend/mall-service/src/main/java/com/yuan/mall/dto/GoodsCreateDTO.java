package com.yuan.mall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品创建参数
 */
@Data
public class GoodsCreateDTO {

    @NotBlank(message = "商品名称不能为空")
    private String name;

    private String discreetName;

    @NotBlank(message = "分类名称不能为空")
    private String categoryName;

    @NotBlank(message = "商品卖点不能为空")
    private String characteristic;

    @NotNull(message = "商品价格不能为空")
    private BigDecimal price;

    @NotNull(message = "划线价不能为空")
    private BigDecimal originalPrice;

    @NotBlank(message = "规格单位不能为空")
    private String unit;

    @NotNull(message = "库存不能为空")
    private Integer stock;

    private String tags;

    private String bannerList;

    private String detailList;

    @NotNull(message = "上架状态不能为空")
    private Boolean status;

    /**
     * 规格 SKU 列表：若为空则默认生成一个“默认规格SKU”，沿用商品级 price/stock。
     */
    private List<GoodsVariantCreateDTO> variants;
}
