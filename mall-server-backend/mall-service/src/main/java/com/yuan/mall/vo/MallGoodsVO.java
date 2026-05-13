package com.yuan.mall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MallGoodsVO {

    private Long id;
    private String name;
    private String discreetName;
    private String categoryName;
    private String characteristic;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String unit;
    private Integer stock;
    private Integer monthlySales;
    private String tags;
    private String bannerList;
    private String detailList;
    private Boolean status;

    /**
     * 规格 SKU 列表（用于后台编辑）
     */
    private List<MallGoodsVariantVO> variants;
}
