package com.yuan.mall.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import com.yuan.mall.interceptor.MiniAuthInterceptor;
import com.yuan.mall.interceptor.AdminAuthInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Web MVC 配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${yuan.upload.path:./data/upload}")
    private String uploadPath;

    private final MiniAuthInterceptor miniAuthInterceptor;
    private final AdminAuthInterceptor adminAuthInterceptor;

    public WebMvcConfig(MiniAuthInterceptor miniAuthInterceptor,
                        AdminAuthInterceptor adminAuthInterceptor) {
        this.miniAuthInterceptor = miniAuthInterceptor;
        this.adminAuthInterceptor = adminAuthInterceptor;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path dir = Paths.get(uploadPath).toAbsolutePath().normalize();
        String location = "file:" + dir + "/";
        registry.addResourceHandler("/upload/**").addResourceLocations(location);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(miniAuthInterceptor)
                .addPathPatterns("/mall/mini/**");
        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/mall/admin/**");
    }
}
