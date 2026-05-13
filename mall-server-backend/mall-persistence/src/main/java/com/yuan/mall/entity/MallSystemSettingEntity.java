package com.yuan.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统设置实体
 */
@Data
@TableName("mall_system_setting")
public class MallSystemSettingEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String mallName;

    private String serviceMobile;

    private String cancelMinutes;

    private String freeShippingAmount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
