package com.yuan.mall.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 小程序微信授权登录参数
 * 前端通过 wx.login 获取 code 后提交，服务端用 code 调用 code2Session 换取 openid 并绑定/创建用户
 */
@Data
public class MiniLoginDTO {

    /** 微信 wx.login 返回的临时 code，必填 */
    @NotBlank(message = "登录 code 不能为空")
    private String code;

    /** 用户昵称，选填；新用户首次登录可传入，老用户以 openid 为准 */
    private String nickName;
}
