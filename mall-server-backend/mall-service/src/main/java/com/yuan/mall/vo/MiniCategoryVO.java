package com.yuan.mall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 小程序分类视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MiniCategoryVO {

    private Long id;
    private String name;
    private String icon;
}
