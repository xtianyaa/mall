package com.yuan.mall.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * HTTP 客户端配置（供微信 code2Session 等调用）
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        // 兼容微信 code2Session 接口返回 content-type 为 text/plain 的情况
        for (HttpMessageConverter<?> converter : restTemplate.getMessageConverters()) {
            if (converter instanceof MappingJackson2HttpMessageConverter jacksonConverter) {
                List<MediaType> types = new ArrayList<>(jacksonConverter.getSupportedMediaTypes());
                if (!types.contains(MediaType.TEXT_PLAIN)) {
                    types.add(MediaType.TEXT_PLAIN);
                    jacksonConverter.setSupportedMediaTypes(types);
                }
            }
        }
        return restTemplate;
    }
}
