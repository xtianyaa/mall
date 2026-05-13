package com.yuan.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 首页 Banner 实体
 */
@Data
@TableName("mall_banner")
public class MallBannerEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String imageUrl;

    private String linkType;

    private String linkUrl;

    private Boolean status;

    private Integer sort;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
