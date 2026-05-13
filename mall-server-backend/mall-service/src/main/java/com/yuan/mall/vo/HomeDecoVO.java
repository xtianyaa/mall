package com.yuan.mall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 首页首屏文案配置 VO（小程序与管理端共用）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeDecoVO {

    /** 搜索框占位文案 */
    private String searchPlaceholder;

    /** 三项服务承诺：{ value, label } */
    private List<StatItem> stats;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatItem {
        private String value;
        private String label;
    }
}
