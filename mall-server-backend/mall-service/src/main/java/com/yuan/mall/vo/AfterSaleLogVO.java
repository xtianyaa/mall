package com.yuan.mall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 售后日志视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AfterSaleLogVO {

    private String action;

    private String remark;

    private String operatorRole;

    private String createTime;
}

