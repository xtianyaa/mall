package com.yuan.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户地址实体
 */
@Data
@TableName("mall_user_address")
public class MallUserAddressEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String name;

    private String mobile;

    private String province;

    private String city;

    private String district;

    private String detail;

    private Boolean isDefault;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
