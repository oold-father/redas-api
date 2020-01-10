package com.cdgeekcamp.redas.api.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Value("${require_api.mq_add_url}")
    private String mqAddUrl;

    public String getMqAddUrl() {
        return mqAddUrl;
    }
}
