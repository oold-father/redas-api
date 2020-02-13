package com.cdgeekcamp.redas.api.core.config;

import com.cdgeekcamp.redas.lib.core.mqConfig.RedasConfigBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PositionUrlMqConfig extends RedasConfigBase {
    @Value("${redas.mq_position_url.host}")
    private String host;

    @Value("${redas.mq_position_url.group}")
    private String group;

    @Value("${redas.mq_position_url.topic}")
    private String topic;

    public PositionUrlMqConfig() {
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
