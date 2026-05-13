package com.yuan.mall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统设置视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MallSystemSettingVO {

    private Long id;
    private String mallName;
    private String serviceMobile;
    private String cancelMinutes;
    private String freeShippingAmount;
}
