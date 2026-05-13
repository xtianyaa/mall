package com.yuan.mall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 小程序地址视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MiniAddressVO {

    private Long id;
    private String name;
    private String mobile;
    private String province;
    private String city;
    private String district;
    private String detail;
    private Boolean isDefault;
}
