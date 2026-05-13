package com.yuan.mall.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商品规格 SKU 创建参数（支持任意维度，JSON 格式）
 */
@Data
public class GoodsVariantCreateDTO {

    /**
     * 规格维度键值对，如 {"颜色":"红","尺码":"S","套餐":"礼盒"}
     * 键为维度名（非数据库列名），值为具体选项值
     */
    private Map<String, String> specs;

    /**
     * 维度名列表（按顺序，最多 5 个），如 ["颜色","尺码","套餐"]
     * 长度必须与 specs 的键集合顺序一致
     */
    private List<String> specNames;

    @NotNull(message = "SKU 销售价不能为空")
    private BigDecimal price;

    @NotNull(message = "SKU 划线价不能为空")
    private BigDecimal originalPrice;

    @NotNull(message = "SKU 计量单位不能为空")
    private String unit;

    @NotNull(message = "SKU 库存不能为空")
    private Integer stock;
}
