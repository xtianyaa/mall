package com.yuan.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商城后台管理员实体
 */
@Data
@TableName("mall_admin")
public class MallAdminEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String token;

    private LocalDateTime tokenExpireTime;

    /** 刷新 token，用于换取新 access token */
    private String refreshToken;

    private LocalDateTime refreshTokenExpireTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
