package com.cdgeekcamp.redas.api.core.config;

import com.cdgeekcamp.redas.lib.core.mqConfig.RedasConfigBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RequireApiConfig extends RedasConfigBase {
    @Value("${require_api.mq_add_url}")
    private String mqAddUrl;

    public String getMqAddUrl() {
        return mqAddUrl;
    }
}
