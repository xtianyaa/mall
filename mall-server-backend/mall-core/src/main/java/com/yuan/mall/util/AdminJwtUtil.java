package com.yuan.mall.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 后台管理员 JWT 工具类
 */
public class AdminJwtUtil {

    /**
     * 生成 JWT
     *
     * @param adminId  管理员 ID
     * @param username 管理员账号
     * @param secret   签名密钥
     * @param ttlMs    过期时间（毫秒）
     * @return token 字符串
     */
    public static String generateToken(Long adminId, String username, String secret, long ttlMs) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date exp = new Date(nowMillis + ttlMs);

        Map<String, Object> claims = new HashMap<>();
        claims.put("adminId", adminId);
        claims.put("username", username);
        claims.put("tokenType", "access");

        byte[] keyBytes = Decoders.BASE64.decode(secret);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(adminId))
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(Keys.hmacShaKeyFor(keyBytes), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 生成刷新用 JWT（仅用于换取新 access token）
     */
    public static String generateRefreshToken(Long adminId, String username, String secret, long ttlMs) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date exp = new Date(nowMillis + ttlMs);
        Map<String, Object> claims = new HashMap<>();
        claims.put("adminId", adminId);
        claims.put("username", username);
        claims.put("tokenType", "refresh");
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(adminId))
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(Keys.hmacShaKeyFor(keyBytes), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析并验证 JWT
     *
     * @param token  token 字符串
     * @param secret 签名密钥
     * @return Claims，解析失败或过期返回 null
     */
    public static Claims parseToken(String token, String secret) {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secret);
            return Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(keyBytes))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }
}

