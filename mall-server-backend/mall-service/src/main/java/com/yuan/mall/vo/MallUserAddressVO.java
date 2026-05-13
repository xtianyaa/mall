package com.yuan.mall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户地址视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MallUserAddressVO {

    private Long id;
    private Long userId;
    private String userName;
    private String mobile;
    private String consignee;
    private String addressText;
    private Boolean isDefault;
}
