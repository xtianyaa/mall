package com.yuan.mall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 小程序商品视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MiniGoodsVO {

    private Long id;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String characteristic;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String unit;
    private Integer stock;
    private Integer monthlySales;
    private List<String> tags;
    private List<String> bannerList;
    private List<String> detailList;

    /**
     * 图文详情（兼容 detailList：后端会从 detailList 解析生成）
     */
    private List<MiniGoodsDetailBlockVO> detailRichList;

    /**
     * 默认规格 SKU，用于老商品兼容/未选择规格时展示
     */
    private Long defaultVariantId;

    /**
     * 规格 SKU 列表（用于详情页选择）
     */
    private List<MiniGoodsVariantVO> variants;

    /**
     * 规格维度名列表（按顺序，最多5个），来自第一个 variant 的 specNames
     * 用于小程序展示维度标题
     */
    private List<String> specNames;
}
