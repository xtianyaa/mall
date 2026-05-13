package com.yuan.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 首页首屏文案配置实体（首页装修）
 */
@Data
@TableName("mall_home_deco")
public class MallHomeDecoEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String searchPlaceholder;

    private String stat1Value;

    private String stat1Label;

    private String stat2Value;

    private String stat2Label;

    private String stat3Value;

    private String stat3Label;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
