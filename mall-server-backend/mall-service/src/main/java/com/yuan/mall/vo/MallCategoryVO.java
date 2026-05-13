package com.yuan.mall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分类视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MallCategoryVO {

    private Long id;
    private String name;
    private String icon;
    private Integer sort;
    private Boolean status;
    private Integer goodsCount;
}
