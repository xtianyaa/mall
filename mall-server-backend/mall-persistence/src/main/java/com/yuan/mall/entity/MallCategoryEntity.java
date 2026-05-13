package com.yuan.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品分类实体
 */
@Data
@TableName("mall_category")
public class MallCategoryEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    /** 分类图标：图片 URL（如 /upload/xxx.png）或为空使用默认展示 */
    private String icon;

    private Integer sort;

    private Boolean status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
