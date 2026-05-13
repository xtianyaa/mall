package com.yuan.mall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 后台登录返回
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminLoginVO {

    private String token;

    private String refreshToken;

    private String username;

    private Long expireTime;

    private Long refreshExpireTime;
}
