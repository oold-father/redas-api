package com.cdgeekcamp.redas.api.core.service;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                //是否发送Cookie
                .allowCredentials(true)
                //放行哪些原始域
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*");
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new EsInterceptor())
                .addPathPatterns("/es/**");

        registry.addInterceptor(new EsInterceptor())
                .addPathPatterns("/webChart/**")
                .excludePathPatterns("/webChart/getTerm");

        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
