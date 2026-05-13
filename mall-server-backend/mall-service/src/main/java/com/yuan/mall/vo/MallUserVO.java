package com.yuan.mall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 用户视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MallUserVO {

    private Long id;
    private String nickName;
    private String mobile;
    private String levelName;
    private Integer orderCount;
    private BigDecimal consumeAmount;
}
