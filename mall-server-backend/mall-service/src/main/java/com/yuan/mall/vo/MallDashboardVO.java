package com.yuan.mall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 工作台数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MallDashboardVO {

    private Integer todayOrderCount;
    private Integer waitShipCount;
    private Integer userCount;
    private String todayAmount;
}
