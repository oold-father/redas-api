package com.cdgeekcamp.redas.api.core.config;

import com.cdgeekcamp.redas.lib.core.mqConfig.RedasConfigBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PositionDetailHtmlMqConfig extends RedasConfigBase {
    @Value("${redas.mq_position_detail_html.host}")
    private String host;

    @Value("${redas.mq_position_detail_html.group}")
    private String group;

    @Value("${redas.mq_position_detail_html.topic}")
    private String topic;

    public PositionDetailHtmlMqConfig() {
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
