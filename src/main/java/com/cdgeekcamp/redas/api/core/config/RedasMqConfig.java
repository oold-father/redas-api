package com.cdgeekcamp.redas.api.core.config;

import com.cdgeekcamp.redas.lib.core.mqConfig.RedasConfigBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RedasMqConfig extends RedasConfigBase {
    @Value("${redas.mq.host}")
    private String host;

    @Value("${redas.mq.group}")
    private String group;

    @Value("${redas.mq.topic}")
    private String topic;

    public RedasMqConfig() {
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
