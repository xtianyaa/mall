package com.yuan.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.yuan.mall.dto.AdminChangePasswordDTO;
import com.yuan.mall.dto.AdminLoginDTO;
import com.yuan.mall.entity.MallAdminEntity;
import com.yuan.mall.mapper.MallAdminMapper;
import com.yuan.mall.service.AdminAuthService;
import com.yuan.mall.util.AdminJwtUtil;
import com.yuan.mall.vo.AdminLoginVO;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 后台管理员认证服务实现：BCrypt 密码、access/refresh token、登出
 */
@Service
public class AdminAuthServiceImpl implements AdminAuthService {

    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    private final MallAdminMapper mallAdminMapper;

    @Value("${yuan.admin.jwt-secret:bWFsbC1hZG1pbi1zZWNyZXQtYXQtbGVhc3QzMmNoYXJz}")
    private String jwtSecret;

    @Value("${yuan.admin.jwt-ttl-ms:43200000}")
    private long jwtTtlMs;

    @Value("${yuan.admin.jwt-refresh-ttl-ms:604800000}")
    private long jwtRefreshTtlMs;

    public AdminAuthServiceImpl(MallAdminMapper mallAdminMapper) {
        this.mallAdminMapper = mallAdminMapper;
    }

    @Override
    public AdminLoginVO login(AdminLoginDTO dto) {
        MallAdminEntity admin = mallAdminMapper.selectOne(new LambdaQueryWrapper<MallAdminEntity>()
                .eq(MallAdminEntity::getUsername, dto.getUsername())
                .last("limit 1"));
        if (admin == null || admin.getPassword() == null) {
            throw new IllegalArgumentException("账号或密码错误");
        }
        String rawPassword = dto.getPassword();
        boolean passwordOk = admin.getPassword().startsWith("$2a$")
                ? PASSWORD_ENCODER.matches(rawPassword, admin.getPassword())
                : rawPassword.equals(admin.getPassword());
        if (!passwordOk) {
            throw new IllegalArgumentException("账号或密码错误");
        }
        if (!admin.getPassword().startsWith("$2a$")) {
            admin.setPassword(PASSWORD_ENCODER.encode(rawPassword));
            mallAdminMapper.updateById(admin);
        }

        String token = AdminJwtUtil.generateToken(admin.getId(), admin.getUsername(), jwtSecret, jwtTtlMs);
        String refreshToken = AdminJwtUtil.generateRefreshToken(admin.getId(), admin.getUsername(), jwtSecret, jwtRefreshTtlMs);
        long expireAt = System.currentTimeMillis() + jwtTtlMs;
        long refreshExpireAt = System.currentTimeMillis() + jwtRefreshTtlMs;

        admin.setToken(token);
        admin.setTokenExpireTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(expireAt), ZoneId.systemDefault()));
        admin.setRefreshToken(refreshToken);
        admin.setRefreshTokenExpireTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(refreshExpireAt), ZoneId.systemDefault()));
        mallAdminMapper.updateById(admin);

        return AdminLoginVO.builder()
                .token(token)
                .refreshToken(refreshToken)
                .username(admin.getUsername())
                .expireTime(expireAt)
                .refreshExpireTime(refreshExpireAt)
                .build();
    }

    @Override
    public boolean validate(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        Claims claims = AdminJwtUtil.parseToken(token, jwtSecret);
        if (claims == null || !"access".equals(claims.get("tokenType"))) {
            return false;
        }
        Long adminId = getAdminIdFromClaims(claims);
        if (adminId == null) {
            return false;
        }
        MallAdminEntity admin = mallAdminMapper.selectById(adminId);
        if (admin == null) {
            return false;
        }
        if (admin.getTokenExpireTime() != null && admin.getTokenExpireTime().isBefore(LocalDateTime.now())) {
            return false;
        }
        return token.equals(admin.getToken());
    }

    @Override
    public AdminLoginVO refresh(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("刷新 token 无效");
        }
        Claims claims = AdminJwtUtil.parseToken(refreshToken, jwtSecret);
        if (claims == null || !"refresh".equals(claims.get("tokenType"))) {
            throw new IllegalArgumentException("刷新 token 无效");
        }
        Long adminId = getAdminIdFromClaims(claims);
        if (adminId == null) {
            throw new IllegalArgumentException("刷新 token 无效");
        }
        MallAdminEntity admin = mallAdminMapper.selectById(adminId);
        if (admin == null || !refreshToken.equals(admin.getRefreshToken())) {
            throw new IllegalArgumentException("刷新 token 无效");
        }
        if (admin.getRefreshTokenExpireTime() == null || admin.getRefreshTokenExpireTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("刷新 token 已过期");
        }

        String newToken = AdminJwtUtil.generateToken(admin.getId(), admin.getUsername(), jwtSecret, jwtTtlMs);
        String newRefreshToken = AdminJwtUtil.generateRefreshToken(admin.getId(), admin.getUsername(), jwtSecret, jwtRefreshTtlMs);
        long expireAt = System.currentTimeMillis() + jwtTtlMs;
        long refreshExpireAt = System.currentTimeMillis() + jwtRefreshTtlMs;

        admin.setToken(newToken);
        admin.setTokenExpireTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(expireAt), ZoneId.systemDefault()));
        admin.setRefreshToken(newRefreshToken);
        admin.setRefreshTokenExpireTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(refreshExpireAt), ZoneId.systemDefault()));
        mallAdminMapper.updateById(admin);

        return AdminLoginVO.builder()
                .token(newToken)
                .refreshToken(newRefreshToken)
                .username(admin.getUsername())
                .expireTime(expireAt)
                .refreshExpireTime(refreshExpireAt)
                .build();
    }

    @Override
    public void logout(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            return;
        }
        Claims claims = AdminJwtUtil.parseToken(accessToken, jwtSecret);
        if (claims == null) {
            return;
        }
        Long adminId = getAdminIdFromClaims(claims);
        if (adminId == null) {
            return;
        }
        mallAdminMapper.update(null, new LambdaUpdateWrapper<MallAdminEntity>()
                .eq(MallAdminEntity::getId, adminId)
                .set(MallAdminEntity::getToken, null)
                .set(MallAdminEntity::getTokenExpireTime, null)
                .set(MallAdminEntity::getRefreshToken, null)
                .set(MallAdminEntity::getRefreshTokenExpireTime, null));
    }

    @Override
    public void changePassword(String accessToken, AdminChangePasswordDTO dto) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException("未登录或 Token 无效");
        }
        Claims claims = AdminJwtUtil.parseToken(accessToken, jwtSecret);
        if (claims == null || !"access".equals(claims.get("tokenType"))) {
            throw new IllegalArgumentException("登录状态已失效");
        }
        Long adminId = getAdminIdFromClaims(claims);
        if (adminId == null) {
            throw new IllegalArgumentException("登录状态已失效");
        }

        MallAdminEntity admin = mallAdminMapper.selectById(adminId);
        if (admin == null || admin.getPassword() == null) {
            throw new IllegalArgumentException("账号不存在");
        }

        String oldRaw = dto.getOldPassword();
        boolean oldOk = admin.getPassword().startsWith("$2a$")
                ? PASSWORD_ENCODER.matches(oldRaw, admin.getPassword())
                : oldRaw.equals(admin.getPassword());
        if (!oldOk) {
            throw new IllegalArgumentException("原密码不正确");
        }

        String newRaw = dto.getNewPassword();
        admin.setPassword(PASSWORD_ENCODER.encode(newRaw));
        admin.setToken(null);
        admin.setTokenExpireTime(null);
        admin.setRefreshToken(null);
        admin.setRefreshTokenExpireTime(null);
        mallAdminMapper.updateById(admin);
    }

    private static Long getAdminIdFromClaims(Claims claims) {
        Object adminIdObj = claims.get("adminId");
        if (adminIdObj == null) {
            return null;
        }
        try {
            return Long.parseLong(String.valueOf(adminIdObj));
        } catch (Exception e) {
            return null;
        }
    }
}

