package com.cdgeekcamp.redas.api.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EsConfig {
        @Value("${redas.elasticsearch.position.host}")
        private String host;

        @Value("${redas.elasticsearch.position.port}")
        private String port;

        @Value("${redas.elasticsearch.position.scheme}")
        private String scheme;

        @Value("${redas.elasticsearch.position.index}")
        private String index;

        @Value("${redas.elasticsearch.position.type}")
        private String type;

        public EsConfig() {
        }

        public EsConfig(String host, String port, String scheme, String index, String type) {
                this.host = host;
                this.port = port;
                this.scheme = scheme;
                this.index = index;
                this.type = type;
        }

        public String getHost() {
                return host;
        }

        public void setHost(String host) {
                this.host = host;
        }

        public int getPort() {
                int port = Integer.parseInt(this.port);
                return port;
        }

        public void setPort(String port) {
                this.port = port;
        }

        public String getScheme() {
                return scheme;
        }

        public void setScheme(String scheme) {
                this.scheme = scheme;
        }

        public String getIndex() {
                return index;
        }

        public void setIndex(String index) {
                this.index = index;
        }

        public String getType() {
                return type;
        }

        public void setType(String type) {
                this.type = type;
        }

}
