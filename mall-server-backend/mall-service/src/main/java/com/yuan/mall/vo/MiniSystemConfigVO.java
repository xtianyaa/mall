package com.yuan.mall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 小程序端系统配置（仅公开字段，如商城名称）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MiniSystemConfigVO {

    private String mallName;
}
