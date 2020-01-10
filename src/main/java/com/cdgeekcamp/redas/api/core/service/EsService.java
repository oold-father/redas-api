package com.cdgeekcamp.redas.api.core.service;

import com.cdgeekcamp.redas.lib.core.esConfig.EsApiCoreConfig;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EsService {
    @Autowired
    private EsApiCoreConfig esApiCoreConfig;

    public RestHighLevelClient getClient() {

        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(esApiCoreConfig.getHost(), esApiCoreConfig.getPort(), esApiCoreConfig.getScheme())));
    }
}
