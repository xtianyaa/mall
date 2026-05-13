package com.yuan.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品规格 SKU 实体
 */
@Data
@TableName("mall_goods_variant")
public class MallGoodsVariantEntity {

    private static final ObjectMapper OM = new ObjectMapper();

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long goodsId;

    /**
     * 商品名称快照（便于展示/下单兜底）
     */
    private String goodsName;

    /**
     * 规格维度键值对，如 {"颜色":"红","尺码":"S","套餐":"礼盒"}
     */
    private String specs;

    /**
     * 维度名列表（按顺序，最多5个），如 ["颜色","尺码","套餐"]
     */
    private String specNames;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private String unit;

    private Integer stock;

    private Integer lockedStock;

    /**
     * 状态：1=上架 0=下架
     */
    private Boolean status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    // -------------------- JSON 便捷访问 --------------------

    /**
     * 将 specs JSON 解析为 Map 返回（只读视图）
     */
    public Map<String, String> getSpecsMap() {
        if (specs == null || specs.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            Map<String, Object> raw = OM.readValue(specs, new TypeReference<>() {});
            Map<String, String> result = new LinkedHashMap<>();
            for (Map.Entry<String, Object> e : raw.entrySet()) {
                result.put(e.getKey(), e.getValue() == null ? "" : String.valueOf(e.getValue()));
            }
            return result;
        } catch (JsonProcessingException e) {
            return Collections.emptyMap();
        }
    }

    /**
     * 将 specNames JSON 解析为 List 返回（只读视图）
     */
    public List<String> getSpecNamesList() {
        if (specNames == null || specNames.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return OM.readValue(specNames, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }

    /**
     * 设置 specs（传入 Map，自动序列化为 JSON 字符串）
     */
    public void setSpecsFromMap(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            this.specs = "{}";
            return;
        }
        try {
            this.specs = OM.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            this.specs = "{}";
        }
    }

    /**
     * 设置 specNames（传入 List，自动序列化为 JSON 字符串）
     */
    public void setSpecNamesFromList(List<String> list) {
        if (list == null || list.isEmpty()) {
            this.specNames = "[]";
            return;
        }
        try {
            this.specNames = OM.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            this.specNames = "[]";
        }
    }

    /**
     * 构造规格展示文案：从 specNames 顺序读取 specs 值，用 "/" 连接
     */
    public String buildLabel() {
        List<String> names = getSpecNamesList();
        Map<String, String> map = getSpecsMap();
        if (names.isEmpty() || map.isEmpty()) {
            return "默认规格";
        }
        List<String> parts = new ArrayList<>();
        for (String name : names) {
            String val = map.get(name);
            if (val != null && !val.isBlank()) {
                parts.add(val);
            }
        }
        return parts.isEmpty() ? "默认规格" : String.join("/", parts);
    }

    /**
     * 可用库存（stock - lockedStock）
     */
    public int getAvailableStock() {
        int s = stock == null ? 0 : stock;
        int l = lockedStock == null ? 0 : lockedStock;
        return Math.max(s - l, 0);
    }
}
