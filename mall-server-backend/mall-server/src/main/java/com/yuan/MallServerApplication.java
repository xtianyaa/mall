package com.yuan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 日期：2026年 03月 09日
 * 版权所有：tyaa
 *
 * @author system
 * Classname: MallServerApplication
 * Description: 商城后端启动类
 */
@SpringBootApplication(scanBasePackages = {"com.yuan"})
@MapperScan(basePackages = {"com.yuan.mall.mapper"})
@EnableScheduling
public class MallServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallServerApplication.class, args);
    }
}
