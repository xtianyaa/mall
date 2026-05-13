package com.yuan.mall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 物流轨迹节点（时间 + 描述）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogisticsTraceVO {

    /** 时间，格式 yyyy-MM-dd HH:mm */
    private String time;
    /** 节点描述 */
    private String desc;
}
