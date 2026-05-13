package com.yuan.mall.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 后台刷新 token 请求参数
 */
@Data
public class AdminRefreshDTO {

    @NotBlank(message = "刷新 token 不能为空")
    private String refreshToken;
}
