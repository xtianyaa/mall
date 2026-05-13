package com.yuan.mall.service;

import com.yuan.mall.dto.AdminChangePasswordDTO;
import com.yuan.mall.dto.AdminLoginDTO;
import com.yuan.mall.vo.AdminLoginVO;

/**
 * 后台管理员认证服务
 */
public interface AdminAuthService {

    /**
     * 管理员登录，返回 JWT 等信息
     *
     * @param dto 登录参数
     * @return 登录结果
     */
    AdminLoginVO login(AdminLoginDTO dto);

    /**
     * 校验访问 token 是否有效
     *
     * @param token access token
     * @return 是否有效
     */
    boolean validate(String token);

    /**
     * 使用刷新 token 换取新的 access/refresh token
     *
     * @param refreshToken 刷新 token
     * @return 新的登录结果
     */
    AdminLoginVO refresh(String refreshToken);

    /**
     * 登出：使当前 access token 失效（清空该管理员的 token 与 refreshToken）
     *
     * @param accessToken 当前访问 token
     */
    void logout(String accessToken);

    /**
     * 修改密码
     *
     * @param accessToken 当前访问 token
     * @param dto         修改密码参数
     */
    void changePassword(String accessToken, AdminChangePasswordDTO dto);
}

