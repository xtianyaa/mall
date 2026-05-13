package com.yuan.mall.service;

/**
 * 小程序端鉴权服务（简化版）
 */
public interface MiniAuthService {

    /**
     * 校验 userId + token 是否有效
     */
    boolean validate(Long userId, String token);
}

