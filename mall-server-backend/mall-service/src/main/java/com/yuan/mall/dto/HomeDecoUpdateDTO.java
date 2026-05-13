package com.yuan.mall.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 首页首屏文案配置更新参数
 */
@Data
public class HomeDecoUpdateDTO {

    @NotBlank(message = "搜索占位文案不能为空")
    private String searchPlaceholder;

    @NotBlank(message = "服务1数值不能为空")
    private String stat1Value;

    @NotBlank(message = "服务1描述不能为空")
    private String stat1Label;

    @NotBlank(message = "服务2数值不能为空")
    private String stat2Value;

    @NotBlank(message = "服务2描述不能为空")
    private String stat2Label;

    @NotBlank(message = "服务3数值不能为空")
    private String stat3Value;

    @NotBlank(message = "服务3描述不能为空")
    private String stat3Label;
}
