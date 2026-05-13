package com.yuan.mall.controller;

import com.yuan.mall.common.Result;
import com.yuan.mall.dto.AdminChangePasswordDTO;
import com.yuan.mall.dto.AdminLoginDTO;
import com.yuan.mall.dto.AdminRefreshDTO;
import com.yuan.mall.service.AdminAuthService;
import com.yuan.mall.vo.AdminLoginVO;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台管理员认证控制器：登录、刷新 token、登出
 */
@Validated
@RestController
@RequestMapping("/mall/admin")
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    public AdminAuthController(AdminAuthService adminAuthService) {
        this.adminAuthService = adminAuthService;
    }

    @PostMapping("/login")
    public Result<AdminLoginVO> login(@Valid @RequestBody AdminLoginDTO dto) {
        return Result.success(adminAuthService.login(dto));
    }

    /**
     * 使用刷新 token 换取新的 access/refresh token
     */
    @PostMapping("/refresh")
    public Result<AdminLoginVO> refresh(@Valid @RequestBody AdminRefreshDTO dto) {
        return Result.success(adminAuthService.refresh(dto.getRefreshToken()));
    }

    /**
     * 登出：服务端使当前 token 失效
     */
    @PostMapping("/logout")
    public Result<Boolean> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = parseBearerToken(authorization);
        adminAuthService.logout(token);
        return Result.success(true);
    }

    /**
     * 修改密码：需要携带 Authorization: Bearer <accessToken>
     */
    @PostMapping("/password/change")
    public Result<Boolean> changePassword(@RequestHeader(value = "Authorization", required = false) String authorization,
                                          @Valid @RequestBody AdminChangePasswordDTO dto) {
        String token = parseBearerToken(authorization);
        adminAuthService.changePassword(token, dto);
        return Result.success(true);
    }

    private static String parseBearerToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        return authorization.substring("Bearer ".length()).trim();
    }
}

