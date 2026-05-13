package com.yuan.mall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 小程序用户信息视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MiniUserProfileVO {

    private Long id;
    private String nickName;
    private String mobile;
    private String levelName;
    private Integer points;
    private Integer couponCount;

    private String token;

    private String tokenExpireTime;
}
