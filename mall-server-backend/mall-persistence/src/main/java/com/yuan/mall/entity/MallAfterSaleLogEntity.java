package com.yuan.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 售后日志实体
 */
@Data
@TableName("mall_after_sale_log")
public class MallAfterSaleLogEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 售后主单 ID
     */
    private Long afterSaleId;

    /**
     * 操作类型：APPLY / APPROVE / REJECT / COMPLETE 等
     */
    private String action;

    /**
     * 操作人 ID（用户或管理员）
     */
    private Long operatorId;

    /**
     * 操作人角色：USER / ADMIN
     */
    private String operatorRole;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

