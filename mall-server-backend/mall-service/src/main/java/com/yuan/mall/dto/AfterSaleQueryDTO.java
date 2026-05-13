package com.yuan.mall.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 售后单查询参数
 */
@Data
public class AfterSaleQueryDTO {

    /**
     * 订单号（可选）
     */
    private String orderNo;

    /**
     * 用户手机号或昵称（可选）
     */
    private String userKeyword;

    /**
     * 售后类型（可选）
     */
    private String type;

    /**
     * 售后状态（可选）
     */
    private String status;

    /**
     * 申请时间起
     */
    private LocalDateTime applyTimeStart;

    /**
     * 申请时间止
     */
    private LocalDateTime applyTimeEnd;

    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 每页大小
     */
    private Integer pageSize;
}

