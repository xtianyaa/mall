package com.yuan.mall.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 微信小程序 code2Session 接口响应
 * 文档：https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/user-login/code2Session.html
 */
@Data
public class WeChatCode2SessionResponse {

    /** 错误码，0 表示成功 */
    private Integer errcode;

    /** 错误信息 */
    private String errmsg;

    /** 用户唯一标识 */
    private String openid;

    /** 会话密钥 */
    @JsonProperty("session_key")
    private String sessionKey;

    /** 用户在开放平台的唯一标识（小程序绑定开放平台时返回） */
    private String unionid;

    public boolean isSuccess() {
        return errcode != null && errcode == 0;
    }
}
