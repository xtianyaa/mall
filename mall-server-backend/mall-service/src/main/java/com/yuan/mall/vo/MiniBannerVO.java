package com.yuan.mall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 小程序首页 Banner 视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MiniBannerVO {

    private Long id;
    private String title;
    private String imageUrl;
    private String linkType;
    private String linkUrl;
    private Integer sort;
}
