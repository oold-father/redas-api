package com.cdgeekcamp.redas.api.core.service;

import com.cdgeekcamp.redas.api.core.config.EsConfig;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EsService {
    @Autowired
    private EsConfig esConfig;

    public RestHighLevelClient getClient() {

        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(esConfig.getHost(), esConfig.getPort(), esConfig.getScheme())));
    }
}
