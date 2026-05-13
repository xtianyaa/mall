package com.yuan.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@TableName("mall_user")
public class MallUserEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String nickName;

    private String mobile;

    private String levelName;

    private Integer points;

    private Integer orderCount;

    private BigDecimal consumeAmount;

    /** 微信小程序 openid，用于 code2Session 登录绑定 */
    private String openid;

    private String token;

    private LocalDateTime tokenExpireTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
