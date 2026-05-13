package com.yuan.mall.wechat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 微信小程序登录：code2Session 客户端
 * 使用 wx.login 获得的 code 换取 openid/session_key
 */
@Component
public class WeChatMiniAuthClient {

    private static final String CODE2SESSION_URL =
            "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";

    private final String appId;
    private final String secret;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WeChatMiniAuthClient(
            @Value("${yuan.wechat.app-id:}") String appId,
            @Value("${yuan.wechat.secret:}") String secret,
            RestTemplate restTemplate) {
        this.appId = appId == null ? "" : appId.trim();
        this.secret = secret == null ? "" : secret.trim();
        this.restTemplate = restTemplate;
    }

    /**
     * 使用小程序端 wx.login 返回的 code 换取 openid
     *
     * @param code 小程序 wx.login 获得的临时 code（5 分钟有效，一次性）
     * @return openid，同一小程序下用户唯一
     * @throws IllegalArgumentException 未配置 appId/secret、或微信接口返回错误
     */
    public String code2Session(String code) {
        if (appId.isEmpty() || secret.isEmpty()) {
            throw new IllegalArgumentException("未配置小程序微信 appId/secret，请在 yuan.wechat 下配置");
        }
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("登录 code 不能为空");
        }
        String url = String.format(CODE2SESSION_URL, appId, secret, code.trim());
        // 微信 code2Session 有时会返回 content-type=text/plain，这里先拿字符串再手动反序列化
        String body = restTemplate.getForObject(url, String.class);
        WeChatCode2SessionResponse resp;
        try {
            resp = objectMapper.readValue(body, WeChatCode2SessionResponse.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("微信 code2Session 响应解析失败：" + e.getMessage(), e);
        }
        if (resp == null) {
            throw new IllegalArgumentException("微信 code2Session 请求无响应");
        }
        if (resp.getOpenid() == null || resp.getOpenid().isBlank()) {
            throw new IllegalArgumentException("微信登录返回的 openid 为空");
        }
        return resp.getOpenid();
    }
}
