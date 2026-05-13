package com.yuan.mall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户优惠券视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MallUserCouponVO {

    private Long id;
    private Long userId;
    private String userName;
    private String couponName;
    private String couponAmount;
    private String useThreshold;
    private String status;
    private String expireTime;
}
