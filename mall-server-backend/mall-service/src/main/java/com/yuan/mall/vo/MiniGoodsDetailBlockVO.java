package com.yuan.mall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 小程序商品图文详情块
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MiniGoodsDetailBlockVO {

    /**
     * text / image
     */
    private String type;

    /**
     * 文本内容或图片路径/URL
     */
    private String value;
}

