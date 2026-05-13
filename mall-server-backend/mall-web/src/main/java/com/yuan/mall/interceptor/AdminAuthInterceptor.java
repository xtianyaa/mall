package com.yuan.mall.interceptor;

import com.yuan.mall.service.AdminAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 后台管理端 JWT 鉴权拦截器
 * - 使用 Authorization: Bearer <token>
 * - 登录接口放行，其余 /mall/admin/** 需要携带有效 token
 */
@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    private final AdminAuthService adminAuthService;

    public AdminAuthInterceptor(AdminAuthService adminAuthService) {
        this.adminAuthService = adminAuthService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }
        String path = request.getRequestURI();
        if (path.equals("/mall/admin/login") || path.equals("/mall/admin/refresh")) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || authHeader.isBlank() || !authHeader.startsWith("Bearer ")) {
            response.setStatus(401);
            return false;
        }
        String token = authHeader.substring("Bearer ".length()).trim();
        if (!adminAuthService.validate(token)) {
            response.setStatus(401);
            return false;
        }
        return true;
    }
}

