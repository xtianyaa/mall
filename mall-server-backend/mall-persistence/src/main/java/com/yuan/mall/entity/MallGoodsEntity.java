package com.yuan.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体
 */
@Data
@TableName("mall_goods")
public class MallGoodsEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long categoryId;

    private String categoryName;

    private String name;

    /**
     * 目前不在前后端商品维护页面中暴露隐秘名称，后续如有需要可复用该字段
     */
    private String discreetName;

    private String characteristic;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private String unit;

    private Integer stock;

    private Integer lockedStock;

    private Integer monthlySales;

    private Boolean status;

    private String tags;

    private String bannerList;

    private String detailList;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

