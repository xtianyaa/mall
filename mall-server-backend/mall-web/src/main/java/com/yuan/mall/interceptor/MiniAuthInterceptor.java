package com.yuan.mall.interceptor;

import com.yuan.mall.service.MiniAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 小程序端简化鉴权拦截器：
 * - 通过请求头传递 X-User-Id + X-Token
 * - 校验 token 与有效期
 *
 * 说明：后续可替换为 JWT / Session / 小程序 code2Session。
 */
@Component
public class MiniAuthInterceptor implements HandlerInterceptor {

    private final MiniAuthService miniAuthService;

    public MiniAuthInterceptor(MiniAuthService miniAuthService) {
        this.miniAuthService = miniAuthService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // OPTIONS 放行
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();
        // 公开接口放行：banner/home/deco/category/goods、system/config（商城名称等）、login
        if (path.startsWith("/mall/mini/banner")
                || path.startsWith("/mall/mini/home")
                || path.startsWith("/mall/mini/category")
                || path.startsWith("/mall/mini/goods")
                || path.startsWith("/mall/mini/system")
                || path.equals("/mall/mini/login")) {
            return true;
        }

        String userIdHeader = request.getHeader("X-User-Id");
        String token = request.getHeader("X-Token");
        if (userIdHeader == null || userIdHeader.isBlank() || token == null || token.isBlank()) {
            response.setStatus(401);
            return false;
        }

        Long userId;
        try {
            userId = Long.parseLong(userIdHeader.trim());
        } catch (Exception ignore) {
            response.setStatus(401);
            return false;
        }

        if (!miniAuthService.validate(userId, token)) {
            response.setStatus(401);
            return false;
        }
        return true;
    }
}

