package com.yuan.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuan.mall.entity.MallUserEntity;
import com.yuan.mall.mapper.MallUserMapper;
import com.yuan.mall.service.MiniAuthService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 小程序端鉴权服务实现（简化版）
 */
@Service
public class MiniAuthServiceImpl implements MiniAuthService {

    private final MallUserMapper mallUserMapper;

    public MiniAuthServiceImpl(MallUserMapper mallUserMapper) {
        this.mallUserMapper = mallUserMapper;
    }

    @Override
    public boolean validate(Long userId, String token) {
        if (userId == null || token == null || token.isBlank()) {
            return false;
        }
        MallUserEntity userEntity = mallUserMapper.selectOne(new LambdaQueryWrapper<MallUserEntity>()
                .eq(MallUserEntity::getId, userId)
                .eq(MallUserEntity::getToken, token)
                .last("limit 1"));
        if (userEntity == null) {
            return false;
        }
        return userEntity.getTokenExpireTime() == null || !userEntity.getTokenExpireTime().isBefore(LocalDateTime.now());
    }
}

