package com.yuan.mall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 小程序：商品规格 SKU 视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MiniGoodsVariantVO {

    private Long id;

    /**
     * 规格维度键值对，如 {"颜色":"红","尺码":"S","套餐":"礼盒"}
     */
    private Map<String, String> specs;

    /**
     * 维度名列表（按顺序），如 ["颜色","尺码","套餐"]
     */
    private List<String> specNames;

    /**
     * 可用库存（stock - lockedStock）
     */
    private Integer stock;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private String unit;
}
