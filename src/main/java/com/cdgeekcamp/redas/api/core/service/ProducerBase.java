package com.cdgeekcamp.redas.api.core.service;

import com.cdgeekcamp.redas.lib.core.api.ApiResponse;

public interface ProducerBase {
    ApiResponse producerHandle(String stringToMq);
}
